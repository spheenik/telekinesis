package telekinesis.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface FromWire {
    void deserialize(ByteBuffer buf) throws IOException;
}
