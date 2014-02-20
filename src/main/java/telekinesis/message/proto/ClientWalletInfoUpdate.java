package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientWalletInfoUpdate;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientWalletInfoUpdate)
public class ClientWalletInfoUpdate extends BaseProtoReceivable<CMsgClientWalletInfoUpdate> {

    @Override
    protected CMsgClientWalletInfoUpdate parseBody(byte[] data) throws IOException {
        return CMsgClientWalletInfoUpdate.parseFrom(data);
    }
}
