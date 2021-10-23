package cs410.lanbros.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
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
			encrMethod.init(Cipher.ENCRYPT_MODE, secretKey);
			decryMethod.init(Cipher.DECRYPT_MODE, secretKey);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
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
}
