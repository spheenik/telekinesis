package telekinesis;

import telekinesis.connection.ClientMessageContext;

public class MessageDispatcher extends Publisher<ClientMessageContext> implements ClientMessageHandler {

    @Override
    public void handleClientMessage(ClientMessageContext ctx, Object message) {
        publish(ctx, message);
    }

}
