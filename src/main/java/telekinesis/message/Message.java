package telekinesis.message;

import telekinesis.model.EMsg;

public class Message {

    private final EMsg type;
    private final Header header;
    private final Body body;
    
    public Message(EMsg type, Header header, Body body) {
        this.type = type;
        this.header = header;
        this.body = body;
    }
    
    public EMsg getType() {
        return type;
    }

    public Header getHeader() {
        return header;
    }
    
    public Body getBody() {
        return body;
    }
    
}
