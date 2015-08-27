package org.linphone.groupchat.exception;

/**
 * 
 * @author Paul Engelke
 *
 * An exception class for recognising invalid group chat sizes.
 */
public class GroupChatSizeException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public GroupChatSizeException(String message) {
		super(message);
	}
}
