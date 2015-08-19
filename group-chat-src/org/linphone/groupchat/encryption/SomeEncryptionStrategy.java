package org.linphone.groupchat.encryption;

import java.util.LinkedList;

import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.interfaces.EncryptionHandler;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.interfaces.EncryptionStrategy;
import org.linphone.groupchat.interfaces.GroupChatStorage.GroupChatMember;

public class SomeEncryptionStrategy implements EncryptionStrategy {
	
	private final EncryptionHandler handler;

	public SomeEncryptionStrategy(EncryptionHandler handler) {
		
		this.handler = handler;
	}

	@Override
	public void sendMessage(String message, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		
	}
	
	@Override
	public String receiveMessage(String message){
		
		return null;
	}

	@Override
	public EncryptionType getEncryptionType() {
		return handler.getEncryptionType();
	}
}
