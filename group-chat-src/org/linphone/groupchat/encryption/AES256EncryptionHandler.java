package org.linphone.groupchat.encryption;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

class AES256EncryptionHandler extends SymmetricEncryptionHandlerImpl implements SymmetricEncryptionHandler {

	public AES256EncryptionHandler(String keySeed) {
		try {
			this.keySeed=keySeed;

			/*		KeyGenerator keygen=KeyGenerator.getInstance("AES");
		java.security.SecureRandom sr=java.security.SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(keySeed.getBytes());
		keygen.init(256, sr);
		sks=new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");*/
//			byte[] keyseed=keySeed.getBytes();
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(Base64.decode(keySeed, Base64.DEFAULT));
			byte[] digest = md.digest();
			sks=new SecretKeySpec(digest, "AES");
		}
		catch (Exception e) {
			Log.e("AESEncryptionHandler()", e.getMessage());
		}
	}

	@Override
	public String encrypt(String message) {
		try {
			//			KeyGenerator keygen=KeyGenerator.getInstance("AES");
			//			java.security.SecureRandom sr=java.security.SecureRandom.getInstance("SHA1PRNG");
			//			sr.setSeed(keySeed.getBytes());
			//			keygen.init(256, sr);
			//			SecretKeySpec sk=new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
			
			Cipher cipher=Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, sks);
			
			return Base64.encodeToString(cipher.doFinal(message.getBytes()), Base64.DEFAULT);
		} catch (Exception e) {
			Log.e("encrypt()", e.getMessage());
			return "";
		}
	}

	@Override
	public String decrypt(String message) {

		try {
			//			KeyGenerator keygen=KeyGenerator.getInstance("AES");
			//			java.security.SecureRandom sr=java.security.SecureRandom.getInstance("SHA1PRNG");
			//			sr.setSeed(keySeed.getBytes());
			//			keygen.init(256, sr);
			//			SecretKeySpec sk=new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");

			Cipher cipher=Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, sks);
			return new String(cipher.doFinal(Base64.decode(message, Base64.DEFAULT)));
		} catch (Exception e){
			Log.e("decrypt()", e.getMessage());
			return "";
		}
	}
}
