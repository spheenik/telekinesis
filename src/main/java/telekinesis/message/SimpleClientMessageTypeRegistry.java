package telekinesis.message;

import com.google.protobuf.GeneratedMessage;
import telekinesis.message.proto.ProtoHeader;
import telekinesis.message.simple.SimpleHeader;
import telekinesis.model.Header;

import java.util.HashMap;
import java.util.Map;

public class SimpleClientMessageTypeRegistry implements ClientMessageTypeRegistry {

    private Map<Integer, Entry> entriesByType = new HashMap<>();
    private Map<Class<?>, Entry> entriesByBodyClass = new HashMap<>();

    public SimpleClientMessageTypeRegistry registerSimple(int messageType, Class<?> bodyClass) {
        Entry e = new Entry(messageType, SimpleHeader.class, bodyClass);
        entriesByType.put(messageType, e);
        entriesByBodyClass.put(bodyClass, e);
        return this;
    }

    public SimpleClientMessageTypeRegistry registerProto(int messageType, Class<? extends GeneratedMessage> bodyClass) {
        Entry e = new Entry(messageType, ProtoHeader.class, bodyClass);
        entriesByType.put(messageType, e);
        entriesByBodyClass.put(bodyClass, e);
        return this;
    }

    @Override
    public boolean knowsMessageType(int messageType) {
        return entriesByType.containsKey(messageType);
    }

    @Override
    public boolean knowsBodyClass(Class<?> bodyClass) {
        return entriesByBodyClass.containsKey(bodyClass);
    }

    @Override
    public Class<? extends Header> getHeaderClassForMessageType(int messageType) {
        Entry entry = entriesByType.get(messageType);
        return entry != null ? entry.headerClass : null;
    }

    @Override
    public Class<? extends Header> getHeaderClassForBody(Object body) {
        Entry entry = entriesByBodyClass.get(resolveBodyClass(body));
        return entry != null ? entry.headerClass : null;
    }

    @Override
    public Class<?> getBodyClassForMessageType(int messageType) {
        Entry entry = entriesByType.get(messageType);
        return entry != null ? entry.bodyClass : null;
    }

    @Override
    public Integer getMessageTypeForBody(Object body) {
        Entry entry = entriesByBodyClass.get(resolveBodyClass(body));
        return entry != null ? entry.messageType : null;
    }

    protected Class<?> resolveBodyClass(Object body) {
        Class<?> bodyClass = body.getClass();
        if (GeneratedMessage.Builder.class.isAssignableFrom(bodyClass)) {
            return ((GeneratedMessage.Builder) body).getDefaultInstanceForType().getClass();
        } else {
            return bodyClass;
        }
    }

    private class Entry {
        private final int messageType;
        private final Class<? extends Header> headerClass;
        private final Class<?> bodyClass;
        public Entry(int messageType, Class<? extends Header> headerClass, Class<?> bodyClass) {
            this.messageType = messageType;
            this.headerClass = headerClass;
            this.bodyClass = bodyClass;
        }
    }

}
