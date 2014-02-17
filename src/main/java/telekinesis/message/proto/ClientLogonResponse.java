package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientLogonResponse;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientLogOnResponse)
public class ClientLogonResponse extends BaseProtoReceivable<CMsgClientLogonResponse> {

    @Override
    protected CMsgClientLogonResponse parseBody(byte[] data) throws IOException {
        return CMsgClientLogonResponse.parseFrom(data);
    }
}
