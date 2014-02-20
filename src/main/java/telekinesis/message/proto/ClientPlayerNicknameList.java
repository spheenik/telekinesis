package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientPlayerNicknameList;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientPlayerNicknameList)
public class ClientPlayerNicknameList extends BaseProtoReceivable<CMsgClientPlayerNicknameList> {

    @Override
    protected CMsgClientPlayerNicknameList parseBody(byte[] data) throws IOException {
        return CMsgClientPlayerNicknameList.parseFrom(data);
    }
}
