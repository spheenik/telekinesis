package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientLoggedOff;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientLoggedOff)
public class ClientLoggedOff extends BaseProtoReceivable<CMsgClientLoggedOff> {

    @Override
    protected CMsgClientLoggedOff parseBody(byte[] data) throws IOException {
        return CMsgClientLoggedOff.parseFrom(data);
    }

}
