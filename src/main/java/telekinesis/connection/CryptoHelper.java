package telekinesis.connection;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Provides Crypto functions used in Steam protocols
 */
public class CryptoHelper {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
	private CryptoHelper() {
	}

	/**
	 * Performs an encryption using AES/CBC/PKCS7 with an input byte array and key, with a random IV prepended using AES/ECB/None
	 */
	public static byte[] SymmetricEncrypt(byte[] input, byte[] key) throws GeneralSecurityException {
		if (key.length != 32) {
		    throw new RuntimeException("key length not 32!");
		}

		// encrypt iv using ECB and provided key
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));

		// generate iv
		final byte[] iv = CryptoHelper.GenerateRandomBlock(16);
		final byte[] cryptedIv = cipher.doFinal(iv);

		// encrypt input plaintext with CBC using the generated (plaintext) IV and the provided key
		cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

		final byte[] cipherText = cipher.doFinal(input);

		// final output is 16 byte ecb crypted IV + cbc crypted plaintext
		final byte[] output = new byte[cryptedIv.length + cipherText.length];
		System.arraycopy(cryptedIv, 0, output, 0, cryptedIv.length);
		System.arraycopy(cipherText, 0, output, cryptedIv.length, cipherText.length);

		return output;
	}

	/**
	 * Decrypts using AES/CBC/PKCS7 with an input byte array and key, using the random IV prepended using AES/ECB/None
	 */
	public static byte[] SymmetricDecrypt(byte[] input, byte[] key) throws GeneralSecurityException {
        if (key.length != 32) {
            throw new RuntimeException("key length not 32!");
        }

		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");

		// first 16 bytes of input is the ECB encrypted IV
		byte[] iv = new byte[16];
		final byte[] cryptedIv = Arrays.copyOfRange(input, 0, 16);

		// the rest is ciphertext
		byte[] cipherText = new byte[input.length - cryptedIv.length];
		cipherText = Arrays.copyOfRange(input, cryptedIv.length, cryptedIv.length + cipherText.length);

		// decrypt the IV using ECB
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
		iv = cipher.doFinal(cryptedIv);

		cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

		// decrypt the remaining ciphertext in cbc with the decrypted IV
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
		return cipher.doFinal(cipherText);
	}

	/**
	 * Generate an array of random bytes given the input length
	 */
	public static byte[] GenerateRandomBlock(int size) {
		final byte[] block = new byte[size];
		final SecureRandom random = new SecureRandom();
		random.nextBytes(block);
		return block;
	}

}
