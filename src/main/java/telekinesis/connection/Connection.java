package telekinesis.connection;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

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

import telekinesis.Scheduler;
import telekinesis.annotations.MessageHandler;
import telekinesis.connection.codec.AESCodec;
import telekinesis.connection.codec.MessageCodec;
import telekinesis.connection.codec.PlainTextCodec;
import telekinesis.event.Event;
import telekinesis.event.Event.EventHandler;
import telekinesis.event.EventEmitter;
import telekinesis.message.Message;
import telekinesis.message.MessageRegistry;
import telekinesis.message.ReceivableMessage;
import telekinesis.message.TransmittableMessage;
import telekinesis.message.internal.ChannelEncryptRequest;
import telekinesis.message.internal.ChannelEncryptResponse;
import telekinesis.message.internal.ChannelEncryptResult;
import telekinesis.message.proto.ClientLogonResponse;
import telekinesis.message.proto.ClientUpdateMachineAuth;
import telekinesis.message.proto.ClientUpdateMachineAuthResponse;
import telekinesis.message.proto.Multi;
import telekinesis.model.EResult;

import com.google.protobuf.ByteString;

public class Connection implements EventEmitter {

    public interface CONNECTION_STATE_CHANGED extends EventHandler.H1<ConnectionState> {
        public void handle(ConnectionState newState) throws Exception;
    };
    
    private static final int MAGIC = 0x31305456; // "VT01"

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final SocketAddress address;
    
    private final HandlerRegistry handlerRegistry = new HandlerRegistry();
    private final BlockingDeque<ByteBuffer> out = new LinkedBlockingDeque<ByteBuffer>();
    
    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private StreamConnection channel;
    private ByteBuffer readBuf = null;
    private ByteBuffer writeBuf = null;

    private PlainTextCodec plainTextCodec = new PlainTextCodec();
    private AESCodec aesCodec = null;
    private boolean encryptionActive = false;
    
    private ConnectionContext connectionContext = new ConnectionContext();
    private ScheduledFuture<?> connectWatchdog = null;
    
    public Connection(SocketAddress address) {
        this.address = address;
        handlerRegistry.addInstance(this);
    }
    
