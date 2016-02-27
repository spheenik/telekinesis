package telekinesis.connection;

public class ClientMessageContext {

    private SteamConnection steamConnection;
    private final long targetJobId;

    ClientMessageContext(SteamConnection steamConnection, long targetJobId) {
        this.steamConnection = steamConnection;
        this.targetJobId = targetJobId;
    }

    public void reply(Object body) {
        steamConnection.send(targetJobId, body);
    }

}
