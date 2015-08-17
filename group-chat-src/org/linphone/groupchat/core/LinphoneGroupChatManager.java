package org.linphone.groupchat.core;

import java.util.LinkedList;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
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
	private GroupChatStorage storage_adapter;
	
	private LinphoneGroupChatManager() {
		
		// GroupChatStorage.getInstance();
	}
	
	public void createGroupChat(String name, LinphoneAddress admin, LinkedList<LinphoneAddress> members /*, EncryptionType encryption_type*/){
		
	}
	
	public LinphoneGroupChatRoom getGroupChat(String id){
		
		return null;
	}
	
	/**
	 * Removes a group chat from the client.
	 * @param id The ID of the group chat to be deleted.
	 */
	public void deleteGroupChat(String id){
		
	}
	
	/**
	 * Function returns a LinkedList object containing the identification information of the group chats.
	 * @return LinkedList containing group chat identification.
	 */
	public LinkedList<String> getGroupChatList(){
		
		return null;
	}
	
	/**
	 * Sends the message to the correct group chat instance.
	 * @param lc The {@link LinphoneCore} instance.
	 * @param cr The {@link LinphoneChatRoom} instance.
	 * @param message The message received for a group chat.
	 */
	public void handleMessage(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message){
		
	}
	
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
