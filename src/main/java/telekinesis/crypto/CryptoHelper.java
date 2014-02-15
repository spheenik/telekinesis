package telekinesis.crypto;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * Provides Crypto functions used in Steam protocols
 */
public class CryptoHelper {

    
	
    /**
     * Performs CRC32 on an input byte array using the CrcStandard.Crc32Bit parameters
     */
    public static byte[] CRCHash(byte[] input) {
        final CRC32 crc = new CRC32();
        crc.update(input);
        final long hash = crc.getValue();

        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt((int) hash);

        final byte[] array = buffer.array();
        final byte[] output = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            output[array.length - 1 - i] = array[i];
        }

        return output;
    }
	

}
