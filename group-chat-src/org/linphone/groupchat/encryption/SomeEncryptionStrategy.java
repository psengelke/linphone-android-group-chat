package org.linphone.groupchat.encryption;

import org.linphone.groupchat.interfaces.EncryptionStrategy;

public class SomeEncryptionStrategy implements EncryptionStrategy{
	
	private EncryptionType encryption_type;

	public SomeEncryptionStrategy() {
	
	}
	
	public void sendMessage(String message, GroupChatMember[] gcm, LinphoneCore lc){
		
	}
	
	public String receiveMessage(String message){
		
		return null;
	}

	public EncryptionType getEncryption_type() {
		return encryption_type;
	}
	
}
