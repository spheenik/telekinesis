package telekinesis.client;


import telekinesis.message.ClientMessageTypeRegistry;
import telekinesis.model.ClientMessageHandler;

public abstract class SteamClientModule implements ClientMessageHandler {

    protected SteamClient steamClient;

    public abstract ClientMessageTypeRegistry getHandledMessages();

    public SteamClient getSteamClient() {
        return steamClient;
    }

    public void setSteamClient(SteamClient steamClient) {
        this.steamClient = steamClient;
    }

}
