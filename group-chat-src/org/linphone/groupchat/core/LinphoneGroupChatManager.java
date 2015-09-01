package org.linphone.groupchat.core;

import java.util.Iterator;
import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.encryption.AES256EncryptionHandler;
import org.linphone.groupchat.encryption.NoEncryptionStrategy;
import org.linphone.groupchat.encryption.SomeEncryptionStrategy;
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupChatSizeException;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.InvalidGroupNameException;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatInfo;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.storage.GroupChatStorageAndroidImpl;
import org.linphone.groupchat.interfaces.EncryptionStrategy;
import org.linphone.groupchat.interfaces.GroupChatStorage;

/**
 * 
 * @author Paul Engelke
 *
 *	This class is responsible for handling the creation and deletion of groups, handling incoming 
 *	group chat messages and various other administrative functions.
 */
public class LinphoneGroupChatManager {
	

	
	private LinkedList<LinphoneGroupChatRoom> chats;
	private GroupChatStorage storage;
	
	private LinphoneGroupChatManager() {
		
		storage = GroupChatStorageAndroidImpl.getInstance(); // use a factory instead
		generateGroupChats();
	}
	
	/**
	 * Function to create a new group chat.
	 * @param name	The name of the new group.
	 * @param admin The creator of the group.
	 * @param members The members in the group (including the administrator).
	 * @param type The type of encryption to be used for the group chat messages.
	 * @throws GroupChatSizeException In the event that the group size is too small.
	 * @throws InvalidGroupNameException In the event that the creator chose a name that matches an existing group 
	 * owned by the creator.
	 * @throws GroupChatExistsException 
	 */
	public void createGroupChat(String name, String admin, LinkedList<GroupChatMember> members, EncryptionType type) 
			throws GroupChatSizeException, InvalidGroupNameException, GroupChatExistsException {
		
		if (members.size() < 2) throw new GroupChatSizeException("Group size too small.");

		GroupChatData group = new GroupChatData();
		group.admin = admin;
		group.group_name = name;
		group.group_id = generateGroupId(admin, name);
		group.members = members;
		LinphoneGroupChatRoom chat = new LinphoneGroupChatRoom(
				group, 
				createEncryptionStrategy(type), 
				storage, 
				null
		);
		
		chat.doInitialization();
		
		chats.add(chat);
	}
	
	/**
	 * A helper method for {@link #generateGroupChats()} that creates the {@link LinphoneGroupChatRoom} instances and appends 
	 * them to the chat list.
	 * @param name The name of the group.
	 * @param id The group ID.
	 * @param admin The admin of the group.
	 * @param members The list of members in the group.
	 * @param type The type of encryption to be used.
	 */
	private void createGroupChat(GroupChatData group){
		
		chats.add(new LinphoneGroupChatRoom(
				group, 
				createEncryptionStrategy(group.encryption_type), 
				storage, 
				null
		));
	}
	
	/**
	 * A function for generating a new group chat identification, as a concatenation of the admin's address and the name of the group.
	 * @param admin_uri The first part of the ID.
	 * @param group_name The second part of the ID.
	 * @return The new group chat ID.
	 * @throws InvalidGroupNameException In the event that another chat exists with the same ID, i.e. where the admin owns another group 
	 * with the same name.
	 */
	private String generateGroupId(String admin_uri, String group_name) throws InvalidGroupNameException {
		
		String id =  + ':' + group_name.replaceAll(" ", "");
		
		LinkedList<String> list = storage.getChatIdList();
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String gid = (String) it.next();
			if (gid.equals(id)) throw new InvalidGroupNameException("You already have a group with that name.");
		}
		
		return id;
	}
	
	/**
	 * A function for handling the instantiation of group chats on {@link LinphoneGroupChatManager} initialisation.
	 */
	private void generateGroupChats(){
		
		LinkedList<GroupChatData> groups = storage.getChatList();
		Iterator<GroupChatData> it = groups.iterator();
		while (it.hasNext()) {
			GroupChatData group = it.next();
			createGroupChat(group);
		}
	}
	
	/**
	 * Factory method for creating {@link EncryptionStrategy} instances.
	 * @param type The {@link EncryptionType} to be used by the group chat instance.
	 * @return A {@link EncryptionStrategy} instance that matches the parameter.
	 */
	private EncryptionStrategy createEncryptionStrategy(EncryptionType type){
		switch (type) {
		case None:
			return new SomeEncryptionStrategy(new AES256EncryptionHandler());
		default:
			return new NoEncryptionStrategy();
		}
	}
	
	/**
	 * A function that returns a {@link LinphoneGroupChatRoom} instance that matches the id parameter.
	 * @param id The id of the requested chat.
	 * @return The {@link LinphoneGroupChatRoom} instance.
	 * @throws GroupDoesNotExistException In the event that the id is invalid.
	 */
	public LinphoneGroupChatRoom getGroupChat(String id) throws GroupDoesNotExistException {
		
		Iterator<LinphoneGroupChatRoom> it = chats.iterator();
		while (it.hasNext()) {
			LinphoneGroupChatRoom room = (LinphoneGroupChatRoom) it.next();
			if (room.getGroupId().equals(id)){
				return room;
			}
		}
		
		throw new GroupDoesNotExistException("Group does not exist!"); // or return null?
	}
	
	/**
	 * Removes a group chat from the client.
	 * @param id The ID of the group chat to be deleted.
	 * @throws GroupDoesNotExistException
	 * 
	 * TODO : work out how to remove self and assign new admin / remove self and prevent messages from other members
	 * 			if admin can't be reached, try another member (proxy admin)
	 */
	public void deleteGroupChat(String id) throws GroupDoesNotExistException {
		
		LinphoneGroupChatRoom chat =  getGroupChat(id);
		chat.removeSelf(); // needs work
		storage.deleteChat(id);
	}
	
	/**
	 * Function returns a LinkedList object containing the identification information of the group chats.
	 * @return LinkedList containing group chat identification.
	 */
	public LinkedList<GroupChatInfo> getGroupChatList(){
		
		LinkedList<GroupChatInfo> list = new LinkedList<>();
		
		Iterator<LinphoneGroupChatRoom> it = chats.iterator();
		while (it.hasNext()) {
			LinphoneGroupChatRoom room = (LinphoneGroupChatRoom) it.next();
			list.add(new GroupChatInfo(room.getGroupId(), room.getName()));
		}
		
		return list;
	}
	
	/**
	 * Sends the message to the correct group chat instance.
	 * @param lc The {@link LinphoneCore} instance.
	 * @param cr The {@link LinphoneChatRoom} instance.
	 * @param message The message received for a group chat.
	 */
	public void handleMessage(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message){
		
		String group_id = message.getCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_GROUP_ID);
		cr.deleteMessage(message);
		Iterator<LinphoneGroupChatRoom> it = chats.iterator();
		while (it.hasNext()) {
			LinphoneGroupChatRoom chat = (LinphoneGroupChatRoom) it.next();
			if (chat.getGroupId().equals(group_id)){
				chat.receiveMessage(message);
				break;
			}
		}
	}
	
	/* -- Singleton Declarations -- */
	
	/**
	 * Getter method for the singleton.
	 * @return The {@link LinphoneGroupChatManager} singleton instance.
	 */
	public static LinphoneGroupChatManager getInstance(){
		
		return InstanceHolder.INSTANCE;
	}
	
	/**
	 * This class provides a thread-safe lazy initialisation of the singleton.
	 */
	private static class InstanceHolder {
		
		private static final LinphoneGroupChatManager INSTANCE = new LinphoneGroupChatManager();
	}
}
