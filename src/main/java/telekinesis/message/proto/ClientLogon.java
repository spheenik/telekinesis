package telekinesis.message.proto;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientLogon;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientLogon)
public class ClientLogon extends BaseProtoSendable<CMsgClientLogon.Builder> {

    @Override
    protected void constructBody() {
        setBody(CMsgClientLogon.newBuilder());
    }
    
}
