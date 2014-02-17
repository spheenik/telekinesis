package telekinesis.message.internal;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.connection.ConnectionContext;
import telekinesis.message.Encodable;
import telekinesis.message.TransmittableMessage;

public abstract class BaseInternalSendable<B extends Encodable> extends BaseInternal<B> implements TransmittableMessage<BaseInternal.Header, B> {

    @Override
    public void prepareTransmission(ConnectionContext context) throws IOException {
    }

    @Override
    public void encodeTo(ByteBuffer buf) throws IOException {
        getHeader().serialize(buf);
        getBody().encodeTo(buf);
    }

}
