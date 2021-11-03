package cs410.lanbros.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import cs410.lanbros.network.packets.InputTypes;
import cs410.lanbros.network.packets.PacketType;
import cs410.lanbros.network.packets.PlayerInputPacket;
import cs410.lanbros.network.packets.WrappedPacket;

/**
 * Manages all connections transits. Encrypts and Decrpts
 * @createdBy Sulaiman Bada
 *
 */
public class TransitManager {

	//instance variables
	private Cipher encrMethod;
	private Cipher decryMethod;
	private SecretKey secretKey;

	/**
	 * Encrption Types that I know so far
	 * AES/ECB/PKCS5Padding
	 * @param encrptionType
	 */
	public TransitManager(String encryptionType) {
		try {
			secretKey = KeyGenerator.getInstance(encryptionType).generateKey();
			encrMethod = Cipher.getInstance(encryptionType);
			decryMethod = Cipher.getInstance(encryptionType);
			encrMethod.init(Cipher.ENCRYPT_MODE, secretKey);
			decryMethod.init(Cipher.DECRYPT_MODE, secretKey);
			
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			System.err.println("Transit Manager Error: " + e.getMessage());
		}
	}
	
	public TransitManager(String encryptionType, SecretKey secretKey) {
		try {
			encrMethod = Cipher.getInstance(encryptionType);
			decryMethod = Cipher.getInstance(encryptionType);
			encrMethod.init(Cipher.ENCRYPT_MODE, secretKey);
			decryMethod.init(Cipher.DECRYPT_MODE, secretKey);
			
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			System.err.println("Transit Manager Error: " + e.getMessage());
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
	
	public SealedObject encryptPacket(Serializable inputPacket) {
		// https://www.baeldung.com/java-aes-encryption-decryption
		try {
			return new SealedObject(inputPacket, encrMethod);
		} catch (IllegalBlockSizeException | IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	public Serializable decryptPacket(SealedObject sealedObject) {
		// https://www.baeldung.com/java-aes-encryption-decryption
		try {
			return (Serializable) sealedObject.getObject(decryMethod);
		} catch (IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	public SecretKey getSecretKey() {
		return secretKey;
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
		TransitManager other = (TransitManager) obj;
		return Objects.equals(decryMethod, other.decryMethod) && Objects.equals(encrMethod, other.encrMethod)
				&& Objects.equals(secretKey, other.secretKey);
	}
}
