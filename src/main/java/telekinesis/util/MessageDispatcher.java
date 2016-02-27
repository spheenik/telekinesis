package telekinesis.util;

import telekinesis.connection.ClientMessageContext;
import telekinesis.model.ClientMessageHandler;

public class MessageDispatcher extends Publisher<ClientMessageContext> implements ClientMessageHandler {

    @Override
    public void handleClientMessage(ClientMessageContext ctx, Object message) {
        publish(ctx, message);
    }

}
