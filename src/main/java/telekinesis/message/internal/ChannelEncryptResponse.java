package telekinesis.message.internal;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import telekinesis.message.ToWire;
import telekinesis.message.annotations.RegisterMessage;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ChannelEncryptResponse)
public class ChannelEncryptResponse extends BaseInternalSendable<ChannelEncryptResponse.Body> {

    @Override
    protected void constructBody() {
        setBody(new Body());
    }

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
            buf.putInt(128);
            buf.put(key);
            CRC32 crc = new CRC32();
            crc.update(key);
            buf.putLong(crc.getValue());
        }

    }

}
