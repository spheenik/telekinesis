package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.crypto.CryptoHelper;
import telekinesis.message.ToWire;
import telekinesis.message.annotations.RegisterMessage;
import telekinesis.model.EMsg;

@RegisterMessage(type = EMsg.ChannelEncryptResponse, headerClass = SimpleHeader.class, bodyClass=ChannelEncryptResponse.Body.class)
public class ChannelEncryptResponse extends BaseInternalSendable<ChannelEncryptResponse.Body> {

    public static class Body implements ToWire {
        
        private int protocolVersion;
        private byte[] key;

        public int getProtocolVersion() {
            return protocolVersion;
        }

        public void setProtocolVersion(int protocolVersion) {
            this.protocolVersion = protocolVersion;
        }

        public byte[] getKey() {
            return key;
        }

        public void setKey(byte[] key) {
            this.key = key;
        }

        public void serialize(ByteBuffer buf) {
            buf.putInt(protocolVersion);
            buf.putInt(128); //
            buf.put(key);
            buf.put(CryptoHelper.CRCHash(key));
            buf.putInt(0);
        }

    }

}
