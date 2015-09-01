package org.linphone.groupchat.encryption;

import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.interfaces.EncryptionStrategy;

class NoEncryptionStrategy implements EncryptionStrategy {

	public NoEncryptionStrategy() {}

	@Override
	public void sendMessage(String message, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		for (GroupChatMember member : members) {
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
			chatRoom.compose();
			LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
			chatRoom.sendChatMessage(newMessage);
			chatRoom.deleteMessage(newMessage);
		}		
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
