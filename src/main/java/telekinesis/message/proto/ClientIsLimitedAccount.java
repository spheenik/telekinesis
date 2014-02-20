package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientIsLimitedAccount;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientIsLimitedAccount)
public class ClientIsLimitedAccount extends BaseProtoReceivable<CMsgClientIsLimitedAccount> {

    @Override
    protected CMsgClientIsLimitedAccount parseBody(byte[] data) throws IOException {
        return CMsgClientIsLimitedAccount.parseFrom(data);
    }
}
