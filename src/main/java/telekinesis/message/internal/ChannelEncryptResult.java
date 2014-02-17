package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.Decodable;
import telekinesis.model.EMsg;
import telekinesis.model.EResult;

@RegisterMessage(EMsg.ChannelEncryptResult)
public class ChannelEncryptResult extends BaseInternalReceivable<ChannelEncryptResult.Body> {

    @Override
    protected void constructBody() {
        setBody(new Body());
    }

    public static class Body implements Decodable {
        private EResult result = EResult.Invalid;

        public EResult getResult() {
            return result;
        }

        public void setResult(EResult result) {
            this.result = result;
        }

        @Override
        public void decodeFrom(ByteBuffer buf) {
            result = EResult.f(buf.getInt());
        }

    }

}
