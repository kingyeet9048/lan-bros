package cs410.lanbros.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Manages all connections transits. Encrypts and Decrpts
 * @createdBy Sulaiman Bada
 *
 */
public class TransitManger {

	//instance variables
	private Cipher encrMethod;
	private Cipher decryMethod;
	private SecretKey secretKey;
	/**
	 * Encrption Types that I know so far
	 * AES/ECB/PKCS5Padding
	 * @param encrptionType
	 */
	public TransitManger(String encrptionType) {
		try {
			secretKey = KeyGenerator.getInstance(encrptionType).generateKey();
			encrMethod = Cipher.getInstance(encrptionType);
			decryMethod = Cipher.getInstance(encrptionType);
			encrMethod.init(Cipher.ENCRYPT_MODE, secretKey);
			decryMethod.init(Cipher.DECRYPT_MODE, secretKey);
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CipherOutputStream wrapOutputStream(OutputStream send) {
		CipherOutputStream outputStream = new CipherOutputStream(send, encrMethod);
		return outputStream;
	}
	
	public CipherInputStream wrapInputStream(InputStream receive) {
		CipherInputStream inputStream = new CipherInputStream(receive, decryMethod);
		return inputStream;
	}
	
	public String encryptData(String input) {
		try {
			return Base64.getEncoder().encodeToString(encrMethod.doFinal(input.getBytes()));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	public String decryptData(String input) {
		try {
			return new String(decryMethod.doFinal(Base64.getDecoder().decode(input)));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
		return null;
	}
	@Override
	public int hashCode() {
		return Objects.hash(decryMethod, encrMethod, secretKey);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransitManger other = (TransitManger) obj;
		return Objects.equals(decryMethod, other.decryMethod) && Objects.equals(encrMethod, other.encrMethod)
				&& Objects.equals(secretKey, other.secretKey);
	}
	
	public static void main(String args[]) {
		TransitManger transitManger = new TransitManger("AES");
		System.out.println(transitManger.encryptData("hello world!"));
		System.out.println(transitManger.decryptData(transitManger.encryptData("hello world!")));
	}
}
