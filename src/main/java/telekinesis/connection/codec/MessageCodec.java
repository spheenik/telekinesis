package telekinesis.connection.codec;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import telekinesis.connection.Message;
import telekinesis.message.ClientMessageTypeRegistry;
import telekinesis.message.MessageFlag;
import telekinesis.message.proto.ProtoHeader;
import telekinesis.message.proto.generated.steam.SM_Base;
import telekinesis.message.proto.generated.steam.SM_ClientServer;
import telekinesis.model.AppId;
import telekinesis.model.Decodable;
import telekinesis.model.Encodable;
import telekinesis.model.Header;
import telekinesis.model.steam.EMsg;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteOrder;
import java.util.zip.ZipInputStream;

public class MessageCodec extends ChannelDuplexHandler {

    private final Logger log;
    private final ClientMessageTypeRegistry registry;

    public MessageCodec(Logger log, ClientMessageTypeRegistry registry) {
        this.log = log;
        this.registry = registry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            int type = in.readInt();
            if (!registry.knowsMessageType(AppId.STEAM, type)) {
                log.info("no decoder for message type %s", EMsg.n(type & MessageFlag.MASK));
                in.skipBytes(in.readableBytes());
                return;
            }
            log.debug("decoding a %s", EMsg.n(type & MessageFlag.MASK));
            Header header = instantiateAndDecodeObject(registry.getHeaderClassForMessageType(AppId.STEAM, type), in);
            Object body = instantiateAndDecodeObject(registry.getBodyClassForMessageType(AppId.STEAM, type), in);
            if (in.readableBytes() != 0) {
                log.warn("discarding %d extra bytes not decoded by message", in.readableBytes());
                in.skipBytes(in.readableBytes());
            }
            if (body instanceof SM_Base.CMsgMulti) {
                unpackMulti(ctx, (SM_Base.CMsgMulti) body);
            } else if (body instanceof SM_ClientServer.CMsgGCClient) {
                SM_ClientServer.CMsgGCClient gcBody = (SM_ClientServer.CMsgGCClient) body;
                int payloadType = gcBody.getMsgtype() | MessageFlag.GC;
                if (!registry.knowsMessageType(gcBody.getAppid(), payloadType)) {
                    log.info("no decoder for GC payload type %d for app id %d", gcBody.getMsgtype() & MessageFlag.MASK, gcBody.getAppid());
                    return;
                }
                if ((gcBody.getMsgtype() & MessageFlag.PROTO) == 0) {
                    log.error("embedded GC has no proto header! Implement this!");
                }
                log.info("decoding GC payload type %d for app id %d", gcBody.getMsgtype() & MessageFlag.MASK, gcBody.getAppid());

                ByteBuf payloadBuf = Unpooled.wrappedBuffer(gcBody.getPayload().asReadOnlyByteBuffer()).order(ByteOrder.LITTLE_ENDIAN);
                //log.info(ByteBufUtil.hexDump(payloadBuf));
                payloadBuf.readInt(); // skip over payload type
                header = instantiateAndDecodeObject(registry.getHeaderClassForMessageType(gcBody.getAppid(), payloadType), payloadBuf);
                body = instantiateAndDecodeObject(registry.getBodyClassForMessageType(gcBody.getAppid(), payloadType), payloadBuf);
                ctx.fireChannelRead(new Message(gcBody.getAppid(), header, body));
            } else {
                ctx.fireChannelRead(new Message(-1, header, body));
            }
        } finally {
            in.release();
        }
    }

    protected <C> C instantiateAndDecodeObject(Class<C> objectClass, ByteBuf in) throws IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {
        if (Decodable.class.isAssignableFrom(objectClass)) {
            C object = objectClass.newInstance();
            ((Decodable) object).decode(in);
            return object;
        } else if (GeneratedMessage.class.isAssignableFrom(objectClass)) {
            GeneratedMessage object = (GeneratedMessage) objectClass.getDeclaredMethod("parseFrom", InputStream.class).invoke(null, new ByteBufInputStream(in));
            return (C) object;
        } else {
            throw new IOException("don't know how to decode a " + objectClass.getName());
        }
    }

    protected void unpackMulti(ChannelHandlerContext ctx, SM_Base.CMsgMulti multi) throws Exception {
        InputStream is = multi.getMessageBody().newInput();
        int isSize = multi.getMessageBody().size();
        if (multi.getSizeUnzipped() > 0) {
            log.debug("multi is zipped, unzipped size is %d", multi.getSizeUnzipped());
            ZipInputStream zis = new ZipInputStream(is);
            zis.getNextEntry();
            is = zis;
            isSize = multi.getSizeUnzipped();
        }
        ByteBuf buf = ctx.alloc().buffer(isSize).order(ByteOrder.LITTLE_ENDIAN);
        while (isSize > 0) {
            isSize -= buf.writeBytes(is, isSize);
        }
        while (buf.readableBytes() != 0) {
            int size = buf.readInt();
            buf.retain();
            channelRead(ctx, buf.readSlice(size));
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msgObj, ChannelPromise promise) throws Exception {
        Message msg = (Message) msgObj;
        Integer type = registry.getMessageTypeForBody(msg.getAppId(), msg.getBody());
        if (type == null) {
            throw new IOException(
                    String.format("unable to find message type for body class %s and app id %s", msg.getBody().getClass().getName(), msg.getAppId())
            );
        }
        if ((type & MessageFlag.GC) != 0) {
            type = type & ~MessageFlag.GC;

            ProtoHeader innerHeader = new ProtoHeader();

            ByteBuf innerOut = ctx.alloc().heapBuffer().order(ByteOrder.LITTLE_ENDIAN);
            innerOut.writeInt(type);
            encodeObject(innerHeader, innerOut);
            encodeObject(msg.getBody(), innerOut);

            SM_ClientServer.CMsgGCClient.Builder newBody = SM_ClientServer.CMsgGCClient.newBuilder();
            newBody.setAppid(msg.getAppId());
            newBody.setMsgtype(type);
            newBody.setPayload(ByteString.copyFrom(innerOut.nioBuffer()));

            msg.getHeader().setRoutingAppId(msg.getAppId());
            msg = msg.withReplacedBody(newBody);

            type = EMsg.ClientToGC.v() | MessageFlag.PROTO;
        }

        ByteBuf out = ctx.alloc().heapBuffer().order(ByteOrder.LITTLE_ENDIAN);
        out.writeInt(type);
        encodeObject(msg.getHeader(), out);
        encodeObject(msg.getBody(), out);
        ctx.writeAndFlush(out, promise);
    }

    protected void encodeObject(Object object, ByteBuf out) throws IOException {
        if (object instanceof Encodable) {
            ((Encodable) object).encode(out);
        } else if (object instanceof GeneratedMessage.Builder) {
            byte[] buf = ((GeneratedMessage.Builder) object).build().toByteArray();
            out.writeBytes(buf);
        } else {
            throw new IOException("don't know how to encode a " + object.getClass().getName());
        }
    }

}
