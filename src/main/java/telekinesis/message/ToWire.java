package telekinesis.message;

import java.nio.ByteBuffer;

public interface ToWire {
    void toWire(ByteBuffer msgBuf);
}
