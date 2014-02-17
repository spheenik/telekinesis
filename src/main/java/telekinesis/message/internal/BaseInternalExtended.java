package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.AbstractMessage;
import telekinesis.message.Decodable;
import telekinesis.model.SteamID;

public abstract class BaseInternalExtended<B> extends AbstractMessage<BaseInternalExtended.Header, B> {

    @Override
    protected void constructHeader() {
        setHeader(new Header());
    }

    public static class Header implements Decodable {
        
        private byte headerSize = 36;
        private short headerVersion = 2;
        private long targetJobId = -1;
        private long sourceJobId = -1;
        private byte headerCanary = (byte) 239;
        private SteamID steamId = new SteamID(0);
        private int sessionId = 0;

        public void decodeFrom(ByteBuffer buf) {
            headerSize = buf.get();
            headerVersion = buf.getShort();
            targetJobId = buf.getLong();
            sourceJobId = buf.getLong();
            headerCanary = buf.get();
            steamId = new SteamID(buf.getLong());
            sessionId = buf.getInt();
        }

        public byte getHeaderSize() {
            return headerSize;
        }

        public void setHeaderSize(byte headerSize) {
            this.headerSize = headerSize;
        }

        public short getHeaderVersion() {
            return headerVersion;
        }

        public void setHeaderVersion(short headerVersion) {
            this.headerVersion = headerVersion;
        }

        public long getTargetJobId() {
            return targetJobId;
        }

        public void setTargetJobId(long targetJobId) {
            this.targetJobId = targetJobId;
        }

        public long getSourceJobId() {
            return sourceJobId;
        }

        public void setSourceJobId(long sourceJobId) {
            this.sourceJobId = sourceJobId;
        }

        public byte getHeaderCanary() {
            return headerCanary;
        }

        public void setHeaderCanary(byte headerCanary) {
            this.headerCanary = headerCanary;
        }

        public SteamID getSteamId() {
            return steamId;
        }

        public void setSteamId(SteamID steamId) {
            this.steamId = steamId;
        }

        public int getSessionId() {
            return sessionId;
        }

        public void setSessionId(int sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Header [headerSize=");
            builder.append(headerSize);
            builder.append(", headerVersion=");
            builder.append(headerVersion);
            builder.append(", targetJobId=");
            builder.append(targetJobId);
            builder.append(", sourceJobId=");
            builder.append(sourceJobId);
            builder.append(", headerCanary=");
            builder.append(headerCanary);
            builder.append(", steamId=");
            builder.append(steamId);
            builder.append(", sessionId=");
            builder.append(sessionId);
            builder.append("]");
            return builder.toString();
        }
        
    }
    
}
