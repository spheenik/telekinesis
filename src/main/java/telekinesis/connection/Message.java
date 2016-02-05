package telekinesis.connection;

import telekinesis.model.Header;

public class Message {

    private final Header header;
    private final Object body;

    public Message(Header header, Object body) {
        this.header = header;
        this.body = body;
    }

    public Header getHeader() {
        return header;
    }

    public Object getBody() {
        return body;
    }
}
