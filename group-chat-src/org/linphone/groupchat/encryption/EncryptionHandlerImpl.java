package org.linphone.groupchat.encryption;

import javax.crypto.spec.SecretKeySpec;

import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.InvalidKeySeedException;

class EncryptionHandlerImpl implements SymmetricEncryptionHandler {
	
	// asymmetric
	protected long key_private;
	protected String key_public;
	
	// symmetric
	protected SecretKeySpec sks;
	protected String keySeed;
	
	protected EncryptionType encryption_type;

	
	public EncryptionHandlerImpl(){}

	@Override
	public String encrypt(String message, String keySeed) {
		return null;
	}

	@Override
	public String decrypt(String message) {
		return null;
	}

	@Override
	public String getPublicKey() {
		return key_public;
	}

	@Override
	public EncryptionType getEncryptionType() {
		return encryption_type;
	}

	@Override
	public void generateAsymmetricKeys() {}

	@Override
	public void setSecretKey(String seed) throws InvalidKeySeedException {
		keySeed=seed;
	}

	@Override
	public String getKeySeed() {
		return keySeed;
	}

	@Override
	public String generateSeed() {
		return null;
	}

	/*@Override
	public long getSecretKey() {
		return key_private;
	}*/
}
