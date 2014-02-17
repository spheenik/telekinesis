package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientEmailAddrInfo;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientEmailAddrInfo)
public class ClientEmailAddrInfo extends BaseProtoReceivable<CMsgClientEmailAddrInfo> {

    @Override
    protected CMsgClientEmailAddrInfo parseBody(byte[] data) throws IOException {
        return CMsgClientEmailAddrInfo.parseFrom(data);
    }
    
}
