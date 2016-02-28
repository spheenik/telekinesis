package telekinesis.message;

import com.google.protobuf.GeneratedMessage;
import telekinesis.message.proto.ProtoHeader;
import telekinesis.message.simple.SimpleHeader;
import telekinesis.model.AppId;
import telekinesis.model.Header;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SimpleClientMessageTypeRegistry implements ClientMessageTypeRegistry {

    public static final SimpleClientMessageTypeRegistry EMPTY = new SimpleClientMessageTypeRegistry();

    private Map<TypeKey, Entry> entriesByType = new HashMap<>();
    private Map<ClassKey, Entry> entriesByBodyClass = new HashMap<>();

    public SimpleClientMessageTypeRegistry registerSimple(int messageType, Class<?> bodyClass) {
        return registerMessageType(AppId.STEAM, messageType, SimpleHeader.class, bodyClass);
    }

    public SimpleClientMessageTypeRegistry registerProto(int messageType, Class<? extends GeneratedMessage> bodyClass) {
        return registerMessageType(AppId.STEAM, messageType | MessageFlag.PROTO, ProtoHeader.class, bodyClass);
    }

    public SimpleClientMessageTypeRegistry registerGC(int appId, int messageType, Class<? extends GeneratedMessage> bodyClass) {
        return registerMessageType(appId, messageType | MessageFlag.GC | MessageFlag.PROTO, ProtoHeader.class, bodyClass);
    }

    private SimpleClientMessageTypeRegistry registerMessageType(int appId, int messageType, Class<? extends Header> headerClass, Class<?> bodyClass) {
        Entry e = new Entry(messageType, appId, headerClass, bodyClass);
        entriesByType.put(new TypeKey(appId, messageType), e);
        entriesByBodyClass.put(new ClassKey(appId, bodyClass), e);
        return this;
    }

    @Override
    public boolean knowsMessageType(int appId, int messageType) {
        return entriesByType.containsKey(new TypeKey(appId, messageType));
    }

    @Override
    public boolean knowsBodyClass(int appId, Class<?> bodyClass) {
        return entriesByBodyClass.containsKey(new ClassKey(appId, bodyClass));
    }

    @Override
    public Class<? extends Header> getHeaderClassForMessageType(int appId, int messageType) {
        Entry entry = entriesByType.get(new TypeKey(appId, messageType));
        return entry != null ? entry.headerClass : null;
    }

    @Override
    public Class<? extends Header> getHeaderClassForBody(int appId, Object body) {
        Entry entry = entriesByBodyClass.get(new ClassKey(appId, resolveBodyClass(body)));
        return entry != null ? entry.headerClass : null;
    }

    @Override
    public Class<?> getBodyClassForMessageType(int appId, int messageType) {
        Entry entry = entriesByType.get(new TypeKey(appId, messageType));
        return entry != null ? entry.bodyClass : null;
    }

    @Override
    public Integer getMessageTypeForBody(int appId, Object body) {
        Entry entry = entriesByBodyClass.get(new ClassKey(appId, resolveBodyClass(body)));
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

    private class TypeKey implements Serializable {
        private final int appId;
        private final int messageType;
        public TypeKey(int appId, int messageType) {
            this.appId = appId;
            this.messageType = messageType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeKey typeKey = (TypeKey) o;

            if (appId != typeKey.appId) return false;
            return messageType == typeKey.messageType;

        }

        @Override
        public int hashCode() {
            int result = appId;
            result = 31 * result + messageType;
            return result;
        }
    }

    private class ClassKey implements Serializable {
        private final int appId;
        private final Class<?> bodyClass;
        public ClassKey(int appId, Class<?> bodyClass) {
            this.appId = appId;
            this.bodyClass = bodyClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassKey classKey = (ClassKey) o;

            if (appId != classKey.appId) return false;
            return bodyClass.equals(classKey.bodyClass);

        }

        @Override
        public int hashCode() {
            int result = appId;
            result = 31 * result + bodyClass.hashCode();
            return result;
        }
    }


    private class Entry {
        private final int messageType;
        private final int appId;
        private final Class<? extends Header> headerClass;
        private final Class<?> bodyClass;
        public Entry(int messageType, int appId, Class<? extends Header> headerClass, Class<?> bodyClass) {
            this.messageType = messageType;
            this.appId = appId;
            this.headerClass = headerClass;
            this.bodyClass = bodyClass;
        }
    }

}
