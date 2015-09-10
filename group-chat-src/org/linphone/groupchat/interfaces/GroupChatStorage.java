package org.linphone.groupchat.interfaces;


import android.graphics.Bitmap;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.groupchat.core.LinphoneGroupChatRoom;
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
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

    public static enum MessageState{
        Read, Unread
    }
    public static enum MessageDirection{
        Incoming, Outgoing
    }

    public void close();

    /**
     * Persists a text message.
     * @param from The sender.
     * @param message The text message.
     * @param direction The direction of the message (incoming, outgoing)
     * @param status The status of the message (read, unread)
     * @param time The time of sending.
     */
    public void saveTextMessage(String from, String message, MessageDirection direction,
                                       MessageState status, long time);
    /**
     * Persists an image to the database.
     * @param from The sender.
     * @param image The image.
     * @param url The image url. TODO url might be redundant.
     * @param time The time of receiving.
     */
    public void saveImageMessage(String from, Bitmap image, String url, long time);

    /**
     * Persists a voice recording to the database.
     * @param from The sender of the voice message.
     * @param time The time at which the message was received.
     * 
     * TODO: determine the correct data structure to use.
     */
    public void saveVoiceRecording(String from, /*Bitstream voiceNote,*/ long time);

    /**
     * Retrieves the messages for a group chat.
     * @param id The group chat ID.
     * @return	A list of group chat messages.
     */
    public LinkedList<LinphoneChatMessage> getMessages(String id);

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
	public void updateSecretKey(String id, Long key);
	
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
    public void updateMemberStatus(GroupChatMember member);
    
    /**
     * Removes a member from a group.
     * @param id The group id.
     * @param member The member to be removed.
     */
    public void removeMember(String id, GroupChatMember member);
    
    /**
     * Deletes the specified group from the database.
     * @param groupId The identification of the group chat.
     * @throws GroupDoesNotExistException Where the chat could not be found or deleted from the database.
     */
    public void deleteChat(String groupId) throws GroupDoesNotExistException;
}
