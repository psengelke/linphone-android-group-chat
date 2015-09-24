package org.linphone.groupchat.encryption;

import java.lang.String;

/**
 *	This class serves as an interface for handling encryption and is used by a
 *	{@link LinphoneGroupChatRoom} instance.
 *
 * @author David Breetzke
 * @author Paul Engelke
 */

public interface EncryptionHandler {

    public static enum EncryptionType{
        None, AES256
    }
    
    public String encrypt(String message, long key);
    public String decrypt(String message);
    public long getPublicKey();
    public long getSecretKey();
    public EncryptionHandler.EncryptionType getEncryptionType();
}