    public void connect() throws IOException {
        initBufs();
        final XnioWorker worker = Xnio.getInstance().createWorker(OptionMap.EMPTY);
        worker.openStreamConnection(address, openListener, OptionMap.EMPTY);
        changeConnectionState(ConnectionState.CONNECTING);
        connectWatchdog = Scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                changeConnectionState(ConnectionState.CONNECTION_TIMEOUT);
                worker.shutdown();
            }
        }, 2, TimeUnit.SECONDS);
    }

    public void disconnect() throws IOException {
        changeConnectionState(ConnectionState.DISCONNECTING);
        channel.getSourceChannel().shutdownReads();
        channel.getSinkChannel().shutdownWrites();
        channel.getSinkChannel().flush();
    }
    
    
    public void send(TransmittableMessage<?, ?> msg) throws IOException {
        msg.prepareTransmission(connectionContext);
        ByteBuffer msgBuf = Util.getNewBuffer();
        msgBuf.position(4);
        msgBuf.putInt(MAGIC);
        getCodec().toWire(msg, msgBuf);
        msgBuf.putInt(0, msgBuf.position() - 8);
        msgBuf.flip();
        out.add(msgBuf);
        if (!channel.getSinkChannel().isWriteResumed()) {
            channel.getSinkChannel().resumeWrites();
        }
    }
    
    public void sendRaw(ByteBuffer msgBuf) throws IOException {
        out.add(msgBuf);
        if (!channel.getSinkChannel().isWriteResumed()) {
            channel.getSinkChannel().resumeWrites();
        }
    }
    
    private void changeConnectionState(ConnectionState newState) {
        connectionState = newState;
        Event.emit(this, CONNECTION_STATE_CHANGED.class, newState);
    }

    private void initBufs() {
        readBuf = Util.getNewBuffer();
        writeBuf = null;
    }

    private MessageCodec getCodec() {
        return encryptionActive ? aesCodec : plainTextCodec;
    }

    private void dumpMessage(String prefix, Message<?, ?> msg) {
        log.info("{}\nHEADER:\n{}\nBODY:\n{}", prefix, msg.getHeader(), msg.getBody());
    }

    private void handleReceive(ReceivableMessage<?, ?> msg) throws IOException {
        if (msg != null) {
            msg.updateContext(connectionContext);
            if (!handlerRegistry.handle(msg)) {
                log.warn("unhandled message of type {}", MessageRegistry.getEMsgForClass(msg.getClass()));
                dumpMessage("", msg);
            }
        }
    }

    private final ChannelListener<StreamConnection> openListener = new ChannelListener<StreamConnection>() {
        public void handleEvent(StreamConnection channel) {
            log.info("connected to {}", channel.getPeerAddress());
            changeConnectionState(ConnectionState.CONNECTED);
            connectWatchdog.cancel(true);
            connectWatchdog = null;
            Connection.this.channel = channel;
            channel.setCloseListener(closeListener);
            channel.getSourceChannel().setReadListener(readListener);
            channel.getSourceChannel().resumeReads();
            channel.getSinkChannel().setWriteListener(writeListener);
        }
    };

    private final ChannelListener<StreamConnection> closeListener = new ChannelListener<StreamConnection>() {
        public void handleEvent(StreamConnection channel) {
            log.info("connection to peer {}", connectionState == ConnectionState.DISCONNECTING ? "closed" : "lost");
            changeConnectionState(connectionState == ConnectionState.DISCONNECTING ? ConnectionState.CLOSED : ConnectionState.LOST);
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
                        if (readBuf.getInt(4) != MAGIC) {
                            throw new IOException("packet from the server doesn't contain proper MAGIC");
                        }
                        readBuf.flip();
                        log.trace("received {} bytes: {}", len + 8, Util.convertByteBufferToString(readBuf, len + 8));
                        readBuf.position(8);
                        ReceivableMessage<?, ?> m = getCodec().fromWire(readBuf);
                        readBuf.compact();
                        handleReceive(m);
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
                if (writeBuf == null || writeBuf.remaining() == 0) {
                    if (out.isEmpty()) {
                        writeBuf = null;
                        channel.suspendWrites();
                        return;
                    } else {
                        writeBuf = out.remove();
                        log.trace("sending {} bytes: {}", writeBuf.limit(), Util.convertByteBufferToString(writeBuf, writeBuf.limit()));
                    }
                    channel.write(writeBuf);
                }
            } catch (IOException e) {
                ChannelListeners.closingChannelExceptionHandler().handleException(channel, e);
            }
        }
    };

    @MessageHandler
    private void handleMessage(ChannelEncryptRequest message) throws IOException {
        log.info("got encryption request for universe {}, protocol version {}", message.getBody().getUniverse(), message.getBody().getProtocolVersion());
        aesCodec = new AESCodec(message.getBody().getUniverse());
        ChannelEncryptResponse resp = new ChannelEncryptResponse();
        resp.getBody().setProtocolVersion(message.getBody().getProtocolVersion());
        resp.getBody().setBlockLength(AESCodec.BLOCK_SIZE);
        resp.getBody().setKey(aesCodec.getEncryptedKey());
        send(resp);
    }
    
    @MessageHandler
    private void handleMessage(ChannelEncryptResult message) throws IOException {
        if (message.getBody().getResult() == EResult.OK) {
            log.info("encryption established");
            encryptionActive = true;
            changeConnectionState(ConnectionState.ESTABLISHED);
        } else {
            log.error("failed to establish encryption, server said '{}'", message.getBody().getResult());
            changeConnectionState(ConnectionState.BROKEN);
        }
    }

    @MessageHandler
    private void handleMessage(Multi message) throws IOException {
        ByteBuffer buf = Util.getNewBuffer();
        if (message.getBody().getSizeUnzipped() > 0) {
            ZipInputStream s = new ZipInputStream(message.getBody().getMessageBody().newInput());
            s.getNextEntry();
            int v;
            while ((v = s.read()) != -1) {
                buf.put((byte) v);
            }
        } else {
            message.getBody().getMessageBody().copyTo(buf);
        }

        buf.flip();
        int len = buf.limit();
        while (buf.position() != len) {
            buf.limit(len);
            int subSize = buf.getInt();
            buf.limit(buf.position() + subSize);
            ReceivableMessage<?, ?> msg = plainTextCodec.fromWire(buf);
            if (msg != null) {
                handleReceive(msg);
                if (buf.remaining() > 0) {
                    log.warn("while reading multi, a message of class {} did not read the all it's data: {} bytes remaining", msg.getClass().getSimpleName(), buf.remaining());
                }
            } 
            buf.position(buf.limit());
        }
    }
    
    @MessageHandler
    private void handleMessage(ClientUpdateMachineAuth message) throws IOException {
        dumpMessage("ClientUpdateMachineAuth:", message);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(message.getBody().getBytes().toByteArray());
            
            ClientUpdateMachineAuthResponse r = new ClientUpdateMachineAuthResponse();
            r.getHeader().setJobidTarget(message.getHeader().getJobidSource());
            r.getBody().setShaFile(ByteString.copyFrom(digest));
            r.getBody().setEresult(EResult.OK.v());
            
            log.info("sentry={}", Util.dumpSHA1(digest));

            send(r);
            
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } 
    }

    @MessageHandler
    public void handleMessage(ClientLogonResponse message) {
        dumpMessage("login response:", message);
    }

}
