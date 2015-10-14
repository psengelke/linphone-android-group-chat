package org.linphone.groupchat.encryption;

import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.communication.MessageParser;
import org.linphone.groupchat.core.LinphoneGroupChatRoom;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.communication.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.storage.GroupChatStorage;

class UnencryptedMessagingStrategy implements MessagingStrategy {

	public UnencryptedMessagingStrategy() {}

	@Override
	public void sendMessage(String id, String message, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		
		for (GroupChatMember member : members) {
			
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
			
			LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
			newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_GROUP_ID, id);
			newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE, LinphoneGroupChatRoom.MSG_HEADER_TYPE_MESSAGE);
			
			chatRoom.sendChatMessage(newMessage);
			chatRoom.deleteMessage(newMessage);
		}		
	}

	@Override
	public void sendMessage(String id, InitialContactInfo info, GroupChatMember member, LinphoneCore lc) {
		
		LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
		
		String message=MessageParser.stringifyInitialContactInfo(info);
		
		LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
		newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_GROUP_ID, id);
		newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE, LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_1);
		
		chatRoom.sendChatMessage(newMessage);
		chatRoom.deleteMessage(newMessage);
	}

	@Override
	public void sendMessage(String id, MemberUpdateInfo info, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		
		String message=MessageParser.stringifyMemberUpdateInfo(info);
		
		for (GroupChatMember member : members) {
			
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
			
			LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
			newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_GROUP_ID, id);
			newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE, LinphoneGroupChatRoom.MSG_HEADER_TYPE_MEMBER_UPDATE);
			
			chatRoom.sendChatMessage(newMessage);
			chatRoom.deleteMessage(newMessage);
		}
	}

	@Override
	public void sendMessage(String id, GroupChatMember info, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		
		String message=MessageParser.stringifyGroupChatMember(info);
		
		
		for (GroupChatMember member : members) {
			
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
			
			LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
			newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_GROUP_ID, id);
			newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE, LinphoneGroupChatRoom.MSG_HEADER_TYPE_ADMIN_CHANGE);
			
			chatRoom.sendChatMessage(newMessage);
			chatRoom.deleteMessage(newMessage);
		}
	}

	@Override
	public MemberUpdateInfo handleMemberUpdate(String message) {
		
		return MessageParser.parseMemberUpdateInfo(message);
	}

	@Override
	public GroupChatMessage handlePlainTextMessage(LinphoneChatMessage message) {
		
		GroupChatMessage gcm=new GroupChatMessage();
		gcm.message=message.getText();
		return gcm;
	}

	@Override
	public GroupChatMessage handleMediaMessage(LinphoneChatMessage message) {
		
		return new GroupChatMessage();
	}

	@Override
	public GroupChatMember handleAdminChange(String message) {
		
		return MessageParser.parseGroupChatMember(message);
	}

	@Override
	public MessagingStrategy handleEncryptionStrategyChange(String message, String id, GroupChatStorage storage) {
		
		return EncryptionFactory.createEncryptionStrategy(EncryptionType.None);
	}

	@Override
	public void handleInitialContactMessage(LinphoneChatMessage message,
			String id, GroupChatStorage storage, LinphoneCore lc) {

		LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(message.getFrom().asStringUriOnly());
		GroupChatMember gcm=new GroupChatMember(message.getTo().getDisplayName(), message.getTo().asStringUriOnly(), false);
		
		LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(MessageParser.stringifyGroupChatMember(gcm));
		newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE, LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_ACCEPT);
		
		chatRoom.sendChatMessage(newMessage);
		chatRoom.deleteMessage(newMessage);

	}
}
