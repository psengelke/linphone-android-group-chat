package org.linphone.groupchat.ui;

import android.widget.TextView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.linphone.core.LinphoneBuffer;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatMessage.State;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.groupchat.core.LinphoneGroupChatRoom;
import org.linphone.groupchat.interfaces.GroupChatStorage;
import org.linphone.core.LinphoneChatMessage.LinphoneChatMessageListener;

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
	private static GroupChatSettingsFragment instance;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		instance = this;
		return null;
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLinphoneChatMessageStateChanged(LinphoneChatMessage msg,
			State state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLinphoneChatMessageFileTransferReceived(
			LinphoneChatMessage msg, LinphoneContent content,
			LinphoneBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLinphoneChatMessageFileTransferSent(LinphoneChatMessage msg,
			LinphoneContent content, int offset, int size,
			LinphoneBuffer bufferToFill) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLinphoneChatMessageFileTransferProgressChanged(
			LinphoneChatMessage msg, LinphoneContent content, int offset,
			int total) {
		// TODO Auto-generated method stub
		
	}
}
