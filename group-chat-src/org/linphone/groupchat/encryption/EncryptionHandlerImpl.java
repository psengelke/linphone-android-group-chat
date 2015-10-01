package org.linphone.groupchat.encryption;

import javax.crypto.spec.SecretKeySpec;

class EncryptionHandlerImpl implements EncryptionHandler {
	
	// asymmetric
	protected long key_private;
	protected long key_public;
	
	// symmetric
	protected SecretKeySpec sks;
	
	protected EncryptionType encryption_type;

	
	public EncryptionHandlerImpl(){}

	@Override
	public String encrypt(String message, long key) {
		return null;
	}

	@Override
	public String decrypt(String message) {
		return null;
	}

	@Override
	public long getPublicKey() {
		return key_public;
	}

	@Override
	public EncryptionType getEncryptionType() {
		return encryption_type;
	}

	@Override
	public long getSecretKey() {
		return key_private;
	}
}
