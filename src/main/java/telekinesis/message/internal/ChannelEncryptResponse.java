package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.crypto.CryptoHelper;
import telekinesis.message.Message;
import telekinesis.message.ToWire;
import telekinesis.message.annotations.RegisterMessage;
import telekinesis.model.EMsg;

@RegisterMessage(type=EMsg.ChannelEncryptResponse, headerClass=SimpleHeader.class)
public class ChannelEncryptResponse extends Message<SimpleHeader> implements ToWire {
    
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

    public ChannelEncryptResponse(EMsg type, Class<SimpleHeader> headerClass) {
        super(type, headerClass);
    }
    
    public void toWire(ByteBuffer msgBuf) {
        getHeader().toWire(msgBuf);
        msgBuf.putInt(protocolVersion);
        msgBuf.putInt(128); // 
        msgBuf.put(key);
        msgBuf.put(CryptoHelper.CRCHash(key));
        msgBuf.putInt(0);
    }

}
