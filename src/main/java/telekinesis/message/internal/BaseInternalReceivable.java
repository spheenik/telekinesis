package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.FromWire;
import telekinesis.message.Message;

public class BaseInternalReceivable<B extends FromWire> extends Message<SimpleHeader, B> {

    @Override
    public void deserialize(ByteBuffer buf) {
        getHeader().deserialize(buf);
        getBody().deserialize(buf);
    }

    @Override
    public void serialize(ByteBuffer buf) {
        throw new UnsupportedOperationException();
    }
    
}
