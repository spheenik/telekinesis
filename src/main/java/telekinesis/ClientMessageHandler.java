package telekinesis;

import telekinesis.connection.ClientMessageContext;

public interface ClientMessageHandler {

    void handleClientMessage(ClientMessageContext ctx, Object message);

}
