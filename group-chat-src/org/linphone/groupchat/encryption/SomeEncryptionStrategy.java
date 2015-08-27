package org.linphone.groupchat.encryption;

import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
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
		for (GroupChatMember member : members) {
			String encryptedMessage=handler.encrypt(message, key);
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
			chatRoom.compose();
			LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(encryptedMessage);
			chatRoom.sendChatMessage(newMessage);
			chatRoom.deleteMessage(newMessage);
		}
	}
	
	@Override
	public String receiveMessage(String message){
		return handler.decrypt(message);
	}

	@Override
	public EncryptionType getEncryptionType() {
		return handler.getEncryptionType();
	}
}
