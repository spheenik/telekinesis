package telekinesis.message;

import java.io.IOException;

import telekinesis.connection.ConnectionContext;

public interface ReceivableMessage<H, B> extends Message<H, B>, Decodable {

    void updateContext(ConnectionContext context) throws IOException;
    
}
