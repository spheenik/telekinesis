package telekinesis.model.steam;

public enum EIntroducerRouting {
    FileShare(0),
    P2PVoiceChat(1),
    P2PNetworking(2);

    private int code;

    private EIntroducerRouting(int code) {
        this.code = code;
    }

    public int v() {
        return code;
    }
}
