package org.linphone.groupchat.exception;

import org.linphone.groupchat.core.LinphoneGroupChatRoom;

/**
 * 
 * An exception that can be thrown by a {@link LinphoneGroupChatRoom} where 
 * a listener is already set at time of assignment.
 * 
 * @author Paul Engelke
 *
 */
public class GroupChatListenerIsSetException extends Exception {

	private static final long serialVersionUID = 1L;

	public GroupChatListenerIsSetException(){}
}
