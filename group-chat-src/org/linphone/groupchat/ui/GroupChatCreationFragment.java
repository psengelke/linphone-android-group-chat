package org.linphone.groupchat.ui;

import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LayoutInflater;
import org.linphone.groupchat.core;
import org.linphone.groupchat.encryption;

public class GroupChatCreationFragment 
{
	private LinphoneGroupChatListener coreListener;
	private LinphoneGroupChatManagaer groupChatManager;
	private LinphoneCoreListenerBase mListener; 	// Really needed?
	
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
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		instance = this;
		
	}
	
	public void onPause()
	{
		
		
	}
	
	public void createGroupChat(String name, LinphoneAddress admin, LinkedList<LinphoneAddress> members, EncryptionType encryptionType)
	{
		
	}
	
	public void toggleEncryption;
	
	public void addMember()
	{
		
	}
	
	
	public void removeMember(TextView participant)
	{
		
	}
}
