package org.linphone.groupchat.encryption;

import org.linphone.groupchat.interfaces.EncryptionHandler;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AES256EncryptionHandler extends EncryptionHandlerImpl implements EncryptionHandler {

	private static String salt;
	private static int keyIterations=65536;
	private static int keySize=256;
	private byte[] ivBytes;
	
	public String generateSalt(){
		SecureRandom rng=new SecureRandom();
		byte bytes[]=new byte[20];
		rng.nextBytes(bytes);
		String salt=new String(bytes);
		return salt;
	}
	
	@Override
	public String encrypt(String message, long key) {
		key_public=key;
		
		try {
			salt=generateSalt();
			byte[] saltBytes=salt.getBytes("UTF-8");
			
			SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec publicKeySpec=new PBEKeySpec(String.valueOf(key).toCharArray(), saltBytes, keyIterations, keySize);
			
			SecretKey privateKey=factory.generateSecret(publicKeySpec);
			SecretKeySpec privateKeySpec=new SecretKeySpec(privateKey.getEncoded(), "AES");
			
			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, privateKeySpec);
			AlgorithmParameters params=cipher.getParameters();
			ivBytes=params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] encryptedTextBytes=cipher.doFinal(message.getBytes("UTF-8"));
			return new Base64().encodeAsString(encryptedTextBytes);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public String decrypt(String message) {
		
		try {
			byte[] saltBytes=salt.getBytes();
			byte[] encryptedTextBytes=new Base64().decodeBase64(message);
			
			SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec publicKeySpec=new PBEKeySpec(String.valueOf(key_public).toCharArray(), saltBytes, keyIterations, keySize);
			
			SecretKey privateKey=factory.generateSecret(publicKeySpec);
			SecretKeySpec privateKeySpec=new SecretKeySpec(privateKey.getEncoded(), "AES");
			
			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKeySpec, new IvParameterSpec(ivBytes));
			
			byte[] decryptedTextBytes=null;
			decryptedTextBytes=cipher.doFinal(encryptedTextBytes);
			return new String(decryptedTextBytes);
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getPublicKey() {
		return super.getPublicKey();
	}
	
}
