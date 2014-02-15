package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.Message;
import telekinesis.message.ToWire;

public class BaseInternalSendable<B extends ToWire> extends Message<SimpleHeader, B> {

    @Override
    public void deserialize(ByteBuffer buf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serialize(ByteBuffer buf) {
        getHeader().serialize(buf);
        getBody().serialize(buf);
    }
    
}
