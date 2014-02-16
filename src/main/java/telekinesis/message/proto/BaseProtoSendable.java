package telekinesis.message.proto;

import java.nio.ByteBuffer;

import telekinesis.message.ToWire;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgProtoBufHeader;

import com.google.protobuf.GeneratedMessage;

public abstract class BaseProtoSendable<B extends GeneratedMessage.Builder<?>> extends BaseProto<CMsgProtoBufHeader.Builder, B> implements ToWire {

    @Override
    protected void constructHeader() {
        setHeader(CMsgProtoBufHeader.newBuilder());
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
