package telekinesis.connection;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.message.ReceivableMessage;
import telekinesis.message.TransmittableMessage;

public interface MessageCodec {
    
    void toWire(TransmittableMessage<?, ?> msg, ByteBuffer dstBuf) throws IOException;
    ReceivableMessage<?, ?> fromWire(ByteBuffer srcBuf) throws IOException;
}
