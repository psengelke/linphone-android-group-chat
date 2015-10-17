package org.linphone.groupchat.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import android.annotation.SuppressLint;
import android.util.Base64;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncryptionHandler extends AsymmetricEncryptionHandlerImpl implements AsymmetricEncryptionHandler {

	private SecureRandom rng;
//	private Key publicKey, privateKey;
	
	@SuppressLint("TrulyRandom")
	public RSAEncryptionHandler(String keySeed) {
		try {
			
			rng=new SecureRandom(keySeed.getBytes());
			KeyPairGenerator gen=KeyPairGenerator.getInstance("RSA", "BC");
			gen.initialize(1024, rng);
			KeyPair pair=gen.generateKeyPair();
//			publicKey=pair.getPublic();
//			privateKey=pair.getPrivate();
			key_public=Base64.encodeToString(pair.getPublic().getEncoded(), Base64.DEFAULT);
			key_private=Base64.encodeToString(pair.getPrivate().getEncoded(), Base64.DEFAULT);
			
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
			byte[] publicKeyBytes=Base64.decode(public_key, Base64.DEFAULT);
			X509EncodedKeySpec publicKeySpec=new X509EncodedKeySpec(publicKeyBytes);
			KeyFactory keyFactory=KeyFactory.getInstance("RSA");
			PublicKey publicKey=keyFactory.generatePublic(publicKeySpec);
			
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
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	@Override
	public String decrypt(String messsage) {
		Cipher cipher;
		try {
			byte[] privateKeyBytes=Base64.decode(key_private, Base64.DEFAULT);
			PKCS8EncodedKeySpec privateKeySpec=new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory keyFactory=KeyFactory.getInstance("RSA");
			PrivateKey privateKey=keyFactory.generatePrivate(privateKeySpec);
			
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
		} catch (InvalidKeySpecException e) {
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
