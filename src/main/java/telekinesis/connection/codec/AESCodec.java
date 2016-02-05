package telekinesis.connection.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import telekinesis.model.steam.EUniverse;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.ByteOrder;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class AESCodec extends ChannelDuplexHandler {

    static Map<EUniverse, byte[]> UNIVERSE_PUBLIC_KEYS = new HashMap<>();

    static {
        Security.addProvider(new BouncyCastleProvider());

        UNIVERSE_PUBLIC_KEYS.put(EUniverse.Invalid, null);

        UNIVERSE_PUBLIC_KEYS.put(EUniverse.Public, new byte[] { (byte) 0x30, (byte) 0x81, (byte) 0x9D, (byte) 0x30, (byte) 0x0D, (byte) 0x06, (byte) 0x09, (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xF7, (byte) 0x0D, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x81, (byte) 0x8B, (byte) 0x00, (byte) 0x30, (byte) 0x81, (byte) 0x87, (byte) 0x02, (byte) 0x81, (byte) 0x81, (byte) 0x00, (byte) 0xDF, (byte) 0xEC, (byte) 0x1A, (byte) 0xD6, (byte) 0x2C, (byte) 0x10, (byte) 0x66, (byte) 0x2C, (byte) 0x17, (byte) 0x35, (byte) 0x3A, (byte) 0x14, (byte) 0xB0, (byte) 0x7C, (byte) 0x59, (byte) 0x11, (byte) 0x7F, (byte) 0x9D, (byte) 0xD3, (byte) 0xD8, (byte) 0x2B, (byte) 0x7A, (byte) 0xE3, (byte) 0xE0, (byte) 0x15, (byte) 0xCD, (byte) 0x19, (byte) 0x1E, (byte) 0x46, (byte) 0xE8, (byte) 0x7B, (byte) 0x87, (byte) 0x74, (byte) 0xA2, (byte) 0x18, (byte) 0x46, (byte) 0x31, (byte) 0xA9, (byte) 0x03, (byte) 0x14, (byte) 0x79, (byte) 0x82, (byte) 0x8E,
                (byte) 0xE9, (byte) 0x45, (byte) 0xA2, (byte) 0x49, (byte) 0x12, (byte) 0xA9, (byte) 0x23, (byte) 0x68, (byte) 0x73, (byte) 0x89, (byte) 0xCF, (byte) 0x69, (byte) 0xA1, (byte) 0xB1, (byte) 0x61, (byte) 0x46, (byte) 0xBD, (byte) 0xC1, (byte) 0xBE, (byte) 0xBF, (byte) 0xD6, (byte) 0x01, (byte) 0x1B, (byte) 0xD8, (byte) 0x81, (byte) 0xD4, (byte) 0xDC, (byte) 0x90, (byte) 0xFB, (byte) 0xFE, (byte) 0x4F, (byte) 0x52, (byte) 0x73, (byte) 0x66, (byte) 0xCB, (byte) 0x95, (byte) 0x70, (byte) 0xD7, (byte) 0xC5, (byte) 0x8E, (byte) 0xBA, (byte) 0x1C, (byte) 0x7A, (byte) 0x33, (byte) 0x75, (byte) 0xA1, (byte) 0x62, (byte) 0x34, (byte) 0x46, (byte) 0xBB, (byte) 0x60, (byte) 0xB7, (byte) 0x80, (byte) 0x68, (byte) 0xFA, (byte) 0x13, (byte) 0xA7, (byte) 0x7A, (byte) 0x8A, (byte) 0x37, (byte) 0x4B, (byte) 0x9E, (byte) 0xC6, (byte) 0xF4, (byte) 0x5D, (byte) 0x5F, (byte) 0x3A, (byte) 0x99, (byte) 0xF9, (byte) 0x9E, (byte) 0xC4, (byte) 0x3A, (byte) 0xE9, (byte) 0x63, (byte) 0xA2,
                (byte) 0xBB, (byte) 0x88, (byte) 0x19, (byte) 0x28, (byte) 0xE0, (byte) 0xE7, (byte) 0x14, (byte) 0xC0, (byte) 0x42, (byte) 0x89, (byte) 0x02, (byte) 0x01, (byte) 0x11 });

        UNIVERSE_PUBLIC_KEYS.put(EUniverse.Beta, new byte[] { (byte) 0x30, (byte) 0x81, (byte) 0x9D, (byte) 0x30, (byte) 0x0D, (byte) 0x06, (byte) 0x09, (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xF7, (byte) 0x0D, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x81, (byte) 0x8B, (byte) 0x00, (byte) 0x30, (byte) 0x81, (byte) 0x87, (byte) 0x02, (byte) 0x81, (byte) 0x81, (byte) 0x00, (byte) 0xAE, (byte) 0xD1, (byte) 0x4B, (byte) 0xC0, (byte) 0xA3, (byte) 0x36, (byte) 0x8B, (byte) 0xA0, (byte) 0x39, (byte) 0x0B, (byte) 0x43, (byte) 0xDC, (byte) 0xED, (byte) 0x6A, (byte) 0xC8, (byte) 0xF2, (byte) 0xA3, (byte) 0xE4, (byte) 0x7E, (byte) 0x09, (byte) 0x8C, (byte) 0x55, (byte) 0x2E, (byte) 0xE7, (byte) 0xE9, (byte) 0x3C, (byte) 0xBB, (byte) 0xE5, (byte) 0x5E, (byte) 0x0F, (byte) 0x18, (byte) 0x74, (byte) 0x54, (byte) 0x8F, (byte) 0xF3, (byte) 0xBD, (byte) 0x56, (byte) 0x69, (byte) 0x5B, (byte) 0x13, (byte) 0x09, (byte) 0xAF, (byte) 0xC8,
                (byte) 0xBE, (byte) 0xB3, (byte) 0xA1, (byte) 0x48, (byte) 0x69, (byte) 0xE9, (byte) 0x83, (byte) 0x49, (byte) 0x65, (byte) 0x8D, (byte) 0xD2, (byte) 0x93, (byte) 0x21, (byte) 0x2F, (byte) 0xB9, (byte) 0x1E, (byte) 0xFA, (byte) 0x74, (byte) 0x3B, (byte) 0x55, (byte) 0x22, (byte) 0x79, (byte) 0xBF, (byte) 0x85, (byte) 0x18, (byte) 0xCB, (byte) 0x6D, (byte) 0x52, (byte) 0x44, (byte) 0x4E, (byte) 0x05, (byte) 0x92, (byte) 0x89, (byte) 0x6A, (byte) 0xA8, (byte) 0x99, (byte) 0xED, (byte) 0x44, (byte) 0xAE, (byte) 0xE2, (byte) 0x66, (byte) 0x46, (byte) 0x42, (byte) 0x0C, (byte) 0xFB, (byte) 0x6E, (byte) 0x4C, (byte) 0x30, (byte) 0xC6, (byte) 0x6C, (byte) 0x5C, (byte) 0x16, (byte) 0xFF, (byte) 0xBA, (byte) 0x9C, (byte) 0xB9, (byte) 0x78, (byte) 0x3F, (byte) 0x17, (byte) 0x4B, (byte) 0xCB, (byte) 0xC9, (byte) 0x01, (byte) 0x5D, (byte) 0x3E, (byte) 0x37, (byte) 0x70, (byte) 0xEC, (byte) 0x67, (byte) 0x5A, (byte) 0x33, (byte) 0x48, (byte) 0xF7, (byte) 0x46, (byte) 0xCE,
                (byte) 0x58, (byte) 0xAA, (byte) 0xEC, (byte) 0xD9, (byte) 0xFF, (byte) 0x4A, (byte) 0x78, (byte) 0x6C, (byte) 0x83, (byte) 0x4B, (byte) 0x02, (byte) 0x01, (byte) 0x11 });

        UNIVERSE_PUBLIC_KEYS.put(EUniverse.Internal, new byte[] { (byte) 0x30, (byte) 0x81, (byte) 0x9D, (byte) 0x30, (byte) 0x0D, (byte) 0x06, (byte) 0x09, (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xF7, (byte) 0x0D, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x81, (byte) 0x8B, (byte) 0x00, (byte) 0x30, (byte) 0x81, (byte) 0x87, (byte) 0x02, (byte) 0x81, (byte) 0x81, (byte) 0x00, (byte) 0xA8, (byte) 0xFE, (byte) 0x01, (byte) 0x3B, (byte) 0xB6, (byte) 0xD7, (byte) 0x21, (byte) 0x4B, (byte) 0x53, (byte) 0x23, (byte) 0x6F, (byte) 0xA1, (byte) 0xAB, (byte) 0x4E, (byte) 0xF1, (byte) 0x07, (byte) 0x30, (byte) 0xA7, (byte) 0xC6, (byte) 0x7E, (byte) 0x6A, (byte) 0x2C, (byte) 0xC2, (byte) 0x5D, (byte) 0x3A, (byte) 0xB8, (byte) 0x40, (byte) 0xCA, (byte) 0x59, (byte) 0x4D, (byte) 0x16, (byte) 0x2D, (byte) 0x74, (byte) 0xEB, (byte) 0x0E, (byte) 0x72, (byte) 0x46, (byte) 0x29, (byte) 0xF9, (byte) 0xDE, (byte) 0x9B, (byte) 0xCE,
                (byte) 0x4B, (byte) 0x8C, (byte) 0xD0, (byte) 0xCA, (byte) 0xF4, (byte) 0x08, (byte) 0x94, (byte) 0x46, (byte) 0xA5, (byte) 0x11, (byte) 0xAF, (byte) 0x3A, (byte) 0xCB, (byte) 0xB8, (byte) 0x4E, (byte) 0xDE, (byte) 0xC6, (byte) 0xD8, (byte) 0x85, (byte) 0x0A, (byte) 0x7D, (byte) 0xAA, (byte) 0x96, (byte) 0x0A, (byte) 0xEA, (byte) 0x7B, (byte) 0x51, (byte) 0xD6, (byte) 0x22, (byte) 0x62, (byte) 0x5C, (byte) 0x1E, (byte) 0x58, (byte) 0xD7, (byte) 0x46, (byte) 0x1E, (byte) 0x09, (byte) 0xAE, (byte) 0x43, (byte) 0xA7, (byte) 0xC4, (byte) 0x34, (byte) 0x69, (byte) 0xA2, (byte) 0xA5, (byte) 0xE8, (byte) 0x44, (byte) 0x76, (byte) 0x18, (byte) 0xE2, (byte) 0x3D, (byte) 0xB7, (byte) 0xC5, (byte) 0xA8, (byte) 0x96, (byte) 0xFD, (byte) 0xE5, (byte) 0xB4, (byte) 0x4B, (byte) 0xF8, (byte) 0x40, (byte) 0x12, (byte) 0xA6, (byte) 0x17, (byte) 0x4E, (byte) 0xC4, (byte) 0xC1, (byte) 0x60, (byte) 0x0E, (byte) 0xB0, (byte) 0xC2, (byte) 0xB8, (byte) 0x40, (byte) 0x4D, (byte) 0x9E,
                (byte) 0x76, (byte) 0x4C, (byte) 0x44, (byte) 0xF4, (byte) 0xFC, (byte) 0x6F, (byte) 0x14, (byte) 0x89, (byte) 0x73, (byte) 0xB4, (byte) 0x13, (byte) 0x02, (byte) 0x01, (byte) 0x11 });
    }


    public static int BLOCK_SIZE_BITS = 128;
    public static int KEY_SIZE_BITS = 256;
    public static int BLOCK_SIZE = BLOCK_SIZE_BITS >> 3;

    private final Key aesKey;
    private final PublicKey rsaKey;

    private final Cipher cIv;
    private final Cipher cMain;

    public AESCodec(EUniverse universe) throws IOException {
        try {
            KeyGenerator aesGenerator = KeyGenerator.getInstance("AES", "BC");
            aesGenerator.init(KEY_SIZE_BITS);
            aesKey = aesGenerator.generateKey();
            KeyFactory rsaFactory = KeyFactory.getInstance("RSA", "BC");
            rsaKey = rsaFactory.generatePublic(new X509EncodedKeySpec(UNIVERSE_PUBLIC_KEYS.get(universe)));

            cIv = Cipher.getInstance("AES/ECB/NoPadding", "BC");
            cMain = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    public byte[] getEncryptedKey() throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, rsaKey);
            return cipher.doFinal(aesKey.getEncoded());
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            cIv.init(Cipher.DECRYPT_MODE, aesKey);

            byte[] decryptedIv = cIv.doFinal(in.array(), in.arrayOffset(), BLOCK_SIZE);
            cMain.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(decryptedIv));

            ByteBuf out = ctx.alloc().heapBuffer(in.readableBytes() - BLOCK_SIZE).order(ByteOrder.LITTLE_ENDIAN);
            int n = cMain.doFinal(in.array(), in.arrayOffset() + BLOCK_SIZE, in.readableBytes() - BLOCK_SIZE, out.array(), out.arrayOffset());

            out.writerIndex(n);

            ctx.fireChannelRead(out);
        } finally {
            in.release();
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            cIv.init(Cipher.ENCRYPT_MODE, aesKey);
            cMain.init(Cipher.ENCRYPT_MODE, aesKey);

            ByteBuf out = ctx.alloc().heapBuffer(BLOCK_SIZE + cMain.getOutputSize(in.readableBytes())).order(ByteOrder.LITTLE_ENDIAN);
            cIv.doFinal(cMain.getIV(), 0, BLOCK_SIZE, out.array(), out.arrayOffset());
            int n = cMain.doFinal(in.array(), in.arrayOffset(), in.readableBytes(), out.array(), out.arrayOffset() + BLOCK_SIZE);

            out.writerIndex(n + BLOCK_SIZE);

            ctx.write(out, promise);
        } finally {
            in.release();
        }
    }

}
