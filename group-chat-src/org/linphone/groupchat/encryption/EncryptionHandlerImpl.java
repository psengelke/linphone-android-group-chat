package org.linphone.groupchat.encryption;

import org.linphone.groupchat.interfaces.EncryptionHandler;

public class EncryptionHandlerImpl implements EncryptionHandler {
	
	protected long key_private;
	protected long key_public;
	protected EncryptionType encryption_type;

	
	public EncryptionHandlerImpl(){
		
	}

	@Override
	public String encrpyt(String message, long key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String decrypt(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getPublicKey() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EncryptionType getEncryptionType() {
		// TODO Auto-generated method stub
		return null;
	}
}
