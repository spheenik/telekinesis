package telekinesis.message;

import java.nio.ByteBuffer;

public interface ToWire {
    void serialize(ByteBuffer buf);
}
