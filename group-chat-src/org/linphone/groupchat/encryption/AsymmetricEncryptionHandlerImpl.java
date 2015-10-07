package org.linphone.groupchat.encryption;

public class AsymmetricEncryptionHandlerImpl implements AsymmetricEncryptionHandler {
	// asymmetric
	protected String key_private;
	protected String key_public;
	
	@Override
	public String encrypt(String message, String public_key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String decrypt(String messsage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPublicKey() {
		return key_public;
	}

}
