package telekinesis.connection;

public class ClientMessageContext {

    private SteamConnection steamConnection;
    private final long sourceJobId;
    private final long targetJobId;

    ClientMessageContext(SteamConnection steamConnection, long sourceJobId, long targetJobId) {
        this.steamConnection = steamConnection;
        this.sourceJobId = sourceJobId;
        this.targetJobId = targetJobId;
    }

    public long getSourceJobId() {
        return sourceJobId;
    }

    public long getTargetJobId() {
        return targetJobId;
    }

    public void reply(Object body) {
        steamConnection.reply(sourceJobId, body);
    }

}
