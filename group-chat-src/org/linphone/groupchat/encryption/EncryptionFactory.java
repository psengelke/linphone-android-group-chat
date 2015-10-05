package org.linphone.groupchat.encryption;

import org.linphone.groupchat.encryption.EncryptionStrategy.EncryptionType;
import org.linphone.groupchat.exception.InvalidKeySeedException;

public class EncryptionFactory {
    
	/**
	 * Factory method for creating {@link EncryptionStrategy} instances.
	 * @param type The {@link EncryptionType} to be used by the group chat instance.
	 * @return A {@link EncryptionStrategy} instance that matches the parameter.
	 * @throws InvalidKeySeedException If a seed could not be generated properly.
	 */
	public static EncryptionStrategy createEncryptionStrategy(EncryptionType type) throws InvalidKeySeedException {
		switch (type) {
		case AES256:
			EncryptionHandler handler = new AES256EncryptionHandler();
			handler.setSecretKey(handler.generateSeed());
			EncryptionStrategy strategy = new SomeEncryptionStrategy(handler);
			return strategy;
		default:
			return new NoEncryptionStrategy();
		}
	}
	
	/**
	 * Factory method for creating {@link EncryptionStrategy} instances.
	 * @param type The {@link EncryptionType} to be used by the group chat instance.
	 * @param seed The value used to seed the secret key for a group chat (set to null if no encryption is used).
	 * @return A {@link EncryptionStrategy} instance that matches the parameter.
	 * @throws InvalidKeySeedException If the passed key is invalid for the specified {@link EncryptionType}.
	 */
	public static EncryptionStrategy createEncryptionStrategy(EncryptionType type, String seed) throws InvalidKeySeedException{
		switch (type) {
		case AES256:
			EncryptionHandler handler = new AES256EncryptionHandler();
			handler.setSecretKey(seed);
			EncryptionStrategy strategy = new SomeEncryptionStrategy(handler);
			return strategy;
		default:
			return new NoEncryptionStrategy();
		}
	}
}
