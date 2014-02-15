package telekinesis.message;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.model.EMsg;

import com.google.protobuf.GeneratedMessage;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MessageFactory {

    private static final Logger log = LoggerFactory.getLogger(MessageFactory.class);

    private static final Map<EMsg, Def> REGISTRY;
    
    private static class Def {
        private final Class<? extends Message> msgClass;
        private final Class<?> headerClass;
        private final Class<?> bodyClass;
        public Def(Class<? extends Message> msgClass, Class<?> headerClass, Class<?> bodyClass) {
            this.msgClass = msgClass;
            this.headerClass = headerClass;
            this.bodyClass = bodyClass;
        }
    }
    
    static {
        REGISTRY = new HashMap<EMsg, Def>();
        Reflections reflections = new Reflections(MessageFactory.class.getPackage().getName());
        for(Class<?> clazz : reflections.getTypesAnnotatedWith(RegisterMessage.class)) {
            RegisterMessage mb = clazz.getAnnotation(RegisterMessage.class);
            REGISTRY.put(mb.type(), new Def((Class<? extends Message>)clazz, mb.headerClass(), mb.bodyClass()));
        }
    }
    
    private static Object instantiate(Class clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
        if (GeneratedMessage.class.isAssignableFrom(clazz)) {
            return null;
        } else if (GeneratedMessage.Builder.class.isAssignableFrom(clazz)) {
            return clazz.getEnclosingClass().getMethod("newInstance").invoke(null);
        } else {
            return clazz.newInstance();
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
            msg = (M) def.msgClass.newInstance();
            msg.setType(type);
            msg.setHeader(instantiate(def.headerClass));
            msg.setBody(instantiate(def.bodyClass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return msg;
    }
        
}
