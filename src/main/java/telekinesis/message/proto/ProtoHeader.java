package telekinesis.message.proto;

import io.netty.buffer.ByteBuf;
import telekinesis.message.proto.generated.SM_Base;
import telekinesis.model.Decodable;
import telekinesis.model.Encodable;
import telekinesis.model.Header;

import java.io.IOException;

public class ProtoHeader implements Header, Decodable, Encodable {

    private final SM_Base.CMsgProtoBufHeader.Builder delegate;

    public ProtoHeader() {
        delegate = SM_Base.CMsgProtoBufHeader.newBuilder();
    }

    @Override
    public boolean hasSteamId() {
        return delegate.hasSteamid();
    }

    @Override
    public long getSteamId() {
        return delegate.getSteamid();
    }

    @Override
    public void setSteamId(long steamId) {
        delegate.setSteamid(steamId);
    }

    @Override
    public boolean hasSessionId() {
        return delegate.hasClientSessionid();
    }

    @Override
    public int getSessionId() {
        return delegate.getClientSessionid();
    }

    @Override
    public void setSessionId(int sessionId) {
        delegate.setClientSessionid(sessionId);
    }

    @Override
    public long getSourceJobId() {
        return delegate.getJobidSource();
    }

    @Override
    public void setSourceJobId(long sourceJobId) {
        delegate.setJobidSource(sourceJobId);
    }

    @Override
    public long getTargetJobId() {
        return delegate.getJobidTarget();
    }

    @Override
    public void setTargetJobId(long targetJobId) {
        delegate.setJobidTarget(targetJobId);
    }

    @Override
    public void decode(ByteBuf in) throws IOException {
        byte[] buf = new byte[in.readInt()];
        in.readBytes(buf);
        delegate.mergeFrom(buf);
    }

    @Override
    public void encode(ByteBuf out) throws IOException {
        byte[] buf = delegate.build().toByteArray();
        out.writeInt(buf.length);
        out.writeBytes(buf);
    }

}
