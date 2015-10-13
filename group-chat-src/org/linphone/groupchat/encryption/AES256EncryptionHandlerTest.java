package org.linphone.groupchat.encryption;

import static org.junit.Assert.*;

import org.junit.Test;

public class AES256EncryptionHandlerTest {

	/**
	 * Note: In a true random scenario, these tests will fail.
	 * {@link EncryptionSeedGenerator}
	 * 
	 */
	
	String seed=EncryptionSeedGenerator.generateSeed(256);
	AES256EncryptionHandler aes=new AES256EncryptionHandler(seed);
	
	@Test
	public void testAES256EncryptionHandler() {
		
	}
	
	@Test
	public void testEncrypt() {
		assertEquals("rRMoEaRyMo0FCNamm85/Fw==", aes.encrypt("hello"));
	}

	@Test
	public void testDecrypt() {
		assertEquals(aes.decrypt("rRMoEaRyMo0FCNamm85/Fw=="), "hello");
	}

	

}
