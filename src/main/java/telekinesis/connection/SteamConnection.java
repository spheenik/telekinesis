package telekinesis.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import telekinesis.ClientMessageHandler;
import telekinesis.MessageDispatcher;
import telekinesis.Publisher;
import telekinesis.Util;
import telekinesis.connection.codec.AESCodec;
import telekinesis.connection.codec.FrameCodec;
import telekinesis.connection.codec.MessageCodec;
import telekinesis.message.proto.generated.steam.SM_Base;
import telekinesis.message.proto.generated.steam.SM_ClientServer;
import telekinesis.message.simple.ChannelEncryptRequest;
import telekinesis.message.simple.ChannelEncryptResponse;
import telekinesis.message.simple.ChannelEncryptResult;
import telekinesis.model.Header;
import telekinesis.model.steam.EMsg;
import telekinesis.model.steam.EResult;
import telekinesis.model.steam.SteamId;
import telekinesis.registry.CodecRegistry;
import telekinesis.registry.CombinedMessageRegistry;
import telekinesis.registry.MessageRegistry;

import java.io.IOException;

public class SteamConnection extends Publisher<SteamConnection> {

    private static final MessageRegistry HANDLED_MESSAGES = new MessageRegistry()
            .registerSimple(EMsg.ChannelEncryptRequest.v(), ChannelEncryptRequest.class)
            .registerSimple(EMsg.ChannelEncryptResponse.v(), ChannelEncryptResponse.class)
            .registerSimple(EMsg.ChannelEncryptResult.v(), ChannelEncryptResult.class)
            .registerProto(EMsg.Multi.v(), SM_Base.CMsgMulti.class);

    private final Logger log;
    private final EventLoopGroup workerGroup;
    private final CombinedMessageRegistry messageRegistry;
    private final ClientMessageHandler messageHandler;
    private final MessageDispatcher selfHandledMessageDispatcher;
    private final IdleTimeoutFunction heartbeatFunction;

    private ConnectionState connectionState;
    private SocketChannel channel;
    private AESCodec aesCodec;
    private long steamId;
    private int sessionId;

    public SteamConnection(EventLoopGroup workerGroup) {
        this(workerGroup, null, "conn-steam");
    }

    public SteamConnection(EventLoopGroup workerGroup, ClientMessageHandler messageHandler, String id) {
        this.workerGroup = workerGroup;
        this.log = LoggerFactory.getLogger(id);
        this.messageRegistry = new CombinedMessageRegistry(HANDLED_MESSAGES);
        this.messageHandler = messageHandler;

        selfHandledMessageDispatcher = new MessageDispatcher();
        selfHandledMessageDispatcher.subscribe(ChannelEncryptRequest.class, this::handleChannelEncryptRequest);
        selfHandledMessageDispatcher.subscribe(ChannelEncryptResult.class, this::handleChannelEncryptResult);

        connectionState = ConnectionState.DISCONNECTED;

        resetState();
        this.heartbeatFunction = new IdleTimeoutFunction(workerGroup) {
            @Override
            protected void onTimout() {
                SM_ClientServer.CMsgClientHeartBeat.Builder msg = SM_ClientServer.CMsgClientHeartBeat.newBuilder();
                send(msg);
            }
        };
    }

    public void addRegistry(CodecRegistry registry) {
        messageRegistry.addRegistry(registry);
    }

    public void removeRegistry(CodecRegistry registry) {
        messageRegistry.removeRegistry(registry);
    }

