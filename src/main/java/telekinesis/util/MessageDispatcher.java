package telekinesis.util;

import telekinesis.connection.ClientMessageContext;
import telekinesis.model.AppId;
import telekinesis.model.ClientMessageHandler;

public class MessageDispatcher extends Publisher<ClientMessageContext> implements ClientMessageHandler {

    private final int appId;

    public MessageDispatcher() {
        this(AppId.STEAM);
    }

    public MessageDispatcher(int appId) {
        this.appId = appId;
    }

    @Override
    public void handleClientMessage(ClientMessageContext ctx, Object message) {
        if (ctx.getAppId() == appId) {
            publish(ctx, message);
        }
    }

}
