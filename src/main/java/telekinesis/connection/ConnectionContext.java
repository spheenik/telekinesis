package telekinesis.connection;

import telekinesis.model.SteamID;

public class ConnectionContext {

    private SteamID steamID = new SteamID();
    private int sessionId;
    
    public SteamID getSteamID() {
        return steamID;
    }
    
    public void setSteamID(SteamID steamID) {
        this.steamID = steamID;
    }
    
    public int getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
    
}
