package org.linphone.groupchat.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import android.util.Base64;
import android.util.Log;

class EncryptionSeedGenerator {
	
	public static SecureRandom random = new SecureRandom();
	
	public static String generateSeed(int bitLength) {
		
		BigInteger r = new BigInteger(bitLength, random);
		
		return Base64.encodeToString(r.toString(32).getBytes(), Base64.DEFAULT);
	}
}
