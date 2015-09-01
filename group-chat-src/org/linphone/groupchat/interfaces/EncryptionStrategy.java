package org.linphone.groupchat.interfaces;


import java.lang.String;
import java.util.LinkedList;

import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.core.LinphoneGroupChatManager.GroupChatMember;

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
    
    public GroupChatMember handleInviteResponse(String message, GroupChatStorage storage);
    public LinkedList<GroupChatMember> handleMemberUpdate(String message, GroupChatStorage storage);
    public String handlePlainTextMessage(String message, GroupChatStorage storage);
    public void handleMediaMessage(String message, GroupChatStorage storage);
    
    
    @Deprecated
    public String receiveMessage(String message);
    
    public EncryptionHandler.EncryptionType getEncryptionType();

}
