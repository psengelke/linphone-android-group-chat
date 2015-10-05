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
 *	strategy will provide functionality for the group chat dependent on the type of encryption chosen 
 *	by the group admin.
 *
 * @author David Breetzke
 * @author Paul Engelke
 */

public interface EncryptionStrategy {

    public static enum EncryptionType{
        None, AES256
    }
	
	/**
	 * Sends a plain text message to the group.
	 * @param message The message to be sent.
	 * @param members The members of the group.
	 * @param lc The {@link LinphoneCore} instance.
	 */
    public void sendMessage(String message, LinkedList<GroupChatMember> members, LinphoneCore lc);
    
    /**
     * Sends a message containing initial contact information. (An invite to the group.)
     * @param info The initial contact information.
     * @param member The communicating member, i.e. the admin or the invitee.
     * @param lc The {@link LinphoneCore} instance.
     */
	public void sendMessage(InitialContactInfo info, GroupChatMember member, LinphoneCore lc);
	
	/**
	 * Sends a message containing new additions or removals from the group, also confirmed members.
	 * @param info The information object containing member changes.
	 * @param members The group chat members.
	 * @param lc The {@link LinphoneCore} instance.
	 */
	public void sendMessage(MemberUpdateInfo info, LinkedList<GroupChatMember> members, LinphoneCore lc);
	
	/**
	 * Sends a message containing an admin change.
	 * @param info The new admin information.
	 * @param members The group chat members.
	 * @param lc The {@link LinphoneCore} instance.
	 */
	public void sendMessage(GroupChatMember info, LinkedList<GroupChatMember> members, LinphoneCore lc);
	
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
    public EncryptionStrategy handleEncryptionStrategyChange(String message, String id, GroupChatStorage storage);
    
    /**
     * Getter for the hidden {@link EncryptionHandler}'s {@link EncryptionType}.
     * @return {@link EncryptionType} of the strategy.
     */
    public EncryptionType getEncryptionType();
}
