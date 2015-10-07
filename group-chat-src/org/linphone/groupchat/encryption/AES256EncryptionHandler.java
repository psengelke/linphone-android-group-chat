package org.linphone.groupchat.encryption;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

class AES256EncryptionHandler extends SymmetricEncryptionHandlerImpl implements SymmetricEncryptionHandler {

	/*private static String salt;
	private static int keyIterations=65536;
	private static int keySize=256;
	private byte[] ivBytes;*/

	/*private String generateSalt(){
		SecureRandom rng=new SecureRandom();
		byte bytes[]=new byte[20];
		rng.nextBytes(bytes);
		String salt=new String(bytes);
		return salt;
	}*/

	public AES256EncryptionHandler(String keySeed) {
		this.keySeed=keySeed;
	}

	@Override
	public String encrypt(String message) {
		try {
			/*salt=generateSalt();
			byte[] saltBytes=salt.getBytes("UTF-8");

			SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");//hash with sha256 instead
			PBEKeySpec publicKeySpec=new PBEKeySpec(String.valueOf(key).toCharArray(), saltBytes, keyIterations, keySize);

			SecretKey privateKey=factory.generateSecret(publicKeySpec);
			SecretKeySpec privateKeySpec=new SecretKeySpec(privateKey.getEncoded(), "AES");

			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, privateKeySpec);
			AlgorithmParameters params=cipher.getParameters();
			ivBytes=params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] encryptedTextBytes=cipher.doFinal(message.getBytes("UTF-8"));
			return new Base64().encodeAsString(encryptedTextBytes);*/

			//			Mac sha256 = Mac.getInstance("HmacSHA256");
			//			SecretKeySpec sk=new SecretKeySpec(String.valueOf(key).getBytes(), "HmacSHA256");
			//			sha256.init(sk);

			//			SecretKeySpec sk=new SecretKeySpec(Arrays.copyOf(String.valueOf(key).getBytes("UTF-8"), 16), "AES");
			SecretKeySpec sk=new SecretKeySpec(keySeed.getBytes("UTF-8"), "AES");
			Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, sk);
			return Base64.encodeBase64String(cipher.doFinal(message.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public String decrypt(String message) {

		try {
			/*byte[] saltBytes=salt.getBytes();
			byte[] encryptedTextBytes=new Base64().decodeBase64(message);

			SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec publicKeySpec=new PBEKeySpec(String.valueOf(key_public).toCharArray(), saltBytes, keyIterations, keySize);

			SecretKey privateKey=factory.generateSecret(publicKeySpec);
			SecretKeySpec privateKeySpec=new SecretKeySpec(privateKey.getEncoded(), "AES");

			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKeySpec, new IvParameterSpec(ivBytes));

			byte[] decryptedTextBytes=null;
			decryptedTextBytes=cipher.doFinal(encryptedTextBytes);
			return new String(decryptedTextBytes);*/

			SecretKeySpec sk=new SecretKeySpec(keySeed.getBytes("UTF-8"), "AES");
			Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, sk);
			return new String(cipher.doFinal(Base64.decodeBase64(message)));
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
