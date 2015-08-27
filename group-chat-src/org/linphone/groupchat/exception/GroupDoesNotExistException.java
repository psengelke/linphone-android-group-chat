package org.linphone.groupchat.exception;

/**
 * 
 * @author Paul Engelke
 *
 *	An exception class for recognising an event involving a non-existing group instance.
 */
public class GroupDoesNotExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public GroupDoesNotExistException(String message) {
		super(message);
	}
}
