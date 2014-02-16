package telekinesis.message.proto;

import java.nio.ByteBuffer;

import telekinesis.message.Message;
import telekinesis.message.MessageRegistry;
import telekinesis.message.MessageRegistry.Def;

import com.google.protobuf.GeneratedMessage;

public class BaseProtoReceivable<H extends GeneratedMessage, B extends GeneratedMessage> extends Message<H, B> {

    @Override
    public void deserialize(ByteBuffer buf) {
        try {
            Def d = MessageRegistry.REGISTRY.get(getEMsg());
            byte[] arr = null;
    
            arr = new byte[buf.getInt()];
            buf.get(arr);
            setHeader((H) d.getHeaderClass().getMethod("parseFrom", byte[].class).invoke(null, arr)); 
    
            arr = new byte[buf.remaining()];
            buf.get(arr);
            setBody((B) d.getBodyClass().getMethod("parseFrom", byte[].class).invoke(null, arr));
            
        } catch (Exception e) {
            throw new RuntimeException(e); 
        } 
    }

    @Override
    public void serialize(ByteBuffer buf) {
        throw new UnsupportedOperationException();
    }


}
