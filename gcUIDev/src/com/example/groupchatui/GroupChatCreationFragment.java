package com.example.groupchatui;


import java.util.LinkedList;

import android.widget.TextView;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

//import org.linphone.core.LinphoneAddress;
//import org.linphone.core.LinphoneCoreListenerBase;
//import org.linphone.groupchat.core.LinphoneGroupChatListener;
//import org.linphone.groupchat.core.LinphoneGroupChatManager;
//import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;

public class GroupChatCreationFragment  extends Fragment
{
//	private LinphoneGroupChatListener coreListener;
//	private LinphoneGroupChatManager groupChatManager;
//	private LinphoneCoreListenerBase mListener; 	// Really needed?
	
	private LinearLayout participants;
	private LinearLayout topBar;
	private TextView back;
	private EditText groupName;
	private TextView groupParticipants;
	private TextView addParticipant;
	private TextView removeParticipant;
	private TextView leaveGroup;
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
}
