package org.linphone.groupchat.encryption;

import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.communication.MessageParser;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.communication.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.encryption.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.storage.GroupChatStorage;

class SomeEncryptionStrategy implements EncryptionStrategy {
	
	private final EncryptionHandler handler;

	public SomeEncryptionStrategy(EncryptionHandler handler) {
		
		this.handler = handler;
	}

	@Override
	public void sendMessage(String message, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		for (GroupChatMember member : members) {
			String encryptedMessage=handler.encrypt(message, handler.getSecretKey());
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
//			chatRoom.compose();
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
		String message=handler.encrypt(MessageParser.stringifyInitialContactInfo(info), handler.getSecretKey());
		LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
		chatRoom.sendChatMessage(newMessage);
		chatRoom.deleteMessage(newMessage);
	}

	@Override
	public void sendMessage(MemberUpdateInfo info, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		String message=handler.encrypt(MessageParser.stringifyMemberUpdateInfo(info), handler.getSecretKey());
		sendMessage(message, members, lc);
	}

	@Override
	public void sendMessage(GroupChatMember info, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		String message=handler.encrypt(MessageParser.stringifyGroupChatMember(info), handler.getSecretKey());
		sendMessage(message, members, lc);
	}

	@Override
	public MemberUpdateInfo handleMemberUpdate(String message, String id, GroupChatStorage storage) {
		return MessageParser.parseMemberUpdateInfo(handler.decrypt(message));
	}

	@Override
	public String handlePlainTextMessage(String message, String id, GroupChatStorage storage) {
		return handler.decrypt(message);
	}

	@Override
	public void handleMediaMessage(String message, String id, GroupChatStorage storage) {
		
	}

	@Override
	public GroupChatMember handleAdminChange(String message, String id, GroupChatStorage storage) {
		return MessageParser.parseGroupChatMember(handler.decrypt(message));
	}

	@Override
	public EncryptionStrategy handleEncryptionStrategyChange(String message, String id, GroupChatStorage storage) {
		
	}

	@Override
	public GroupChatMember handleInitialContactMessage(String message, String id, GroupChatStorage storage,
			boolean encrypted) {
		String decryptedMessage=handler.decrypt(message);
		InitialContactInfo ic=MessageParser.parseInitialContactInfo(decryptedMessage);
		if (ic.public_key==0 && ic.secret_key==0)
		{
			ic.public_key=handler.getPublicKey();
			String newMessage=MessageParser.stringifyInitialContactInfo(ic);
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(ic.group.admin);
			LinphoneChatMessage lcMessage=chatRoom.createLinphoneChatMessage(newMessage);
			chatRoom.sendChatMessage(lcMessage);
			chatRoom.deleteMessage(lcMessage);
		}
		else
		{
			if (ic.public_key!=0 && ic.secret_key==0)
			{
				ic.secret_key=handler.getSecretKey();
				storage.updateSecretKey(id, ic.secret_key);
			}
		}
	}
}
