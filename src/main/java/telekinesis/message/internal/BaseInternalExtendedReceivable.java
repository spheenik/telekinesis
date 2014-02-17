package telekinesis.message.internal;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.connection.ConnectionContext;
import telekinesis.message.Decodable;
import telekinesis.message.ReceivableMessage;

public abstract class BaseInternalExtendedReceivable<B extends Decodable> extends BaseInternalExtended<B> implements ReceivableMessage<BaseInternalExtended.Header, B> {

    @Override
    public void updateContext(ConnectionContext context) throws IOException {
    }

    @Override
    public void decodeFrom(ByteBuffer buf) throws IOException {
        getHeader().decodeFrom(buf);
        getBody().decodeFrom(buf);
    }

}
