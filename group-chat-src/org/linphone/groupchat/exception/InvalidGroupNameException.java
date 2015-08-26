package org.linphone.groupchat.exception;

/**
 * 
 * @author Paul Engelke
 *
 *	An exception class for recognising an invalid group names.
 */
public class InvalidGroupNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidGroupNameException(String message) {
		super(message);
	}
}
