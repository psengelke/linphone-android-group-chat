package org.linphone.groupchat.core;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.communication.MessageParser;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage.MessageDirection;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage.MessageState;
import org.linphone.groupchat.communication.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.communication.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.encryption.MessagingStrategy;
import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupChatListenerIsSetException;
import org.linphone.groupchat.exception.GroupChatSizeException;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.IsAdminException;
import org.linphone.groupchat.exception.MemberDoesNotExistException;
import org.linphone.groupchat.exception.MemberExistsException;
import org.linphone.groupchat.exception.PermissionRequiredException;
import org.linphone.groupchat.storage.GroupChatStorage;

import android.graphics.Bitmap;

/**
 * This class serves as the chat {@link LinphoneChatRoom} for group chats. It provides all the functionality 
 * required for Linphone users to interact with groups of which they are members.
 * 
 * @author Paul Engelke
 */
public class LinphoneGroupChatRoom {
	
	public static final String MSG_HEADER_GROUP_ID = "GROUP-CHAT-ID";
	public static final String MSG_HEADER_TYPE  = "GROUP-CHAT-MESSAGE-TYPE";
	public static final String MSG_HEADER_TYPE_MESSAGE = "LinphoneGroupChatRoom.plain_message";
	public static final String MSG_HEADER_TYPE_INVITE_STAGE_1 = "LinphoneGroupChatRoom.invite_stage_1";
	public static final String MSG_HEADER_TYPE_INVITE_STAGE_2 = "LinphoneGroupChatRoom.invite_stage_2";
	public static final String MSG_HEADER_TYPE_INVITE_STAGE_3 = "LinphoneGroupChatRoom.invite_stage_3";
	public static final String MSG_HEADER_TYPE_INVITE_ACCEPT = "LinphoneGroupChatRoom.accept_invite";
	public static final String MSG_HEADER_TYPE_MEMBER_UPDATE = "LinphoneGroupChatRoom.member_update";
	public static final String MSG_HEADER_TYPE_ADMIN_CHANGE = "LinphoneGroupChatRoom.admin_update";
	public static final String MSG_HEADER_TYPE_GET_GROUP_INFO = "LinphoneGroupChatRoom.get_group_info";
	public static final String MSG_HEADER_TYPE_RECEIVE_GROUP_INFO = "LinphoneGroupChatRoom.receive_group_info";
	
	/*Add new headers here*/
	
	// this variable is set to true once the client has received the group information after logging in to Linphone.
	private boolean updated = false;
	
	private GroupChatRoomListener listener;
	
	private LinkedList<GroupChatMember> members;
	private String admin;
	private String group_id;
	private String group_name;
	private Bitmap image;
	private MessagingStrategy messenger;
	
	private LinphoneCore lc;
	private GroupChatStorage storage;
	
	private static final int MAX_MEMBERS = 50;
	
	/**
	 * Constructor.
	 * 
	 * @param group Contains the necessary data for group construction.
	 * @param messenger The encryption strategy to be used.
	 * @param storage The storage adapter for persistence purposes.
	 * @param lc The {@link LinphoneCore} instance for the client.
	 */
	public LinphoneGroupChatRoom(GroupChatData group, 
			MessagingStrategy messenger, 
			GroupChatStorage storage, 
			LinphoneCore lc){
		
		this.group_id = group.group_id;
		this.group_name = group.group_name;
		this.admin = group.admin;
		this.members = group.members;
		
		this.messenger = messenger;
		this.storage = storage;
		this.lc = lc;
		
		
		try {
			this.storage.createGroupChat(group);
			//if this point it reached, the group is new: invite members.
			doInitialization(group);
		} catch (GroupChatExistsException e){
			// group exists, request updated group info instead.
			requestGroupInfo();
		}
	}
	
