package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.FromWire;
import telekinesis.message.Message;
import telekinesis.message.annotations.RegisterMessage;
import telekinesis.model.EMsg;
import telekinesis.model.EResult;

@RegisterMessage(type=EMsg.ChannelEncryptResult, headerClass=SimpleHeader.class)
public class ChannelEncryptResult extends Message<SimpleHeader> implements FromWire {
    
    private EResult result = EResult.Invalid;
    
    public ChannelEncryptResult(EMsg type, Class<SimpleHeader> headerClass) {
        super(type, headerClass);
    }
    
    public EResult getResult() {
        return result;
    }

    public void setResult(EResult result) {
        this.result = result;
    }

    public void fromWire(ByteBuffer msgBuf) {
        getHeader().fromWire(msgBuf);
        result = EResult.f(msgBuf.getInt());
    }

}
