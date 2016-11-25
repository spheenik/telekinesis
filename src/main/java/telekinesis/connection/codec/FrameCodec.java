package telekinesis.connection.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.ByteOrder;

public class FrameCodec extends ChannelDuplexHandler {

    private static final int MAGIC = 0x31305456; // "VT01"

    private final Logger log;
    private ByteBuf bin;

    public FrameCodec(Logger log) {
        this.log = log;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        bin = ctx.alloc().heapBuffer().order(ByteOrder.LITTLE_ENDIAN);
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        bin.release();
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        bin.writeBytes(in);
        in.release();

        int n = bin.readableBytes();
        if (n < 8) {
            return;
        }
        int len = bin.getInt(0);
        if (bin.getInt(4) != MAGIC) {
            throw new IOException("packet from the server doesn't contain proper MAGIC");
        }
        if (len > n - 8) {
            return;
        }

        bin.skipBytes(8);
        ByteBuf frame = ctx.alloc().heapBuffer(len).order(ByteOrder.LITTLE_ENDIAN);
        frame.writeBytes(bin, len);
        bin.discardReadBytes();
        log.debug("received a frame with %d bytes", len + 8);
        ctx.fireChannelRead(frame);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf out = (ByteBuf) msg;
        int len = out.readableBytes();

        ByteBuf bout = ctx.alloc().heapBuffer(8).order(ByteOrder.LITTLE_ENDIAN);
        bout.writeInt(len);
        bout.writeInt(MAGIC);

        ctx.write(bout);
        ctx.write(out, promise);

        log.debug("sent a frame with %d bytes", len + 8);
    }

}
