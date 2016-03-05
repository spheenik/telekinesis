package telekinesis.util;

import com.google.protobuf.ByteString;

import java.util.zip.CRC32;

public class CrcUtil {

    public static int crc32(ByteString in) {
        CRC32 crc32 = new CRC32();
        crc32.update(in.asReadOnlyByteBuffer());
        return (int) crc32.getValue();
    }

}
