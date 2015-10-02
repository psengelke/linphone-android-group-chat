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

	public Object getSecretKey() {
		return key_private;
	}

	@Override
	public void generateAsymmetricKeys() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String encrypt(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSecretKey(String seed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getKeySeed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] generateSeed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encrypt(String message, Object secretKey) {
		// TODO Auto-generated method stub
		return null;
	}
}
