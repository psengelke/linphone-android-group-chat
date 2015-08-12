package org.linphone.groupchat.ui;

import org.linphone.ChatFragment;
import org.linphone.groupchat.core;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class GroupChatMessagingFragment extends ChatFragment
{
	private static GroupChatMessagingFragment instance;		// Not in documentation
	
	private TextView groupName;
	private ImageView groupPicture;
	private TextView remoteMemberComposing;
	
	private LinphoneGroupChatRoom chatroom;
	private GroupChatMessageAdapter groupChatMessageAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		instance = this;
		
	}
	
	public void onPause()
	{
		
		
	}
	
	public void onResume()
	{
		
		
	}
	
	
	public static GroupChatMessagingFragment instance() 
	{
		return instance;
	}
	
	private void displayChatHeader(String displayName, String pictureUri) 
	{
		

	}
	
	private void sendImageMessage(String path) 
	{
		
		
	}
	
	private void sendTextMessage(String messageToSend) 
	{
		
		
		
	}
}
