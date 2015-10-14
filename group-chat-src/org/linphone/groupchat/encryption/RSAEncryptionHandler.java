package org.linphone.groupchat.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class RSAEncryptionHandler extends AsymmetricEncryptionHandlerImpl implements AsymmetricEncryptionHandler {

	private SecureRandom rng;
	private Key publicKey, privateKey;
	
	public RSAEncryptionHandler(String keySeed) {
		try {
			
			rng=new SecureRandom(keySeed.getBytes());
			KeyPairGenerator gen=KeyPairGenerator.getInstance("RSA", "BC");
			gen.initialize(1024, rng);
			KeyPair pair=gen.generateKeyPair();
			publicKey=pair.getPublic();
			privateKey=pair.getPrivate();
			key_public=pair.getPublic().getEncoded().toString();
			key_private=pair.getPrivate().getEncoded().toString();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public String encrypt(String message, String public_key) {
		try {
			Cipher cipher=Cipher.getInstance("RSA/None/NoPadding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey, rng);
//			byte[] cipherText=cipher.doFinal(message.getBytes());
			return Base64.encodeToString(cipher.doFinal(message.getBytes()), Base64.DEFAULT);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	@Override
	public String decrypt(String messsage) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(cipher.doFinal(Base64.decode(messsage, Base64.DEFAULT)));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getPublicKey() {
		return super.getPublicKey();
	}

}
