package org.linphone.groupchat.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import android.util.Log;

class EncryptionSeedGenerator {
	
	public static SecureRandom random = new SecureRandom();
	
	public static String generateSeed(int bitLength) {
		
		BigInteger r = new BigInteger(bitLength, random);
		
		Log.e("generateSeed", r.toString(32)); // check string
		
		return r.toString(32);
		
//		Random rng=new Random();
//		byte[] r = new byte[bitLength];
//		rng.nextBytes(r);
//		Log.e("generateSeed", r.toString()); // check string
//		return new String(r);
	}
}
