package telekinesis.client;

import com.google.protobuf.ByteString;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import telekinesis.client.module.SteamFriends;
import telekinesis.connection.ClientMessageContext;
import telekinesis.connection.ConnectionState;
import telekinesis.connection.SteamConnection;
import telekinesis.message.SimpleClientMessageTypeRegistry;
import telekinesis.model.ClientMessageHandler;
import telekinesis.model.SteamClientDelegate;
import telekinesis.model.steam.EMsg;
import telekinesis.model.steam.EPersonaState;
import telekinesis.model.steam.EResult;
import telekinesis.util.MessageDispatcher;
import telekinesis.util.Publisher;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashSet;
import java.util.Set;

import static telekinesis.message.proto.generated.steam.SM_ClientServer.*;

public class SteamClient extends Publisher<SteamClient> implements ClientMessageHandler {

    private static final SimpleClientMessageTypeRegistry HANDLED_MESSAGES = new SimpleClientMessageTypeRegistry()
            .registerProto(EMsg.ClientLogon.v(), CMsgClientLogon.class)
            .registerProto(EMsg.ClientLogOnResponse.v(), CMsgClientLogonResponse.class)
            .registerProto(EMsg.ClientUpdateMachineAuth.v(), CMsgClientUpdateMachineAuth.class)
            .registerProto(EMsg.ClientUpdateMachineAuthResponse.v(), CMsgClientUpdateMachineAuthResponse.class)
            .registerProto(EMsg.ClientAccountInfo.v(), CMsgClientAccountInfo.class)
            .registerProto(EMsg.ClientNewLoginKey.v(), CMsgClientNewLoginKey.class)
            .registerProto(EMsg.ClientNewLoginKeyAccepted.v(), CMsgClientNewLoginKeyAccepted.class)
            .registerProto(EMsg.ClientHeartBeat.v(), CMsgClientHeartBeat.class);

    private final Logger log;
    private final EventLoopGroup workerGroup;
    private final SteamClientDelegate credentials;
    private final MessageDispatcher selfHandledMessageDispatcher;
    private final Set<SteamClientModule> modules;

    private SteamConnection connection;
    private long nextSourceJobId = 0L;

    public SteamClient(EventLoopGroup workerGroup, String id, SteamClientDelegate credentials) {
        this.workerGroup = workerGroup;
        this.log = LoggerFactory.getLogger(id);
        this.credentials = credentials;
        this.modules = new LinkedHashSet<>();

        selfHandledMessageDispatcher = new MessageDispatcher();
        selfHandledMessageDispatcher.subscribe(CMsgClientLogonResponse.class, this::handleClientLogonResponse);
        selfHandledMessageDispatcher.subscribe(CMsgClientUpdateMachineAuth.class, this::handleClientUpdateMachineAuth);
        selfHandledMessageDispatcher.subscribe(CMsgClientAccountInfo.class, this::handleClientAccountInfo);
        selfHandledMessageDispatcher.subscribe(CMsgClientNewLoginKey.class, this::handleClientNewLoginKey);

        connection = new SteamConnection(workerGroup, this, log.getName() + "-conn");
        connection.addRegistry(HANDLED_MESSAGES);
        connection.subscribe(ConnectionState.class, this::handleConnectionStateChange);

        registerModule(new SteamFriends());
    }

    public void registerModule(SteamClientModule module) {
        if (modules.add(module)) {
            connection.addRegistry(module.getHandledMessages());
            module.setSteamClient(this);
        }
    }

    public void unregisterModule(SteamClientModule module) {
        if (modules.remove(module)) {
            connection.removeRegistry(module.getHandledMessages());
            module.setSteamClient(null);
        }
    }

    public <T extends SteamClientModule> T getModule(Class<T> moduleClass) {
        for (SteamClientModule module : modules) {
            if (moduleClass.isAssignableFrom(module.getClass())) {
                return (T) module;
            }
        }
        return null;
    }

    public void connect() {
        connection.connect("208.78.164.9", 27018);
    }

    public void disconnect() {
        connection.disconnect();
    }

    public void send(Object body) {
        connection.send(body);
    }

    public void request(Object body) {
        long jid = nextSourceJobId++;
        connection.request(jid, body);
    }

    // TODO: only for testing, remove this
    public boolean isConnectionAlive() {
        return connection != null;
    }

    protected void performLogon() throws IOException {
        log.info("performing logon for {}", credentials.getAccountName());
        CMsgClientLogon.Builder logon = CMsgClientLogon.newBuilder();
        logon.setProtocolVersion(65575);
        logon.setAccountName(credentials.getAccountName());
        logon.setPassword(credentials.getPassword());
        byte[] sentrySha = credentials.getSentrySha1();
        if (sentrySha != null) {
            logon.setEresultSentryfile(EResult.OK.v());
            logon.setShaSentryfile(ByteString.copyFrom(sentrySha));
        } else {
            logon.setEresultSentryfile(EResult.FileNotFound.v());
        }
        connection.send(logon);
    }

    protected void handleConnectionStateChange(SteamConnection conn, ConnectionState newState) throws IOException {
        switch(newState) {
            case ESTABLISHED:
                performLogon();
                break;
            case BROKEN:
                connection.disconnect();
                break;
            case CONNECTION_FAILED:
            case CLOSED:
            case LOST:
                connection = null;
                break;

            default:
                break;
        }
    }

    @Override
    public void handleClientMessage(ClientMessageContext ctx, Object message) {
        selfHandledMessageDispatcher.handleClientMessage(ctx, message);
        for (SteamClientModule module : modules) {
            module.handleClientMessage(ctx, message);
        }
    }

    protected void handleClientLogonResponse(ClientMessageContext ctx, CMsgClientLogonResponse msg) {
        log.info("received logon response");
        if (msg.getEresult() == EResult.OK.v()) {
            connection.enableHeartbeat(msg.getOutOfGameHeartbeatSeconds());

            getModule(SteamFriends.class).setPersonaState(EPersonaState.Online);
        }
    }

    protected void handleClientUpdateMachineAuth(ClientMessageContext ctx, CMsgClientUpdateMachineAuth msg) throws IOException, NoSuchAlgorithmException {
        log.info("received update machine auth request");
        log.info(msg.toString());

        if (msg.getCubtowrite() != msg.getBytes().size()) {
            throw new IOException("assert failed: bytes.size != cubtowrite");
        }

        CMsgClientUpdateMachineAuthResponse.Builder builder = CMsgClientUpdateMachineAuthResponse.newBuilder();
        try {
            credentials.writeSentry(msg.getFilename(), msg.getOffset(), msg.getBytes());
            builder.setShaFile(ByteString.copyFrom(credentials.getSentrySha1()));
            builder.setEresult(EResult.OK.v());
            builder.setCubwrote(msg.getCubtowrite());
            builder.setFilename(msg.getFilename());
        } catch (IOException e) {
            builder.setEresult(EResult.DiskFull.v());
        }
        ctx.reply(builder);

    }

    protected void handleClientAccountInfo(ClientMessageContext ctx, CMsgClientAccountInfo msg) {
        log.info("received account info");
    }

    protected void handleClientNewLoginKey(ClientMessageContext ctx, CMsgClientNewLoginKey msg) throws IOException {
        log.info("received client new login key");
        CMsgClientNewLoginKeyAccepted.Builder response = CMsgClientNewLoginKeyAccepted.newBuilder();
        response.setUniqueId(msg.getUniqueId());
        ctx.reply(response);
    }
}
