package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgMulti;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.Multi)
public class Multi extends BaseProtoReceivable<CMsgMulti> {

    @Override
    protected CMsgMulti parseBody(byte[] data) throws IOException {
        return CMsgMulti.parseFrom(data);
    }
}
