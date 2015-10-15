package org.linphone.groupchat.encryption;


import java.lang.String;
import java.util.LinkedList;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.communication.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.communication.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.storage.GroupChatStorage;

/**
 *	This class serves as an interface for use by an {@link LinphoneGroupChatRoom} instance. The 
 *	strategy will provide messaging functionality for the group chat dependent on the type of encryption chosen 
 *	by the group admin.
 *
 * @author David Breetzke
 * @author Paul Engelke
 */

public interface MessagingStrategy {

    public static enum EncryptionType{
        None, AES256
    }
	
	/**
	 * Sends a plain text message to the group.
	 * @param id The group's id, needed for header.
	 * @param message The message to be sent.
	 * @param members The members of the group.
	 * @param lc The {@link LinphoneCore} instance.
	 */
    public void sendMessage(String id, String message, LinkedList<GroupChatMember> members, LinphoneCore lc);
    
    /**
     * Sends a message containing initial contact information. (An invite to the group.)
     * @param id The group's id, needed for header.
     * @param info The initial contact information.
     * @param member The communicating member, i.e. the admin or the invitee.
     * @param lc The {@link LinphoneCore} instance.
     */
	public void sendMessage(String id, InitialContactInfo info, GroupChatMember member, LinphoneCore lc);
	
	/**
	 * Sends a message containing new additions or removals from the group, also confirmed members.
	 * @param id The group's id, needed for header.
	 * @param info The information object containing member changes.
	 * @param members The group chat members.
	 * @param lc The {@link LinphoneCore} instance.
	 */
	public void sendMessage(String id, MemberUpdateInfo info, LinkedList<GroupChatMember> members, LinphoneCore lc);
	
	/**
	 * Sends a message containing an admin change.
	 * @param id The group's id, needed for header.
	 * @param info The new admin information.
	 * @param members The group chat members.
	 * @param lc The {@link LinphoneCore} instance.
	 */
	public void sendMessage(String id, GroupChatMember info, LinkedList<GroupChatMember> members, LinphoneCore lc);
	
    /**
     * Handler for the initial contact messages.
     * @param message The message to be parsed and analysed.
     * @param id The group id for persistence purposes.
     * @param storage The storage adapter instance.
     * @param lc The {@link LinphoneCore} to be used for sending a reply.
     */
    public void handleInitialContactMessage(LinphoneChatMessage message, String id, GroupChatStorage storage, LinphoneCore lc);
    
    /**
     * Handler for member updates (additions and removals).
     * @param message The message to be parsed and analysed.
     * @return An object containing the members added and removed.
     */
    public MemberUpdateInfo handleMemberUpdate(String message);
    
    /**
     * Handler for plain text messages.
     * @param message The message to be parsed.
     * @param id The group chat id for persistence purposes.
     * @param storage The storage adapter instance.
     * @return A message object.
     */
    public GroupChatMessage handlePlainTextMessage(LinphoneChatMessage message);
    
    /**
     * Handler for media messages.
     * @param message The message to be parsed.
     * @return A message object.
     */
    public GroupChatMessage handleMediaMessage(LinphoneChatMessage message);
    
    /**
     * Handler for admin changes.
     * @param message The message to be parsed.
     * @return The new group chat admin.
     */
    public GroupChatMember handleAdminChange(String message);
    
    /**
     * Handler for encryption type changes.
     * @param message The message to be parsed.
     * @param id The group id for persistence purposes.
     * @param storage The storage adapter instance.
     * @return A new encryption strategy object.
     */
    public MessagingStrategy handleEncryptionStrategyChange(String message, String id, GroupChatStorage storage);
}
