package telekinesis.connection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.ReflectionUtils;

import telekinesis.annotations.Handler;
import telekinesis.message.ReceivableMessage;

public class HandlerRegistry {
    
    private static final Map<Class<?>, Map<Class<? extends ReceivableMessage<?, ?>>, Method>> DEFINITION = new HashMap<Class<?>, Map<Class<? extends ReceivableMessage<?, ?>>, Method>>() {
        private static final long serialVersionUID = -9163157001989000810L;
        @Override
        @SuppressWarnings("unchecked")
        public Map<Class<? extends ReceivableMessage<?, ?>>, Method> get(Object keyObj) {
            Class<?> key = (Class<?>) keyObj; 
            Map<Class<? extends ReceivableMessage<?, ?>>, Method> result = super.get(key);
            if (result == null) {
                result = new HashMap<Class<? extends ReceivableMessage<?, ?>>, Method>();
                Set<Method> handlers = ReflectionUtils.getAllMethods(key, ReflectionUtils.withAnnotation(Handler.class));
                for (Method h : handlers) {
                    Class<? extends ReceivableMessage<?, ?>> handledMessageClass = (Class<? extends ReceivableMessage<?, ?>>) h.getParameterTypes()[0];
                    h.setAccessible(true);
                    result.put(handledMessageClass, h);
                }
                put(key, result);
            }
            return result;
        }
    };
    
    private class Invoker {
        
        private final Object instance;
        private final Method method;
        
        private Invoker(Object instance, Method method) {
            this.instance = instance;
            this.method = method;
        }
        private void invoke(ReceivableMessage<?, ?> message) {
            try {
                method.invoke(instance, message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private final Map<Class<? extends ReceivableMessage<?, ?>>, Invoker> invokers = new HashMap<Class<? extends ReceivableMessage<?, ?>>, Invoker>();
    
    public void addInstance(final Object instance) {
        Map<Class<? extends ReceivableMessage<?, ?>>, Method> def = DEFINITION.get(instance.getClass());
        for (Map.Entry<Class<? extends ReceivableMessage<?, ?>>, Method> e : def.entrySet()) {
            if (invokers.containsKey(e.getKey())) {
                throw new RuntimeException("handler for " + e.getKey().getName() + " already set");
            }
            invokers.put(e.getKey(), new Invoker(instance, e.getValue()));
        }
    }
    
    public boolean handle(ReceivableMessage<?, ?> message) {
        Invoker i = invokers.get(message.getClass());
        if (i != null) {
            i.invoke(message);
        }
        return i != null;
    }
    
}
