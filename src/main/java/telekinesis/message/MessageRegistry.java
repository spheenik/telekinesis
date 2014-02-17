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

import telekinesis.annotations.RegisterMessage;
import telekinesis.message.proto.BaseProto;
import telekinesis.message.proto.generated.SteammessagesBase;
import telekinesis.model.EMsg;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MessageRegistry {

    private static final Logger log = LoggerFactory.getLogger(MessageRegistry.class);

    private static final Map<EMsg, Class<? extends AbstractMessage>> TYPE_BY_EMSG;
    private static final Map<Class<? extends AbstractMessage>, EMsg> EMSG_BY_TYPE;

    static {
        TYPE_BY_EMSG = new HashMap<EMsg, Class<? extends AbstractMessage>>();
        EMSG_BY_TYPE = new HashMap<Class<? extends AbstractMessage>, EMsg>();

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

        for (Class<?> clazz : reflections.getTypesAnnotatedWith(RegisterMessage.class)) {
            RegisterMessage mb = clazz.getAnnotation(RegisterMessage.class);
            TYPE_BY_EMSG.put(mb.value(), (Class<? extends AbstractMessage>) clazz);
            EMSG_BY_TYPE.put((Class<? extends AbstractMessage>) clazz, mb.value());
        }
    }

    public static int getWireCodeForClass(Class msg) {
        EMsg eMsg = EMSG_BY_TYPE.get(msg);
        if (eMsg == null) {
            throw new RuntimeException("EMsg for given class not found!");
        }
        return BaseProto.class.isAssignableFrom(msg) ? eMsg.v() | 0x80000000 : eMsg.v();
    }
    
    public static <M extends AbstractMessage> M forEMsg(EMsg eMsg) {
        Class<? extends AbstractMessage> clazz = TYPE_BY_EMSG.get(eMsg);
        if (clazz == null) {
            log.warn("no message definition for {}", eMsg);
            return null;
        }
        M msg = null;
        try {
            msg = (M) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return msg;
    }

}
