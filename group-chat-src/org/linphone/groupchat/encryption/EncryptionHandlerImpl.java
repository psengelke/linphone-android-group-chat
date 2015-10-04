package org.linphone.groupchat.encryption;

import javax.crypto.spec.SecretKeySpec;

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
	public void setSecretKey(String seed) {
		keySeed=seed;
	}

	@Override
	public String getKeySeed() {
		return keySeed;
	}

	@Override
	public char[] generateSeed() {
		return null;
	}

	/*@Override
	public long getSecretKey() {
		return key_private;
	}*/
}
