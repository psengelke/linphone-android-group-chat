package org.linphone.groupchat.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

class EncryptionSeedGenerator {
	
	public static String generateSeed(int bitLength) {
		/*SecureRandom rng=new SecureRandom();
		String keySeed=null;
		
		try {
			keySeed=new BigInteger(bitLength, rng).toString(8);
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		return keySeed;*/
		Random rng=new Random();
		byte[] r = new byte[bitLength];
		rng.nextBytes(r);
		return new String(r);
	}
}
