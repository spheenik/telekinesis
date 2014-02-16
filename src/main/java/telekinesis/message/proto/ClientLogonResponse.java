package telekinesis.message.proto;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgProtoBufHeader;
import telekinesis.message.proto.generated.SteammessagesClientserver.CMsgClientLogonResponse;
import telekinesis.model.EMsg;

@RegisterMessage(type=EMsg.ClientLogOnResponse, headerClass=CMsgProtoBufHeader.class, bodyClass=CMsgClientLogonResponse.class)
public class ClientLogonResponse extends BaseProtoReceivable<CMsgProtoBufHeader, CMsgClientLogonResponse> {
}
