package telekinesis.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ToWire {
    void serialize(ByteBuffer buf) throws IOException;
}
