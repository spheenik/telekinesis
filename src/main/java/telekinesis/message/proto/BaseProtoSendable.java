package telekinesis.message.proto;

import java.nio.ByteBuffer;

import telekinesis.message.Message;

import com.google.protobuf.GeneratedMessage;

public class BaseProtoSendable<H extends GeneratedMessage.Builder<?>, B extends GeneratedMessage.Builder<?>> extends Message<H, B> {

    @Override
    public void deserialize(ByteBuffer buf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serialize(ByteBuffer buf) {
        byte[] arr = null;
        arr = getHeader().build().toByteArray();
        buf.putInt(arr.length);
        buf.put(arr);
        arr = getBody().build().toByteArray();
        buf.put(arr);
    }

}
