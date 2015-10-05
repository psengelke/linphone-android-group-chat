package org.linphone.groupchat.encryption;

import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.communication.MessageParser;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.communication.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.encryption.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.storage.GroupChatStorage;

class NoEncryptionStrategy implements EncryptionStrategy {

	public NoEncryptionStrategy() {}

	@Override
	public void sendMessage(String message, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		for (GroupChatMember member : members) {
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
//			chatRoom.compose();
			LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
			chatRoom.sendChatMessage(newMessage);
			chatRoom.deleteMessage(newMessage);
		}		
	}

//	@Override
	public String receiveMessage(String message) {
		return message;
	}

	@Override
	public EncryptionType getEncryptionType() {
		return EncryptionType.None;
	}

	@Override
	public void sendMessage(InitialContactInfo info, GroupChatMember member, LinphoneCore lc) {
		LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
		String message=MessageParser.stringifyInitialContactInfo(info);
		LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
		chatRoom.sendChatMessage(newMessage);
		chatRoom.deleteMessage(newMessage);
	}

	@Override
	public void sendMessage(MemberUpdateInfo info, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		String message=MessageParser.stringifyMemberUpdateInfo(info);
		sendMessage(message, members, lc);
	}

	@Override
	public void sendMessage(GroupChatMember info, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		String message=MessageParser.stringifyGroupChatMember(info);
		sendMessage(message, members, lc);
	}

//	@Override
	public MemberUpdateInfo handleMemberUpdate(String message, String id, GroupChatStorage storage) {
		return MessageParser.parseMemberUpdateInfo(message);
	}

//	@Override
	public String handlePlainTextMessage(String message, String id, GroupChatStorage storage) {
		return message;
	}

//	@Override
	public void handleMediaMessage(String message, String id, GroupChatStorage storage) {

	}

//	@Override
	public GroupChatMember handleAdminChange(String message, String id, GroupChatStorage storage) {
		return MessageParser.parseGroupChatMember("");//handler.decrypt(message));
	}

	@Override
	public EncryptionStrategy handleEncryptionStrategyChange(String message, String id, GroupChatStorage storage) {
		return null;
		
	}

//	@Override
	public GroupChatMember handleInitialContactMessage(String message, String id, GroupChatStorage storage,
			boolean encrypted) {
		try{
			if (encrypted)
				throw new Exception();
			InitialContactInfo ic=MessageParser.parseInitialContactInfo(message);
			
		}
		catch(Exception e){
			System.err.println("Encrypted message passed to NoEncryptionStrategy");
			return null;
		}
		return null;
	}

	@Override
	public void handleInitialContactMessage(LinphoneChatMessage message,
			LinphoneCore lc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInitialContactMessage(LinphoneChatMessage message,
			String id, GroupChatStorage storage, LinphoneCore lc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MemberUpdateInfo handleMemberUpdate(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GroupChatMessage handlePlainTextMessage(LinphoneChatMessage message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GroupChatMessage handleMediaMessage(LinphoneChatMessage message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GroupChatMember handleAdminChange(String message) {
		// TODO Auto-generated method stub
		return null;
	}
}
