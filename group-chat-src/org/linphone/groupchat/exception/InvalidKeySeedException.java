package org.linphone.groupchat.exception;

/**
 * 
 * An error for invalid key values used by EncryptionHandlers.
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
