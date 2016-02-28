package telekinesis.connection.codec;

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
                log.info("no decoder for message type {}", EMsg.n(type & MessageFlag.MASK));
                in.skipBytes(in.readableBytes());
                return;
            }
            Header header = instantiateAndDecodeObject(registry.getHeaderClassForMessageType(AppId.STEAM, type), in);
            Object body = instantiateAndDecodeObject(registry.getBodyClassForMessageType(AppId.STEAM, type), in);
            if (in.readableBytes() != 0) {
                log.debug("discarding {} extra bytes not decoded by message", in.readableBytes());
                in.skipBytes(in.readableBytes());
            }
            if (body instanceof SM_Base.CMsgMulti) {
                unpackMulti(ctx, (SM_Base.CMsgMulti) body);
            } else if (body instanceof SM_ClientServer.CMsgGCClient) {
                SM_ClientServer.CMsgGCClient gcBody = (SM_ClientServer.CMsgGCClient) body;
                int payloadType = gcBody.getMsgtype() | MessageFlag.GC;
                if (!registry.knowsMessageType(gcBody.getAppid(), payloadType)) {
                    log.info("no decoder for GC payload type {} for app id {}", gcBody.getMsgtype() & MessageFlag.MASK, gcBody.getAppid());
                    return;
                }
                body = instantiateAndDecodeObject(
                        registry.getBodyClassForMessageType(gcBody.getAppid(), payloadType),
                        Unpooled.wrappedBuffer(gcBody.getPayload().asReadOnlyByteBuffer())
                );
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
            log.debug("multi is zipped, unzipped size is {}", multi.getSizeUnzipped());
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
            GeneratedMessage.Builder gcPayload = (GeneratedMessage.Builder) msg.getBody();
            SM_ClientServer.CMsgGCClient.Builder gcBody = SM_ClientServer.CMsgGCClient.newBuilder();
            gcBody.setAppid(msg.getAppId());
            gcBody.setMsgtype(type & ~MessageFlag.GC);
            gcBody.setPayload(gcPayload.build().toByteString());

            type = EMsg.ClientToGC.v() | MessageFlag.PROTO;
            msg = msg.withReplacedBody(gcBody);
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
