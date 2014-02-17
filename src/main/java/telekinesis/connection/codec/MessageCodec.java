package telekinesis.connection.codec;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import telekinesis.connection.Util;
import telekinesis.message.MessageRegistry;
import telekinesis.message.ReceivableMessage;
import telekinesis.message.TransmittableMessage;

public abstract class MessageCodec {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    protected ByteBuffer messageToBuffer(TransmittableMessage<?, ?> msg) throws IOException {
        ByteBuffer dstBuf = Util.getNewBuffer();
        dstBuf.putInt(MessageRegistry.getWireCodeForClass(msg.getClass()));
        msg.encodeTo(dstBuf);
        return dstBuf;
    }
    
    public abstract void toWire(TransmittableMessage<?, ?> msg, ByteBuffer dstBuf) throws IOException;
    public abstract ReceivableMessage<?, ?> fromWire(ByteBuffer srcBuf) throws IOException;
}
