package telekinesis.message;

import java.io.IOException;

import telekinesis.connection.ConnectionContext;

public interface ReceivableMessage<H, B> extends Message<H, B>, FromWire {

    void updateContext(ConnectionContext context) throws IOException;
    
}
