package org.linphone.groupchat.encryption;

public class RSAEncryptionHandler extends AsymmetricEncryptionHandlerImpl implements AsymmetricEncryptionHandler {

	public RSAEncryptionHandler(String keySeed) {
		//TODO: generate key pair
	}

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
		return super.getPublicKey();
	}

}
