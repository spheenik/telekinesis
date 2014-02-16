package telekinesis.message;


public abstract class Message<H, B> {

    private H header;
    private B body;
    
    protected abstract void constructHeader();
    protected abstract void constructBody();
    
    public Message() {
        constructHeader();
        constructBody();
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
    
    public <M extends Message<?, ?>> M asResponseFor(Message<?, ?> request) {
        return (M) this;
    }
    
}
