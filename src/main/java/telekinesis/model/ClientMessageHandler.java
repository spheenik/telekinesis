package telekinesis.model;

import telekinesis.connection.ClientMessageContext;

public interface ClientMessageHandler {

    void handleClientMessage(ClientMessageContext ctx, Object message) throws Exception;

}
