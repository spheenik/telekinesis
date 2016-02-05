package telekinesis;

import com.google.protobuf.ByteString;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import telekinesis.connection.ConnectionState;
import telekinesis.connection.IdleTimeoutFunction;
import telekinesis.connection.SteamConnection;
import telekinesis.message.proto.generated.SM_ClientServer;
import telekinesis.model.SteamClientDelegate;
import telekinesis.model.steam.EMsg;
import telekinesis.model.steam.EResult;
import telekinesis.registry.MessageRegistry;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SteamClient extends Publisher<SteamClient> {

    private static final MessageRegistry HANDLED_MESSAGES = new MessageRegistry()
            .registerProto(EMsg.ClientLogon.v(), SM_ClientServer.CMsgClientLogon.class)
            .registerProto(EMsg.ClientLogOnResponse.v(), SM_ClientServer.CMsgClientLogonResponse.class)
            .registerProto(EMsg.ClientUpdateMachineAuth.v(), SM_ClientServer.CMsgClientUpdateMachineAuth.class)
            .registerProto(EMsg.ClientUpdateMachineAuthResponse.v(), SM_ClientServer.CMsgClientUpdateMachineAuthResponse.class)
            .registerProto(EMsg.ClientAccountInfo.v(), SM_ClientServer.CMsgClientAccountInfo.class)
            .registerProto(EMsg.ClientNewLoginKey.v(), SM_ClientServer.CMsgClientNewLoginKey.class)
            .registerProto(EMsg.ClientNewLoginKeyAccepted.v(), SM_ClientServer.CMsgClientNewLoginKeyAccepted.class)
            .registerProto(EMsg.ClientHeartBeat.v(), SM_ClientServer.CMsgClientHeartBeat.class);

    private final Logger log;
    private final EventLoopGroup workerGroup;
    private final SteamClientDelegate credentials;

    private SteamConnection connection;
    private IdleTimeoutFunction heartbeatFunction;

    public SteamClient(EventLoopGroup workerGroup, String id, SteamClientDelegate credentials) {
        this.workerGroup = workerGroup;
        this.log = LoggerFactory.getLogger(id);
        this.credentials = credentials;
    }

    public void connect() {
        connection = new SteamConnection(workerGroup, log.getName() + "-conn");
        connection.addRegistry(HANDLED_MESSAGES);
        connection.connect("208.78.164.9", 27018);
        connection.subscribe(ConnectionState.class, this::handleConnectionStateChange);
        connection.subscribe(SM_ClientServer.CMsgClientLogonResponse.class, this::handleClientLogonResponse);
        connection.subscribe(SM_ClientServer.CMsgClientUpdateMachineAuth.class, this::handleClientUpdateMachineAuth);
        connection.subscribe(SM_ClientServer.CMsgClientAccountInfo.class, this::handleClientAccountInfo);
        connection.subscribe(SM_ClientServer.CMsgClientNewLoginKey.class, this::handleClientNewLoginKey);
    }

    public void disconnect() {
        if (heartbeatFunction != null) {
            heartbeatFunction.cancel();
            heartbeatFunction = null;
        }
        connection.disconnect();
    }

    public void send(Object body) {
        if (heartbeatFunction != null) {
            heartbeatFunction.reset();
        }
        connection.send(body);
    }

    // TODO: only for testing, remove this
    public boolean isConnectionAlive() {
        return connection != null;
    }

    protected void performLogon() throws IOException {
        log.info("performing logon for {}", credentials.getAccountName());
        SM_ClientServer.CMsgClientLogon.Builder logon = SM_ClientServer.CMsgClientLogon.newBuilder();
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

    protected void handleConnectionStateChange(SteamConnection.SteamConnectionContext ctx, ConnectionState newState) throws IOException {
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

    protected void handleClientLogonResponse(SteamConnection.SteamConnectionContext ctx, SM_ClientServer.CMsgClientLogonResponse msg) {
        log.info("received logon response");
        log.info(msg.toString());
        if (msg.getEresult() == EResult.OK.v()) {
            heartbeatFunction = new IdleTimeoutFunction(workerGroup, msg.getOutOfGameHeartbeatSeconds()) {
                @Override
                protected void onTimout() {
                    System.out.println("HEARTBEAT");
                    SM_ClientServer.CMsgClientHeartBeat.Builder msg = SM_ClientServer.CMsgClientHeartBeat.newBuilder();
                    connection.send(msg);
                }
            };
        }
    }

    protected void handleClientUpdateMachineAuth(SteamConnection.SteamConnectionContext ctx, SM_ClientServer.CMsgClientUpdateMachineAuth msg) throws IOException, NoSuchAlgorithmException {
        log.info("received update machine auth request");
        log.info(msg.toString());

        if (msg.getCubtowrite() != msg.getBytes().size()) {
            throw new IOException("assert failed: bytes.size != cubtowrite");
        }

        SM_ClientServer.CMsgClientUpdateMachineAuthResponse.Builder builder = SM_ClientServer.CMsgClientUpdateMachineAuthResponse.newBuilder();
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

    protected void handleClientAccountInfo(SteamConnection.SteamConnectionContext ctx, SM_ClientServer.CMsgClientAccountInfo msg) {
        log.info("received account info");
        //log.info(msg.toString());
    }

    protected void handleClientNewLoginKey(SteamConnection.SteamConnectionContext ctx, SM_ClientServer.CMsgClientNewLoginKey msg) throws IOException {
        log.info("received client new login key");
        log.info(msg.toString());

        SM_ClientServer.CMsgClientNewLoginKeyAccepted.Builder response = SM_ClientServer.CMsgClientNewLoginKeyAccepted.newBuilder();
        response.setUniqueId(msg.getUniqueId());
        ctx.reply(response);
    }
}
