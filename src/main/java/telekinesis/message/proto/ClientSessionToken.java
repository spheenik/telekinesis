package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientSessionToken;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientSessionToken)
public class ClientSessionToken extends BaseProtoReceivable<CMsgClientSessionToken> {

    @Override
    protected CMsgClientSessionToken parseBody(byte[] data) throws IOException {
        return CMsgClientSessionToken.parseFrom(data);
    }
    
}
