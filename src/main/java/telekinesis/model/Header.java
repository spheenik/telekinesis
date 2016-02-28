package telekinesis.model;

public interface Header {

    boolean hasSteamId();
    long getSteamId();
    void setSteamId(long steamId);
    boolean hasSessionId();
    int getSessionId();
    void setSessionId(int sessionId);

    long getSourceJobId();
    void setSourceJobId(long sourceJobId);
    long getTargetJobId();
    void setTargetJobId(long targetJobId);

    void setRoutingAppId(int appId);

}

