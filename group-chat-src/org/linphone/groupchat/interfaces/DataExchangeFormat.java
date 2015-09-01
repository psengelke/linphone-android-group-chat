package org.linphone.groupchat.interfaces;

import java.util.LinkedList;

import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;

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
	}
	
	/**
	 * Public structure for storing group members for {@link LinphoneGroupChatRoom} instances.
	 */
	public class GroupChatMember {
		public String sip;
		public String name;
	}
}
