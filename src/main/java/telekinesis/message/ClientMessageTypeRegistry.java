package telekinesis.message;

import telekinesis.model.Header;

public interface ClientMessageTypeRegistry {

    boolean knowsMessageType(int messageType);
    boolean knowsBodyClass(Class<?> bodyClass);

    Class<? extends Header> getHeaderClassForMessageType(int messageType);
    Class<? extends Header> getHeaderClassForBody(Object body);

    Class<?> getBodyClassForMessageType(int messageType);
    Integer getMessageTypeForBody(Object body);

}
