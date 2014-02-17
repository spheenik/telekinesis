package telekinesis.connection;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Util {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static ByteBuffer getNewBuffer() {
        ByteBuffer result = ByteBuffer.allocate(8192);
        result.order(ByteOrder.LITTLE_ENDIAN);
        return result;
    }
    
    public static String convertByteArrayToString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    public static String convertByteBufferToString(ByteBuffer buf, int limit) {
        char[] hexChars = new char[limit * 3];
        for (int j = 0; j < limit; j++) {
            int v = buf.get(j) & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }
    
    public static String dumpSHA1(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    public static byte[] restoreSHA1(String src) {
        byte[] result = new byte[20];
        for (int j = 0; j < 20; j++) {
            int x = 
                (Arrays.binarySearch(hexArray, src.charAt(j*2)) << 4)
                + Arrays.binarySearch(hexArray, src.charAt(j*2+1));
            result[j] = (byte) x;
        }
        return result;
        
    }

    

}
