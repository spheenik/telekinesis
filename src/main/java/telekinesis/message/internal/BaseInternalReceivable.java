package telekinesis.message.internal;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.message.FromWire;

public abstract class BaseInternalReceivable<B extends FromWire> extends BaseInternal<B> implements FromWire {

    @Override
    public void deserialize(ByteBuffer buf) throws IOException {
        getHeader().deserialize(buf);
        getBody().deserialize(buf);
    }

}
