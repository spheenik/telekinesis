package telekinesis.connection;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.message.Message;

public interface MessageCodec {
    
    void toWire(Message<?, ?> msg, ByteBuffer dstBuf) throws IOException;
    Message<?, ?> fromWire(ByteBuffer srcBuf) throws IOException;
}
