package telekinesis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import telekinesis.annotations.MessageHandler;
import telekinesis.connection.Connection;
import telekinesis.connection.ConnectionState;
import telekinesis.connection.HandlerRegistry;
import telekinesis.connection.Util;
import telekinesis.event.Event;
import telekinesis.event.Event.EventHandler;
import telekinesis.event.EventEmitter;
import telekinesis.message.MessageRegistry;
import telekinesis.message.ReceivableMessage;
import telekinesis.message.TransmittableMessage;
import telekinesis.message.proto.ClientLogonResponse;
import telekinesis.message.proto.ClientServersAvailable;
import telekinesis.message.proto.ClientSessionToken;
import telekinesis.message.proto.ClientUpdateMachineAuth;
import telekinesis.message.proto.ClientUpdateMachineAuthResponse;
import telekinesis.model.EResult;

import com.google.protobuf.ByteString;

public class SteamClient implements EventEmitter {
    
    public interface POST_CONSTRUCT extends EventHandler.H1<SteamClient> {
        public void handle(SteamClient client) throws Exception;        
    };
    public interface PRE_DESTROY extends EventHandler.H1<SteamClient> {
        public void handle(SteamClient client) throws Exception;        
    };
    public interface CONNECTED extends EventHandler.H0 {}; 
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    private boolean exitEventLoop = false;
    
    private HandlerRegistry handlerRegistry = null;
    private Connection connection = null;
    private User user;
    
    public void connect() throws IOException {
        connection = new Connection(new InetSocketAddress("146.66.152.12", 27017));
        user = new User(this);
        handlerRegistry = new HandlerRegistry();
        handlerRegistry.addInstance(this);
        handlerRegistry.addInstance(connection);
        handlerRegistry.addInstance(user);
        Event.register(connection, connectionStateHandler);
        Event.register(connection, messageReceivedHandler);
        connection.connect();
    }
    
    public void disconnect() throws IOException {
        connection.disconnect();
    }

    public void send(TransmittableMessage<?, ?> msg) throws IOException {
        connection.send(msg);
    }
    
    public void run() {
        Scheduler.registerSteamClient(this);
        Event.emit(SteamClient.this, POST_CONSTRUCT.class, SteamClient.this);
        while(!exitEventLoop) {
            Event.executeNextAction();
        }
        Event.emit(SteamClient.this, PRE_DESTROY.class, SteamClient.this);
        Event.executeRemainingActions();
    }
    
    public User getUser() {
        return user;
    }
    
    @MessageHandler
    public void handleMessage(ClientServersAvailable message) {
        //message.dumpToLog(log, "ClientServersAvailable:");
    }

    @MessageHandler
    public void handleMessage(ClientLogonResponse message) {
        message.dumpToLog(log, "ClientLogonResponse:");
    }
    
    @MessageHandler
    public void handleMessage(ClientSessionToken message) {
        message.dumpToLog(log, "ClientSessionToken:");
    }

    @MessageHandler
    private void handleMessage(ClientUpdateMachineAuth message) throws IOException {
        message.dumpToLog(log, "ClientUpdateMachineAuth:");
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


    private final Connection.CONNECTION_STATE_CHANGED connectionStateHandler = new Connection.CONNECTION_STATE_CHANGED() {
        @Override
        public void handle(ConnectionState newState) throws IOException {
            log.info("new connection state: {}", newState);
            switch (newState) {
                case ESTABLISHED:
                    Event.emit(SteamClient.this, CONNECTED.class);
                    break;
               
                case CONNECTION_TIMEOUT:
                case CLOSED:
                case LOST:
                case BROKEN:
                    Event.deregisterEmitter(connection);
                    connection = null;
                    exitEventLoop = true;
                    break;
                    
                default:
                    break;
            }
        }
    };
    
    private final Connection.MESSAGE_RECEIVED messageReceivedHandler = new Connection.MESSAGE_RECEIVED() {
        @Override
        public void handle(ReceivableMessage<?, ?> message) throws IOException {
            if (!handlerRegistry.handle(message)) {
                log.warn("unhandled message of type {}", MessageRegistry.getEMsgForClass(message.getClass()));
                //message.dumpToLog(log, "");
            }
        }
    };
    
}
