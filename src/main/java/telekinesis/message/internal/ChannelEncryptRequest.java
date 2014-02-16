package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.FromWire;
import telekinesis.message.annotations.RegisterMessage;
import telekinesis.model.EMsg;
import telekinesis.model.EUniverse;

@RegisterMessage(EMsg.ChannelEncryptRequest)
public class ChannelEncryptRequest extends BaseInternalReceivable<ChannelEncryptRequest.Body>  {
    
    @Override
    protected void constructBody() {
        setBody(new Body());
    }

    public static class Body implements FromWire {

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

        public void deserialize(ByteBuffer buf) {
            protocolVersion = buf.getInt();
            universe = EUniverse.f(buf.getInt());
        }
        
    }
    
}
