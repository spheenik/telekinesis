package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientCMList;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientCMList)
public class ClientCMList extends BaseProtoReceivable<CMsgClientCMList> {

    @Override
    protected CMsgClientCMList parseBody(byte[] data) throws IOException {
        return CMsgClientCMList.parseFrom(data);
    }
    
}
