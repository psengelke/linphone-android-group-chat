package org.linphone.groupchat.storage;

import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.core.LinphoneGroupChatRoom;
import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.MemberDoesNotExistException;

import java.lang.String;
import java.util.LinkedList;

/**
 *	This class serves as an interface for interaction between the persistence tool and a
 *	{@link LinphoneGroupChatRoom} instance.
 *
 *  @author David Breetzke
 *  @author Paul Engelke
 */

public interface GroupChatStorage {

    public static enum MessageState{
        Unread, Read
    }
    public static enum MessageDirection{
        Incoming, Outgoing
    }

    public void close();

    /**
     * Persists a text message.
     * @param id The group id.
     * @param message The message object.
     */
    public void saveTextMessage(String id, GroupChatMessage message);
    
    /**
     * Persists an image to the database.
     * @param id The group id.
     * @param message The message object.
     */
    public void saveImageMessage(String id, GroupChatMessage message);

    /**
     * Persists a voice recording to the database.
     * @param id The group id.
     * @param message The message object.
     * 
     * TODO: determine the correct data structure to use.
     */
    public void saveVoiceRecording(String id, GroupChatMessage message);

    /**
     * Retrieves the messages for a group chat.
     * @param id The group chat ID.
     * @return	A list of group chat messages.
     */
    public LinkedList<GroupChatMessage> getMessages(String id);
    
    /**
     * Retrieves the messages for a group chat up to a limit..
     * @param id The group chat ID.
     * @return	A list of group chat messages.
     */
    public LinkedList<GroupChatMessage> getMessages(String id, int limit);

    /**
     * A function that returns a list of all the groups persistent in the database.
     * @return {@link LinkedList} of {@link GroupChatData} objects.
     */
    public LinkedList<GroupChatData> getChatList();
    
    /**
     * Retrieve a list of group chat IDs.
     * @return A list of string IDs for the existing group chats.
     */
    public LinkedList<String> getChatIdList();

    /**
     * Retrieves a list of members for a group.
     * @param groupId The id of the group in question.
     * @return A list of {@link GroupChatMember} items.
     */
    public LinkedList<GroupChatMember> getMembers(String groupId);

    /**
     * Persists a new group chat to the database.
     * @param data The group chat information in a format understandable by the {@link GroupChatData} implementation.
     */
    public void createGroupChat(GroupChatData data) throws GroupChatExistsException;
    
    /**
     * Adds a new member to the group.
     * @param id The id of the group chat.
     * @param member The member to be added.
     */
    public void addMember(String id, GroupChatMember member);

    /**
     * Acquires the number of unread messages for a group.
     * @param id The group id.
     * @return A count of the unread messages.
     */
    public int getUnreadMessageCount(String id);
    
    /**
     * Marks all the new group messages as read.
     * @param groupId
     */
    public void markChatAsRead(String groupId);
    
    /**
     * Changes the name of the group.
     * @param name The new group name.
     * @param grouId The group's id.
     */
    public void updateGroupName(String grouId, String name);

	/**
	* Sets the secret key for the group chat where encryption is used.
	* @param id The group chat id.
	* @param key The new secret key.
	*/
	public void setSecretKey(String id, String key);
	
	/**
	 * Get the secret key seed.
	 * @param id The group chat id.
	 */
	public String getSecretKey(String id);
	
    /**
     * Changes the encryption type used by the group.
     * @param id The group id.
     * @param type The encryption type to be used.
     */
    public void updateEncryptionType(String id, EncryptionType type);
    
    /**
     * Updates the pending status of a group member.
     * @param member The member to be updated.
     */
    public void updateMemberStatus(String id, GroupChatMember member) throws MemberDoesNotExistException;
    
    /**
     * Changes the admin of the group.
     * @param id The group id.
     * @param member The new admin.
     * @throws GroupDoesNotExistException If the group id matches no group in the database.
     */
    public void updateAdmin(String id, GroupChatMember member) throws GroupDoesNotExistException;
    
    /**
     * Removes a member from a group.
     * @param id The group id.
     * @param member The member to be removed.
     */
    public void removeMember(String id, GroupChatMember member);
    
    /**
     * Deletes all the messages in a group's history.
     * @param id The group id.
     */
    public void deleteMessages(String id);
    
    /**
     * Deletes the specified group from the database.
     * @param groupId The identification of the group chat.
     * @throws GroupDoesNotExistException Where the chat could not be found or deleted from the database.
     */
    public void deleteChat(String groupId) throws GroupDoesNotExistException;
}
