package org.linphone;

/**
 * @class GroupChatMessagingFragment
 * This Fragment handles the interface for GroupChat Messaging
 * @implements OnClickListener to handle button click events
 */

import java.util.LinkedList;

import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.core.GroupChatRoomListener;
import org.linphone.groupchat.core.LinphoneGroupChatManager;
import org.linphone.groupchat.core.LinphoneGroupChatRoom;
import org.linphone.groupchat.exception.GroupChatListenerIsSetException;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.ui.GroupBubbleChat;
import org.linphone.ui.BubbleChat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupChatMessagingFragment extends Fragment  implements OnClickListener, GroupChatRoomListener
{
	private static GroupChatMessagingFragment instance;		
	
	private TextView groupNameView;
	private ImageView groupPicture;
	private TextView remoteMemberComposing;
	private TextView back, info;
	private TextView sendMsgBtn;
	private ListView msgList;
	private EditText msgToSend;
	private String groupName, groupID;
	private LinkedList<GroupChatMessage> history;

	private LinphoneGroupChatRoom chatroom;

	private GroupChatMessageAdapter adapter;
	
//	private LinphoneGroupChatRoom chatroom;
//	private GroupChatMessageAdapter groupChatMessageAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		instance = this;
		
		View view = inflater.inflate(R.layout.groupchat, container, false);
		setRetainInstance(true);
		
		msgList = (ListView) view.findViewById(R.id.group_message_list);
		
		// Determine which groupChat to create interface for
		groupID = getArguments().getString("groupID");
		groupName = getArguments().getString("groupName");
		LinphoneGroupChatManager lgm = LinphoneGroupChatManager.getInstance();
		try {
			chatroom = lgm.getGroupChat(groupID);
			chatroom.setGroupChatRoomListener(this);
			groupName = chatroom.getName();
		} catch (GroupDoesNotExistException e1) {
			e1.printStackTrace();
			AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage(e1.getMessage());
            builder1.setCancelable(true);
            builder1.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert11 = builder1.create();
            alert11.show();
		} catch (GroupChatListenerIsSetException e) {
			// TODO alert here
			e.printStackTrace();
		}
		
		// Use groupID to retrieve messages
		history = chatroom.getHistory();
		refreshMsgList();

		groupNameView = (TextView) view.findViewById(R.id.groupName);
		groupNameView.setText(groupName);
		
		back = (TextView) view.findViewById(R.id.back);
		back.setOnClickListener(this);
		
		info = (TextView) view.findViewById(R.id.group_info);
		info.setOnClickListener(this);
		
		TextView sendPic = (TextView) view.findViewById(R.id.sendPicture);
		sendPic.setVisibility(View.INVISIBLE);
		
		sendMsgBtn = (TextView) view.findViewById(R.id.sendMessage);
		sendMsgBtn.setOnClickListener(this);
		msgToSend = (EditText) view.findViewById(R.id.message);
		
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		return view;
		
	}
	
	public void onPause()
	{
		super.onPause();
		Log.e("msgFragmet on pause", "msgFragmet on pause");
		chatroom.unsetGroupChatListener();
		LinphoneService.instance().resetMessageNotifCount();
		LinphoneService.instance().removeMessageNotification();
	}
	
	public void onResume()
	{
		super.onResume();
		Log.e("msgFragment onResume", "msgFragment onResume");
		try {
			chatroom = LinphoneGroupChatManager.getInstance().getGroupChat(groupID);
		} catch (GroupDoesNotExistException e) {
			getActivity().finish();
		}
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
		int id = v.getId();
		
		if (id == R.id.back)	// back button clicked
		{
			getActivity().finish();
		}
		else if (id == R.id.group_info)		// info button clicked
		{
			GroupChatActivity activity =  (GroupChatActivity) getActivity();
			Bundle extras = new Bundle();
			extras.putString("groupName", groupName);
			extras.putString("groupID", groupID);
			// replace fragment with GroupChatSettingsFragment
			activity.changeFragment("gcSettingsFragment", extras);
		}
		else if (id == R.id.sendMessage)
		{
			String message = msgToSend.getText().toString();
			msgToSend.setText("");
			if (!message.isEmpty())
			{
				chatroom.sendMessage(message);
				refreshMsgList();
			}
		}
	}
	
	public void refreshMsgList()
	{
		history = chatroom.getHistory();
		msgList.setAdapter(new GroupChatMessageAdapter());
		chatroom.markAsRead();
	}
	
	/**
	 * Adapter for GroupChatMessages
	 * @author Izak Blom
	 */
	public class GroupChatMessageAdapter extends BaseAdapter
	{
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			GroupChatMessage message = history.get(position);
			Context context = getActivity();
			GroupBubbleChat bubble = new GroupBubbleChat(context, message);//, GroupChatMessagingFragment.this);
			View v = bubble.getView();
			
			//registerForContextMenu(v);
			RelativeLayout rlayout = new RelativeLayout(context);
			rlayout.addView(v);
			
			return rlayout;
		}

		public int getCount() {
			return history.size();
		}

		public Object getItem(int position) {
			return history.get(position);
		}

		public long getItemId(int position) {
			return history.get(position).id;
		}
	}

	@Override
	public void onMessageReceived(GroupChatMessage message) {
		Log.e("onMessageReceived", "onMsgReceived in GroupChatMessagingFragment");
		refreshMsgList();
	}
}
