package telekinesis.message;

public interface MessageHandler<B extends Message<?>> {

    void handleMessage(B message);
}
