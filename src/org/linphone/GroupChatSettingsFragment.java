package org.linphone;

/***
 * @class GroupChatSettingsFragment
 * @author Izak Blom
 * This Fragment handles the user interface for groupchat settings manipulation and viewing
 * @implements OnClickListener for button click events and OnItemClickListener for ListView item click events
 */

import java.util.LinkedList;
import java.util.List;

import android.widget.TextView;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

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
	private TextView back, edit, next;
	private TextView groupNameView, encryptionType;
	private ListView groupParticipants;
	private ImageView clearGroupName, addMember, clearMemberField;
	private EditText groupNameEdit, newMember;
	private RadioGroup editEncryptionGroup;
	private RadioButton encryptionNone, encryptionAES;
	private RelativeLayout addMemberLayout;
	private TextView removeParticipant;
	private TextView leaveGroup;
	private LayoutInflater mInflater;
	private List<String> members = new LinkedList<String>();
	private boolean isEditMode;
	private String groupName;
	private TextView encryptionTypeLbl;
	private static GroupChatSettingsFragment instance;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Bundle args = getArguments();
		super.onCreate(savedInstanceState);
		instance = this;
		View view = inflater.inflate(R.layout.groupchat_info, container, false);
		setRetainInstance(true);
		mInflater = inflater;
		
		groupName = args.getString("groupName");
		isEditMode = false;
		
		back = (TextView) view.findViewById(R.id.back);
		back.setOnClickListener(this);
		
		edit = (TextView) view.findViewById(R.id.edit);
		edit.setOnClickListener(this);
		
		next = (TextView) view.findViewById(R.id.groupchatinfo_next);
		next.setOnClickListener(this);
		
		groupNameView = (TextView) view.findViewById(R.id.strGroupName);
		groupNameView.setText(args.getString("groupName"));
		encryptionType = (TextView) view.findViewById(R.id.encType);
		encryptionTypeLbl = (TextView) view.findViewById(R.id.encTypeLabel);
		
		newMember = (EditText) view.findViewById(R.id.newMemberGroupChat);
		groupNameEdit = (EditText) view.findViewById(R.id.GroupChatNameEdit);
		editEncryptionGroup = (RadioGroup) view.findViewById(R.id.groupchatinfo_radioGroup);
		addMemberLayout = (RelativeLayout) view.findViewById(R.id.groupchatinfo_addMemberLayout);
		
		clearGroupName = (ImageView) view.findViewById(R.id.clearGroupNameFieldEdit);
		clearGroupName.setOnClickListener(this);
		
		addMember = (ImageView) view.findViewById(R.id.addMember);
		addMember.setOnClickListener(this);
		
		clearMemberField = (ImageView) view.findViewById(R.id.clearMemberField);
		clearMemberField.setOnClickListener(this);
		
		//TODO Adjust this mock list of members:
				members = new LinkedList<String>();
				members.add("Piotr Tchaikovsky");
				members.add("WA Mozart");
				members.add("Shostakovich");
		
		groupParticipants = (ListView) view.findViewById(R.id.memberList);
		groupParticipants.setOnItemClickListener(this);
		groupParticipants.setAdapter(new MembersAdapter());
		
		//TODO Get handle to appropriate GroupChatStorage interface to retrieve parameters
		
		
		
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
		int id = v.getId();
		
		if (id == R.id.back)		// back button clicked. call popBackStack() to make previous fragment (GroupChatMessagingFragme) visible
		{
			getActivity().getSupportFragmentManager().popBackStack();
		}
		else if (id == R.id.edit)		// edit button clicked
		{
			// Change interface to accomodate for edit functionality
			// Make delete members buttons visible
			isEditMode = true;
			edit.setVisibility(View.GONE);
			next.setVisibility(View.VISIBLE);
			groupNameEdit.setVisibility(View.VISIBLE);
			groupNameView.setVisibility(View.GONE);
			groupNameEdit.setText(groupName);
			editEncryptionGroup.setVisibility(View.VISIBLE);
			addMemberLayout.setVisibility(View.VISIBLE);
			encryptionType.setVisibility(View.GONE);
			encryptionTypeLbl.setVisibility(View.GONE);
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
		else if (id == R.id.groupchatinfo_next)		// next button clicked after edit
		{
			// Reset interface after edit complete
			isEditMode = false;
			edit.setVisibility(View.VISIBLE);
			next.setVisibility(View.GONE);
			groupNameView.setVisibility(View.VISIBLE);
			groupNameEdit.setVisibility(View.GONE);
			editEncryptionGroup.setVisibility(View.GONE);
			addMemberLayout.setVisibility(View.GONE);
			encryptionType.setVisibility(View.VISIBLE);
			encryptionTypeLbl.setVisibility(View.VISIBLE);
			if (groupNameEdit.getText().toString().equals(groupName) == false)
			{
				groupName = groupNameEdit.getText().toString();
				groupNameView.setText(groupName);
				//TODO Update group name via appropriate GroupChatStorage interface
			}
			closeKeyboard(getActivity(), groupNameEdit.getWindowToken());
		}
		else if (id == R.id.addMember)	// add a member button clicked
		{
			String newSip = newMember.getText().toString();
			newMember.setText("");		// clear newMember EditText
			newMember.clearFocus();
			// Hide keyboard after member added
			closeKeyboard(getActivity(), newMember.getWindowToken());
			
			if (!newSip.isEmpty())
			{
				//TODO test valid sip address or contact
				members.add(newSip);
				// Update groupParticipants ListView
				groupParticipants.setAdapter(new MembersAdapter());
			}
		}
		else if (id == R.id.clearMemberField)	// Clear newMember EditText button clicked
		{
			newMember.setText("");
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (isEditMode)	// Action only needed when editMode (after edit button clicked)
		{
			members.remove(view.getTag());
			groupParticipants.setAdapter(new MembersAdapter());
		}
	}
	
	public void resetAdapter()
	{
		groupParticipants.setAdapter(new MembersAdapter());
	}
	
	/**
	 * Usage: closeKeyboard(getActivity(), yourEditText.getWindowToken());
	 * @param c
	 * @param windowToken
	 */
	public static void closeKeyboard(Context c, IBinder windowToken) {
	    InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
	    mgr.hideSoftInputFromWindow(windowToken, 0);
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
	
	/**
	 * Adapter to update members ListView
	 * @author Izak Blom
	 *
	 */
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
			if (isEditMode)
				delete.setVisibility(View.VISIBLE);
			else
				delete.setVisibility(view.GONE);
			
			return view;
		}
		
	}
}