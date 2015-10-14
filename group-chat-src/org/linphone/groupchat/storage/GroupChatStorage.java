package org.linphone.groupchat.storage;

import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.core.LinphoneGroupChatRoom;
import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.MemberDoesNotExistException;
import org.linphone.groupchat.exception.MemberExistsException;

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

    public void close();

    /**
     * Persists a text message.
     * @param id The group id.
     * @param message The message object.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public void saveTextMessage(String id, GroupChatMessage message) throws GroupDoesNotExistException;
    
    /**
     * Persists an image to the database.
     * @param id The group id.
     * @param message The message object.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public void saveImageMessage(String id, GroupChatMessage message) throws GroupDoesNotExistException;

    /**
     * Persists a voice recording to the database.
     * @param id The group id.
     * @param message The message object.
     * @throws GroupDoesNotExistException if the group does not exist.
     * 
     * TODO: determine the correct data structure to use.
     */
    public void saveVoiceRecording(String id, GroupChatMessage message) throws GroupDoesNotExistException;

    /**
     * Retrieves the messages for a group chat.
     * @param id The group chat ID.
     * @return	A list of group chat messages.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public LinkedList<GroupChatMessage> getMessages(String id) throws GroupDoesNotExistException;
    
    /**
     * Retrieves the messages for a group chat up to a limit..
     * @param id The group chat ID.
     * @return	A list of group chat messages.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public LinkedList<GroupChatMessage> getMessages(String id, int limit) throws GroupDoesNotExistException;

    /**
     * A function that returns a list of all the groups persistent in the database.
     * @return {@link LinkedList} of {@link GroupChatData} objects.
     */
    public LinkedList<GroupChatData> getChatList();
    
    /**
     * Retrieves all the group IDs of existing groups.
     * @return A linkedlist of string IDs.
     */
    public LinkedList<String> getChatIdList();

    /**
     * Retrieves a list of members for a group.
     * @param groupId The id of the group in question.
     * @return A list of {@link GroupChatMember} items.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public LinkedList<GroupChatMember> getMembers(String groupId) throws GroupDoesNotExistException;

    /**
     * Persists a new group chat to the database.
     * @param data The group chat information in a format understandable by the {@link GroupChatData} implementation.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public void createGroupChat(GroupChatData data) throws GroupChatExistsException;
    
    /**
     * Adds a new member to the group.
     * @param id The id of the group chat.
     * @param member The member to be added.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public void addMember(String id, GroupChatMember member) throws GroupDoesNotExistException, MemberExistsException;

    /**
     * Acquires the number of unread messages for a group.
     * @param id The group id.
     * @return A count of the unread messages.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public int getUnreadMessageCount(String id) throws GroupDoesNotExistException;
    
    /**
     * Marks all the new group messages as read.
     * @param id The group id.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public void markChatAsRead(String id) throws GroupDoesNotExistException;
    
    /**
     * Changes the name of the group.
     * @param name The new group name.
     * @param id The group's id.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public void setGroupName(String id, String name) throws GroupDoesNotExistException;

	/**
	* Sets the secret key for the group chat where encryption is used.
	* @param id The group chat id.
	* @param key The new secret key.
	* @throws GroupDoesNotExistException if the group does not exist.
	*/
	public void setSecretKey(String id, String key) throws GroupDoesNotExistException;
	
	/**
	 * Get the secret key seed.
	 * @param id The group chat id.
	 * @throws GroupDoesNotExistException if the group does not exist.
	 */
	public String getSecretKey(String id) throws GroupDoesNotExistException;
	
    /**
     * Sets the encryption type used by the group.
     * @param id The group id.
     * @param type The encryption type to be used.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public void setEncryptionType(String id, EncryptionType type) throws GroupDoesNotExistException;
    
    /**
     * Get the encryption type used by the group.
     * @param id The group id.
     * @return The encryption type.
     * @throws GroupDoesNotExistException if the group does not exist.
     */
    public EncryptionType getEncryptionType(String id) throws GroupDoesNotExistException;
    
    /**
     * Updates the pending status of a group member.
     * @param member The member to be updated.
     * @throws GroupDoesNotExistException if the group does not exist.
     * @throws MemberDoesNotExistException if the member is not in the group.
     */
    public void setMemberStatus(String id, GroupChatMember member) throws GroupDoesNotExistException, MemberDoesNotExistException;
    
    /**
     * Changes the admin of the group.
     * @param id The group id.
     * @param member The new admin.
     * @throws GroupDoesNotExistException If the group id matches no group in the database.
     */
    public void setAdmin(String id, GroupChatMember member) throws GroupDoesNotExistException, MemberDoesNotExistException;
    
    /**
     * Removes a member from a group.
     * @param id The group id.
     * @param member The member to be removed.
     * @throws GroupDoesNotExistException if the group chat does not exist.
     * @throws MemberDoesNotExistException if the member is not in the group.
     */
    public void removeMember(String id, GroupChatMember member) throws GroupDoesNotExistException, MemberDoesNotExistException;
    
    /**
     * Deletes all the messages in a group's history.
     * @param id The group id.
     * @throws GroupDoesNotExistException if the group chat doesn't exist;
     */
    public void deleteMessages(String id) throws GroupDoesNotExistException;
    
    /**
     * Deletes the specified group from the database.
     * @param id The identification of the group chat.
     * @throws GroupDoesNotExistException Where the chat could not be found or deleted from the database.
     */
    public void deleteChat(String id) throws GroupDoesNotExistException;
}
