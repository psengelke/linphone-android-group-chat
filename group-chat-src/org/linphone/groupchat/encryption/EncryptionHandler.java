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
    
    public void generateAsymmetricKeys();
    public String getPublicKey();
    
    public String encrypt(String message);
    public String decrypt(String message);
    public void setSecretKey(String seed);
    public String getKeySeed();
    
    public EncryptionHandler.EncryptionType getEncryptionType();
    
    public char[] generateSeed();
}
