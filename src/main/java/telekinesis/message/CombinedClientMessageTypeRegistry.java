package telekinesis.message;

import telekinesis.model.Header;

import java.util.ArrayList;
import java.util.List;

public class CombinedClientMessageTypeRegistry implements ClientMessageTypeRegistry {

    private final List<ClientMessageTypeRegistry> registries;

    public CombinedClientMessageTypeRegistry(ClientMessageTypeRegistry... registries) {
        this.registries = new ArrayList<>();
        for (ClientMessageTypeRegistry registry : registries) {
            this.registries.add(registry);
        }
    }

    public void addRegistry(ClientMessageTypeRegistry registry) {
        this.registries.add(registry);
    }

    public void removeRegistry(ClientMessageTypeRegistry registry) {
        this.registries.remove(registry);
    }

    @Override
    public boolean knowsMessageType(int messageType) {
        for (ClientMessageTypeRegistry registry : registries) {
            if (registry.knowsMessageType(messageType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean knowsBodyClass(Class<?> bodyClass) {
        for (ClientMessageTypeRegistry registry : registries) {
            if (registry.knowsBodyClass(bodyClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class<? extends Header> getHeaderClassForMessageType(int messageType) {
        for (ClientMessageTypeRegistry registry : registries) {
            Class<? extends Header> result = registry.getHeaderClassForMessageType(messageType);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Class<? extends Header> getHeaderClassForBody(Object body) {
        for (ClientMessageTypeRegistry registry : registries) {
            Class<? extends Header> result = registry.getHeaderClassForBody(body);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Class<?> getBodyClassForMessageType(int messageType) {
        for (ClientMessageTypeRegistry registry : registries) {
            Class<?> result = registry.getBodyClassForMessageType(messageType);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Integer getMessageTypeForBody(Object body) {
        for (ClientMessageTypeRegistry registry : registries) {
            Integer result = registry.getMessageTypeForBody(body);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

}
