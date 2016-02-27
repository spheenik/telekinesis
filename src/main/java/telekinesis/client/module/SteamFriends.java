package telekinesis.client.module;

import com.google.protobuf.ByteString;
import telekinesis.client.SteamClientModule;
import telekinesis.connection.ClientMessageContext;
import telekinesis.message.ClientMessageTypeRegistry;
import telekinesis.message.SimpleClientMessageTypeRegistry;
import telekinesis.message.proto.generated.steam.SM_ClientServer;
import telekinesis.model.steam.EMsg;
import telekinesis.model.steam.EPersonaState;
import telekinesis.util.MessageDispatcher;

import java.io.UnsupportedEncodingException;
import java.time.Instant;

public class SteamFriends extends SteamClientModule {

    private static final SimpleClientMessageTypeRegistry HANDLED_MESSAGES = new SimpleClientMessageTypeRegistry()
            .registerProto(EMsg.ClientChangeStatus.v(), SM_ClientServer.CMsgClientChangeStatus.class)
            .registerProto(EMsg.ClientPersonaState.v(), SM_ClientServer.CMsgClientPersonaState.class)
            .registerProto(EMsg.ClientFriendsList.v(), SM_ClientServer.CMsgClientFriendsList.class)
            .registerProto(EMsg.ClientPlayerNicknameList.v(), SM_ClientServer.CMsgClientPlayerNicknameList.class)
            .registerProto(EMsg.ClientFriendMsgIncoming.v(), SM_ClientServer.CMsgClientFriendMsgIncoming.class)
            .registerProto(EMsg.ClientFriendMsg.v(), SM_ClientServer.CMsgClientFriendMsg.class);

    private final MessageDispatcher selfHandledMessageDispatcher;

    public SteamFriends() {
        selfHandledMessageDispatcher = new MessageDispatcher();
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgClientPersonaState.class, this::handleClientPersonaState);
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgClientFriendsList.class, this::handleClientFriendsList);
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgClientPlayerNicknameList.class, this::handleClientPlayerNicknameList);
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgClientFriendMsgIncoming.class, this::handleClientFriendMsgIncoming);
    }

    @Override
    public ClientMessageTypeRegistry getHandledMessages() {
        return HANDLED_MESSAGES;
    }

    @Override
    public void handleClientMessage(ClientMessageContext ctx, Object message) {
        selfHandledMessageDispatcher.handleClientMessage(ctx, message);
    }

    public void handleClientPersonaState(ClientMessageContext ctx, SM_ClientServer.CMsgClientPersonaState msg) {
        System.out.println(msg);
    }

    public void handleClientFriendsList(ClientMessageContext ctx, SM_ClientServer.CMsgClientFriendsList msg) {
        System.out.println(msg);
    }

    public void handleClientPlayerNicknameList(ClientMessageContext ctx, SM_ClientServer.CMsgClientPlayerNicknameList msg) {
        System.out.println(msg);
    }

    public void handleClientFriendMsgIncoming(ClientMessageContext ctx, SM_ClientServer.CMsgClientFriendMsgIncoming msg) throws UnsupportedEncodingException {
        System.out.println(msg);


        String in = new String(msg.toByteArray(), "UTF-8");
        String out = String.format("You said: %s", in);

        SM_ClientServer.CMsgClientFriendMsg.Builder builder = SM_ClientServer.CMsgClientFriendMsg.newBuilder();
        builder.setSteamid(msg.getSteamidFrom());
        builder.setMessage(ByteString.copyFrom(out.getBytes()));
        builder.setRtime32ServerTimestamp((int) Instant.now().getEpochSecond());

        steamClient.send(builder);
    }

    public void setPersonaState(EPersonaState personaState) {
        SM_ClientServer.CMsgClientChangeStatus.Builder builder = SM_ClientServer.CMsgClientChangeStatus.newBuilder();
        builder.setPlayerName("tkbot");
        builder.setPersonaState(personaState.v());
        steamClient.request(builder);
    }

}