	/**
	 * Sends invites to all group members. Call only when this is a new group.
	 * @throws GroupChatExistsException In the case that the group has already been initialised.
	 */
	private void doInitialization(GroupChatData group) throws GroupChatExistsException {
		
		InitialContactInfo info = new InitialContactInfo();
		info.group = group;
		
		Iterator<GroupChatMember> it = getOtherMembers().iterator();
		while (it.hasNext()) {
			GroupChatMember member = (GroupChatMember) it.next();
			messenger.sendMessage(info, member, lc);
		}
		
		try {
			this.members = storage.getMembers(group_id);
		} catch (GroupDoesNotExistException e) {
			// valid id
		}
	}
	
	/**
	 * This function sends a broadcast message to all group members, requesting the latest 
	 * group chat state.
	 * Called by the constructor once the group has been initialised.
	 */
	private void requestGroupInfo(){
		
		// TODO delegate to messenger
		
		Iterator<GroupChatMember> it = getOtherMembers().iterator();
		while (it.hasNext()){
			GroupChatMember m = it.next();
			if (!lc.getDefaultProxyConfig().getIdentity().equals(m.sip)){
				LinphoneChatRoom cr = lc.getOrCreateChatRoom(m.sip);
				LinphoneChatMessage message = cr.createLinphoneChatMessage("");
				message.addCustomHeader(MSG_HEADER_TYPE, MSG_HEADER_TYPE_GET_GROUP_INFO);
				cr.sendChatMessage(message);
				cr.deleteMessage(message);
			}
		}
	}
	
	/**
	 * Removes self from the group if self is not the admin.
	 * @throws IsAdminException  If the admin tries to remove themselves without assigning a new admin.
	 */
	public void removeSelf() throws IsAdminException {

		String me = lc.getDefaultProxyConfig().getIdentity();
		
		if (me.equals(admin)) throw new IsAdminException("You must assign a new admin first.");
		
		MemberUpdateInfo info = new MemberUpdateInfo();
		info.removed.add(new GroupChatMember(null, me, false)); //TODO: user's name?
		messenger.sendMessage(info, getOtherMembers(), lc);
	}
	
	/**
	 * A function to add a new member to the group chat. The method creates a new database 
	 * entry for the pending message.
	 * @param member The member to be added.
	 * @throws PermissionRequiredException If not the admin.
	 * @throws GroupChatSizeException If the new member addition exceeds the maximum group size.
	 * @throws MemberExistsException If the new member is an existing member in the group.
	 */
	public void addMember(GroupChatMember member) throws PermissionRequiredException, 
		GroupChatSizeException, MemberExistsException {
		
		if (!lc.getDefaultProxyConfig().getIdentity().equals(admin)) throw new PermissionRequiredException();
		
		if (members.size() == MAX_MEMBERS) throw new GroupChatSizeException("Exceeds group chat size.");
		
		member.pending = true;
		try{storage.addMember(group_id, member);} catch (GroupDoesNotExistException e){
			// TODO handle error
		}
		members.add(member);
		
		try{
			InitialContactInfo info = new InitialContactInfo();
			info.group = new GroupChatData();
			info.group.group_id = group_id;
			info.group.group_name = group_name;
			info.group.members = members;
			info.group.encryption_type = storage.getEncryptionType(group_id);
			
			messenger.sendMessage(info, member, lc);
		} catch (GroupDoesNotExistException e){
			// TODO handle error
		}
		
	}
	
	/**
	 * A function that updates the status of a pending invite to confirmed and 
	 * broadcast to the rest of the group.
	 * @param message The text body containing the member's details.
	 */
	private void updateMember(String message){
		
		GroupChatMember member = MessageParser.parseGroupChatMember(message);
		try {
			storage.setMemberStatus(group_id, member);
			
			// now tell the group of the update.
			MemberUpdateInfo info = new MemberUpdateInfo();
			info.confirmed.add(new GroupChatMember(member.name, member.sip, true));
			messenger.sendMessage(info, getOtherMembers(), lc);
		} catch (MemberDoesNotExistException e) {
			
			// member was removed before adding, send remove to user.
			LinkedList<GroupChatMember> m = new LinkedList<>();
			m.add(member);
			MemberUpdateInfo info = new MemberUpdateInfo();
			info.removed.add(member);
			messenger.sendMessage(info, m, lc);
		} catch (GroupDoesNotExistException e) {
			// TODO handler error
		}
	}
	
