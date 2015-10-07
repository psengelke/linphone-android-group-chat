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
	public void sendMessage(String message, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		for (GroupChatMember member : members) {
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
			//			chatRoom.compose();
			LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
			chatRoom.sendChatMessage(newMessage);
			chatRoom.deleteMessage(newMessage);
		}		
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
		return null;
	}

	@Override
	public GroupChatMember handleAdminChange(String message) {
		return MessageParser.parseGroupChatMember(message);
	}

	@Override
	public MessagingStrategy handleEncryptionStrategyChange(String message, String id, GroupChatStorage storage) {
		return null;
	}

	/*@Override
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
	}*/

	@Override
	public void handleInitialContactMessage(LinphoneChatMessage message,
			String id, GroupChatStorage storage, LinphoneCore lc) {

		//		String header=message.getCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE);
		//		if (header!=null && (header.equals(LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_1) || header.equals(LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_2) || header.equals(LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_3)))
		//		{
		LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(message.getFrom().asStringUriOnly());
		GroupChatMember gcm=new GroupChatMember(message.getFrom().getDisplayName(), message.getFrom().asStringUriOnly(), true);
		LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(MessageParser.stringifyGroupChatMember(gcm));
		newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE, LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_ACCEPT);
		chatRoom.sendChatMessage(newMessage);
		chatRoom.deleteMessage(newMessage);
		//		}

		//	}
	}