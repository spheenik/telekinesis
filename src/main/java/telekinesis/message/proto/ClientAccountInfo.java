package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientAccountInfo;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientAccountInfo)
public class ClientAccountInfo extends BaseProtoReceivable<CMsgClientAccountInfo> {

    @Override
    protected CMsgClientAccountInfo parseBody(byte[] data) throws IOException {
        return CMsgClientAccountInfo.parseFrom(data);
    }
    
}
