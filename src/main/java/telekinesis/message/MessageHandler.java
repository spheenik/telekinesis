package telekinesis.message;

import java.io.IOException;

public interface MessageHandler<B extends Message<?, ?>> {

    void handleMessage(B message) throws IOException;
}
