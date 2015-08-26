package org.linphone.groupchat.interfaces;


import android.graphics.Bitmap;
import android.provider.ContactsContract.CommonDataKinds.SipAddress;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.groupchat.core.LinphoneGroupChatRoom;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;

import java.lang.String;
import java.util.LinkedList;

/**
 *
 * @author David Breetzke
 * @author Paul Engelke
 *
 *	This class serves as an interface for interaction between the persistence tool and a
 *	{@link LinphoneGroupChatRoom} instance.
 */

public interface GroupChatStorage {
	
	/**
	 * Public structure for storing group members for {@link LinphoneGroupChatRoom} instances.
	 */
	public class GroupChatMember {
		
//		public SipAddress sip;
//		public long public_key;
		public String sip;
		public String name;
	}
	
	/**
	 * Public structure for data from a group stored in the database.
	 */
	public class GroupChatData {
		
		public String group_id;
		public String group_name;
		public LinkedList<GroupChatMember> members;
		public String admin;
		public EncryptionType encryption_type;
	}

    public static enum MessageState{
        Read, Unread
    }
    public static enum MessageDirection{
        Incoming, Outgoing
    }


    //public GroupChatStorage getInstance();

    public void close();

    //public void updateMessageStatus(String to, String message, MessageState status);

    //public void updateMessageStatus(String to, String id, MessageState status);

    public void saveTextMessage(String from, String message, MessageDirection direction,
                                       MessageState status, long time);

    public void saveImageMessage(String from, Bitmap image, String url, long time);

    // not sure bitstream exists
    public void saveVoiceRecording(String from, Bitstream voiceNote, long time);

    public LinkedList<LinphoneChatMessage> getMessage(String id);

    /**
     * A function that returns a list of all the groups persistent in the database.
     * @return {@link LinkedList} of {@link GroupChatData} objects.
     */
    public LinkedList<GroupChatData> getChatList();

    //maybe make return Boolean? -- could do, we have to look at return types where possible as well
    // as exceptions for testability and system control
    // and stability
    public void deleteChat(String groupId);

    public void markChatAsRead(String groupId);

    public LinkedList<GroupChatMember> getMembers(String groupId);

    public void updateEncryptionType(String id, EncryptionHandler.EncryptionType type);

    /**
     * Persists a new group chat to the database.
     * @param data The group chat information in a format understandable by the {@link GroupChatData} implementation.
     */
    public void createGroupChat(GroupChatData data);
    public void updateMemberPublicKey();
}
