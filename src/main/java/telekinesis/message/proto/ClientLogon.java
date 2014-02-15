package telekinesis.message.proto;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgProtoBufHeader;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientLogon;
import telekinesis.model.EMsg;

@RegisterMessage(type=EMsg.ClientLogon, headerClass=CMsgProtoBufHeader.Builder.class, bodyClass=CMsgClientLogon.Builder.class)
public class ClientLogon extends BaseProtoSendable<CMsgProtoBufHeader.Builder, CMsgClientLogon.Builder> {
}
