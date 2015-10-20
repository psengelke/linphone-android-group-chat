package org.linphone.groupchat.encryption;

import java.security.MessageDigest;

import javax.crypto.spec.SecretKeySpec;

import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.InvalidKeySeedException;

import android.util.Base64;
import android.util.Log;

class SymmetricEncryptionHandlerImpl implements SymmetricEncryptionHandler {

	// symmetric
	protected String keySeed;
	protected SecretKeySpec sks;
	protected EncryptionType encryption_type;


	public SymmetricEncryptionHandlerImpl(){}

	@Override
	public String encrypt(String message) {
		return null;
	}

	@Override
	public String decrypt(String message) {
		return null;
	}

	@Override
	public void setSecretKey(String key) throws InvalidKeySeedException {
		if (key==null)
			throw new InvalidKeySeedException();
		else {
			try {
				keySeed=key;
				MessageDigest md = MessageDigest.getInstance("SHA-256");

				md.update(Base64.decode(keySeed, Base64.DEFAULT));
				byte[] digest = md.digest();
				sks=new SecretKeySpec(digest, "AES");
			}
			catch (Exception e) {
				Log.e("setSecretKey()", e.getMessage());
			}
		}
	}

	@Override
	public String getSecretKey() {

		return keySeed;
	}
}
