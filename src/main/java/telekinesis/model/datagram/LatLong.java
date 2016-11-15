package telekinesis.model.datagram;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

@JsonDeserialize(using = LatLong.Deserializer.class)
@JsonSerialize(using = LatLong.Serializer.class)
public class LatLong {

    private final double latitude;
    private final double longitude;

    public LatLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double distance(LatLong o) {
        double phi1 = Math.toRadians(latitude);
        double phi2 = Math.toRadians(o.latitude);
        double dphi = Math.toRadians(o.latitude - latitude);
        double dlda = Math.toRadians(o.longitude - longitude);

        double a = Math.sin(dphi/2) * Math.sin(dphi/2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(dlda/2) * Math.sin(dlda/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c * 6373;
    }

    public static class Deserializer extends StdDeserializer<LatLong> {

        public Deserializer() {
            super((Class)null);
        }

        @Override
        public LatLong deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            ArrayNode node = jsonParser.readValueAsTree();
            return new LatLong(node.get(0).asDouble(), node.get(1).asDouble());
        }
    }

    public static class Serializer extends StdSerializer<LatLong> {

        public Serializer() {
            super((Class) null);
        }

        @Override
        public void serialize(LatLong latLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartArray();
            jsonGenerator.writeNumber(latLong.getLatitude());
            jsonGenerator.writeNumber(latLong.getLongitude());
            jsonGenerator.writeEndArray();
        }
    }

}
