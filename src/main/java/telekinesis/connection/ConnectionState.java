package telekinesis.connection;

public enum ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTION_FAILED,
    CONNECTED,
    ESTABLISHED,
    DISCONNECTING,
    CLOSED,
    LOST,
    BROKEN
}
