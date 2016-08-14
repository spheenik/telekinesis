package telekinesis.util;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class CrcUtil {

    public static int crc32(ByteBuffer buf) {
        CRC32 crc32 = new CRC32();
        crc32.update(buf);
        return (int) crc32.getValue();
    }

    public static int crc16(ByteBuffer in) {
        int c = crc32(in);
        return ((c >>> 16) ^ c) & 0xffff;
    }

    public static int crc32(ByteString in) {
        return crc32(in.asReadOnlyByteBuffer());
    }

    public static int crc16(ByteString in) {
        return crc16(in.asReadOnlyByteBuffer());
    }

    public static int crc32(ByteBuf in) {
        return crc32(in.nioBuffer());
    }

    public static int crc16(ByteBuf in) {
        return crc16(in.nioBuffer());
    }

}
