package telekinesis.message.proto;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientUpdateMachineAuthResponse;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientUpdateMachineAuthResponse)
public class ClientUpdateMachineAuthResponse extends BaseProtoSendable<CMsgClientUpdateMachineAuthResponse.Builder> {

    @Override
    protected void constructBody() {
        setBody(CMsgClientUpdateMachineAuthResponse.newBuilder());
    }

}
