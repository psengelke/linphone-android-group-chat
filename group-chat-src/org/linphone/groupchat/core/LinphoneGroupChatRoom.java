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
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupChatSizeException;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.EncryptionHandler;
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
	
	public static final String MSG_HEADER_GROUP_ID = "LinphoneGroupChatRoom.group_id";
	public static final String MSG_HEADER_TYPE_MESSAGE = "LinphoneGroupChatRoom.plain_message";
	// we may have to add new headers for the distributed communication protocol for groups,
	// i.e invite messages, new group member additions and deletions
	
	private LinkedList<GroupChatMember> members;
	
	private String admin;
	private String group_id;
	private String group_name;
	private String group_image_url; // may change to a BitMap?
	
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
	
	public void doInitialization() throws GroupChatSizeException, GroupChatExistsException {
		
		Iterator<GroupChatMember> it = this.members.iterator();
		while (it.hasNext()) {
			GroupChatMember member = (GroupChatMember) it.next();
			
			// don't invite self!!!
		}
		
		if (members.size() < 2) throw new GroupChatSizeException("Group size too small.");
		
		GroupChatData data = new GroupChatData();
		data.group_id = this.group_id;
		data.group_name = this.group_name;
		data.admin = this.admin;
		data.members = this.members;
		data.encryption_type = this.getEncryptionType();
		
		storage.createGroupChat(data);
	}
	
	public void removeSelf(){
		
		// remove self from group
		// if admin,  assign new admin : pass as parameter
	}
	
	/**
	 * A function to add a new member to the group chat. The method creates a new database 
	 * entry for the member and pushes an invite notification to the new member.
	 * @param address The member to be added.
	 * @return True if the addition was successful (that is, the invite was successful) and false otherwise.
	 */
	public boolean addMember(GroupChatMember member){
		
		// add
		return false;
	}
	
	/**
	 * A function to remove a member from the group chat. This may be done by a member removing themselves 
	 * or in the event that the admin wishes to remove a member.
	 * @param address The member to be removed.
	 * @return True if the member was removed, else false.
	 */
	public boolean removeMember(String address){
		
		return false;
	}
	
	/**
	 * This method is called when a new message for the group has been received by the Linphone client. 
	 * The method handles administrative tasks concerning a new message.
	 * @param message
	 */
	public void receiveMessage(LinphoneChatMessage message){
		
		// parse message content based on message headers
	}

	/* Getters & Setters */
	
	public void setGroupImage(String url){
		
		this.group_image_url = url;
	}
	
	public Bitmap getGroupImage(){
		
		return null;
	}
	
	public void setName(String name){
		
		this.group_name = name;
	}
	
	public String getName(){
		
		return group_name;
	}
	
	public String getGroupId(){
		
		return group_id;
	}
	
	public void setAdmin(LinphoneAddress address){
		
	}
	
	public LinphoneAddress getAdmin(){
		
		return null;
	}
	
	public void setEncryptionStrategy(EncryptionStrategy encryption_strategy) {
		this.encryption_strategy = encryption_strategy;
	}
	
	public EncryptionHandler.EncryptionType getEncryptionType(){
		return encryption_strategy.getEncryptionType();
	}
	
	/* LinphoneChatRoom Implementation*/ 
	
	@Override
	public LinphoneAddress getPeerAddress() {

		return null;
	}

	@Override
	public void sendMessage(String message) {

	}

	@Override
	public void sendMessage(LinphoneChatMessage message, StateListener listener) {
		
	}

	@Override
	public LinphoneChatMessage createLinphoneChatMessage(String message) {

		return null;
	}

	@Override
	public LinphoneChatMessage[] getHistory() {

		return null;
	}

	@Override
	public LinphoneChatMessage[] getHistory(int limit) {

		return null;
	}

	@Override
	public LinphoneChatMessage[] getHistoryRange(int begin, int end) {

		return null;
	}

	@Override
	public void destroy() {

	}

	@Override
	public int getUnreadMessagesCount() {

		return 0;
	}

	@Override
	public int getHistorySize() {

		return 0;
	}

	@Override
	public void deleteHistory() {

	}

	@Override
	public void compose() {
	
	}

	@Override
	public boolean isRemoteComposing() {

		return false;
	}

	@Override
	public void markAsRead() {

	}

	@Override
	public void deleteMessage(LinphoneChatMessage message) {
		
	}

	@Override
	public LinphoneChatMessage createLinphoneChatMessage(String message, String url, State state, long timestamp,
			boolean isRead, boolean isIncoming) {

		return null;
	}

	@Override
	public LinphoneCore getCore() {

		return null;
	}

	@Override
	public LinphoneChatMessage createFileTransferMessage(LinphoneContent content) {

		return null;
	}

	@Override
	public void sendChatMessage(LinphoneChatMessage message) {

	}
}
