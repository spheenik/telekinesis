package telekinesis.connection;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.Xnio;
import org.xnio.XnioWorker;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;

import telekinesis.message.Message;
import telekinesis.message.MessageFactory;
import telekinesis.model.EMsg;

public class Connection {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final SocketAddress address;
    
    private final BlockingDeque<byte[]> out = new LinkedBlockingDeque<byte[]>();
    
    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private StreamConnection channel; 
    private ByteBuffer readBuf = ByteBuffer.allocate(2048);
    private ByteBuffer writeBuf = ByteBuffer.allocate(2048);

    public Connection(SocketAddress address) {
        this.address = address;
    }
    
    private void initBufs() {
        readBuf.order(ByteOrder.LITTLE_ENDIAN);
        writeBuf.order(ByteOrder.LITTLE_ENDIAN);
        readBuf.clear();
        writeBuf.clear();
        writeBuf.flip();
    }
    
    public void connect() throws IOException {
        initBufs();
        XnioWorker worker = Xnio.getInstance().createWorker(OptionMap.EMPTY);
        worker.openStreamConnection(address, openListener, OptionMap.EMPTY);
        connectionState = ConnectionState.CONNECTING;
    }
    
    public void disconnect() throws IOException {
        connectionState = ConnectionState.DISCONNECTING;
        channel.getSourceChannel().shutdownReads();
        channel.getSinkChannel().shutdownWrites();
        channel.getSinkChannel().flush();
    }
    
    public void send(byte[] data) throws IOException {
        out.add(data);
        if (!channel.getSinkChannel().isWriteResumed()) {
            channel.getSinkChannel().resumeWrites();
        }
    }
    
    private void handleReceive(byte[] data) {
        Message msg = MessageFactory.build(data);
        if (msg.getType() == EMsg.ChannelEncryptRequest) {
            log.debug("YEA!");
        }
    }
    
    private final ChannelListener<StreamConnection> openListener = new ChannelListener<StreamConnection>() {
        public void handleEvent(StreamConnection channel) {
            log.info("connection to {} established", channel.getPeerAddress());
            connectionState = ConnectionState.CONNECTED;
            Connection.this.channel = channel;
            channel.setCloseListener(closeListener);
            channel.getSourceChannel().setReadListener(readListener);
            channel.getSourceChannel().resumeReads();
            channel.getSinkChannel().setWriteListener(writeListener);
        }
    };
    
    private final ChannelListener<StreamConnection> closeListener = new ChannelListener<StreamConnection>() {
        public void handleEvent(StreamConnection channel) {
            log.info("connection to {} {}", channel.getPeerAddress(), connectionState == ConnectionState.DISCONNECTING ? "closed" : "lost");
            connectionState = ConnectionState.DISCONNECTED;
            channel.getWorker().shutdown();
        }                
    };
    
    final ChannelListener<ConduitStreamSourceChannel> readListener = new ChannelListener<ConduitStreamSourceChannel>() {
        public void handleEvent(ConduitStreamSourceChannel channel) {
            try {
                int n = channel.read(readBuf);
                if (readBuf.position() >= 4) {
                    int len = readBuf.getInt(0);
                    if (readBuf.position() >= len + 8) {
                        log.info("packet with length {} received", len);
                        byte[] data = new byte[len];
                        readBuf.flip();
                        readBuf.position(8);
                        readBuf.get(data);
                        readBuf.compact();
                        handleReceive(data);
                    }
                }
                if (n == -1) {
                    channel.close();
                }
            } catch (IOException e) {
                ChannelListeners.closingChannelExceptionHandler().handleException(channel, e);
            }
        }
    };
    
    final ChannelListener<ConduitStreamSinkChannel> writeListener = new ChannelListener<ConduitStreamSinkChannel>() {
        public void handleEvent(ConduitStreamSinkChannel channel) {
            try {
                if (writeBuf.remaining() == 0) {
                    if (out.isEmpty()) {
                        channel.suspendWrites();
                        return;
                    } else {
                        writeBuf.clear();
                        writeBuf.put(out.remove());
                        writeBuf.flip();
                    }
                    channel.write(writeBuf);
                }
            } catch (IOException e) {
                ChannelListeners.closingChannelExceptionHandler().handleException(channel, e);
            }
        }
    };

}
