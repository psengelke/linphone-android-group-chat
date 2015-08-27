package com.example.groupchatui;


import java.util.LinkedList;
import java.util.List;

import android.widget.TextView;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

//import org.linphone.core.LinphoneAddress;
//import org.linphone.core.LinphoneCoreListenerBase;
//import org.linphone.groupchat.core.LinphoneGroupChatListener;
//import org.linphone.groupchat.core.LinphoneGroupChatManager;
//import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;

public class GroupChatCreationFragment  extends Fragment implements OnClickListener, OnItemClickListener
{
//	private LinphoneGroupChatListener coreListener;
//	private LinphoneGroupChatManager groupChatManager;
//	private LinphoneCoreListenerBase mListener; 	// Really needed?
	
	private LinearLayout participants;
	private LinearLayout topBar;
	private TextView back, next;
	private EditText groupName, newParticipant;
	private ListView groupParticipants;
	private ImageView addParticipant, clearGroupName, clearSipAddress;
	private TextView removeParticipant, noMembers;
	private TextView setUpGroup;
	private RadioButton encryptionNone, encryptionAES;
	private static GroupChatCreationFragment instance;
	private LayoutInflater mInflater;
	private List<String> members = new LinkedList<String>();
	private boolean isEditMode = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		instance = this;
		View view = inflater.inflate(R.layout.groupchat_creation, container, false);
		setRetainInstance(true);
		
		mInflater = inflater;
		
		back = (TextView) view.findViewById(R.id.back);
		back.setOnClickListener(this);
		
		next = (TextView) view.findViewById(R.id.next);
		next.setOnClickListener(this);
		next.setEnabled(false);
		
		encryptionNone = (RadioButton) view.findViewById(R.id.radio_none);
		encryptionAES = (RadioButton) view.findViewById(R.id.radio_EncAES);
		
		groupName = (EditText) view.findViewById(R.id.newGroupChatName);
		
		addParticipant = (ImageView) view.findViewById(R.id.addMember);
		addParticipant.setOnClickListener(this);
		
		newParticipant = (EditText) view.findViewById(R.id.newMemberGroupChat);
		
		clearGroupName = (ImageView) view.findViewById(R.id.clearGroupNameField);
		clearGroupName.setOnClickListener(this);
		
		clearSipAddress = (ImageView) view.findViewById(R.id.clearMemberField);
		clearSipAddress.setOnClickListener(this);
		
		noMembers = (TextView) view.findViewById(R.id.noGroupMembers);
		noMembers.setVisibility(View.VISIBLE);
		
		groupParticipants = (ListView) view.findViewById(R.id.memberList);
		groupParticipants.setOnItemClickListener(this);
		
		groupParticipants.setAdapter(new MembersAdapter());
		
		groupParticipants.setVisibility(View.INVISIBLE);
		
		
		return view;
	}
	
	public void onRadioButtonClicked(View view)
	{
		
	}
	
	public void onPause()
	{
		
		super.onPause();
	}
	
//	public void createGroupChat(String name, LinphoneAddress admin, LinkedList<LinphoneAddress> members, EncryptionType encryptionType)
//	{
//		
//	}
	
	public void toggleEncryption()
	{
		
	}
	
	public void addMember()
	{
		
	}
	
	/**
	 * This function removes a member only from the user interface, since the group has yet to be created
	 * @param participant The TextView to be removed from the fragment.
	 */
	public void removeMember(TextView participant)
	{
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.back)
		{
			//getFragmentManager().popBackStackImmediate();
			getActivity().finish();
		}
		else if (id == R.id.addMember)
		{
			// TODO Test for valid sip address before adding
			String newContact = newParticipant.getText().toString();
			newParticipant.setText("");
			
			if (!newContact.isEmpty())
			{
				members.add(newContact);
				groupParticipants.setAdapter(new MembersAdapter());
				groupParticipants.setVisibility(View.VISIBLE);
				noMembers.setVisibility(View.INVISIBLE);
			}
		}
		else if (id == R.id.clearGroupNameField)
			groupName.setText("");
		else if (id == R.id.clearMemberField)
			newParticipant.setText("");
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
	
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
