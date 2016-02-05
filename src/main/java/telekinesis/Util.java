package telekinesis;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class Util {

    public static <I> I newInstance(Class<I> instanceClass) {
        try {
            return instanceClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeProtoToByteBuf(Message message, ByteBuf out) throws IOException {
        ByteBufOutputStream stream = new ByteBufOutputStream(out);
        message.writeTo(stream);
        stream.flush();
    }

}
