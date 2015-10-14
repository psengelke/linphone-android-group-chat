package org.linphone.groupchat.exception;
/**
 * 
 * @author Jessica Lessev
 *
 * Throw in cases where a member is already present in the group.
 */
public class MemberExistsException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public MemberExistsException(){
		super();
	}
}

