package telekinesis.connection;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
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

import telekinesis.crypto.KeyDictionary;
import telekinesis.crypto.RSACrypto;
import telekinesis.message.Message;
import telekinesis.message.MessageFactory;
import telekinesis.message.MessageHandler;
import telekinesis.message.internal.ChannelEncryptRequest;
import telekinesis.message.internal.ChannelEncryptResponse;
import telekinesis.message.internal.ChannelEncryptResult;
import telekinesis.model.EMsg;
import telekinesis.model.EResult;

public class Connection {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    private static final int MAGIC = 0x31305456; // "VT01"

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SocketAddress address;

    private final BlockingDeque<ByteBuffer> out = new LinkedBlockingDeque<ByteBuffer>();
    private final SecureRandom rng = new SecureRandom();

    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private StreamConnection channel;
    private ByteBuffer readBuf = null;
    private ByteBuffer writeBuf = null;

    private byte[] sessionKey = null;
    private boolean encryptionActive = false;

    public Connection(SocketAddress address) {
        this.address = address;
    }

    private ByteBuffer getNewBuffer() {
        ByteBuffer result = ByteBuffer.allocate(2048);
        result.order(ByteOrder.LITTLE_ENDIAN);
        return result;
    }

    private void initBufs() {
        readBuf = getNewBuffer();
        writeBuf = null;
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

    private MessageCodec getCodec() {
        return encryptionActive ? encryptionCodec : plainTextCodec;
    }

    public void send(Message<?, ?> msg) {
        ByteBuffer msgBuf = getNewBuffer();
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

    private void handleReceive(Message<?, ?> msg) {
        if (msg.getType() == EMsg.ChannelEncryptRequest) {
            encryptRequestHandler.handleMessage((ChannelEncryptRequest) msg);
        } else if (msg.getType() == EMsg.ChannelEncryptResult) {
            encryptResultHandler.handleMessage((ChannelEncryptResult) msg);
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
                        if (readBuf.getInt(4) != MAGIC) {
                            throw new IOException("packet from the server doesn't contain proper MAGIC");
                        }
                        readBuf.flip();
                        log.info("received data: {}", Util.convertByteBufferToString(readBuf, len + 8));
                        readBuf.position(8);
                        Message<?, ?> m = getCodec().fromWire(readBuf);
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
                        log.info("sending data: {}", Util.convertByteBufferToString(writeBuf, writeBuf.limit()));
                    }
                    channel.write(writeBuf);
                }
            } catch (IOException e) {
                ChannelListeners.closingChannelExceptionHandler().handleException(channel, e);
            }
        }
    };

    final MessageHandler<ChannelEncryptRequest> encryptRequestHandler = new MessageHandler<ChannelEncryptRequest>() {
        public void handleMessage(ChannelEncryptRequest message) {
            log.info("got encryption request for universe {}, protocol version {}", message.getBody().getUniverse(), message.getBody().getProtocolVersion());
            sessionKey = new byte[32];
            rng.nextBytes(sessionKey);
            ChannelEncryptResponse resp = MessageFactory.forType(EMsg.ChannelEncryptResponse);
            resp.getBody().setProtocolVersion(message.getBody().getProtocolVersion());
            resp.getBody().setKey(new RSACrypto(KeyDictionary.getPublicKey(message.getBody().getUniverse())).encrypt(sessionKey));
            send(resp);
        }
    };

    final MessageHandler<ChannelEncryptResult> encryptResultHandler = new MessageHandler<ChannelEncryptResult>() {
        public void handleMessage(ChannelEncryptResult message) {
            if (message.getBody().getResult() == EResult.OK) {
                log.info("encryption established");
                encryptionActive = true;
            } else {
                log.error("failed to establish encryption, server said '{}'", message.getBody().getResult());
            }
        }
    };

    final MessageCodec plainTextCodec = new MessageCodec() {

        public void toWire(Message<?, ?> msg, ByteBuffer dstBuf) {
            dstBuf.putInt(msg.getType().v());
            msg.serialize(dstBuf);
        }

        public Message<?, ?> fromWire(ByteBuffer srcBuf) {
            EMsg type = EMsg.f(srcBuf.getInt());
            Message<?, ?> msg = MessageFactory.forType(type);
            if (msg != null) {
                msg.deserialize(srcBuf);
            }
            return msg;
        }
    };

    final MessageCodec encryptionCodec = new MessageCodec() {

        public void toWire(Message<?, ?> msg, ByteBuffer dstBuf) {
            try {
                // encrypt iv using ECB and provided key
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sessionKey, "AES"));
                // generate iv
                final byte[] iv = new byte[16];
                rng.nextBytes(iv);
                // encode iv
                dstBuf.put(cipher.doFinal(iv));
                // encrypt input plaintext with CBC using the generated
                // (plaintext) IV and the provided key
                cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sessionKey, "AES"), new IvParameterSpec(iv));
                // generate plaintext message buf
                ByteBuffer srcBuf = getNewBuffer();
                srcBuf.putInt(msg.getType().v());
                msg.serialize(srcBuf);
                // encode message
                cipher.doFinal(srcBuf, dstBuf);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        public Message<?, ?> fromWire(ByteBuffer srcBuf) {
            try {
                // first 16 bytes of input is the ECB encrypted IV
                byte[] iv = new byte[16];
                byte[] cryptedIv = new byte[16];
                srcBuf.get(cryptedIv);

                // decrypt the IV using ECB
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sessionKey, "AES"));
                iv = cipher.doFinal(cryptedIv);

                // decrypt the remaining ciphertext in cbc with the decrypted IV
                cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sessionKey, "AES"), new IvParameterSpec(iv));
                ByteBuffer dstBuf = getNewBuffer();
                cipher.doFinal(srcBuf, dstBuf);

                // construct message
                EMsg type = EMsg.f(dstBuf.getInt());
                Message<?, ?> msg = MessageFactory.forType(type);
                if (msg != null) {
                    msg.deserialize(dstBuf);
                }
                return msg;
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }
    };

}
