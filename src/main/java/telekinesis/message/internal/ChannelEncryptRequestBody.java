package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.Body;
import telekinesis.message.FromWire;
import telekinesis.message.annotations.MessageBody;
import telekinesis.model.EMsg;
import telekinesis.model.EUniverse;

@MessageBody(type=EMsg.ChannelEncryptRequest, headerClass=SimpleHeader.class)
public class ChannelEncryptRequestBody implements Body, FromWire {
    
    private int protocolVersion = 1;
    private EUniverse universe;
    
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
        protocolVersion = msgBuf.getInt();
        universe = EUniverse.f(msgBuf.getInt());
    }

}
