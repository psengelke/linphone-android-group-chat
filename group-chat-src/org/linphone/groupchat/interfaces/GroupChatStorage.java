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

    public void saveTextMessage(String from, String message, MessageDirection direction,
                                       MessageState status, long time);

    public void saveImageMessage(String from, Bitmap image, String url, long time);

    // not sure bitstream exists TODO: find out what "Bitstream" should be
    public void saveVoiceRecording(String from, /*Bitstream voiceNote,*/ long time);

    /**
     * Retrieves the messages for a group chat.
     * @param id The group chat ID.
     * @return	A list of gruop chat messages.
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

    public void markChatAsRead(String groupId);

    public void updateEncryptionType(String id, EncryptionHandler.EncryptionType type);
    
    /**
     * Updates the pending status of a group member.
     * @param member The member to be updated.
     */
    public void updateMemberStatus(GroupChatMember member);
    
    /**
     * Deletes the specified group from the database.
     * @param groupId The identification of the group chat.
     * @throws GroupDoesNotExistException Where the chat could not be found or deleted from the database.
     */
    public void deleteChat(String groupId) throws GroupDoesNotExistException;
}
