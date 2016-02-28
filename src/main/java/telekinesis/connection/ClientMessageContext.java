package telekinesis.connection;

public class ClientMessageContext {

    private SteamConnection steamConnection;
    private final int appId;
    private final long sourceJobId;
    private final long targetJobId;

    ClientMessageContext(SteamConnection steamConnection, int appId, long sourceJobId, long targetJobId) {
        this.steamConnection = steamConnection;
        this.appId = appId;
        this.sourceJobId = sourceJobId;
        this.targetJobId = targetJobId;
    }

    public long getSourceJobId() {
        return sourceJobId;
    }

    public long getTargetJobId() {
        return targetJobId;
    }

    public int getAppId() {
        return appId;
    }

    public void reply(Object body) {
        steamConnection.reply(appId, sourceJobId, body);
    }

}
