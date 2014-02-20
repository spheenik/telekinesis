package telekinesis.message.internal;

import java.io.IOException;
import java.nio.ByteBuffer;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.Decodable;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientMarketingMessageUpdate2)
public class ClientMarketingMessageUpdate2 extends BaseInternalExtendedReceivable<ClientMarketingMessageUpdate2.Body>  {

    @Override
    protected void constructBody() {
        setBody(new Body());
    }

    public static class Body implements Decodable {
        
        int marketingMessageUpdateTime;
        int count;
        
        public int getMarketingMessageUpdateTime() {
            return marketingMessageUpdateTime;
        }

        public void setMarketingMessageUpdateTime(int marketingMessageUpdateTime) {
            this.marketingMessageUpdateTime = marketingMessageUpdateTime;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public void decodeFrom(ByteBuffer buf) throws IOException {
            buf.getInt(marketingMessageUpdateTime);
            buf.getInt(count);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ClientMarketingMessageUpdate2.Body [marketingMessageUpdateTime=");
            builder.append(marketingMessageUpdateTime);
            builder.append(", count=");
            builder.append(count);
            builder.append("]");
            return builder.toString();
        }

    }
}
