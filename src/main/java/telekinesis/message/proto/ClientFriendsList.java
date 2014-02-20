package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientFriendsList;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientFriendsList)
public class ClientFriendsList extends BaseProtoReceivable<CMsgClientFriendsList> {

    @Override
    protected CMsgClientFriendsList parseBody(byte[] data) throws IOException {
        return CMsgClientFriendsList.parseFrom(data);
    }
}
