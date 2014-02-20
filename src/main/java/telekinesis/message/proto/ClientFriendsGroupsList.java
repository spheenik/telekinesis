package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientFriendsGroupsList;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientFriendsGroupsList)
public class ClientFriendsGroupsList extends BaseProtoReceivable<CMsgClientFriendsGroupsList> {

    @Override
    protected CMsgClientFriendsGroupsList parseBody(byte[] data) throws IOException {
        return CMsgClientFriendsGroupsList.parseFrom(data);
    }
}
