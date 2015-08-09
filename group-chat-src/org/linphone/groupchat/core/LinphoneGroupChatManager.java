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
		
	}
	
	public void createGroupChat(String name, LinphoneAddress admin, LinkedList<LinphoneAddress> members /*, EncryptionType encryption_type*/){
		
	}
	
	public void deleteGroupChat(String id){
		
	}
	
	public LinkedList<LinphoneGroupChatRoom> getGroupChatList(){
		
		return null;
	}
	
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
