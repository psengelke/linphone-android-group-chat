package org.linphone.groupchat.encryption;

interface AsymmetricEncryptionHandler {

	public void encrypt(String message);
	public String decrypt(String message);
}
