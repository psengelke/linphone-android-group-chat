package org.linphone.groupchat.ui;

import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LayoutInflater;
import org.linphone.groupchat.core;

public class GroupChatSettingsFragment extends Fragment implements OnClickListener, LinphoneChatMessageListener
{
	private LinphoneGroupChatRoom groupChatRoom;
	private LinphoneCoreListenerBase mListener; 	// Really needed?
	private GroupChatStorage groupChatStorage;
	private String sipUri;
	private String displayName;
	private String pictureUri;	
	
	private LinearLayout topBar;
	private TextView back;
	private EditText groupName;
	private TextView groupParticipants;
	private TextView addParticipant;
	private TextView removeParticipant;
	private TextView leaveGroup;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		instance = this;
		
	}
	
	public void onPause()
	{
		
		
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
}
