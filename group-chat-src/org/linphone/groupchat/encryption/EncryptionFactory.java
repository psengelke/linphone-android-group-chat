package org.linphone.groupchat.encryption;

import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.InvalidKeySeedException;

public class EncryptionFactory {
    
	/**
	 * Factory method for creating {@link MessagingStrategy} instances.
	 * @param type The {@link EncryptionType} to be used by the group chat instance.
	 * @return A {@link MessagingStrategy} instance that matches the parameter.
	 */
	public static MessagingStrategy createEncryptionStrategy(EncryptionType type) {
		switch (type) {
		case AES256:
			EncryptionHandler handler = new AES256EncryptionHandler();
			try {
				handler.setSecretKey(handler.generateSeed());
			} catch (InvalidKeySeedException e) {
				// this should never be reached as handler.generateSeed() will always return a valid seed.
			}
			MessagingStrategy strategy = new EncryptedMessagingStrategy(handler);
			return strategy;
		default:
			return new UnencryptedMessagingStrategy();
		}
	}
	
	/**
	 * Factory method for creating {@link MessagingStrategy} instances.
	 * @param type The {@link EncryptionType} to be used by the group chat instance.
	 * @param seed The value used to seed the secret key for a group chat (set to null if no encryption is used).
	 * @return A {@link MessagingStrategy} instance that matches the parameter.
	 * @throws InvalidKeySeedException If the passed key is invalid for the specified {@link EncryptionType}.
	 */
	public static MessagingStrategy createEncryptionStrategy(EncryptionType type, String seed) throws InvalidKeySeedException{
		switch (type) {
		case AES256:
			EncryptionHandler handler = new AES256EncryptionHandler();
			handler.setSecretKey(seed);
			MessagingStrategy strategy = new EncryptedMessagingStrategy(handler);
			return strategy;
		default:
			return new UnencryptedMessagingStrategy();
		}
	}
}
