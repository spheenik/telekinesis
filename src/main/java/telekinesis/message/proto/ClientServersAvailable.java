package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientServersAvailable;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientServersAvailable)
public class ClientServersAvailable extends BaseProtoReceivable<CMsgClientServersAvailable> {

    @Override
    protected CMsgClientServersAvailable parseBody(byte[] data) throws IOException {
        return CMsgClientServersAvailable.parseFrom(data);
    }
}
