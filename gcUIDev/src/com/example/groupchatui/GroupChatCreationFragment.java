package com.example.groupchatui;


import java.util.LinkedList;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

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
	private EditText groupName;
	private ListView groupParticipants;
	private ImageView addParticipant;
	private TextView removeParticipant;
	private TextView setUpGroup;
	private CheckBoxPreference encryptionCheckbox;
	private static GroupChatCreationFragment instance;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		//super.onCreate(savedInstanceState);
		instance = this;
		View view = inflater.inflate(R.layout.groupchat_creation, container, false);
		setRetainInstance(true);
		
		back = (TextView) view.findViewById(R.id.back);
		back.setOnClickListener(this);
		
		next = (TextView) view.findViewById(R.id.next);
		next.setOnClickListener(this);
		
		groupName = (EditText) view.findViewById(R.id.newGroupChatName);
		
		addParticipant = (ImageView) view.findViewById(R.id.addMember);
		addParticipant.setOnClickListener(this);
		
		groupParticipants = (ListView) view.findViewById(R.id.memberList);
//		groupParticipants.setOnClickListener(this);
//		registerForContextMenu(groupParticipants);
		
		
		
		
		return view;
	}
	
	public void onPause()
	{
		
		
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
			getFragmentManager().popBackStackImmediate();
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
}
