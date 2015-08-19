package org.linphone.groupchat.core;

import java.util.Iterator;
import java.util.LinkedList;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.encryption.AES256EncryptionHandler;
import org.linphone.groupchat.encryption.NoEncryptionStrategy;
import org.linphone.groupchat.encryption.SomeEncryptionStrategy;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.interfaces.EncryptionStrategy;
import org.linphone.groupchat.interfaces.GroupChatStorage;

import exception.GroupChatSizeException;
import exception.GroupDoesNotExistException;


/**
 * 
 * @author Paul Engelke
 *
 *	This class is responsible for handling the creation and deletion of groups, handling incoming 
 *	group chat messages and various other administrative functions.
 */
public class LinphoneGroupChatManager {

	/**
	 * This inner class is used to store basic chat information for requests.
	 */
	public class GroupChatInfo {

		public String id;
		public String name;
		
		public GroupChatInfo(String id, String name){
			this.id = id;
			this.name = name;
		}
	}
	
	private LinkedList<LinphoneGroupChatRoom> chats;
	private GroupChatStorage storage_adapter;
	
	private LinphoneGroupChatManager() {
		
		// GroupChatStorage.getInstance();
	}
	
	/**
	 * Function to create a new group chat.
	 * @param name	The name of the new group.
	 * @param admin The creator of the group.
	 * @param members The members in the group (including the administrator).
	 * @param type The type of encryption to be used for the group chat messages.
	 * @throws GroupChatSizeException In the event that the group size is too small.
	 */
	public void createGroupChat(String name, LinphoneAddress admin, LinkedList<LinphoneAddress> members, 
			EncryptionType type) 
			throws GroupChatSizeException {
		
		if (members.size() < 2) throw new GroupChatSizeException("Group size too small.");
		
		EncryptionStrategy strategy;
		switch (type) {
		case None:
			strategy = new SomeEncryptionStrategy(new AES256EncryptionHandler());
			break;
		default:
			strategy = new NoEncryptionStrategy();
			break;
		}
		
		chats.add(new LinphoneGroupChatRoom(name, "", admin, members, strategy, null, null));
	}
	
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
	 */
	public void deleteGroupChat(String id) throws GroupDoesNotExistException {
		
		// query database and try to delete the group, else throw exception?
		throw new GroupDoesNotExistException("Group does not exist!");
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
		
		// retrieve group chat id from message header, get correct group chat and pass the message on.
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
