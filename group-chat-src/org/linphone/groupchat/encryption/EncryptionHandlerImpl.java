package org.linphone.groupchat.encryption;

import javax.crypto.spec.SecretKeySpec;

import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.InvalidKeySeedException;

class EncryptionHandlerImpl implements EncryptionHandler {
	
	// asymmetric
	protected long key_private;
	protected String key_public;
	
	// symmetric
	protected SecretKeySpec sks;
	protected String keySeed;
	
	protected EncryptionType encryption_type;

	
	public EncryptionHandlerImpl(){}

	@Override
	public String encrypt(String message) {
		return null;
	}

	@Override
	public String decrypt(String message) {
		return null;
	}
}
