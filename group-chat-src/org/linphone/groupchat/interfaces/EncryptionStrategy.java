package org.linphone.groupchat.interfaces;


import java.lang.String;
import java.util.LinkedList;

import org.linphone.core.LinphoneCore;

/**
 *
 * @author David Breetzke
 * @author Paul Engelke
 *
 *	This class serves as an interface for use by an {@link LinphoneGroupChatRoom} instance.
 */

public interface EncryptionStrategy {

    public void sendMessage(String message, LinkedList<GroupChatStorage.GroupChatMember> members, LinphoneCore lc);
    public String receiveMessage(String message);
    public EncryptionHandler.EncryptionType getEncryptionType();

}
