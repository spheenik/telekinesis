package telekinesis;

import telekinesis.registry.CodecRegistry;

public interface SteamClientModule extends ClientMessageHandler {

    CodecRegistry getHandledMessages();

}
