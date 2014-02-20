package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.Decodable;
import telekinesis.model.EMsg;
import telekinesis.model.EResult;

@RegisterMessage(EMsg.ClientUpdateGuestPassesList)
public class ClientUpdateGuestPassesList extends BaseInternalExtendedReceivable<ClientUpdateGuestPassesList.Body>  {
    
    @Override
    protected void constructBody() {
        setBody(new Body());
    }

    public static class Body implements Decodable {
        
        EResult result;
        int countGuestPassesToGive;
        int countGuestPassesToRedeem;
        
        public EResult getResult() {
            return result;
        }

        public void setResult(EResult result) {
            this.result = result;
        }

        public int getCountGuestPassesToGive() {
            return countGuestPassesToGive;
        }

        public void setCountGuestPassesToGive(int countGuestPassesToGive) {
            this.countGuestPassesToGive = countGuestPassesToGive;
        }

        public int getCountGuestPassesToRedeem() {
            return countGuestPassesToRedeem;
        }

        public void setCountGuestPassesToRedeem(int countGuestPassesToRedeem) {
            this.countGuestPassesToRedeem = countGuestPassesToRedeem;
        }

        @Override
        public void decodeFrom(ByteBuffer buf) {
            result = EResult.f(buf.getInt());
            countGuestPassesToGive = buf.getInt();
            countGuestPassesToRedeem = buf.getInt();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ClientUpdateGuestPassesList.Body [result=");
            builder.append(result);
            builder.append(", countGuestPassesToGive=");
            builder.append(countGuestPassesToGive);
            builder.append(", countGuestPassesToRedeem=");
            builder.append(countGuestPassesToRedeem);
            builder.append("]");
            return builder.toString();
        }
        
    }
    
}
