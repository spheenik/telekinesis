package telekinesis.message.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import telekinesis.message.Header;
import telekinesis.model.EMsg;

@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.TYPE)
public @interface RegisterMessage {
    EMsg type();
    Class<? extends Header> headerClass();
}