	/**
	 * A function to remove a member from the group chat. This may be done by a member removing themselves 
	 * or in the event that the admin wishes to remove a member.
	 * @param address The member to be removed.
	 * @throws IsAdminException If the admin tries to remove themselves before assigning a new admin.
	 * @throws GroupDoesNotExistException If the the group chat does not exist.
	 */
	public void removeMember(GroupChatMember member) throws PermissionRequiredException, IsAdminException, 
		GroupDoesNotExistException {
		
		String me = lc.getDefaultProxyConfig().getIdentity();
		
		if (!me.equals(admin)) throw new PermissionRequiredException();
		if (member.sip.equals(admin)) throw new IsAdminException("You must assign a new admin first.");
		if (me.equals(member.sip)){
			LinphoneGroupChatManager.getInstance().deleteGroupChat(group_id);
			return;
		} // TODO might not be necessary.
		
		try {
			storage.removeMember(group_id, member);
			
			// now tell the group
			MemberUpdateInfo info = new MemberUpdateInfo();
			info.removed.add(member);
			messenger.sendMessage(info, getOtherMembers(), lc);
		} catch (MemberDoesNotExistException e) {
			// member should not show up on group if deleted.
		}
	}
	
	/**
	 * This method is called when a new message for the group has been received by the Linphone client.
	 * The method handles administrative tasks concerning a new message.
	 * @param message The message object to be handled by the group chat.
	 */
	public void receiveMessage(LinphoneChatMessage message){
		
		String  type = message.getCustomHeader(MSG_HEADER_TYPE);
		switch (type) {
		case MSG_HEADER_TYPE_MESSAGE:
			handlePlainTextMessage(message);
			break;
		case MSG_HEADER_TYPE_INVITE_STAGE_1:
		case MSG_HEADER_TYPE_INVITE_STAGE_2:
			messenger.handleInitialContactMessage(message, group_id, storage, lc);
			break;
		case MSG_HEADER_TYPE_INVITE_STAGE_3:
			messenger.handleInitialContactMessage(message, group_id, storage, lc);
			break;
		case MSG_HEADER_TYPE_INVITE_ACCEPT:
			updateMember(message.getText());
			break;
		case MSG_HEADER_TYPE_MEMBER_UPDATE:
			handleMemberUpdate(message.getText());
			break;
		case MSG_HEADER_TYPE_ADMIN_CHANGE:
			handleAdminChange(message.getText());
			break;
		case MSG_HEADER_TYPE_GET_GROUP_INFO:
			handleGroupInfoRequest(message);
			break;
		case MSG_HEADER_TYPE_RECEIVE_GROUP_INFO:
			try {
				handleGroupInfoReceived(message);
			} catch (GroupDoesNotExistException e) {
				// handle error
			}
			break;
		default:
			break;
		}
	}
	
	/* Message Handlers */
	
	/**
	 * Handles the storage of a plain text message and pushes the message to the current 
	 * {@link GroupChatRoomListener} if present.
	 * @param message The message to be parsed and stored.
	 */
	private void handlePlainTextMessage(LinphoneChatMessage message){
		
		GroupChatMessage m = messenger.handlePlainTextMessage(message);
		
		try {
			storage.saveTextMessage(group_id, m);
			if (listener != null) listener.onMessageReceived(m);
		} catch (GroupDoesNotExistException e) {
			// should not occur, this group uses it's own valid id
		}
	}
	
