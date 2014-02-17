package telekinesis.message.proto;

import telekinesis.message.AbstractMessage;
import telekinesis.message.proto.generated.SteammessagesBase.CMsgProtoBufHeaderOrBuilder;

import com.google.protobuf.MessageOrBuilder;

public abstract class BaseProto<H extends CMsgProtoBufHeaderOrBuilder, B extends MessageOrBuilder> extends AbstractMessage<H, B> {

}
