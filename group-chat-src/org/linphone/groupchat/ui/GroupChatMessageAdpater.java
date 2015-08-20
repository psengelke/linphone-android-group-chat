package org.linphone.groupchat.ui;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.ui.BubbleChat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;


public class GroupChatMessageAdpater extends BaseAdapter
{
	LinphoneChatMessage[] history;
	Context context;
	
	public GroupChatMessageAdpater(Context context, LinphoneChatMessage[] history) 
	{
		this.history = history;
		this.context = context;
	}
	
	public void refreshHistory()
	{
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinphoneChatMessage message = history[position];
		
		BubbleChat bubble = new BubbleChat(context, message, GroupChatMessagingFragment.this);
		View v = bubble.getView();
		
		//registerForContextMenu(v);
		RelativeLayout rlayout = new RelativeLayout(context);
		rlayout.addView(v);
		
		return rlayout;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
