package telekinesis.message;

import java.nio.ByteBuffer;

public interface FromWire {
    void fromWire(ByteBuffer msgBuf);
}
