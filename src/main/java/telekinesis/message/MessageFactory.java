package telekinesis.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import telekinesis.message.annotations.MessageBody;
import telekinesis.model.EMsg;

public class MessageFactory {

    private static final Logger log = LoggerFactory.getLogger(MessageFactory.class);
    
    private static class Def {
        private final EMsg type;
        private final Class<? extends Header> headerClass;
        private final Class<? extends Body> bodyClass;
        public Def(EMsg type, Class<? extends Header> headerClass, Class<? extends Body> bodyClass) {
            this.type = type;
            this.headerClass = headerClass;
            this.bodyClass = bodyClass;
        }
    }
    
    private static final Map<EMsg, Def> REGISTRY;
    
    static {
        REGISTRY = new HashMap<EMsg, Def>();
        Reflections reflections = new Reflections(MessageFactory.class.getPackage().getName());
        for(Class<?> clazz : reflections.getTypesAnnotatedWith(MessageBody.class)) {
            MessageBody mb = clazz.getAnnotation(MessageBody.class);
            REGISTRY.put(mb.type(), new Def(mb.type(), mb.headerClass(), (Class<? extends Body>)clazz));
        }
    }
    
    public static Message build(byte[] data) {
        ByteBuffer msgBuf = ByteBuffer.wrap(data);
        msgBuf.order(ByteOrder.LITTLE_ENDIAN);
        EMsg type = EMsg.f(msgBuf.getInt());
        Def def = REGISTRY.get(type);
        if (def == null) {
            log.debug("no message definition for type {}", type);
            return null;
        }
        Header header = null;
        try {
            header = (Header) def.headerClass.newInstance();
            ((FromWire) header).fromWire(msgBuf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Body body = null;
        try {
            body = (Body) def.bodyClass.newInstance();
            ((FromWire) body).fromWire(msgBuf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Message(type, header, body);
    };
}
