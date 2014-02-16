package telekinesis.message.proto;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgMulti;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgProtoBufHeader;
import telekinesis.model.EMsg;

@RegisterMessage(type=EMsg.Multi, headerClass=CMsgProtoBufHeader.class, bodyClass=CMsgMulti.class)
public class Multi extends BaseProtoReceivable<CMsgProtoBufHeader, CMsgMulti> {
}
