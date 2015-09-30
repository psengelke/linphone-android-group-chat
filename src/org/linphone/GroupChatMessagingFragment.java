package org.linphone;




import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GroupChatMessagingFragment extends Fragment  implements OnClickListener
{
	private static GroupChatMessagingFragment instance;		
	
	private TextView groupNameView;
	private ImageView groupPicture;
	private TextView remoteMemberComposing;
	private TextView back, info;
	private ListView msgList;
	private String groupName;
	
//	private LinphoneGroupChatRoom chatroom;
//	private GroupChatMessageAdapter groupChatMessageAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		instance = this;
		
		View view = inflater.inflate(R.layout.groupchat, container, false);
		setRetainInstance(true);
		
		groupName = getArguments().getString("groupName");
		
		groupNameView = (TextView) view.findViewById(R.id.groupName);
		groupNameView.setText(groupName);
		
		back = (TextView) view.findViewById(R.id.back);
		back.setOnClickListener(this);
		
		info = (TextView) view.findViewById(R.id.group_info);
		info.setOnClickListener(this);
		
		msgList = (ListView) view.findViewById(R.id.group_message_list);
		
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		//TODO Get handle to appropriate GroupChatStorage interface to retrieve parameters and messages
		
		return view;
		
	}
	
	public void onPause()
	{
		super.onPause();
		
	}
	
	public void onResume()
	{
		super.onResume();
		
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		
		if (id == R.id.back)
		{
			//getFragmentManager().popBackStackImmediate();
			getActivity().finish();
		}
		else if (id == R.id.group_info)
		{
			GroupChatActivity activity =  (GroupChatActivity) getActivity();
			Bundle extras = new Bundle();
			extras.putString("groupName", groupName);
			activity.changeFragment("gcSettingsFragment", extras);
		}
	}
	
	public class GroupChatMessageAdapter extends BaseAdapter
	{
//		LinphoneChatMessage[] history;
		Context context;
		
		public GroupChatMessageAdapter(Context context/*,LinphoneChatMessage[] history*/) 
		{
//			this.history = history;
			this.context = context;
		}
		
		public void refreshHistory()
		{
			
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
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
			return null;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
