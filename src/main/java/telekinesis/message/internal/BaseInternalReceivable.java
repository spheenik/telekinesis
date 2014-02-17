package telekinesis.message.internal;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.connection.ConnectionContext;
import telekinesis.message.FromWire;
import telekinesis.message.ReceivableMessage;

public abstract class BaseInternalReceivable<B extends FromWire> extends BaseInternal<B> implements ReceivableMessage<BaseInternal.Header, B> {

    @Override
    public void updateContext(ConnectionContext context) throws IOException {
    }

    @Override
    public void deserialize(ByteBuffer buf) throws IOException {
        getHeader().deserialize(buf);
        getBody().deserialize(buf);
    }

}
