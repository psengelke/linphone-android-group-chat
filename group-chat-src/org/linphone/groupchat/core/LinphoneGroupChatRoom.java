package org.linphone.groupchat.core;

import java.util.Iterator;
import java.util.LinkedList;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatMessage.State;
import org.linphone.core.LinphoneChatMessage.StateListener;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.encryption.MessageParser;
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupChatSizeException;
import org.linphone.groupchat.exception.PermissionRequiredException;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.interfaces.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.interfaces.EncryptionStrategy;
import org.linphone.groupchat.interfaces.GroupChatStorage;

import android.graphics.Bitmap;

/**
 * 
 * @author Paul Engelke
 *
 * This class serves as the chat {@link LinphoneChatRoom} for group chats. It provides all the functionality 
 * required for Linphone users to interact with groups of which they are members.
 */
@SuppressWarnings("deprecation")
public class LinphoneGroupChatRoom implements LinphoneChatRoom {
	
	public static final String MSG_HEADER_GROUP_ID = "GROUP-CHAT-ID";
	public static final String MSG_HEADER_TYPE  = "GROUP-CHAT-MESSAGE-TYPE";
	public static final String MSG_HEADER_TYPE_MESSAGE = "LinphoneGroupChatRoom.plain_message";
	public static final String MSG_HEADER_TYPE_INVITE = "LinphoneGroupChatRoom.invite";
	public static final String MSG_HEADER_TYPE_INVITE_ACCEPT = "LinphoneGroupChatRoom.accept_invite";
	public static final String MSG_HEADER_TYPE_MEMBER_UPDATE = "LinphoneGroupChatRoom.member_update";
	public static final String MSG_HEADER_TYPE_ADMIN_CHANGE = "LinphoneGroupChatRoom.admin_update";
	// more
	
	private LinkedList<GroupChatMember> members;
	
	private String admin;
	private String group_id;
	private String group_name;
	private Bitmap image;
	
	private EncryptionStrategy encryption_strategy;
	
	private LinphoneCore linphone_core;
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
		this.linphone_core = lc;
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
		group.members = this.members;
		group.encryption_type = this.getEncryptionType();
		
		storage.createGroupChat(group);
		
		InitialContactInfo info = new InitialContactInfo();
		info.group = group;
		
