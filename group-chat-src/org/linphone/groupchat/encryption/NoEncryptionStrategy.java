package org.linphone.groupchat.encryption;

import org.linphone.groupchat.interfaces.EncryptionHandler;

public class NoEncryptionStrategy {
	
	private EncryptionType encryption_type;
	private EncryptionHandler encryption_handler;

	public NoEncryptionStrategy() {
	
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
