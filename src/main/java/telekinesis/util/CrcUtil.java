package telekinesis.util;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;

import java.util.zip.CRC32;

public class CrcUtil {

    public static int crc32(ByteString in) {
        CRC32 crc32 = new CRC32();
        crc32.update(in.asReadOnlyByteBuffer());
        return (int) crc32.getValue();
    }

    public static int crc16(ByteString in) {
        int c = crc32(in);
        return ((c >>> 16) ^ c) & 0xffff;
    }

    public static int crc32(ByteBuf in) {
        CRC32 crc32 = new CRC32();
        crc32.update(in.nioBuffer());
        return (int) crc32.getValue();
    }

    public static int crc16(ByteBuf in) {
        int c = crc32(in);
        return ((c >>> 16) ^ c) & 0xffff;
    }

}
