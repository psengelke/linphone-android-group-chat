package org.linphone.groupchat.encryption;

import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.InvalidKeySeedException;

class SymmetricEncryptionHandlerImpl implements SymmetricEncryptionHandler {

	// symmetric
	protected String keySeed;
	
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
		else
			keySeed=key;
	}
}
