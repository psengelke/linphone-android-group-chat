package org.linphone.groupchat.interfaces;


import java.lang.String;
import java.util.LinkedList;

import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.interfaces.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;

/**
 *
 * @author David Breetzke
 * @author Paul Engelke
 *
 *	This class serves as an interface for use by an {@link LinphoneGroupChatRoom} instance. The 
 *	strategy will provide functionality for the group chat dependent on the type of encryption chosen 
 *	by the group admin.
 */

public interface EncryptionStrategy {

	/**
	 * Sends a plain text message to the group.
	 * @param message The message to be sent.
	 * @param members The members of the group.
	 * @param lc The {@link LinphoneCore} instance.
	 */
    public void sendMessage(String message, LinkedList<GroupChatMember> members, LinphoneCore lc);
    
    /**
     * Sends a message containing initial contact information.
     * @param info The initial contact information.
     * @param member The communicating member, i.e. the admin or the invitee.
     * @param lc The {@link LinphoneCore} instance.
     */
	public void sendMessage(InitialContactInfo info, GroupChatMember member, LinphoneCore lc);
	
	/**
	 * Sends a message containing new additions or removals from the group.
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
     * Handler for initial contact messages (when a new member is added).
     * @param message The message to be parsed and analysed.
     * @param id The group id for persistence purposes.
     * @param storage The storage adapter instance.
     * @param encrypted Whether or not the message is encrypted.
     * @return Information on the member communicating.
     */
    public GroupChatMember handleInitialContactMessage(String message, String id, GroupChatStorage storage, boolean encrypted);
    
    /**
     * Handler for member updates (additions and removals).
     * @param message The message to be parsed and analysed.
     * @param id The group id for persistence purposes.
     * @param storage The storage adapter instance.
     * @return An object containing the members added and removed.
     */
    public MemberUpdateInfo handleMemberUpdate(String message, String id, GroupChatStorage storage);
    
    /**
     * Handler for plain text messages.
     * @param message The message to be parsed.
     * @param id The group chat id for persistence purposes.
     * @param storage The storage adapter instance.
     * @return The string value of the parsed message.
     */
    public String handlePlainTextMessage(String message, String id, GroupChatStorage storage);
    
    /**
     * Handler for media messages.
     * @param message The message to be parsed.
     * @param id The group id for storage purposes.
     * @param storage The storage adapter instance.
     */
    public void handleMediaMessage(String message, String id, GroupChatStorage storage);
    
    /**
     * Handler for admin changes.
     * @param message The message to be parsed.
     * @param id The group chat id for persistence purposes.
     * @param storage The storage adapter instance.
     * @return The new group chat admin.
     */
    public GroupChatMember handleAdminChange(String message, String id, GroupChatStorage storage);
    
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
    
    @Deprecated
    public String receiveMessage(String message);

}
