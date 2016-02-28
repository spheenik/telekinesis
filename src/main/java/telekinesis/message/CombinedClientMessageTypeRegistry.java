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
    public boolean knowsMessageType(int appId, int messageType) {
        for (ClientMessageTypeRegistry registry : registries) {
            if (registry.knowsMessageType(appId, messageType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean knowsBodyClass(int appId, Class<?> bodyClass) {
        for (ClientMessageTypeRegistry registry : registries) {
            if (registry.knowsBodyClass(appId, bodyClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class<? extends Header> getHeaderClassForMessageType(int appId, int messageType) {
        for (ClientMessageTypeRegistry registry : registries) {
            Class<? extends Header> result = registry.getHeaderClassForMessageType(appId, messageType);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Class<? extends Header> getHeaderClassForBody(int appId, Object body) {
        for (ClientMessageTypeRegistry registry : registries) {
            Class<? extends Header> result = registry.getHeaderClassForBody(appId, body);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Class<?> getBodyClassForMessageType(int appId, int messageType) {
        for (ClientMessageTypeRegistry registry : registries) {
            Class<?> result = registry.getBodyClassForMessageType(appId, messageType);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Integer getMessageTypeForBody(int appId, Object body) {
        for (ClientMessageTypeRegistry registry : registries) {
            Integer result = registry.getMessageTypeForBody(appId, body);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

}