	/**
	 * Handles the persistence of member updates.
	 * @param message The message to be parsed.
	 */
	private void handleMemberUpdate(String message){
		
		MemberUpdateInfo info = messenger.handleMemberUpdate(message);
		
		try {
			Iterator<GroupChatMember> it;
	
			it = info.added.iterator();
			while (it.hasNext()){
				try {storage.addMember(group_id, it.next());} catch (MemberExistsException e) {
					// member exists would have been handled on admin side
				}
			}
	
			it = info.removed.iterator();
			while (it.hasNext()){
				GroupChatMember m = it.next();
				try {
					storage.removeMember(group_id, m);
				} catch (MemberDoesNotExistException e1) {
					// ignore this case as it should not happen
				}
				
				if (lc.getDefaultProxyConfig().getIdentity().equals(m.sip)){
					
					members = new LinkedList<>();
					try {
						LinphoneGroupChatManager.getInstance().deleteGroupChat(group_id);
					} catch (GroupDoesNotExistException | IsAdminException e) {}
				}
			}
			
			it = info.confirmed.iterator();
			while (it.hasNext()) {
				GroupChatMember member = it.next();
				try {
					storage.setMemberStatus(group_id, member);
				} catch (MemberDoesNotExistException e) {
					
					// add the member and update
					try {
						storage.addMember(group_id, member);
						storage.setMemberStatus(group_id, member);
					} catch (GroupDoesNotExistException | MemberExistsException | MemberDoesNotExistException ex){
						// ignore
					}
				
				}
			}
			
			// get updated versions from database.
			members = storage.getMembers(group_id); 
		} catch (GroupDoesNotExistException e){
			
		}
	}
	
	/**
	 * Handles the event in which the admin of the group has changed.
	 * @param message The message to be parsed.
	 */
	private void handleAdminChange(String message){
		
		GroupChatMember m = messenger.handleAdminChange(message);
		
		try {
			storage.setAdmin(group_id, m);
			admin = m.sip;
		} catch (GroupDoesNotExistException e) {
			// reached this group, valid group_id in use.
		} catch (MemberDoesNotExistException e) {
			// TODO
		}
	}
	
	/**
	 * Sends the group info to the client requesting it.
	 * @param message A {@link LinphoneChatMessage} object used 
	 * to obtain the sender's address.
	 */
	private void handleGroupInfoRequest(LinphoneChatMessage message){
		
		// TODO delegate to messenger
		try {
			GroupChatData group = new GroupChatData();
			group.admin = admin;
			group.group_id = group_id;
			group.group_name = group_name;
			group.encryption_type = storage.getEncryptionType(group_id);
			group.members = members;
			
			LinphoneChatRoom cr = lc.getOrCreateChatRoom(message.getFrom().asStringUriOnly());
			String m = MessageParser.stringifyGroupChatData(group);
			LinphoneChatMessage _message = cr.createLinphoneChatMessage(m);
			_message.addCustomHeader(MSG_HEADER_TYPE, MSG_HEADER_TYPE_RECEIVE_GROUP_INFO);
			cr.sendChatMessage(_message);
			cr.deleteMessage(_message);
		} catch (GroupDoesNotExistException e){
			// group uses valid id
		}
	}
	
