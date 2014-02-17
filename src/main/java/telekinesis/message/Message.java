package telekinesis.message;

public interface Message<H, B> {

    public H getHeader();
    public void setHeader(H header);
    
    public B getBody();
    public void setBody(B body);
    
}
