package telekinesis.message.internal;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.Decodable;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientVACBanStatus)
public class ClientVACBanStatus extends BaseInternalExtendedReceivable<ClientVACBanStatus.Body>  {

    @Override
    protected void constructBody() {
        setBody(new Body());
    }

    public static class Body implements Decodable {
        
        private int numBans;

        public int getNumBans() {
            return numBans;
        }

        public void setNumBans(int numBans) {
            this.numBans = numBans;
        }

        @Override
        public void decodeFrom(ByteBuffer buf) throws IOException {
            buf.getInt(numBans);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ClientVACBanStatus.Body [numBans=");
            builder.append(numBans);
            builder.append("]");
            return builder.toString();
        }

    }
}