    public void connect(String host, int port) {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        b.remoteAddress(host, port);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(FrameCodec.class.getSimpleName(), new FrameCodec(log));
                pipeline.addLast(MessageCodec.class.getSimpleName(), new MessageCodec(log, messageRegistry));
                pipeline.addLast(ConnectionHandler.class.getSimpleName(), new ConnectionHandler());
            }
        });
        ChannelFuture channelFuture = b.connect();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    channel = (SocketChannel) future.channel();
                    log.info("connected to peer {}", channel.remoteAddress());
                    changeConnectionState(ConnectionState.CONNECTED);
                } else {
                    log.info("connection attempt failed: {}", future.cause().getMessage());
                    changeConnectionState(ConnectionState.CONNECTION_FAILED);
                }
            }
        });
        changeConnectionState(ConnectionState.CONNECTING);
    }

    public void disconnect() {
        if (channel != null && connectionState != ConnectionState.DISCONNECTING) {
            disableHeartbeat();
            changeConnectionState(ConnectionState.DISCONNECTING);
            channel.close();
        }
    }

    public class ConnectionHandler extends SimpleChannelInboundHandler<Message> {

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            channel = null;
            switch (connectionState) {
                case CONNECTING:
                    log.info("connection attempt timed out");
                    changeConnectionState(ConnectionState.CONNECTION_FAILED);
                    break;
                case DISCONNECTING:
                    log.info("connection closed");
                    changeConnectionState(ConnectionState.CLOSED);
                    break;
                default:
                    log.info("connection lost");
                    changeConnectionState(ConnectionState.LOST);
                    break;
            }
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message msg) throws Exception {
            log.debug("received " + msg.getBody().getClass());
            Header h = msg.getHeader();
            if (h.hasSteamId()) {
                steamId = h.getSteamId();
            }
            if (h.hasSessionId()) {
                sessionId = h.getSessionId();
            }
            ClientMessageContext ctx = new ClientMessageContext(SteamConnection.this, h.getSourceJobId());
            selfHandledMessageDispatcher.handleClientMessage(ctx, msg.getBody());
            messageHandler.handleClientMessage(ctx, msg.getBody());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("Exception in pipeline", cause);
        }

    }

    public void send(Object body) {
        send(-1L, body);
    }

    protected void send(long targetJobId, Object body) {
        heartbeatFunction.resetTimer();
        log.info("sending message " + body.getClass());
        Class<? extends Header> headerClass = messageRegistry.getHeaderClassForBody(body);
        if (headerClass == null) {
            throw new RuntimeException("don't now header class for body of class " + body.getClass().getName());
        }
        Header header = Util.newInstance(headerClass);
        Message message = new Message(header, body);

        header.setSteamId(steamId);
        header.setSessionId(sessionId);
        header.setTargetJobId(targetJobId);

        channel.writeAndFlush(message);
    }

    private void resetState() {
        this.steamId = SteamId.DEFAULT;
        this.sessionId = 0;
    }

    private void changeConnectionState(ConnectionState newState) {
        if (connectionState == newState) {
            return;
        }
        connectionState = newState;
        publish(this, connectionState);
    }

    protected void handleChannelEncryptRequest(ClientMessageContext ctx, ChannelEncryptRequest in) throws IOException {
        log.info("got encryption request for universe {}, protocol version {}", in.getUniverse(), in.getProtocolVersion());
        aesCodec = new AESCodec(in.getUniverse());
        ChannelEncryptResponse out = new ChannelEncryptResponse();
        out.setProtocolVersion(in.getProtocolVersion());
        out.setBlockLength(AESCodec.BLOCK_SIZE_BITS);
        out.setKey(aesCodec.getEncryptedKey());
        ctx.reply(out);
    }

    protected void handleChannelEncryptResult(ClientMessageContext ctx, ChannelEncryptResult msg) {
        if (msg.getResult() == EResult.OK) {
            log.info("encryption established");
            channel.pipeline().addAfter(
                    FrameCodec.class.getSimpleName(),
                    AESCodec.class.getSimpleName(),
                    aesCodec
            );
            changeConnectionState(ConnectionState.ESTABLISHED);
        } else {
            log.error("failed to establish encryption, server said '{}'", msg.getResult());
            changeConnectionState(ConnectionState.BROKEN);
        }
        aesCodec = null;
    }

    public void enableHeartbeat(int seconds) {
        heartbeatFunction.enable(seconds);
    }

    public void disableHeartbeat() {
        heartbeatFunction.disable();
    }

}
