package telekinesis.model;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface Encodable {
    void encode(ByteBuf out) throws IOException;
}
