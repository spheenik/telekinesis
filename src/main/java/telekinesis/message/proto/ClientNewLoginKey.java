package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientNewLoginKey;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientNewLoginKey)
public class ClientNewLoginKey extends BaseProtoReceivable<CMsgClientNewLoginKey> {

    @Override
    protected CMsgClientNewLoginKey parseBody(byte[] data) throws IOException {
        return CMsgClientNewLoginKey.parseFrom(data);
    }
}
