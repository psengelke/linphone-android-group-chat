package org.linphone.groupchat.encryption;

import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.interfaces.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.interfaces.EncryptionHandler;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.interfaces.EncryptionStrategy;
import org.linphone.groupchat.interfaces.GroupChatStorage;

class SomeEncryptionStrategy implements EncryptionStrategy {
	
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

	@Override
	public void sendMessage(InitialContactInfo info, GroupChatMember member, LinphoneCore lc) {
		LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
		String message=handler.encrypt(MessageParser.stringifyInitialContactInfo(info), key);
		LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
		chatRoom.sendChatMessage(newMessage);
		chatRoom.deleteMessage(newMessage);
	}

	@Override
	public void sendMessage(MemberUpdateInfo info, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		String message=handler.encrypt(MessageParser.stringifyMemberUpdateInfo(info), key);
		sendMessage(message, members, lc);
	}

	@Override
	public void sendMessage(GroupChatMember info, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		String message=handler.encrypt(MessageParser.stringifyGroupChatMember(info), key);
		sendMessage(message, members, lc);
	}

	@Override
	public GroupChatMember handleInitialContactMessage(String message, String id, GroupChatStorage storage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MemberUpdateInfo handleMemberUpdate(String message, String id, GroupChatStorage storage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String handlePlainTextMessage(String message, String id, GroupChatStorage storage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleMediaMessage(String message, String id, GroupChatStorage storage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GroupChatMember handleAdminChange(String message, String id, GroupChatStorage storage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EncryptionStrategy handleEncryptionStrategyChange(String message, String id, GroupChatStorage storage) {
		// TODO Auto-generated method stub
		return null;
	}
}
