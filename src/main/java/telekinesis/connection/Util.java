package telekinesis.connection;

import java.nio.ByteBuffer;

public class Util {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

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

}
