package telekinesis.message.internal;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.connection.ConnectionContext;
import telekinesis.message.ToWire;
import telekinesis.message.TransmittableMessage;

public abstract class BaseInternalSendable<B extends ToWire> extends BaseInternal<B> implements TransmittableMessage<BaseInternal.Header, B> {

    @Override
    public void prepareTransmission(ConnectionContext context) throws IOException {
    }

    @Override
    public void serialize(ByteBuffer buf) throws IOException {
        getHeader().serialize(buf);
        getBody().serialize(buf);
    }

}
