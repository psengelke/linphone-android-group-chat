package org.linphone.groupchat.encryption;

public interface AsymmetricEncryptionHandler {

	public String encrypt(String message, String public_key);
	public String decrypt(String messsage);
	public String getPublicKey();
}
