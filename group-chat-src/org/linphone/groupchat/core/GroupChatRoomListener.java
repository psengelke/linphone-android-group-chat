package org.linphone.groupchat.core;

import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;

/**
 * This interface's intent is to support UI component that require {@link LinphoneGroupChatRoom} updates 
 * to be pushed to the component. 
 * 
 * @author Paul Engelke
 *
 */
public interface GroupChatRoomListener {
	
	public static final String GROUP_CHAT_SYSTEM_MESSAGE = "GroupChatRoomListner.group_chat_system_message";
	
	public void onMessageReceived(GroupChatMessage message);
}
