package telekinesis.client.module;

import com.google.protobuf.ByteString;
import telekinesis.client.SteamClientModule;
import telekinesis.connection.ClientMessageContext;
import telekinesis.message.ClientMessageTypeRegistry;
import telekinesis.message.SimpleClientMessageTypeRegistry;
import telekinesis.message.proto.generated.steam.SM_ClientServer;
import telekinesis.model.steam.EMsg;
import telekinesis.util.MessageDispatcher;

import java.util.Deque;
import java.util.LinkedList;

public class GameConnectTokens extends SteamClientModule {

    private static final SimpleClientMessageTypeRegistry HANDLED_MESSAGES = new SimpleClientMessageTypeRegistry()
            .registerProto(EMsg.ClientGameConnectTokens.v(), SM_ClientServer.CMsgClientGameConnectTokens.class);

    private final MessageDispatcher selfHandledMessageDispatcher;
    private final Deque<ByteString> tokens;

    public GameConnectTokens() {
        selfHandledMessageDispatcher = new MessageDispatcher();
        selfHandledMessageDispatcher.subscribe(SM_ClientServer.CMsgClientGameConnectTokens.class, this::handleClientGameConnectTokens);

        tokens = new LinkedList<>();
    }

    @Override
    public ClientMessageTypeRegistry getHandledMessages() {
        return HANDLED_MESSAGES;
    }

    @Override
    public void handleClientMessage(ClientMessageContext ctx, Object message) {
        selfHandledMessageDispatcher.handleClientMessage(ctx, message);
    }

    private void handleClientGameConnectTokens(ClientMessageContext clientMessageContext, SM_ClientServer.CMsgClientGameConnectTokens msg) {
        msg.getTokensList().forEach(tokens::offer);
        while (tokens.size() > msg.getMaxTokensToKeep()) {
            tokens.removeFirst();
        }
    }

    public ByteString retrieveToken() {
        return tokens.removeFirst();
    }


}
