package org.linphone.groupchat.encryption;

import org.linphone.groupchat.interfaces.EncryptionHandler;

class EncryptionHandlerImpl implements EncryptionHandler {
	
	protected long key_private;
	protected long key_public;
	protected EncryptionType encryption_type;

	
	public EncryptionHandlerImpl(){
		
	}

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
}
