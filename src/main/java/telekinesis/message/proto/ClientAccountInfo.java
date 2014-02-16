package telekinesis.message.proto;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgProtoBufHeader;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientAccountInfo;
import telekinesis.model.EMsg;

@RegisterMessage(type=EMsg.ClientAccountInfo, headerClass=CMsgProtoBufHeader.class, bodyClass=CMsgClientAccountInfo.class)
public class ClientAccountInfo extends BaseProtoReceivable<CMsgProtoBufHeader, CMsgClientAccountInfo> {
}
