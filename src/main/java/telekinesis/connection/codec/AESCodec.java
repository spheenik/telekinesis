package telekinesis.connection.codec;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import telekinesis.connection.Util;
import telekinesis.message.MessageRegistry;
import telekinesis.message.ReceivableMessage;
import telekinesis.message.TransmittableMessage;
import telekinesis.model.EMsg;

public class AESCodec extends MessageCodec {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public static int BLOCK_SIZE = 128;
    public static int KEY_SIZE = 256;
    
    private final Key key;
    
    public AESCodec() throws IOException {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES", "BC");
            generator.init(KEY_SIZE);
            key = generator.generateKey();
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    public void toWire(TransmittableMessage<?, ?> msg, ByteBuffer dstBuf) throws IOException {
        try {
            Cipher cIv = Cipher.getInstance("AES/ECB/NoPadding", "BC");
            cIv.init(Cipher.ENCRYPT_MODE, key);
            Cipher cMain = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cMain.init(Cipher.ENCRYPT_MODE, key);

            ByteBuffer ivBuf = ByteBuffer.wrap(cMain.getParameters().getParameterSpec(IvParameterSpec.class).getIV());
            
            ByteBuffer msgBuf = Util.getNewBuffer();
            msgBuf.putInt(MessageRegistry.getWireCodeForClass(msg.getClass()));
            msg.encodeTo(msgBuf);
            msgBuf.flip();
            
            cIv.doFinal(ivBuf, dstBuf);
            cMain.doFinal(msgBuf, dstBuf);
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    public ReceivableMessage<?, ?> fromWire(ByteBuffer srcBuf) throws IOException {
        try {
            Cipher cIv = Cipher.getInstance("AES/ECB/NoPadding", "BC");
            cIv.init(Cipher.DECRYPT_MODE, key);
            byte[] cryptedIv = new byte[BLOCK_SIZE>>3];
            srcBuf.get(cryptedIv);
            IvParameterSpec iv = new IvParameterSpec(cIv.doFinal(cryptedIv));
            
            Cipher cMain = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cMain.init(Cipher.DECRYPT_MODE, key, iv);
            ByteBuffer msgBuf = Util.getNewBuffer();
            cMain.doFinal(srcBuf, msgBuf);
            
            msgBuf.flip();
            EMsg eMsg = EMsg.f(msgBuf.getInt());
            log.debug("got encrypted message of type {}", eMsg);
            ReceivableMessage<?, ?> msg = (ReceivableMessage<?, ?>) MessageRegistry.forEMsg(eMsg);
            if (msg != null) {
                msg.decodeFrom(msgBuf);
            }                
            return msg;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    public byte[] getEncodedKey() {
        return key.getEncoded();
    }
    
}