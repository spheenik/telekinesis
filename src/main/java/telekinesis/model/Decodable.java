package telekinesis.model;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface Decodable {
    void decode(ByteBuf in) throws IOException;
}
