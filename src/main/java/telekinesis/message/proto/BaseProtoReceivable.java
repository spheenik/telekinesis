package telekinesis.message.proto;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.connection.ConnectionContext;
import telekinesis.message.ReceivableMessage;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgProtoBufHeader;
import telekinesis.model.SteamID;

import com.google.protobuf.GeneratedMessage;

public abstract class BaseProtoReceivable<B extends GeneratedMessage> extends BaseProto<CMsgProtoBufHeader, B> implements ReceivableMessage<CMsgProtoBufHeader, B> {

    @Override
    protected void constructHeader() {
    }

    @Override
    protected void constructBody() {
    }
    
    abstract protected B parseBody(byte[] data) throws IOException;
    
    @Override
    public void updateContext(ConnectionContext context) throws IOException {
        if (getHeader().hasSteamid()) {
            context.setSteamID(new SteamID(getHeader().getSteamid()));
        }
        if (getHeader().hasClientSessionid()) {
            context.setSessionId(getHeader().getClientSessionid());
        }
    }

    @Override
    public void decodeFrom(ByteBuffer buf) throws IOException {
        byte[] arr = null;

        arr = new byte[buf.getInt()];
        buf.get(arr);
        setHeader(CMsgProtoBufHeader.parseFrom(arr));

        arr = new byte[buf.remaining()];
        buf.get(arr);
        setBody(parseBody(arr));
    }

}
