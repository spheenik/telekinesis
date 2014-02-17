package telekinesis.message;


public abstract class AbstractMessage<H, B> implements Message<H, B> {

    private H header;
    private B body;
    
    protected abstract void constructHeader();
    protected abstract void constructBody();
    
    public AbstractMessage() {
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
    
}
