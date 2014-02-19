package telekinesis.connection;

public enum ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTION_TIMEOUT,
    CONNECTED,
    ESTABLISHED,
    DISCONNECTING,
    CLOSED,
    LOST,
    BROKEN
}
