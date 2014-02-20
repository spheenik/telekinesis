package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientGameConnectTokens;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientGameConnectTokens)
public class ClientGameConnectTokens extends BaseProtoReceivable<CMsgClientGameConnectTokens> {

    @Override
    protected CMsgClientGameConnectTokens parseBody(byte[] data) throws IOException {
        return CMsgClientGameConnectTokens.parseFrom(data);
    }
    
}
