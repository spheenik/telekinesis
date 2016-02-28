package telekinesis.connection;

import telekinesis.model.Header;

public class Message {

    private final int appId;
    private final Header header;
    private final Object body;

    public Message(int appId, Header header, Object body) {
        this.appId = appId;
        this.header = header;
        this.body = body;
    }

    public int getAppId() {
        return appId;
    }

    public Header getHeader() {
        return header;
    }

    public Object getBody() {
        return body;
    }

    public Message withReplacedBody(Object newBody) {
        return new Message(appId, header, newBody);
    }

}
