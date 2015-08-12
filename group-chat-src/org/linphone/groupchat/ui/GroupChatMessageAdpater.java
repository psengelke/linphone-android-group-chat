package org.linphone.groupchat.ui;

import org.linphone.groupchat.core;


public class GroupChatMessageAdpater extends ChatMessageAdapter
{
	
	public GroupChatMessageAdapter(Context context, LinphoneChatMessage[] history) 
	{
		this.history = history;
		this.context = context;
	}
	
	@Override
	public void refreshHistory()
	{
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinphoneChatMessage message = history[position];
		
		BubbleChat bubble = new BubbleChat(context, message, GroupChatMessagingFragment.this);
		View v = bubble.getView();
		
		registerForContextMenu(v);
		RelativeLayout rlayout = new RelativeLayout(context);
		rlayout.addView(v);
		
		return rlayout;
	}
}
