package telekinesis.registry;

import telekinesis.model.Header;

public interface CodecRegistry {

    boolean knowsMessageType(int messageType);
    boolean knowsBodyClass(Class<?> bodyClass);

    Class<? extends Header> getHeaderClassForMessageType(int messageType);
    Class<? extends Header> getHeaderClassForBody(Object body);

    Class<?> getBodyClassForMessageType(int messageType);
    Integer getMessageTypeForBody(Object body);

}
