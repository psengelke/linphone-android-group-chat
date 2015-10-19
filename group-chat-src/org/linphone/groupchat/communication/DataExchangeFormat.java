package org.linphone.groupchat.communication;


import java.util.Date;
import java.util.LinkedList;

import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;

/**
 *	This interface serves as a description of data exchange formats for communication 
 *	between core interfaces and attempts to minimise coupling.
 *
 * @author Paul Engelke
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
		public String public_key;
		public String secret_key;
		
		public InitialContactInfo() {
			
			group = new GroupChatData();
			public_key = "uadfhisdfdisihfsadihsfd";
			secret_key = "kdkfhsdihfisdhfdsih";
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
		
	    public static enum MessageState{
	        Unread, Read
	    }
	    
	    public static enum MessageDirection{
	        Incoming, Outgoing
	    }
		
		public int id;
		public String sender;
		public String message;
		public Date time;
		public MessageState state;
		public MessageDirection direction;
		
		public boolean isOutgoing()
		{
			return (direction == MessageDirection.Outgoing);
		}
		
		public boolean isIncoming()
		{
			return (direction == MessageDirection.Incoming);
		}
		
		public boolean isRead()
		{
			return (state == MessageState.Read);
		}
		
		public boolean isUnread()
		{
			return (state == MessageState.Unread);
		}
	}
}
