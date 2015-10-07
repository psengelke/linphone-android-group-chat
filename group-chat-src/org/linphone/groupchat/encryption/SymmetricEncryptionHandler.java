package org.linphone.groupchat.encryption;

import java.lang.String;

/**
 *	This class serves as an interface for handling symmetric encryption and is used by a
 *	{@link LinphoneGroupChatRoom} instance.
 *
 * @author David Breetzke
 * @author Paul Engelke
 */

interface SymmetricEncryptionHandler {
    
    public String encrypt(String message, String keySeed);
    public String decrypt(String message);
}
