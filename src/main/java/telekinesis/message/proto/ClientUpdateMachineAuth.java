package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientUpdateMachineAuth;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientUpdateMachineAuth)
public class ClientUpdateMachineAuth extends BaseProtoReceivable<CMsgClientUpdateMachineAuth> {

    @Override
    protected CMsgClientUpdateMachineAuth parseBody(byte[] data) throws IOException {
        return CMsgClientUpdateMachineAuth.parseFrom(data);
    }
}
