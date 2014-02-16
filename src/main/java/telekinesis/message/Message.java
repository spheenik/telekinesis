package telekinesis.message;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import telekinesis.message.MessageRegistry.Def;
import telekinesis.model.EMsg;

import com.google.protobuf.GeneratedMessage;

public abstract class Message<H, B> implements FromWire, ToWire {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    public static final int ProtoMask = 0x80000000;
    public static final int EMsgMask = ~ProtoMask;
    
    private int code;
    private H header;
    private B body;

    public void setCode(int type) {
        this.code = type;
    }

    public int getCode() {
        return code;
    }
    
    public EMsg getEMsg() {
        return EMsg.f(code);
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
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object instantiate(Class clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
        if (GeneratedMessage.class.isAssignableFrom(clazz)) {
            return null;
        } else if (GeneratedMessage.Builder.class.isAssignableFrom(clazz)) {
            return clazz.getEnclosingClass().getMethod("newBuilder").invoke(null);
        } else {
            return clazz.newInstance();
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <M extends Message> M forEMsg(EMsg eMsg) {
        Def def = MessageRegistry.REGISTRY.get(eMsg);
        if (def == null) {
            return null;
        }
        M msg = null;
        try {
            msg = (M) def.getMsgClass().newInstance();
            int code = eMsg.v();
            if (def.isProtoBuf()) {
                code |= ProtoMask;
            }
            msg.setCode(code);
            msg.setHeader(instantiate(def.getHeaderClass()));
            msg.setBody(instantiate(def.getBodyClass()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return msg;
    }
    
}
