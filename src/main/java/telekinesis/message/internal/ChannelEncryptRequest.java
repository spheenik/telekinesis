package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.Message;
import telekinesis.message.FromWire;
import telekinesis.message.annotations.RegisterMessage;
import telekinesis.model.EMsg;
import telekinesis.model.EUniverse;

@RegisterMessage(type=EMsg.ChannelEncryptRequest, headerClass=SimpleHeader.class)
public class ChannelEncryptRequest extends Message<SimpleHeader> implements FromWire {
    
    private int protocolVersion = 1;
    private EUniverse universe;
    
    public ChannelEncryptRequest(EMsg type, Class<SimpleHeader> headerClass) {
        super(type, headerClass);
    }
    
    public int getProtocolVersion() {
        return protocolVersion;
    }
    
    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
    
    public EUniverse getUniverse() {
        return universe;
    }
    
    public void setUniverse(EUniverse universe) {
        this.universe = universe;
    }

    public void fromWire(ByteBuffer msgBuf) {
        getHeader().fromWire(msgBuf);
        protocolVersion = msgBuf.getInt();
        universe = EUniverse.f(msgBuf.getInt());
    }

}
