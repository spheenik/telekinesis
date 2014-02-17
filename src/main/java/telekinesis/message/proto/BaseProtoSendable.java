package telekinesis.message.proto;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.connection.ConnectionContext;
import telekinesis.message.TransmittableMessage;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgProtoBufHeader;

import com.google.protobuf.GeneratedMessage;

public abstract class BaseProtoSendable<B extends GeneratedMessage.Builder<?>> extends BaseProto<CMsgProtoBufHeader.Builder, B> implements TransmittableMessage<CMsgProtoBufHeader.Builder, B> {

    @Override
    protected void constructHeader() {
        setHeader(CMsgProtoBufHeader.newBuilder());
    }
    
    @Override
    public void prepareTransmission(ConnectionContext context) throws IOException {
        getHeader().setSteamid(context.getSteamID().convertToLong());
        getHeader().setClientSessionid(context.getSessionId());
    }

    @Override
    public void encodeTo(ByteBuffer buf) {
        byte[] arr = null;
        arr = getHeader().build().toByteArray();
        buf.putInt(arr.length);
        buf.put(arr);
        arr = getBody().build().toByteArray();
        buf.put(arr);
    }

}
