package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.AbstractMessage;
import telekinesis.message.Decodable;
import telekinesis.message.Encodable;

public abstract class BaseInternal<B> extends AbstractMessage<BaseInternal.Header, B> {

    @Override
    protected void constructHeader() {
        setHeader(new Header());
    }

    public static class Header implements Encodable, Decodable {

        private long sourceJobId = -1L;
        private long targetJobId = -1L;
        
        public long getSourceJobId() {
            return sourceJobId;
        }

        public void setSourceJobId(long sourceJobId) {
            this.sourceJobId = sourceJobId;
        }
        
        public long getTargetJobId() {
            return targetJobId;
        }
        
        public void setTargetJobId(long targetJobId) {
            this.targetJobId = targetJobId;
        }

        public void decodeFrom(ByteBuffer buf) {
            sourceJobId = buf.getLong();
            targetJobId = buf.getLong();
        }

        public void encodeTo(ByteBuffer buf) {
            buf.putLong(sourceJobId);
            buf.putLong(targetJobId);
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
    
}
