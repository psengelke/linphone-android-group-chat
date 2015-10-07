package org.linphone.groupchat.encryption;

import java.lang.String;

import org.linphone.groupchat.exception.InvalidKeySeedException;

/**
 *	This class serves as an interface for handling symmetric encryption and is used by a
 *	{@link LinphoneGroupChatRoom} instance.
 *
 * @author David Breetzke
 * @author Paul Engelke
 */

interface SymmetricEncryptionHandler {
    
    public String encrypt(String message);
    public String decrypt(String message);
    public void setSecretKey(String key) throws InvalidKeySeedException;
}
