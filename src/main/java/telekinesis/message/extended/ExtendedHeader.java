package telekinesis.message.extended;

import io.netty.buffer.ByteBuf;
import telekinesis.model.Decodable;
import telekinesis.model.Encodable;
import telekinesis.model.Header;

import java.io.IOException;

public class ExtendedHeader implements Header, Encodable, Decodable {

    private long steamId;
    private int sessionId;
    private long sourceJobId = -1L;
    private long targetJobId = -1L;

    @Override
    public boolean hasSteamId() {
        return true;
    }

    @Override
    public long getSteamId() {
        return steamId;
    }

    @Override
    public void setSteamId(long steamId) {
        this.steamId = steamId;
    }

    @Override
    public boolean hasSessionId() {
        return true;
    }

    @Override
    public int getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
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
        in.skipBytes(1); // headerSize;
        in.skipBytes(2); // headerVersion
        targetJobId = in.readLong();
        sourceJobId = in.readLong();
        in.skipBytes(1); // headerCanary
        steamId = in.readLong();
        sessionId = in.readInt();
    }

    @Override
    public void encode(ByteBuf out) throws IOException {
        out.writeByte(36);
        out.writeShort(2);
        out.writeLong(targetJobId);
        out.writeLong(sourceJobId);
        out.writeByte(239);
        out.writeLong(steamId);
        out.writeInt(sessionId);
    }

    @Override
    public String toString() {
        return "ExtendedHeader{" +
                "steamId=" + steamId +
                ", sessionId=" + sessionId +
                ", sourceJobId=" + sourceJobId +
                ", targetJobId=" + targetJobId +
                '}';
    }

}