package telekinesis.message;

import telekinesis.model.EMsg;

public class Message<H extends Header> {

    private final EMsg type;
    private final H header;

    public Message(EMsg type, Class<H> headerClass) {
        this.type = type;
        try {
            this.header = headerClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public EMsg getType() {
        return type;
    }

    public H getHeader() {
        return header;
    }

}
