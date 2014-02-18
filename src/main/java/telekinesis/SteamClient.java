package telekinesis;

import java.io.IOException;
import java.net.InetSocketAddress;

import telekinesis.connection.Connection;
import telekinesis.event.Event;
import telekinesis.event.Event.EventHandler;
import telekinesis.event.EventEmitter;
import telekinesis.message.TransmittableMessage;

public class SteamClient implements EventEmitter, Runnable {
    
    public interface POST_CONSTRUCT extends EventHandler.H0 {};
    public interface PRE_DESTROY extends EventHandler.H0 {}; 
    
    public <C extends Event.EventHandler> void on(Class<C> handlerClass, C handler) {
        Event.register(this, handlerClass, handler);
    }
    
    private Connection connection = null;
    
    public void connect() throws IOException {
        connection = new Connection(new InetSocketAddress("146.66.152.12", 27017));
        Event.passthrough(connection, Connection.CONNECTION_ESTABLISHED.class, this); 
        connection.connect();
    }
    
    public void disconnect() throws IOException {
        connection.disconnect();
    }

    public void send(TransmittableMessage<?, ?> msg) throws IOException {
        connection.send(msg);
    }
    
    public void run() {
        Event.emit(this, POST_CONSTRUCT.class);
        Event.eventLoop();
        Event.emit(this, PRE_DESTROY.class);
    }
    

}
