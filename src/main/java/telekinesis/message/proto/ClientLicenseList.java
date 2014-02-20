package telekinesis.message.proto;

import java.io.IOException;

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientLicenseList;
import telekinesis.model.EMsg;

@RegisterMessage(EMsg.ClientLicenseList)
public class ClientLicenseList extends BaseProtoReceivable<CMsgClientLicenseList> {

    @Override
    protected CMsgClientLicenseList parseBody(byte[] data) throws IOException {
        return CMsgClientLicenseList.parseFrom(data);
    }
    
}
