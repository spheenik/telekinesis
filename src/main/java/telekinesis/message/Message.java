package telekinesis.message;

import telekinesis.model.EMsg;

public abstract class Message<H, B> implements FromWire, ToWire {

    private EMsg type;
    private H header;
    private B body;

    public void setType(EMsg type) {
        this.type = type;
    }

    public EMsg getType() {
        return type;
    }

    public H getHeader() {
        return header;
    }

    public void setHeader(H header) {
        this.header = header;
    }
    
    public B getBody() {
        return body;
    }

    public void setBody(B body) {
        this.body = body;
    }
    
}
