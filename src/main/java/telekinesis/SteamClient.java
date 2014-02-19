package telekinesis;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import telekinesis.connection.Connection;
import telekinesis.connection.ConnectionState;
import telekinesis.event.Event;
import telekinesis.event.Event.EventHandler;
import telekinesis.event.EventEmitter;
import telekinesis.message.TransmittableMessage;

public class SteamClient implements EventEmitter {
    
    public interface POST_CONSTRUCT extends EventHandler.H0 {};
    public interface PRE_DESTROY extends EventHandler.H0 {}; 
    public interface CONNECTED extends EventHandler.H0 {}; 
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private Connection connection = null;
    private boolean exitEventLoop = false;
    
    public void connect() throws IOException {
        connection = new Connection(new InetSocketAddress("146.66.152.12", 27017));
        Event.register(connection, Connection.CONNECTION_STATE_CHANGED.class, connectionStateHandler); 
        connection.connect();
    }
    
    public void disconnect() throws IOException {
        connection.disconnect();
    }

    public void send(TransmittableMessage<?, ?> msg) throws IOException {
        connection.send(msg);
    }
    
    public void run() {
        Event.emit(SteamClient.this, POST_CONSTRUCT.class);
        while(!exitEventLoop) {
            Event.executeNextAction();
        }
        Event.emit(SteamClient.this, PRE_DESTROY.class);
    }
    
    private final Connection.CONNECTION_STATE_CHANGED connectionStateHandler = new Connection.CONNECTION_STATE_CHANGED() {
        @Override
        public void handle(ConnectionState newState) {
            log.info("new connection state: {}", newState);
            switch (newState) {
                case ESTABLISHED:
                    Event.emit(SteamClient.this, CONNECTED.class);
                    break;
               
                case CLOSED:
                case LOST:
                case BROKEN:
                    connection = null;
                    exitEventLoop = true;
                    break;
            }
        }
    };
    

}
