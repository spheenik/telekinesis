package telekinesis.model.datagram;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonSerialize(using = SocketRange.Serializer.class)
public class SocketRange {

    private static final Pattern P = Pattern.compile("(.*?):(\\d+)-(\\d+)");

    private final InetAddress address;
    private final int portFrom;
    private final int portTo;

    public SocketRange(String jsonValue) throws IOException {
        Matcher matcher = P.matcher(jsonValue);
        if (!matcher.matches()) {
            throw new IOException("invalid socket range");
        }
        address = InetAddress.getByName(matcher.group(1));
        portFrom = Integer.valueOf(matcher.group(2));
        portTo = Integer.valueOf(matcher.group(3));
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPortFrom() {
        return portFrom;
    }

    public int getPortTo() {
        return portTo;
    }

    public int getAddressCount() {
        return portTo - portFrom + 1;
    }


    public static class Serializer extends StdSerializer<SocketRange> {

        public Serializer() {
            super((Class) null);
        }

        @Override
        public void serialize(SocketRange sr, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(String.format("%s:%s-%s", sr.getAddress().getHostAddress(), sr.getPortFrom(), sr.getPortTo()));
        }
    }

}
