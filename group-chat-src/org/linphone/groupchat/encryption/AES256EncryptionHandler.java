package org.linphone.groupchat.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

class AES256EncryptionHandler extends SymmetricEncryptionHandlerImpl implements SymmetricEncryptionHandler {

	public AES256EncryptionHandler(String keySeed) {
		this.keySeed=keySeed;
	}

	@Override
	public String encrypt(String message) {
		try {
			KeyGenerator keygen=KeyGenerator.getInstance("AES");
			java.security.SecureRandom sr=java.security.SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(keySeed.getBytes());
			keygen.init(256, sr);
			SecretKeySpec sk=new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
			
			Cipher cipher=Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, sk);
			return Base64.encodeBase64String(cipher.doFinal(message.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String decrypt(String message) {

		try {
			KeyGenerator keygen=KeyGenerator.getInstance("AES");
			java.security.SecureRandom sr=java.security.SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(keySeed.getBytes());
			keygen.init(256, sr);
			SecretKeySpec sk=new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
			
			Cipher cipher=Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, sk);
			return new String(cipher.doFinal(Base64.decodeBase64(message)));
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
