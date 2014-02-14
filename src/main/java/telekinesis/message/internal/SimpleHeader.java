package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.FromWire;
import telekinesis.message.Header;
import telekinesis.message.ToWire;

public class SimpleHeader implements Header, FromWire, ToWire {

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

    public void fromWire(ByteBuffer msgBuf) {
        sourceJobId = msgBuf.getLong();
        targetJobId = msgBuf.getLong();
    }

    public void toWire(ByteBuffer msgBuf) {
        msgBuf.putLong(sourceJobId);
        msgBuf.putLong(targetJobId);
    }
    
}
