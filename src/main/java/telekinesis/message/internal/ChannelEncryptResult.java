package telekinesis.message.internal;

import java.nio.ByteBuffer;

import telekinesis.message.FromWire;
import telekinesis.message.annotations.RegisterMessage;
import telekinesis.model.EMsg;
import telekinesis.model.EResult;

@RegisterMessage(type = EMsg.ChannelEncryptResult, headerClass = SimpleHeader.class, bodyClass=ChannelEncryptResult.Body.class)
public class ChannelEncryptResult extends BaseInternalReceivable<ChannelEncryptResult.Body> {

    public static class Body implements FromWire {
        private EResult result = EResult.Invalid;

        public EResult getResult() {
            return result;
        }

        public void setResult(EResult result) {
            this.result = result;
        }

        public void deserialize(ByteBuffer buf) {
            result = EResult.f(buf.getInt());
        }

    }

}
