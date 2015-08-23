package org.linphone.groupchat.encryption;

import java.util.LinkedList;

import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.interfaces.EncryptionStrategy;
import org.linphone.groupchat.interfaces.GroupChatStorage.GroupChatMember;

public class NoEncryptionStrategy implements EncryptionStrategy {

	public NoEncryptionStrategy() {}

	@Override
	public void sendMessage(String message, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String receiveMessage(String message) {
		return message;
	}

	@Override
	public EncryptionType getEncryptionType() {
		return EncryptionType.None;
	}
}
