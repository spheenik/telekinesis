package telekinesis.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface Encodable {
    void encodeTo(ByteBuffer buf) throws IOException;
}
