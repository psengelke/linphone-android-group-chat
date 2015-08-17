package org.linphone.groupchat.interfaces;


import java.lang.String;

/**
 *
 * @author David Breetzke
 *
 *	This class serves as an interface for use by an {@link LinphoneGroupChatRoom} instance.
 */

public interface EncryptionStrategy {

    public static void sendMessage(String message, GroupChatMember[] members, LinphoneCore lc){}

    public static String receiveMessage(String message){}

    public EncryptionType getEncryptionType(){}

}
