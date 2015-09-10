package org.linphone.groupchat.exception;

/**
 * 
 * @author Paul Engelke
 * 
 * An exception for tasks that require a certain level of access to be executed.
 *
 */
public class PermissionRequiredException extends Exception {

	private static final long serialVersionUID = 1L;

	public PermissionRequiredException(){
		
		super("Permission denied.");
	}
	
	public PermissionRequiredException(String message){
		super(message);
	}
}
