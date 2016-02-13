package telekinesis.connection.codec;

import com.google.protobuf.GeneratedMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import telekinesis.connection.Message;
import telekinesis.message.proto.ProtoHeader;
import telekinesis.message.proto.generated.steam.SM_Base;
import telekinesis.model.Decodable;
import telekinesis.model.Encodable;
import telekinesis.model.Header;
import telekinesis.model.steam.EMsg;
import telekinesis.registry.CodecRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteOrder;
import java.util.zip.ZipInputStream;

public class MessageCodec extends ChannelDuplexHandler {

    private static final int PROTO_FLAG = 0x80000000;
    private static final int PROTO_MASK = ~PROTO_FLAG;

    private final Logger log;
    private final CodecRegistry registry;

    public MessageCodec(Logger log, CodecRegistry registry) {
        this.log = log;
        this.registry = registry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            int type = in.readInt() & PROTO_MASK;
            if (!registry.knowsMessageType(type)) {
                log.debug("no decoder for message type {}", EMsg.n(type));
                in.skipBytes(in.readableBytes());
                return;
            }
            Header header = instantiateAndDecodeObject(registry.getHeaderClassForMessageType(type), in);
            Object body = instantiateAndDecodeObject(registry.getBodyClassForMessageType(type), in);
            if (in.readableBytes() != 0) {
                log.debug("discarding {} extra bytes not decoded by message", in.readableBytes());
                in.skipBytes(in.readableBytes());
            }
            if (body instanceof SM_Base.CMsgMulti) {
                unpackMulti(ctx, (SM_Base.CMsgMulti) body);
            } else {
                ctx.fireChannelRead(new Message(header, body));
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
        Integer type = registry.getMessageTypeForBody(msg.getBody());
        if (type == null) {
            throw new IOException("unable to find message type for body class " + msg.getBody().getClass().getName());
        }
        ByteBuf out = ctx.alloc().heapBuffer().order(ByteOrder.LITTLE_ENDIAN);
        int flag = msg.getHeader() instanceof ProtoHeader ? PROTO_FLAG : 0;
        out.writeInt(type | flag);
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
