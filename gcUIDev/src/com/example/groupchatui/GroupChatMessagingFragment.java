package com.example.groupchatui;




import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupChatMessagingFragment extends Fragment
{
	private static GroupChatMessagingFragment instance;		// Not in documentation
	
	private TextView groupName;
	private ImageView groupPicture;
	private TextView remoteMemberComposing;
	
//	private LinphoneGroupChatRoom chatroom;
//	private GroupChatMessageAdapter groupChatMessageAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		instance = this;
		
		View view = inflater.inflate(R.layout.groupchat, container);
		
		setRetainInstance(true);
		return view;
		
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
	
//	public class GroupChatMessageAdapter extends BaseAdapter
//	{
//		LinphoneChatMessage[] history;
//		Context context;
//		
//		public GroupChatMessageAdapter(Context context, LinphoneChatMessage[] history) 
//		{
//			this.history = history;
//			this.context = context;
//		}
//		
//		public void refreshHistory()
//		{
//			
//		}
//		
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent)
//		{
//			LinphoneChatMessage message = history[position];
//			
//			BubbleChat bubble = new BubbleChat(context, message, GroupChatMessagingFragment.this);
//			View v = bubble.getView();
//			
//			//registerForContextMenu(v);
//			RelativeLayout rlayout = new RelativeLayout(context);
//			rlayout.addView(v);
//			
//			return rlayout;
//		}
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		
//	}
}
