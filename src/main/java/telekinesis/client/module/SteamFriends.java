package telekinesis.client.module;

import telekinesis.client.SteamClientModule;
import telekinesis.connection.ClientMessageContext;
import telekinesis.message.ClientMessageTypeRegistry;
import telekinesis.message.SimpleClientMessageTypeRegistry;
import telekinesis.message.proto.generated.steam.SM_ClientServer;
import telekinesis.model.steam.EChatEntryType;
import telekinesis.model.steam.EMsg;
import telekinesis.model.steam.EPersonaState;
import telekinesis.util.CStringUtil;
import telekinesis.util.MessageDispatcher;

import java.io.IOException;
import java.time.Instant;

public class SteamFriends extends SteamClientModule {

    private static final SimpleClientMessageTypeRegistry HANDLED_MESSAGES = new SimpleClientMessageTypeRegistry()
            .registerProto(EMsg.ClientChangeStatus.v(), SM_ClientServer.CMsgClientChangeStatus.class)
            .registerProto(EMsg.ClientPersonaChangeResponse.v(), SM_ClientServer.CMsgPersonaChangeResponse.class)
            .registerProto(EMsg.ClientPersonaState.v(), SM_ClientServer.CMsgClientPersonaState.class)
            .registerProto(EMsg.ClientFriendsList.v(), SM_ClientServer.CMsgClientFriendsList.class)
            .registerProto(EMsg.ClientPlayerNicknameList.v(), SM_ClientServer.CMsgClientPlayerNicknameList.class)
            .registerProto(EMsg.ClientFriendMsgIncoming.v(), SM_ClientServer.CMsgClientFriendMsgIncoming.class)
            .registerProto(EMsg.ClientFriendMsg.v(), SM_ClientServer.CMsgClientFriendMsg.class)
            .registerProto(EMsg.ClientUDSInviteToGame.v(), SM_ClientServer.CMsgClientUDSInviteToGame.class);

    private final MessageDispatcher selfHandledMessageDispatcher;

    public SteamFriends() {
        selfHandledMessageDispatcher = new MessageDispatcher();
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgPersonaChangeResponse.class, this::handleClientPersonaChangeResponse);
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgClientPersonaState.class, this::handleClientPersonaState);
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgClientFriendsList.class, this::handleClientFriendsList);
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgClientPlayerNicknameList.class, this::handleClientPlayerNicknameList);
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgClientFriendMsgIncoming.class, this::handleClientFriendMsgIncoming);
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgClientUDSInviteToGame.class, this::handleClientUDSInviteToGame);


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
    }

    public void handleClientFriendsList(ClientMessageContext ctx, SM_ClientServer.CMsgClientFriendsList msg) {
    }

    public void handleClientPlayerNicknameList(ClientMessageContext ctx, SM_ClientServer.CMsgClientPlayerNicknameList msg) {
    }

    public void handleClientFriendMsgIncoming(ClientMessageContext ctx, SM_ClientServer.CMsgClientFriendMsgIncoming msg) throws IOException {
        EChatEntryType type = EChatEntryType.f(msg.getChatEntryType());
        switch(type) {
            case Invalid:
                break;

            case ChatMsg:
                String in = CStringUtil.decodeUtf8(msg.getMessage());
                String out = String.format("You said: %s", in);
                sendChat(msg.getSteamidFrom(), out);
                break;

            case Typing:
                break;

            case InviteGame:
                sendChat(msg.getSteamidFrom(), "I was invited to a game");
                break;

            case Emote:
                break;

            case LobbyGameStart:
                sendChat(msg.getSteamidFrom(), "I received a LobbyGameStart");
                break;

            case LeftConversation:
                sendChat(msg.getSteamidFrom(), "You left the conversation");
                break;
        }
    }

    private void sendChat(long steamId, String msg) throws IOException {
        SM_ClientServer.CMsgClientFriendMsg.Builder builder = SM_ClientServer.CMsgClientFriendMsg.newBuilder();
        builder.setSteamid(steamId);
        builder.setChatEntryType(EChatEntryType.ChatMsg.v());
        builder.setMessage(CStringUtil.encodeUtf8(msg));
        builder.setRtime32ServerTimestamp((int) Instant.now().getEpochSecond());
        steamClient.send(builder);
    }

    public void setPersonaState(EPersonaState personaState) {
        SM_ClientServer.CMsgClientChangeStatus.Builder builder = SM_ClientServer.CMsgClientChangeStatus.newBuilder();
        builder.setPlayerName("tkbot");
        builder.setPersonaState(personaState.v());
        steamClient.request(builder, (ctx, payload) -> {
        });
    }

    private void handleClientPersonaChangeResponse(ClientMessageContext clientMessageContext, SM_ClientServer.CMsgPersonaChangeResponse msg) {
    }

    private void handleClientUDSInviteToGame(ClientMessageContext clientMessageContext, SM_ClientServer.CMsgClientUDSInviteToGame msg) {
        System.out.println(msg);
    }




}
