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

			Cipher cipher=Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, sks);
			return new String(cipher.doFinal(Base64.decode(message, Base64.DEFAULT)));
		} catch (Exception e){
			Log.e("decrypt()", e.getMessage());
			return "";
		}
	}
}
