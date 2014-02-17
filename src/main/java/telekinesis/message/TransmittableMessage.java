package telekinesis.message;

import java.io.IOException;

import telekinesis.connection.ConnectionContext;

public interface TransmittableMessage<H, B> extends Message<H, B>, Encodable {

    void prepareTransmission(ConnectionContext context) throws IOException;

}
