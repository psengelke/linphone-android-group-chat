package org.linphone.groupchat.core;

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
import org.linphone.groupchat.communication.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.communication.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.encryption.EncryptionStrategy;
import org.linphone.groupchat.encryption.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupChatSizeException;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.IsAdminException;
import org.linphone.groupchat.exception.MemberDoesNotExistException;
import org.linphone.groupchat.exception.PermissionRequiredException;
import org.linphone.groupchat.storage.GroupChatStorage;

import android.graphics.Bitmap;

/**
 * This class serves as the chat {@link LinphoneChatRoom} for group chats. It provides all the functionality 
 * required for Linphone users to interact with groups of which they are members.
 * 
 * @author Paul Engelke
 */
@SuppressWarnings("deprecation")
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
	
	private EncryptionStrategy encryption_strategy;
	
	private LinphoneCore lc;
	private GroupChatStorage storage;
	
	private static final int MAX_MEMBERS = 50;
	
	/**
	 * Constructor.
	 * 
	 * @param group Contains the necessary data for group construction.
	 * @param encryption_strategy The encryption strategy to be used.
	 * @param storage The storage adapter for persistence purposes.
	 * @param lc The {@link LinphoneCore} instance for the client.
	 */
	public LinphoneGroupChatRoom(GroupChatData group, 
			EncryptionStrategy encryption_strategy, 
			GroupChatStorage storage, 
			LinphoneCore lc){
		
		this.group_id = group.group_id;
		this.group_name = group.group_name;
		this.admin = group.admin;
		this.members = group.members;
		
		this.encryption_strategy = encryption_strategy;
		
		this.storage = storage;
		this.lc = lc;
		
		requestGroupInfo();
	}
	
	/**
	 * Sends invites to all group members. Call only when this is a new group.
	 * @throws GroupChatExistsException In the case that the group has already been initialised.
	 */
	public void doInitialization() throws GroupChatExistsException {
		
		GroupChatData group = new GroupChatData();
		group.group_id = this.group_id;
		group.group_name = this.group_name;
		group.admin = this.admin;
		group.members = getMembers();
		group.encryption_type = this.getEncryptionType();
		
		storage.createGroupChat(group);
		
		InitialContactInfo info = new InitialContactInfo();
		info.group = group;
		
		Iterator<GroupChatMember> it = getOtherMembers().iterator();
		while (it.hasNext()) {
			GroupChatMember member = (GroupChatMember) it.next();
			encryption_strategy.sendMessage(info, member, lc);
		}
	}
	
	/**
	 * This function sends a broadcast message to all group members, requesting the latest 
	 * group chat state.
	 * Called by the constructor once the group has been initialised.
	 */
	private void requestGroupInfo(){
		
		Iterator<GroupChatMember> it = getOtherMembers().iterator();
		while (it.hasNext()){
			GroupChatMember m = it.next();
			if (!lc.isMyself(m.sip)){
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

		//TODO: Figure out how the bloody hell to get this client's sip address.
		
		if (lc.isMyself(admin)) throw new IsAdminException("You must assign a new admin first.");
		
		MemberUpdateInfo info = new MemberUpdateInfo();
		info.removed.add(new GroupChatMember(null, null, false));
		encryption_strategy.sendMessage(info, getOtherMembers(), lc);
	}
	
	/**
	 * A function to add a new member to the group chat. The method creates a new database 
	 * entry for the pending message.
	 * @param member The member to be added.
	 * @throws PermissionRequiredException If not the admin.
	 * @throws GroupChatSizeException If the new member addition exceeds the maximum group size.
	 */
	public void addMember(GroupChatMember member) throws PermissionRequiredException, GroupChatSizeException {
		
		if (!lc.isMyself(admin)) throw new PermissionRequiredException();
		
		if (members.size() == MAX_MEMBERS) throw new GroupChatSizeException("Exceeds group chat size.");
		
		member.pending = true;
		storage.addMember(group_id, member);
		members.add(member);
		
		InitialContactInfo info = new InitialContactInfo();
		info.group = new GroupChatData();
		info.group.group_id = group_id;
		info.group.group_name = group_name;
		info.group.members = members;
		info.group.encryption_type = encryption_strategy.getEncryptionType();
		
		encryption_strategy.sendMessage(info, member, lc);
	}
	
	/**
	 * A function that updates the status of a pending invite to confirmed and 
	 * broadcast to the rest of the group.
	 * @param message The text body containing the member's details.
	 */
	private void updateMember(String message){
		
		GroupChatMember member = MessageParser.parseGroupChatMember(message);
		try {
			storage.updateMemberStatus(group_id, member);
			
			// now tell the group of the update.
			MemberUpdateInfo info = new MemberUpdateInfo();
			info.confirmed.add(new GroupChatMember(member.name, member.sip, true));
			encryption_strategy.sendMessage(info, getOtherMembers(), lc);
		} catch (MemberDoesNotExistException e) {
			
			// member was removed before adding, send remove to user.
			LinkedList<GroupChatMember> m = new LinkedList<>();
			m.add(member);
			MemberUpdateInfo info = new MemberUpdateInfo();
			info.removed.add(member);
			encryption_strategy.sendMessage(info, m, lc);
		}
	}
	
	/**
	 * A function to remove a member from the group chat. This may be done by a member removing themselves 
	 * or in the event that the admin wishes to remove a member.
	 * @param address The member to be removed.
	 * @throws IsAdminException If the admin tries to remove themselves before assigning a new admin.
	 */
	public void removeMember(GroupChatMember member) throws PermissionRequiredException, IsAdminException {

		//TODO: Need to possibly call remove group function from {@link LinphoneGroupChatManager}.
		
		if (!lc.isMyself(admin)) throw new PermissionRequiredException();
		if (lc.isMyself(member.sip)) throw new IsAdminException("You must assign a new admin first.");
		
		storage.removeMember(group_id, member);
		
		//now tell the group
		MemberUpdateInfo info = new MemberUpdateInfo();
		info.removed.add(member);
		encryption_strategy.sendMessage(info, getOtherMembers(), lc);
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
			encryption_strategy.handleInitialContactMessage(message.getText(), lc);
			break;
		case MSG_HEADER_TYPE_INVITE_STAGE_3:
			encryption_strategy.handleInitialContactMessage(message.getText(), group_id, storage, lc);
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
		
		GroupChatMessage m = encryption_strategy.handlePlainTextMessage(message);
		
		storage.saveTextMessage(group_id, m);
		
		if (listener != null) listener.onMessageReceived(m);
	}
	
	/**
	 * Handles the persistence of member updates.
	 * @param message The message to be parsed.
	 */
	private void handleMemberUpdate(String message){
		
		MemberUpdateInfo info = encryption_strategy.handleMemberUpdate(message);
		
		Iterator<GroupChatMember> it;

		it = info.added.iterator();
		while (it.hasNext()){
			storage.addMember(group_id, it.next());
		}

		it = info.removed.iterator();
		while (it.hasNext()){
			GroupChatMember m = it.next();
			storage.removeMember(group_id, m);
			
			if (lc.isMyself(m.sip)){
				
				members = new LinkedList<>();
				try {
					LinphoneGroupChatManager.getInstance().deleteGroupChat(group_id);
				} catch (GroupDoesNotExistException | IsAdminException e) {}
			}
		}
		
		it = info.confirmed.iterator();
		while (it.hasNext()) {
			try {
				storage.updateMemberStatus(group_id, it.next());
			} catch (MemberDoesNotExistException e) {}
		}
		
		members = storage.getMembers(group_id); // get updated versions from database.
	}
	
	/**
	 * Handles the event in which the admin of the group has changed.
	 * @param message The message to be parsed.
	 */
	private void handleAdminChange(String message){
		
		GroupChatMember m = encryption_strategy.handleAdminChange(message);
		
		try {
			storage.updateAdmin(group_id, m);
			admin = m.sip;
		} catch (GroupDoesNotExistException e) {
			//TODO: handle error
		}
	}
	
	/**
	 * Sends the group info to the client requesting it.
	 * @param message A {@link LinphoneChatMessage} object used 
	 * to obtain the sender's address.
	 */
	private void handleGroupInfoRequest(LinphoneChatMessage message){
		
		GroupChatData group = new GroupChatData();
		group.admin = admin;
		group.group_id = group_id;
		group.group_name = group_name;
		group.encryption_type = encryption_strategy.getEncryptionType();
		group.members = members;
		
		LinphoneChatRoom cr = lc.getOrCreateChatRoom(message.getFrom().asStringUriOnly());
		LinphoneChatMessage _message = cr.createLinphoneChatMessage("");
		_message.addCustomHeader(MSG_HEADER_TYPE, MSG_HEADER_TYPE_RECEIVE_GROUP_INFO);
		cr.sendChatMessage(_message);
		cr.deleteMessage(_message);
	}
	
	/**
	 * Handles potentially new group information after requesting upon start up.
	 * @param message The message containing the group information.
	 * @throws GroupDoesNotExistException If the accessed group id is invalid.
	 */
	private void handleGroupInfoReceived(LinphoneChatMessage message) throws GroupDoesNotExistException {
		
		if (updated) return;
		
		GroupChatData group = MessageParser.parseGroupChatData(message.getText());
		
		if (!group.group_id.equals(group_id)) return; // not meant for this group
		
		// compare details
		
		if (!group.admin.equals(admin)){
			admin = group.admin;
			storage.updateAdmin(group_id, new GroupChatMember("", admin, false));
		}
		
		if (!group.group_name.equals(group_name)){
			group_name = group.group_name;
			storage.updateGroupName(group_id, group_name);
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
			
			if (lc.isMyself(m.sip)){
				// TODO: remove this group.
				return;
			}
			
			storage.removeMember(group_id, m);
			it1.remove();
		}
		
		// add members
		it2 = group.members.iterator();
		while (it2.hasNext()){
			storage.addMember(group_id, it2.next());
		}
		
		members = storage.getMembers(group_id);
	}

	/* Getters & Setters */
	
	public void setGroupImage(Bitmap image) throws PermissionRequiredException{
		
		if (!lc.isMyself(admin)) throw new PermissionRequiredException();
		
		this.image = image;
	}
	
	public Bitmap getGroupImage(){
		
		return image;
	}
	
	public void setName(String name) throws PermissionRequiredException {
		
		if (!lc.isMyself(admin)) throw new PermissionRequiredException();
		
		this.group_name = name;
		
		// broadcast name change -- not supported yet.
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
			if (!lc.isMyself(m.sip)){
				members.add(new GroupChatMember(m.name, m.sip, m.pending));
			}
		}
		
		return members;
	}
	
	public void setGroupChatRoomListener(GroupChatRoomListener listner){
		
		this.listener = listner;
	}
	
	public void unsetGroupChatListner(){
		
		listener = null;
	}
	
	/**
	 * Sets the admin of the group.
	 * @param member The new admin.
	 * @throws PermissionRequiredException If not the admin.
	 */
	public void setAdmin(GroupChatMember member) throws PermissionRequiredException {
		
		if (!lc.isMyself(admin)) throw new PermissionRequiredException();
		
		encryption_strategy.sendMessage(member, getOtherMembers(), lc);
	}
	
	public String getAdmin(){
		
		return admin;
	}
	
	public void setEncryptionStrategy(EncryptionStrategy encryption_strategy) {
		this.encryption_strategy = encryption_strategy;
	}
	
	public EncryptionType getEncryptionType(){
		return encryption_strategy.getEncryptionType();
	}
	
	public void destroy() {
		throw new UnsupportedOperationException();
	}

	public void sendMessage(String message) {

		encryption_strategy.sendMessage(message, getOtherMembers(), lc);
	}
	
	public void sendMedia(LinphoneContent content) {

		throw new UnsupportedOperationException();
	}

	public LinkedList<GroupChatMessage> getHistory() {

		throw new UnsupportedOperationException();
	}

	public LinkedList<GroupChatMessage> getHistory(int limit) {

		throw new UnsupportedOperationException();
	}

	public int getUnreadMessagesCount() {

		throw new UnsupportedOperationException();
	}

	public void deleteHistory() {
		throw new UnsupportedOperationException();
	}

	public void compose() {
		throw new UnsupportedOperationException();
	}

	public void markAsRead() {
		throw new UnsupportedOperationException();
	}

	public void deleteMessage(GroupChatMessage message) {
		throw new UnsupportedOperationException();
	}
}
