package telekinesis.message;

import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import telekinesis.message.annotations.RegisterMessage;
import telekinesis.message.proto.generated.SteammessagesBase;
import telekinesis.model.EMsg;

import com.google.protobuf.GeneratedMessage;

@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class MessageRegistry {

    private static final Logger log = LoggerFactory.getLogger(MessageRegistry.class);

    public static final Map<EMsg, Def> REGISTRY;
    
    public static class Def {
        private final Class<? extends Message> msgClass;
        private final Class<?> headerClass;
        private final Class<?> bodyClass;
        public Def(Class<? extends Message> msgClass, Class<?> headerClass, Class<?> bodyClass) {
            this.msgClass = msgClass;
            this.headerClass = headerClass;
            this.bodyClass = bodyClass;
        }
        public boolean isProtoBufBuilder() {
            return GeneratedMessage.Builder.class.isAssignableFrom(headerClass);
        }
        public boolean isProtoBuf() {
            return GeneratedMessage.class.isAssignableFrom(headerClass) || GeneratedMessage.Builder.class.isAssignableFrom(headerClass);
        }
        public Class<? extends Message> getMsgClass() {
            return msgClass;
        }
        public Class<?> getHeaderClass() {
            return headerClass;
        }
        public Class<?> getBodyClass() {
            return bodyClass;
        }
        
    }
    
    static {
        REGISTRY = new HashMap<EMsg, Def>();
        
        Reflections reflections = new Reflections(new ConfigurationBuilder()
            .filterInputsBy(
                new FilterBuilder()
                .excludePackage(SteammessagesBase.class)
            )
            .setUrls(
                ClasspathHelper.forPackage(MessageRegistry.class.getPackage().getName())
            )
            .setScanners(
                new TypeAnnotationsScanner()
            )
        );
        for(Class<?> clazz : reflections.getTypesAnnotatedWith(RegisterMessage.class)) {
            RegisterMessage mb = clazz.getAnnotation(RegisterMessage.class);
            REGISTRY.put(mb.type(), new Def((Class<? extends Message>)clazz, mb.headerClass(), mb.bodyClass()));
        }
    }
            
}
