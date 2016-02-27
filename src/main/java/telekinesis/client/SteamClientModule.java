package telekinesis.client;


import telekinesis.message.ClientMessageTypeRegistry;
import telekinesis.model.ClientMessageHandler;

public interface SteamClientModule extends ClientMessageHandler {

    ClientMessageTypeRegistry getHandledMessages();

}
