package telekinesis.message.simple;

import io.netty.buffer.ByteBuf;
import telekinesis.model.Decodable;
import telekinesis.model.Encodable;
import telekinesis.model.Header;

import java.io.IOException;

public class SimpleHeader implements Header, Encodable, Decodable {

    private long sourceJobId = -1L;
    private long targetJobId = -1L;

    @Override
    public boolean hasSteamId() {
        return false;
    }

    @Override
    public long getSteamId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSteamId(long steamId) {
    }

    @Override
    public boolean hasSessionId() {
        return false;
    }

    @Override
    public int getSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSessionId(int sessionId) {
    }

    @Override
    public long getSourceJobId() {
        return sourceJobId;
    }

    @Override
    public void setSourceJobId(long sourceJobId) {
        this.sourceJobId = sourceJobId;
    }

    @Override
    public long getTargetJobId() {
        return targetJobId;
    }

    @Override
    public void setTargetJobId(long targetJobId) {
        this.targetJobId = targetJobId;
    }

    @Override
    public void setRoutingAppId(int appId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf in) throws IOException {
        sourceJobId = in.readLong();
        targetJobId = in.readLong();
    }

    @Override
    public void encode(ByteBuf out) throws IOException {
        out.writeLong(sourceJobId);
        out.writeLong(targetJobId);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Header [sourceJobId=");
        builder.append(sourceJobId);
        builder.append(", targetJobId=");
        builder.append(targetJobId);
        builder.append("]");
        return builder.toString();
    }

}