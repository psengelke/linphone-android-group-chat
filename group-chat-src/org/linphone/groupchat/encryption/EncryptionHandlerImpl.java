package org.linphone.groupchat.encryption;

import org.linphone.groupchat.interfaces.EncryptionHandler;

public class EncryptionHandlerImpl implements EncryptionHandler{
	
	protected long key_private;
	protected long key_public;
	protected EncryptionType encryption_type;
	
	public EncryptionHandlerImpl(){
		
	}
	
	public void encrypt(String message, long key){
		
	}
	
	public void decrypt(String message){
		
	}

	public long getKey_public() {
		return key_public;
	}

	public EncryptionType getEncryption_type() {
		return encryption_type;
	}
}