	/**
	 * Handles potentially new group information after requesting upon start up.
	 * @param message The message containing the group information.
	 * @throws GroupDoesNotExistException If the accessed group id is invalid.
	 */
	private void handleGroupInfoReceived(LinphoneChatMessage message) throws GroupDoesNotExistException {
		
		if (updated) return;
		
		GroupChatData group = MessageParser.parseGroupChatData(message.getText());
		
		if (!group.group_id.equals(this.group_id)) return; // not meant for this group
		
		try {
			// compare details
			if (!group.admin.equals(admin)){
				this.admin = group.admin;
				try {
					storage.setAdmin(group_id, new GroupChatMember("", this.admin, false));
				} catch (MemberDoesNotExistException e) {
					// valid admin is handled on admin side.
				}
			}
			
			if (!group.group_name.equals(group_name)){
				this.group_name = group.group_name;
				storage.setGroupName(group_id, group_name);
			}
		} catch (GroupDoesNotExistException e){
			// group uses valid id
		}
		
		// copy members
		LinkedList<GroupChatMember> tmp = getMembers();
		Iterator<GroupChatMember> it1;
		
		// remove like members from lists, remaining are either new or removed.
		it1 = tmp.iterator();
		Iterator<GroupChatMember> it2;
		while (it1.hasNext()){
			it2 = group.members.iterator();
			GroupChatMember m1 = it1.next();
			while (it2.hasNext()){
				GroupChatMember m2 = it2.next();
				if (m1.sip.equals(m2.sip)){
					it1.remove();
					it2.remove();
					break;
				}
			}
		}
		
		if (tmp.size() == 0 && group.members.size() == 0) return; // members are the same.
		
		// remove members that were not matched with the new group
		it1 = tmp.iterator();
		while (it1.hasNext()){
			
			GroupChatMember m = it1.next();
			
			if (lc.getDefaultProxyConfig().getIdentity().equals(m.sip)){ // member is this client
				
				try {// delete group chat
					LinphoneGroupChatManager.getInstance().deleteGroupChat(group_name);
					return;
				} catch (IsAdminException e) {
					// ignore, 
				}
			} else { // else not this client
				
				try{
					storage.removeMember(group_id, m);
				} catch (GroupDoesNotExistException | MemberDoesNotExistException e){
					// valid id, ignore member if not in group
				}
			}
		
			it1.remove();
		}
		
		// add members
		it2 = group.members.iterator();
		while (it2.hasNext()){
			try {storage.addMember(group_id, it2.next());} catch (MemberExistsException e){
				//member exists, ignore
			}
		}
		
		members = storage.getMembers(group_id);
	}

	/* Getters & Setters */
	
	public void setGroupImage(Bitmap image) throws PermissionRequiredException {
		
		if (!lc.getDefaultProxyConfig().getIdentity().equals(admin)) throw new PermissionRequiredException();
		
		this.image = image;
	}
	
	public Bitmap getGroupImage(){
		
		return image;
	}
	
	public void setName(String name) throws PermissionRequiredException {
		
		if (!lc.getDefaultProxyConfig().getIdentity().equals(admin)) throw new PermissionRequiredException();
		
		try {
			storage.setGroupName(group_id, name);
			this.group_name = name;
			// TODO broadcast name change -- not supported yet.
		} catch (GroupDoesNotExistException e) {
			// valid group id
		}
	}
	
	public String getName(){
		
		return group_name;
	}
	
	public String getGroupId(){
		
		return group_id;
	}
	
	/**
	 * Creates and returns a deep copy of the group members.
	 * @return A list of {@link GroupChatMember}s.
	 */
	public LinkedList<GroupChatMember> getMembers(){
		
		LinkedList<GroupChatMember> members = new LinkedList<>();
		
		Iterator<GroupChatMember> it = this.members.iterator();
		while (it.hasNext()){
			
			GroupChatMember m = it.next();
			members.add(new GroupChatMember(m.name, m.sip, m.pending));
		}
		
		return members;
	}
	
	/**
	 * Creates and returns a deep copy of the group's members, excluding self.
	 * Used specifically for sending messages.
	 * @return A list of {@link GroupChatMember}s.
	 */
	private LinkedList<GroupChatMember> getOtherMembers(){
		
		LinkedList<GroupChatMember> members = new LinkedList<>();
		
		Iterator<GroupChatMember> it = this.members.iterator();
		while (it.hasNext()){
			
			GroupChatMember m = it.next();
			if (!lc.getDefaultProxyConfig().getIdentity().equals(m.sip)){
				members.add(new GroupChatMember(m.name, m.sip, m.pending));
			}
		}
		
		return members;
	}
	
	/**
	 * Sets a listner for the {@link LinphoneGroupChatRoom}.
	 * @param listner The listner to receive push messages from the {@link LinphoneGroupChatRoom}.
	 * @throws GroupChatListenerIsSetException If a listener is currently set.
	 */
	public synchronized void setGroupChatRoomListener(GroupChatRoomListener listner) throws GroupChatListenerIsSetException{
		
		if (this.listener != null) throw new GroupChatListenerIsSetException(); // TODO not sure if necessary, might use a lock instead.
		this.listener = listner;
	}
	
