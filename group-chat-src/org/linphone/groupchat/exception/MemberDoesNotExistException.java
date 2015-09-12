package org.linphone.groupchat.exception;

/**
 * 
 * @author Paul Engelke
 *
 * Throw in cases where a member is accessed that is not in the group.
 */
public class MemberDoesNotExistException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public MemberDoesNotExistException(){
		super();
	}
}
