package telekinesis.message.proto;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.message.FromWire;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgProtoBufHeader;

import com.google.protobuf.GeneratedMessage;

public abstract class BaseProtoReceivable<B extends GeneratedMessage> extends BaseProto<CMsgProtoBufHeader, B> implements FromWire {

    @Override
    protected void constructHeader() {
    }

    @Override
    protected void constructBody() {
    }
    
    abstract protected B parseBody(byte[] data) throws IOException;

    @Override
    public void deserialize(ByteBuffer buf) throws IOException {
        byte[] arr = null;

        arr = new byte[buf.getInt()];
        buf.get(arr);
        setHeader(CMsgProtoBufHeader.parseFrom(arr));

        arr = new byte[buf.remaining()];
        buf.get(arr);
        setBody(parseBody(arr));
    }

}
