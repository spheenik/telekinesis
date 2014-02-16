package telekinesis.connection;

import java.nio.ByteBuffer;

import telekinesis.message.Message;

public interface MessageCodec {
    
    void toWire(Message<?, ?> msg, ByteBuffer dstBuf);
    Message<?, ?> fromWire(ByteBuffer srcBuf);
}