		Iterator<GroupChatMember> it = members.iterator();
		while (it.hasNext()) {
			GroupChatMember member = (GroupChatMember) it.next();
			encryption_strategy.sendMessage(info, member, linphone_core);
		}
	}
	
	/**
	 * Removes self from the group if self is not the admin.
	 * @return True if not the admin, else false.
	 */
	public boolean removeSelf(){
		
		if ("" == admin) return false;
		
		MemberUpdateInfo info = new MemberUpdateInfo();
		info.removed.add(new GroupChatMember(null, admin, false));
		encryption_strategy.sendMessage(info, members, linphone_core);
		
		return true;
	}
	
	/**
	 * A function to add a new member to the group chat. The method creates a new database 
	 * entry for the pending message.
	 * @param member The member to be added.
	 * @throws PermissionRequiredException If not the admin.
	 * @throws GroupChatSizeException If the new member addition exceeds the maximum group size.
	 */
	public void addMember(GroupChatMember member) throws PermissionRequiredException, GroupChatSizeException {
		
		if (!"".equals(admin)) throw new PermissionRequiredException();
		
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
		
		encryption_strategy.sendMessage(info, member, linphone_core);
	}
	
	/**
	 * A function that updates the status of a pending invite to confirmed and 
	 * broadcast to the rest of the group.
	 * @param message The text body containing the member's details.
	 */
	private void updateMember(String message){
		
		GroupChatMember member = MessageParser.parseGroupChatMember(message);
		storage.updateMemberStatus(member);
		
		// now tell the group of the update.
	}
	
	/**
	 * A function to remove a member from the group chat. This may be done by a member removing themselves 
	 * or in the event that the admin wishes to remove a member.
	 * @param address The member to be removed.
	 */
	public void removeMember(String address) throws PermissionRequiredException {

		// TODO and throw exception
		if (!"".equals(admin)) throw new PermissionRequiredException();
	}
	
	/**
	 * This method is called when a new message for the group has been received by the Linphone client. 
	 * The method handles administrative tasks concerning a new message.
	 * @param message
	 */
	public void receiveMessage(LinphoneChatMessage message){
		
		String  type = message.getCustomHeader(MSG_HEADER_TYPE);
		switch (type) {
		case MSG_HEADER_TYPE_MESSAGE:
			encryption_strategy.handlePlainTextMessage(message.getText(), group_id, storage);
			break;
		case MSG_HEADER_TYPE_INVITE:
			encryption_strategy.handleInitialContactMessage(message.getText(), group_id, storage);
			break;
		case MSG_HEADER_TYPE_INVITE_ACCEPT:
			updateMember(message.getText());
			break;
		case MSG_HEADER_TYPE_MEMBER_UPDATE:
			encryption_strategy.handleMemberUpdate(message.getText(), group_id, storage);
			break;
		case MSG_HEADER_TYPE_ADMIN_CHANGE:
			encryption_strategy.handleAdminChange(message.getText(), group_id, storage);
			break;
		default:
			break;
		}
	}

	/* Getters & Setters */
	
	public void setGroupImage(Bitmap image) throws PermissionRequiredException{
		
		if (!"".equals(admin)) throw new PermissionRequiredException();
		
		this.image = image;
	}
	
	public Bitmap getGroupImage(){
		
		return image;
	}
	
	public void setName(String name) throws PermissionRequiredException{
		
		if (!"".equals(admin)) throw new PermissionRequiredException();
		
		this.group_name = name;
		
		// broadcast name change
	}
	
	public String getName(){
		
		return group_name;
	}
	
	public String getGroupId(){
		
		return group_id;
	}
	
	/**
	 * Getter for the group's members.
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
	 * Sets the admin of the group.
	 * @param member The new admin.
	 * @throws PermissionRequiredException If not the admin.
	 */
	public void setAdmin(GroupChatMember member) throws PermissionRequiredException {
		
		// TODO access this user's sip address
		if (!"".equals(admin)) throw new PermissionRequiredException();
		
		LinkedList<GroupChatMember> members = new LinkedList<>();
		members.add(member);
		
		encryption_strategy.sendMessage(member, members, linphone_core);
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
	
	/* LinphoneChatRoom Implementation*/ 
	
	@Override
	public LinphoneAddress getPeerAddress() {

		throw new UnsupportedOperationException();
	}

	@Override
	public void sendMessage(String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendMessage(LinphoneChatMessage message, StateListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LinphoneChatMessage createLinphoneChatMessage(String message) {

		throw new UnsupportedOperationException();
	}

	@Override
	public LinphoneChatMessage[] getHistory() {

		throw new UnsupportedOperationException();
	}

	@Override
	public LinphoneChatMessage[] getHistory(int limit) {

		throw new UnsupportedOperationException();
	}

	@Override
	public LinphoneChatMessage[] getHistoryRange(int begin, int end) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void destroy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getUnreadMessagesCount() {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getHistorySize() {

		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteHistory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void compose() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRemoteComposing() {

		throw new UnsupportedOperationException();
	}

	@Override
	public void markAsRead() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteMessage(LinphoneChatMessage message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LinphoneChatMessage createLinphoneChatMessage(String message, String url, State state, long timestamp,
			boolean isRead, boolean isIncoming) {

		throw new UnsupportedOperationException();
	}

	@Override
	public LinphoneCore getCore() {

		throw new UnsupportedOperationException();
	}

	@Override
	public LinphoneChatMessage createFileTransferMessage(LinphoneContent content) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void sendChatMessage(LinphoneChatMessage message) {
		throw new UnsupportedOperationException();
	}
}
