package telekinesis.message.internal;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.message.ToWire;

public abstract class BaseInternalSendable<B extends ToWire> extends BaseInternal<B> implements ToWire {

    @Override
    public void serialize(ByteBuffer buf) throws IOException {
        getHeader().serialize(buf);
        getBody().serialize(buf);
    }

}
