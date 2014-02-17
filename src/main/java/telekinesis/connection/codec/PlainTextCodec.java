package telekinesis.connection.codec;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.message.MessageRegistry;
import telekinesis.message.ReceivableMessage;
import telekinesis.message.TransmittableMessage;
import telekinesis.model.EMsg;

public class PlainTextCodec extends MessageCodec {

    public void toWire(TransmittableMessage<?, ?> msg, ByteBuffer dstBuf) throws IOException {
        dstBuf.putInt(MessageRegistry.getWireCodeForClass(msg.getClass()));
        msg.encodeTo(dstBuf);
    }

    public ReceivableMessage<?, ?> fromWire(ByteBuffer srcBuf) throws IOException {
        int type = srcBuf.getInt();
        EMsg eMsg = EMsg.f(type);
        log.debug("got plaintext message of type {}", eMsg);
        ReceivableMessage<?, ?> msg = (ReceivableMessage<?, ?>) MessageRegistry.forEMsg(eMsg);
        if (msg != null) {
            msg.decodeFrom(srcBuf);
        } 
        return msg;
    }
    
}