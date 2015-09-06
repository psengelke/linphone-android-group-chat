package com.example.groupchatui;


import java.util.LinkedList;
import java.util.List;

import android.widget.TextView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

//import org.linphone.core.LinphoneBuffer;
//import org.linphone.core.LinphoneChatMessage;
//import org.linphone.core.LinphoneChatMessage.State;
//import org.linphone.core.LinphoneContent;
//import org.linphone.core.LinphoneCoreListenerBase;
//import org.linphone.groupchat.core.LinphoneGroupChatRoom;
//import org.linphone.groupchat.interfaces.GroupChatStorage;
//import org.linphone.core.LinphoneChatMessage.LinphoneChatMessageListener;

public class GroupChatSettingsFragment extends Fragment implements OnClickListener//, LinphoneChatMessageListener
, OnItemClickListener
{
//	private LinphoneGroupChatRoom groupChatRoom;
//	private LinphoneCoreListenerBase mListener; 	// Really needed?
//	private GroupChatStorage groupChatStorage;
	private String sipUri;
	private String displayName;
	private String pictureUri;	
	
	private LinearLayout topBar;
	private TextView back, edit, groupName, encryptionType;
	private ListView groupParticipants;
	private ImageView clearGroupName, addParticipant;
	private TextView removeParticipant;
	private TextView leaveGroup;
	private LayoutInflater mInflater;
	private List<String> members = new LinkedList<String>();
	private static GroupChatSettingsFragment instance;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		instance = this;
		View view = inflater.inflate(R.layout.groupchat_info, container, false);
		setRetainInstance(true);
		mInflater = inflater;
		
		back = (TextView) view.findViewById(R.id.back);
		back.setOnClickListener(this);
		
		edit = (TextView) view.findViewById(R.id.edit);
		edit.setOnClickListener(this);
		
		groupName = (TextView) view.findViewById(R.id.strGroupName);
		encryptionType = (TextView) view.findViewById(R.id.encType);
		
		clearGroupName = (ImageView) view.findViewById(R.id.clearGroupNameField);
		clearGroupName.setOnClickListener(this);
		
		groupParticipants = (ListView) view.findViewById(R.id.memberList);
		groupParticipants.setOnItemClickListener(this);
		
		return view;
	}
	
	public void onPause()
	{
		super.onPause();
		
	}
	
	public void onResume()
	{
		super.onPause();
	}
	
	public void displayGroupName()
	{
		
	}
	
	public void displayGroupMembers()
	{
		
	}
	
	public void leaveGroup()
	{
		
	}
	
	public void addMember()
	{
		
	}
	
	
	public void removeMember()
	{
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void onLinphoneChatMessageStateChanged(LinphoneChatMessage msg,
//			State state) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onLinphoneChatMessageFileTransferReceived(
//			LinphoneChatMessage msg, LinphoneContent content,
//			LinphoneBuffer buffer) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onLinphoneChatMessageFileTransferSent(LinphoneChatMessage msg,
//			LinphoneContent content, int offset, int size,
//			LinphoneBuffer bufferToFill) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onLinphoneChatMessageFileTransferProgressChanged(
//			LinphoneChatMessage msg, LinphoneContent content, int offset,
//			int total) {
//		// TODO Auto-generated method stub
//		
//	}
	
	class MembersAdapter extends BaseAdapter
	{

		@Override
		public int getCount() {
			return members.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			
			if (convertView != null)
				view = convertView;
			else
				view = mInflater.inflate(R.layout.memberlist_cell, parent, false);
			
			String contact = members.get(position);
			view.setTag(contact);
			
			TextView sipUri = (TextView) view.findViewById(R.id.sipUri);
			sipUri.setText(contact);
			
			ImageView delete = (ImageView) view.findViewById(R.id.delete);
			delete.setVisibility(view.VISIBLE);
			
			return view;
		}
		
	}
}
