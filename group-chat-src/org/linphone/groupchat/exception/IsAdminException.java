package org.linphone.groupchat.exception;

/**
 * 
 * @author Paul Engelke
 *
 *
 * An exception for when a group admin cannot do something while being the admin 
 * of a group.
 */
public class IsAdminException extends Exception {

	private static final long serialVersionUID = 1L;

	public IsAdminException(String message){
		super(message);
	}
}
