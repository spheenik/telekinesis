package telekinesis.message;

import org.slf4j.Logger;

public interface Message<H, B> {

    public H getHeader();
    public void setHeader(H header);
    
    public B getBody();
    public void setBody(B body);
    
    public void dumpToLog(Logger log, String prefix);
 
}
