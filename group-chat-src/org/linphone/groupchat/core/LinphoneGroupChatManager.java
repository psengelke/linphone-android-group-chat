package org.linphone.groupchat.core;

import java.util.Iterator;
import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.communication.MessageParser;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.encryption.EncryptionFactory;
import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupChatSizeException;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.InvalidGroupNameException;
import org.linphone.groupchat.exception.InvalidKeySeedException;
import org.linphone.groupchat.exception.IsAdminException;
import org.linphone.groupchat.storage.GroupChatStorage;
import org.linphone.groupchat.storage.GroupChatStorageFactory;

import android.util.Log;

/**
 *	This class is responsible for handling the creation and deletion of groups, handling incoming 
 *	group chat messages and various other administrative functions.
 *
 * @author Paul Engelke
 */
public class LinphoneGroupChatManager {
	
	private LinkedList<LinphoneGroupChatRoom> chats;
	private GroupChatStorage storage;
	
	private LinphoneGroupChatManager() {
		
		storage = GroupChatStorageFactory.getOrCreateGroupChatStorage(GroupChatStorageFactory.StorageAdapterType.SQLite);
		chats = new LinkedList<>();
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
	 * @return The new group's id.
	 * @throws GroupChatExistsException In the case that the group already exists on initialisation.
	 */
	public String createGroupChat(String name, String admin, LinkedList<GroupChatMember> members, EncryptionType type) 
			throws GroupChatSizeException, InvalidGroupNameException, GroupChatExistsException {
		
		if (members.size() < 2) throw new GroupChatSizeException("Group size too small.");

		GroupChatData group = new GroupChatData();
		group.admin = admin;
		group.group_name = name;
		group.group_id = generateGroupId(admin, name);
		group.members = members;
		
		Iterator<GroupChatMember> it = group.members.iterator();
		while (it.hasNext()){
			
			GroupChatMember m = it.next();
			if (m.sip.equals(admin)){
				m.pending = false;
				break;
			}
		}
		
		group.encryption_type = type;
		
		LinphoneGroupChatRoom chat;
		chat = new LinphoneGroupChatRoom(
				group, 
				EncryptionFactory.createEncryptionStrategy(type),
				storage, 
				LinphoneGroupChatListener.getLinphoneCore()
		);
		
		//chat.doInitialization();
		chats.add(chat);
		
		return group.group_id;
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
		if (list != null)
		{
			Iterator<String> it = list.iterator();
			while (it.hasNext()) {
				String gid = (String) it.next();
				if (gid.equals(id)) throw new InvalidGroupNameException("You already have a group with that name.");
			}
		}
		
		return id;
	}
	
	/**
	 * A function for handling the instantiation of group chats on {@link LinphoneGroupChatManager} initialization.
	 */
	private void generateGroupChats(){
		
		LinkedList<GroupChatData> groups = storage.getChatList();
		Log.e("groups.size in LM", "" + groups.size());
		Iterator<GroupChatData> it = groups.iterator();
		while (it.hasNext()) {
			GroupChatData group = it.next();
			try {
				LinphoneGroupChatRoom cr = new LinphoneGroupChatRoom(
						group, 
						EncryptionFactory.createEncryptionStrategy(
								group.encryption_type, storage.getSecretKey(group.group_id)), 
						storage, 
						LinphoneGroupChatListener.getLinphoneCore()
				);
				chats.add(cr);
			} catch (InvalidKeySeedException e) {
				// TODO handle error appropriately...
				e.printStackTrace();
			} catch (GroupDoesNotExistException e){
				// TODO handle case where not all groups could be instantiated.
				e.printStackTrace();
			}
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
	 * @throws IsAdminException If the admin tries to delete the chat before assigning a new admin.
	 */
	public void deleteGroupChat(String id) throws GroupDoesNotExistException, IsAdminException {
		
		Iterator<LinphoneGroupChatRoom> it = chats.iterator();
		while (it.hasNext()) {
			LinphoneGroupChatRoom chat = (LinphoneGroupChatRoom) it.next();
			if (chat.getGroupId().equals(id)){

				chat.removeSelf();
				storage.deleteChat(id);
				it.remove();
				return;
			}
		}
		
		throw new GroupDoesNotExistException("Group does not exist!");
	}
	
	/**
	 * Function returns all the group chats handled by the manager.
	 * @return LinkedList containing the group chats.
	 */
	public LinkedList<LinphoneGroupChatRoom> getGroupChatList(){
		
		LinkedList<LinphoneGroupChatRoom> list = new LinkedList<>();
		
		Iterator<LinphoneGroupChatRoom> it = chats.iterator();
		while (it.hasNext()) {
			LinphoneGroupChatRoom cr = it.next();
			list.add(cr);
		}
		
		return list;
	}
	
	/**
	 * Sends the message to the correct group chat instance.
	 * @param lc The {@link LinphoneCore} instance.
	 * @param cr The {@link LinphoneChatRoom} instance.
	 * @param message The message received for a group chat.
	 * @throws InvalidKeySeedException 
	 */
	public void handleMessage(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {
		
		cr.deleteMessage(message);
		
		String message_type = message.getCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_TYPE);
		if (message_type != null && message_type.equals(LinphoneGroupChatRoom.MSG_HEADER_TYPE_INVITE_STAGE_1)){ // new group
			
			InitialContactInfo info = MessageParser.parseInitialContactInfo(message.getText());
			
			try {
				storage.createGroupChat(info.group);
				
				LinphoneGroupChatRoom group;
				group = new LinphoneGroupChatRoom(
						info.group, 
						EncryptionFactory.createEncryptionStrategy(info.group.encryption_type), 
						storage,
						lc
				);
				
				chats.add(group);
				
				group.receiveMessage(message);
			} catch (GroupChatExistsException e) {
				
			}
			
		} else { // existing group
		
			Log.e("LinphoneGroupChatManager", "handle normal message in LGM");
			String group_id = message.getCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_GROUP_ID);
			Iterator<LinphoneGroupChatRoom> it = chats.iterator();
			while (it.hasNext()) {
				LinphoneGroupChatRoom chat = (LinphoneGroupChatRoom) it.next();
				if (chat.getGroupId().equals(group_id)){
					chat.receiveMessage(message);
					break;
				}
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
