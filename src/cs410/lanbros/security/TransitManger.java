package cs410.lanbros.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
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
	
	public SealedObject encryptPacket(Serializable inputPacket) {
		try {
			return new SealedObject(inputPacket, encrMethod);
		} catch (IllegalBlockSizeException | IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	public Serializable decryptPacket(SealedObject sealedObject) {
		try {
			return (Serializable) sealedObject.getObject(decryMethod);
		} catch (IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | IOException e) {
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
	
	//example
	public static void main(String args[]) {
		TransitManger transitManger = new TransitManger("AES");
		PlayerInputPacket inputPacket = new PlayerInputPacket();
		inputPacket.setInputTypes(InputTypes.LEFT_MOVEMENT);
		WrappedPacket wrappedPacket = new WrappedPacket(inputPacket, PacketType.PLAYER_INPUT);
		System.out.println(transitManger.encryptPacket(wrappedPacket));
		WrappedPacket returnedPacket = ((WrappedPacket) transitManger.decryptPacket(transitManger.encryptPacket(wrappedPacket)));
		
	}
}
