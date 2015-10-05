package org.linphone.groupchat.exception;

import org.linphone.groupchat.encryption.EncryptionHandler;

/**
 * 
 * An error for invalid key values used by {@link EncryptionHandler}s.
 * 
 * @author Paul Engelke
 *
 */
public class InvalidKeySeedException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidKeySeedException(){}
	
	public InvalidKeySeedException(String message){
		
		super(message);
	}
}
