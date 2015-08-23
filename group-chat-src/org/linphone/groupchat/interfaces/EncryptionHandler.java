package org.linphone.groupchat.interfaces;

import java.lang.String;

/**
 *
 * @author David Breetzke
 * @author Paul Engelke
 *
 *	This class serves as an interface for handling encryption and is used by a
 *	{@link LinphoneGroupChatRoom} instance.
 */

public interface EncryptionHandler {

    public static enum EncryptionType{
        None, AES256
    }
    
    public String encrypt(String message, long key);
    public String decrypt(String message);
    public long getPublicKey();
    public EncryptionHandler.EncryptionType getEncryptionType();
}
