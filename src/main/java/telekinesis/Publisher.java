package telekinesis;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Publisher<C> {

    private final Map<Class, Set> subscribers = new HashMap<>();

    private <E> Set<Handler<C, E>> getHandlers(Class<E> eventClass) {
        return subscribers.get(eventClass);
    }

    public <E> void subscribe(Class<E> eventClass, Handler<C, E> handler) {
        Set<Handler<C, E>> handlers = getHandlers(eventClass);
        if (handlers == null) {
            handlers = new LinkedHashSet<>();
            subscribers.put(eventClass, handlers);
        }
        handlers.add(handler);
    }

    public <E> void unsubscribe(Class<E> eventClass, Handler<C, E> handler) {
        Set<Handler<C, E>> handlers = getHandlers(eventClass);
        handlers.remove(handler);
        if (handlers.size() == 0) {
            subscribers.remove(handlers);
        }
    }

    protected <E> boolean isSubscribed(E event) {
        Set<Handler<C, E>> handlers = getHandlers((Class<E>)event.getClass());
        return handlers != null;
    }

    protected <E> void publish(C ctx, E event) {
        Set<Handler<C, E>> handlers = getHandlers((Class<E>)event.getClass());
        if (handlers == null) {
            return;
        }
        for (Handler<C, E> handler : handlers) {
            try {
                handler.handle(ctx, event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface Handler<C, P> {
        void handle(C ctx, P payload) throws Exception;
    }

}
