package telekinesis.message.simple;

import io.netty.buffer.ByteBuf;
import telekinesis.model.Encodable;

import java.util.zip.CRC32;

public class ChannelEncryptResponse implements Encodable {

    private int protocolVersion;
    private int blockLength;
    private byte[] key;

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getBlockLength() {
        return blockLength;
    }

    public void setBlockLength(int blockLength) {
        this.blockLength = blockLength;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeInt(protocolVersion);
        out.writeInt(blockLength);
        out.writeBytes(key);
        CRC32 crc = new CRC32();
        crc.update(key);
        out.writeLong(crc.getValue());
    }

}


