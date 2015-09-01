package org.linphone.groupchat.encryption;

import org.linphone.groupchat.interfaces.EncryptionStrategy;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;

public class EncryptionFactory {

	/**
	 * Factory method for creating {@link EncryptionStrategy} instances.
	 * @param type The {@link EncryptionType} to be used by the group chat instance.
	 * @return A {@link EncryptionStrategy} instance that matches the parameter.
	 */
	public static EncryptionStrategy createEncryptionStrategy(EncryptionType type){
		switch (type) {
		case None:
			return new SomeEncryptionStrategy(new AES256EncryptionHandler());
		default:
			return new NoEncryptionStrategy();
		}
	}
}
