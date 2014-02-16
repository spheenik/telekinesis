package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.FromWire;
import telekinesis.message.Message;
import telekinesis.message.ToWire;

public abstract class BaseInternal<B> extends Message<BaseInternal.Header, B> {

    @Override
    protected void constructHeader() {
        setHeader(new Header());
    }

    public static class Header implements FromWire, ToWire {

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

        public void deserialize(ByteBuffer buf) {
            sourceJobId = buf.getLong();
            targetJobId = buf.getLong();
        }

        public void serialize(ByteBuffer buf) {
            buf.putLong(sourceJobId);
            buf.putLong(targetJobId);
        }
        
    }
    
}