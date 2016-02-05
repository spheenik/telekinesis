package telekinesis.registry;

import telekinesis.model.Header;

import java.util.ArrayList;
import java.util.List;

public class CombinedMessageRegistry implements CodecRegistry {

    private final List<CodecRegistry> registries;

    public CombinedMessageRegistry(CodecRegistry... registries) {
        this.registries = new ArrayList<>();
        for (CodecRegistry registry : registries) {
            this.registries.add(registry);
        }
    }

    public void addRegistry(CodecRegistry registry) {
        this.registries.add(registry);
    }

    @Override
    public boolean knowsMessageType(int messageType) {
        for (CodecRegistry registry : registries) {
            if (registry.knowsMessageType(messageType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean knowsBodyClass(Class<?> bodyClass) {
        for (CodecRegistry registry : registries) {
            if (registry.knowsBodyClass(bodyClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class<? extends Header> getHeaderClassForMessageType(int messageType) {
        for (CodecRegistry registry : registries) {
            Class<? extends Header> result = registry.getHeaderClassForMessageType(messageType);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Class<? extends Header> getHeaderClassForBody(Object body) {
        for (CodecRegistry registry : registries) {
            Class<? extends Header> result = registry.getHeaderClassForBody(body);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Class<?> getBodyClassForMessageType(int messageType) {
        for (CodecRegistry registry : registries) {
            Class<?> result = registry.getBodyClassForMessageType(messageType);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Integer getMessageTypeForBody(Object body) {
        for (CodecRegistry registry : registries) {
            Integer result = registry.getMessageTypeForBody(body);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

}
