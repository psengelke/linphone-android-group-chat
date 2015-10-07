package org.linphone.groupchat.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;

class GenerateEncryptionSeeds {
	
	public static String generateSeed(int bitLength, int rngSeed) {
		SecureRandom rng=new SecureRandom(String.valueOf(rngSeed).getBytes());
		String keySeed=null;
		
		try {
			keySeed=new BigInteger(bitLength, rng).toString(32);
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		return keySeed;
	}
}
