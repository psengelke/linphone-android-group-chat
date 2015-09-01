package org.linphone.groupchat.interfaces;


import java.lang.String;
import java.util.LinkedList;

import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.DataExchangeFormat.MemberUpdateInfo;

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

    public void sendMessage(String message, LinkedList<GroupChatMember> members, LinphoneCore lc);
    
    /**
     * Handler for initial contact messages (when a new member is added).
     * @param message The message to be parsed and analysed.
     * @param id The group id for persistence purposes.
     * @param storage The storage adapter instance.
     * @return Information on the member communicating.
     */
    public GroupChatMember handleInitialContactMessage(String message, String id, GroupChatStorage storage);
    
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
    public GroupChatMember handleAdminChange(String message, String id, String storage);
    
    /**
     * Handler for encryption type changes.
     * @param message The message to be parsed.
     * @param id The group id for persistence purposes.
     * @param storage The storage adapter instance.
     * @return A new encryption strategy object.
     */
    public EncryptionStrategy handleEncryptionStrategyChange(String message, String id, GroupChatStorage storage);
    
    @Deprecated
    public String receiveMessage(String message);
    
    public EncryptionHandler.EncryptionType getEncryptionType();

}