	/**
	 * Removes the current {@link GroupChatRoomListener}.
	 */
	public void unsetGroupChatListner(){
		
		// TODO maybe check that the caller is the set listener, if not deny permission.
		listener = null;
	}
	
	/**
	 * Sets the admin of the group.
	 * @param member The new admin.
	 * @throws PermissionRequiredException If not the admin.
	 */
	public void setAdmin(GroupChatMember member) throws PermissionRequiredException {
		
		if (!lc.getDefaultProxyConfig().getIdentity().equals(admin)) throw new PermissionRequiredException();
		
		try {
			
			storage.setAdmin(group_id, member);
			messenger.sendMessage(member, getOtherMembers(), lc);
		} catch (GroupDoesNotExistException e){
			// valid id
		} catch (MemberDoesNotExistException e) {
			// member should not show if not valid
		}
	}
	
	public String getAdmin(){
		
		return admin;
	}
	
	/**
	 * Changes the current messaging strategy, only if that new strategy is uses no encryption.
	 * @param messenger The new {@link MessagingStrategy}.
	 * @throws PermissionRequiredException If caller is not the admin client.
	 */
	public void setMessagingStrategy(MessagingStrategy messenger) throws PermissionRequiredException {
		
		if (!admin.equals(lc.getDefaultProxyConfig().getIdentity())) throw new PermissionRequiredException();
		try {
			if (storage.getEncryptionType(group_id) != EncryptionType.None) return;
			this.messenger = messenger;
		} catch (GroupDoesNotExistException e) {
			// valid id
		}
	}
	
	public EncryptionType getEncryptionType(){
		try {
			return storage.getEncryptionType(group_id);
		} catch (GroupDoesNotExistException e){
			return EncryptionType.None; // default, should not reach here, as valid id
		}
	}

	/**
	 * Send a text message to the group.
	 * @param message The message string.
	 */
	public void sendMessage(String message) {

		try {
			GroupChatMessage m = new GroupChatMessage();
			m.sender = lc.getDefaultProxyConfig().getIdentity();
			m.direction = MessageDirection.Outgoing;
			m.state = MessageState.Read;
			m.time = new Date();
			m.message = message;
			storage.saveTextMessage(group_id, m);
			messenger.sendMessage(message, getOtherMembers(), lc);
		} catch (GroupDoesNotExistException e){
			// valid id
		}
	}
	
	public void sendMedia(LinphoneContent content) {

		throw new UnsupportedOperationException();
	}

	/**
	 * Get all the messages for a group chat.
	 * @return A list of the messages.
	 */
	public LinkedList<GroupChatMessage> getHistory() {

		try {
			return storage.getMessages(group_id);
		} catch (GroupDoesNotExistException e){
			return new LinkedList<GroupChatMessage>();
		}
	}

	/**
	 * Get a limited number of messages for the group chat.
	 * @param limit The number of messages requested.
	 * @return A list with a maximum of 'limit' number of messages.
	 */
	public LinkedList<GroupChatMessage> getHistory(int limit) {

		try {
			return storage.getMessages(group_id, limit);
		} catch (GroupDoesNotExistException e){
			return new LinkedList<GroupChatMessage>();
		}
	}

	/**
	 * Gets the number of unread messages.
	 * @return The number of messages as an integer.
	 */
	public int getUnreadMessagesCount() {

		try{
			return storage.getUnreadMessageCount(group_id);
		} catch (GroupDoesNotExistException e){
			return -1;
		}
	}

	/**
	 * Delete all the group's messages.
	 */
	public void deleteHistory() {

		try {
			storage.deleteMessages(group_id);
		} catch (GroupDoesNotExistException e){
			// valid id
		}
	}

	public void compose() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Marks all the group chat's messages as read.
	 */
	public void markAsRead() {

		try {
			storage.markChatAsRead(group_id);
		} catch (GroupDoesNotExistException e){
			// valid id
		}
	}

	public void deleteMessage(GroupChatMessage message) {
		throw new UnsupportedOperationException();
	}
}
