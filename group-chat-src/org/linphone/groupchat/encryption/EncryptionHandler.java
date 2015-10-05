package org.linphone.groupchat.encryption;

import java.lang.String;

import org.linphone.groupchat.encryption.EncryptionStrategy.EncryptionType;
import org.linphone.groupchat.exception.InvalidKeySeedException;

/**
 *	This class serves as an interface for handling encryption and is used by a
 *	{@link LinphoneGroupChatRoom} instance.
 *
 * @author David Breetzke
 * @author Paul Engelke
 */

public interface EncryptionHandler {
    
    public void generateAsymmetricKeys();
    public String getPublicKey();
    
    public String encrypt(String message, String keySeed);
    public String decrypt(String message);
    public void setSecretKey(String seed) throws InvalidKeySeedException;
    public String getKeySeed();
    
    public EncryptionType getEncryptionType();
    
    public String generateSeed();
}
