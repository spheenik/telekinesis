package telekinesis.message;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.model.EMsg;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MessageFactory {

    private static final Logger log = LoggerFactory.getLogger(MessageFactory.class);

    private static final Map<EMsg, Def> REGISTRY;
    
    private static class Def {
        private final Class<? extends Header> headerClass;
        private final Class<? extends Message> msgClass;
        public Def(Class<? extends Header> headerClass, Class<? extends Message> msgClass) {
            this.headerClass = headerClass;
            this.msgClass = msgClass;
        }
    }
    
    static {
        REGISTRY = new HashMap<EMsg, Def>();
        Reflections reflections = new Reflections(MessageFactory.class.getPackage().getName());
        for(Class<?> clazz : reflections.getTypesAnnotatedWith(RegisterMessage.class)) {
            RegisterMessage mb = clazz.getAnnotation(RegisterMessage.class);
            REGISTRY.put(mb.type(), new Def(mb.headerClass(), (Class<? extends Message>)clazz));
        }
    }

    
    public static <M extends Message> M forType(EMsg type) {
        Def def = REGISTRY.get(type);
        if (def == null) {
            log.debug("no message definition for type {}", type);
            return null;
        }
        M msg = null;
        try {
            Constructor<? extends Message> c = def.msgClass.getConstructor(EMsg.class, Class.class);
            msg = (M) c.newInstance(type, def.headerClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return msg;
    }
    
    
    public static <M extends Message> M fromByteArray(byte[] data) {
        ByteBuffer msgBuf = ByteBuffer.wrap(data);
        msgBuf.order(ByteOrder.LITTLE_ENDIAN);
        EMsg type = EMsg.f(msgBuf.getInt());
        M msg = forType(type);
        if (msg != null) {
            ((FromWire) msg).fromWire(msgBuf);
        }
        return msg;
    };
    
}
