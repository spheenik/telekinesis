package telekinesis.message.proto;

import telekinesis.message.Message;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgProtoBufHeaderOrBuilder;

import com.google.protobuf.MessageOrBuilder;

public abstract class BaseProto<H extends CMsgProtoBufHeaderOrBuilder, B extends MessageOrBuilder> extends Message<H, B> {

}
