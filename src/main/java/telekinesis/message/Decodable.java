package telekinesis.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface Decodable {
    void decodeFrom(ByteBuffer buf) throws IOException;
}
