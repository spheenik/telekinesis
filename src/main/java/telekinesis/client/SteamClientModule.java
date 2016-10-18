package telekinesis.client;


import telekinesis.message.ClientMessageTypeRegistry;
import telekinesis.model.AppId;
import telekinesis.model.ClientMessageHandler;
import telekinesis.util.Publisher;

public abstract class SteamClientModule<C> extends Publisher<C> implements ClientMessageHandler {

    protected final int appId;
    protected SteamClient steamClient;

    public abstract ClientMessageTypeRegistry getHandledMessages();

    public SteamClientModule() {
        this(AppId.STEAM);
    }

    public SteamClientModule(int appId) {
        this.appId = appId;
    }

    public int getAppId() {
        return appId;
    }

    public SteamClient getSteamClient() {
        return steamClient;
    }

    public void setSteamClient(SteamClient steamClient) {
        this.steamClient = steamClient;
    }

}
