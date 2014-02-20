package telekinesis.message;

import org.slf4j.Logger;

import ch.qos.logback.classic.Level;

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
    
    public void dumpToLog(Logger log, String text) {
        dumpToLog(Level.INFO, log, text);
    }
    
    public void dumpToLog(Level level, Logger log, String text) {
        String format = "{}\nHEADER:\n{}\nBODY:\n{}";
        switch(level.levelInt) {
            case Level.TRACE_INT:
                log.trace(format, text, getHeader(), getBody());
                break;
            case Level.DEBUG_INT:
                log.debug(format, text, getHeader(), getBody());
                break;
            case Level.INFO_INT:
                log.info(format, text, getHeader(), getBody());
                break;
            case Level.WARN_INT:
                log.warn(format, text, getHeader(), getBody());
                break;
            case Level.ERROR_INT:
                log.error(format, text, getHeader(), getBody());
                break;
        }
    }
    
}
