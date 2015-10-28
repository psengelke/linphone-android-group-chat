package org.linphone.groupchat.encryption;

import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.communication.MessageParser;
import org.linphone.groupchat.core.LinphoneGroupChatRoom;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.communication.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.communication.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.InvalidKeySeedException;
import org.linphone.groupchat.storage.GroupChatStorage;

import android.util.Log;

class EncryptedMessagingStrategy implements MessagingStrategy {

	private final SymmetricEncryptionHandler sHandler;
	private final AsymmetricEncryptionHandler aHandler;

	public EncryptedMessagingStrategy(SymmetricEncryptionHandler handler, AsymmetricEncryptionHandler aHandler) {

		this.sHandler = handler;
		this.aHandler = aHandler;
	}

	@Override
	public void sendMessage(String id, String message, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		for (GroupChatMember member : members) {
			String encryptedMessage=sHandler.encrypt(message);
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
			
			LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(encryptedMessage);
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
		
		String message=sHandler.encrypt(MessageParser.stringifyMemberUpdateInfo(info));
		
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
		
		String message=sHandler.encrypt(MessageParser.stringifyGroupChatMember(info));

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
		return MessageParser.parseMemberUpdateInfo(sHandler.decrypt(message));
	}

	@Override
	public GroupChatMessage handlePlainTextMessage(LinphoneChatMessage message) {
		
		GroupChatMessage gcm=new GroupChatMessage();
		gcm.message=sHandler.decrypt(message.getText());
		
		return gcm;
	}

	@Override
	public GroupChatMessage handleMediaMessage(LinphoneChatMessage message) {
		return null;
	}

	@Override
	public GroupChatMember handleAdminChange(String message) {
		return MessageParser.parseGroupChatMember(sHandler.decrypt(message));
	}

	@Override
	public MessagingStrategy handleEncryptionStrategyChange(String message, String id, GroupChatStorage storage) {

		EncryptionType type = MessageParser.parseEncryptionType(message);

		if (type != EncryptionType.None) 
			return null;

		new EncryptionFactory();
		return EncryptionFactory.createEncryptionStrategy(type);

	}

	@Override
	public void handleInitialContactMessage(LinphoneChatMessage message,
			String id, GroupChatStorage storage, LinphoneCore lc) {
		
		String header=message.getCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE);
		if (header!=null)
		{
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(message.getFrom().asStringUriOnly());
			LinphoneChatMessage newMessage;
			
			switch (header) {
			case LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_1: 
				
				newMessage=chatRoom.createLinphoneChatMessage(aHandler.getPublicKey());
				newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_GROUP_ID, id);
				newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE, LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_2);
				chatRoom.sendChatMessage(newMessage);
				chatRoom.deleteMessage(newMessage);
				break;
			case LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_2:
				
				try {
					storage.setSecretKey(id, sHandler.getSecretKey());
					String encryptedKey=aHandler.encrypt(sHandler.getSecretKey(), message.getText());
					newMessage=chatRoom.createLinphoneChatMessage(encryptedKey);
					newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_GROUP_ID, id);
					newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE, LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_3);
					chatRoom.sendChatMessage(newMessage);
					chatRoom.deleteMessage(newMessage);
				} catch (GroupDoesNotExistException e){
					Log.e("handleInitialContactMessage(err1)", e.getMessage());
				}
				break;
			case LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_3:
				
				String key=aHandler.decrypt(message.getText());
				try {
					sHandler.setSecretKey(key);
					storage.setSecretKey(id, sHandler.getSecretKey());
					GroupChatMember gcm=new GroupChatMember(message.getTo().getDisplayName(), message.getTo().asStringUriOnly(), false);
					newMessage=chatRoom.createLinphoneChatMessage(MessageParser.stringifyGroupChatMember(gcm));
					newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_GROUP_ID, id);
					newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE, LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_ACCEPT);
					chatRoom.sendChatMessage(newMessage);
					chatRoom.deleteMessage(newMessage);
				} catch (InvalidKeySeedException | GroupDoesNotExistException e) {
					Log.e("handleInitialContactMessage(err2)", e.getMessage());
				}
				break;
			default: 
				break;
			}
		}
	}
}
