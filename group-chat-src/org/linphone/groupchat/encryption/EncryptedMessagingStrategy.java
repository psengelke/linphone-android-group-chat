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
import org.linphone.groupchat.exception.InvalidKeySeedException;
import org.linphone.groupchat.storage.GroupChatStorage;

class EncryptedMessagingStrategy implements MessagingStrategy {

	private final SymmetricEncryptionHandler sHandler;
	private final AsymmetricEncryptionHandler aHandler;

	public EncryptedMessagingStrategy(SymmetricEncryptionHandler handler, AsymmetricEncryptionHandler aHandler) {

		this.sHandler = handler;
		this.aHandler=aHandler;
	}

	@Override
	public void sendMessage(String message, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		for (GroupChatMember member : members) {
			String encryptedMessage=sHandler.encrypt(message);
			LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
			//			chatRoom.compose();
			LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(encryptedMessage);
			chatRoom.sendChatMessage(newMessage);
			chatRoom.deleteMessage(newMessage);
		}
	}

	@Override
	public void sendMessage(InitialContactInfo info, GroupChatMember member, LinphoneCore lc) {
		LinphoneChatRoom chatRoom=lc.getOrCreateChatRoom(member.sip);
		String message=sHandler.encrypt(MessageParser.stringifyInitialContactInfo(info));
		LinphoneChatMessage newMessage=chatRoom.createLinphoneChatMessage(message);
		chatRoom.sendChatMessage(newMessage);
		chatRoom.deleteMessage(newMessage);
	}

	@Override
	public void sendMessage(MemberUpdateInfo info, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		String message=sHandler.encrypt(MessageParser.stringifyMemberUpdateInfo(info));
		sendMessage(message, members, lc);
	}

	@Override
	public void sendMessage(GroupChatMember info, LinkedList<GroupChatMember> members, LinphoneCore lc) {
		String message=sHandler.encrypt(MessageParser.stringifyGroupChatMember(info));
		sendMessage(message, members, lc);
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

	/*	@Override
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
		return null;
	}*/

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
//				handler.generateAsymmetricKeys();
				newMessage=chatRoom.createLinphoneChatMessage(aHandler.getPublicKey());
				chatRoom.sendChatMessage(newMessage);
				chatRoom.deleteMessage(newMessage);
				break;
			case LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_2:
				String encryptedKey=aHandler.encrypt(message.getText(), aHandler.getPublicKey());
				newMessage=chatRoom.createLinphoneChatMessage(encryptedKey);
				chatRoom.sendChatMessage(newMessage);
				chatRoom.deleteMessage(newMessage);
				break;
			case LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_3:
				String key=aHandler.decrypt(message.getText());
				try {
					sHandler.setSecretKey(key);
				} catch (InvalidKeySeedException e) {
					e.printStackTrace();
					break;
				}
				storage.setSecretKey(id, key);
				GroupChatMember gcm=new GroupChatMember(message.getFrom().getDisplayName(), message.getFrom().asStringUriOnly(), true);
				newMessage=chatRoom.createLinphoneChatMessage(MessageParser.stringifyGroupChatMember(gcm));
				newMessage.addCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE, LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_ACCEPT);
				chatRoom.sendChatMessage(newMessage);
				chatRoom.deleteMessage(newMessage);
				break;
			default: 
				break;
			}
		}
	}
}
