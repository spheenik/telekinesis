package telekinesis.util;

import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class CStringUtil {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static String decodeUtf8(ByteString in) throws IOException {
        return decode(UTF8, in.asReadOnlyByteBuffer());
    }

    public static String decode(Charset charset, ByteBuffer in) throws IOException {
        int last = in.limit() - 1;
        if (in.get(last) != 0) {
            throw new IOException("not null terminated");
        }
        in.limit(last);
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer out = decoder.decode(in);
        return out.toString();
    }

    public static ByteString encodeUtf8(String in) throws IOException {
        ByteBuffer buffer = encode(UTF8, in);
        return ByteString.copyFrom(buffer);
    }

    public static ByteBuffer encode(Charset charset, String in) throws IOException {
        ByteBuffer out = ByteBuffer.allocate(in.length() * 2 + 1);
        CharsetEncoder encoder = charset.newEncoder();
        CoderResult coderResult = encoder.encode(CharBuffer.wrap(in), out, true);
        if (coderResult.isError()) {
            throw new IOException("encoding failed");
        }
        out.put((byte) 0);
        out.flip();
        return out;
    }

}
