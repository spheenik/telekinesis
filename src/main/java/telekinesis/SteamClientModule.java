package telekinesis;


import telekinesis.message.ClientMessageTypeRegistry;

public interface SteamClientModule extends ClientMessageHandler {

    ClientMessageTypeRegistry getHandledMessages();

}
