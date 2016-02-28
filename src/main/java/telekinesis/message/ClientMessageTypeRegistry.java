package telekinesis.message;

import telekinesis.model.Header;

public interface ClientMessageTypeRegistry {

    boolean knowsMessageType(int appId, int messageType);
    boolean knowsBodyClass(int appId, Class<?> bodyClass);

    Class<? extends Header> getHeaderClassForMessageType(int appId, int messageType);
    Class<? extends Header> getHeaderClassForBody(int appId, Object body);

    Class<?> getBodyClassForMessageType(int appId, int messageType);
    Integer getMessageTypeForBody(int appId, Object body);

}
