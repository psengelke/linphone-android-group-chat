package org.linphone.groupchat.interfaces;

import java.util.Date;
import java.util.LinkedList;

import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.interfaces.GroupChatStorage.MessageDirection;
import org.linphone.groupchat.interfaces.GroupChatStorage.MessageState;

/**
 * 
 * @author Paul Engelke
 *
 *
 *	This interface serves as a description of data exchange formats for communication 
 *	between core interfaces and attempts to minimise coupling.
 */
public interface DataExchangeFormat {

	/**
	 * Stores group chat data for communication between interfaces.
	 */
	public class GroupChatData {
		
		public String group_id;
		public String group_name;
		public LinkedList<GroupChatMember> members;
		public String admin;
		public EncryptionType encryption_type;
		
		public GroupChatData(){
			group_id = "";
			group_name = "";
			admin = "";
			members = new LinkedList<>();
			encryption_type = EncryptionType.None;
		}
	}
	
	/**
	 * Stores simple data about a group for use by the front-end.
	 */
	public class GroupChatInfo {

		public String id;
		public String name;
		
		public GroupChatInfo(String id, String name){
			this.id = id;
			this.name = name;
		}
	}
	
	/**
	 * Stores group member information for group chat instances.
	 */
	public class GroupChatMember {
		public String sip;
		public String name;
		
		public boolean pending;
		
		public GroupChatMember(String name, String sip, boolean pending){
			
			this.name = name;
			this.sip = sip;
			this.pending = pending;
		}
	}
	
	/**
	 * Stores response information for an initial contact communication session, 
	 * i.e. when a new member is added to a group.
	 */
	public class InitialContactInfo {
		
		public GroupChatData group;
		public long public_key;
		public long secret_key;
		
		public InitialContactInfo() {
			
			group = new GroupChatData();
			public_key = 0;
			secret_key = 0;
		}
	}
	
	/**
	 *  Stores information about member additions and deletions.
	 */
	public class MemberUpdateInfo {
		
		public LinkedList<GroupChatMember> added;
		public LinkedList<GroupChatMember> removed;
		public LinkedList<GroupChatMember> confirmed;
		
		public MemberUpdateInfo(){
			added = new LinkedList<>();
			confirmed = new LinkedList<>();
			removed = new LinkedList<>();
		}
	}
	
	public class GroupChatMessage {
		
		public int id;
		public String sender;
		public String message;
		public Date time;
		public MessageState state;
		public MessageDirection direction;
	}
}
