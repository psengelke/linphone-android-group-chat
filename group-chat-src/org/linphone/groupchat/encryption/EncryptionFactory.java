package org.linphone.groupchat.encryption;

import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.InvalidKeySeedException;

public class EncryptionFactory {
	
	private final static int AES256_SEED_LENGTH = 256;
	private final static int RSA_SEED_LENGTH = 256;
    
	/**
	 * Factory method for creating {@link MessagingStrategy} instances.
	 * @param type The {@link EncryptionType} to be used by the group chat instance.
	 * @return A {@link MessagingStrategy} instance that matches the parameter.
	 */
	public static MessagingStrategy createEncryptionStrategy(EncryptionType type) {
		switch (type) {
		case AES256:
			SymmetricEncryptionHandler shandler = new AES256EncryptionHandler(GenerateEncryptionSeeds.generateSeed(AES256_SEED_LENGTH));
			AsymmetricEncryptionHandler ahandler = new RSAEncryptionHandler(GenerateEncryptionSeeds.generateSeed(RSA_SEED_LENGTH));
			MessagingStrategy strategy = new EncryptedMessagingStrategy(shandler, ahandler);
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
			SymmetricEncryptionHandler shandler = new AES256EncryptionHandler(seed);
			AsymmetricEncryptionHandler ahandler = new RSAEncryptionHandler(GenerateEncryptionSeeds.generateSeed(RSA_SEED_LENGTH));
			MessagingStrategy strategy = new EncryptedMessagingStrategy(shandler, ahandler);
			return strategy;
		default:
			return new UnencryptedMessagingStrategy();
		}
	}
}
