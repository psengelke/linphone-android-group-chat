package org.linphone;

/**
 * @class GroupChatCreationFragment
 * @author Izak Blom
 * This Fragment handles the User Interface for GroupChatCreation
 * @implements OnclickListener for button clicks and OnItemClickListener for click events on ListView elements
 */

import java.util.LinkedList;
import java.util.List;

import org.linphone.core.LinphoneCore;
import org.linphone.groupchat.communication.DataExchangeFormat;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.core.LinphoneGroupChatManager;
import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupChatSizeException;
import org.linphone.groupchat.exception.InvalidGroupNameException;

import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
	private TextView noMembers;
	private RadioButton encryptionNone, encryptionAES;
	private static GroupChatCreationFragment instance;
	private LayoutInflater mInflater;
	private LinkedList<GroupChatMember> members = new LinkedList<GroupChatMember>();
	private boolean isEditMode = false;
	private String encryptionChoice = "";
	private String groupNameString = "";
	private final String ENC_NONE = "None";
	private final String ENC_AES = "AES";
	
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
		encryptionNone.setOnClickListener(this);
		encryptionAES = (RadioButton) view.findViewById(R.id.radio_EncAES);
		encryptionAES.setOnClickListener(this);
		
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
		groupParticipants.setVisibility(View.GONE);
		groupParticipants.setOnItemClickListener(this);
		
		groupParticipants.setAdapter(new MembersAdapter());
		
		groupParticipants.setVisibility(View.INVISIBLE);
		
		// TextChangedListener for when groupName field is changed. Possibly next button should be enabled
		// or disabled. Checked with testDone() method
		groupName.addTextChangedListener(new TextWatcher() {
			   public void afterTextChanged(Editable s) {
			   }
			   public void beforeTextChanged(CharSequence s, int start, 
			     int count, int after) {
			   }
			   public void onTextChanged(CharSequence s, int start, 
			     int before, int count) {
				   testDone();
			   }
			  });
		
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
	
	
	public void removeMember(TextView participant)
	{
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.back)	// back button clicked
		{
			getActivity().finish();
		}
		else if (id == R.id.addMember)	// add member button clicked
		{
			String newContact = newParticipant.getText().toString();
			
			// Clear EditText content
			newParticipant.setText("");
			// Hide keyboard
			closeKeyboard(getActivity(), newParticipant.getWindowToken());
			String sipUri = newContact;
			if (!sipUri.equals(""))
			{
				// Test for valid sip address
				if (!LinphoneUtils.isSipAddress(sipUri)) {
					if (LinphoneManager.getLc().getDefaultProxyConfig() == null) {
						return;
					}
					sipUri = sipUri + "@" + LinphoneManager.getLc().getDefaultProxyConfig().getDomain();
				}
				if (!LinphoneUtils.isStrictSipAddress(sipUri)) {
					sipUri = "sip:" + sipUri;
				}
				DataExchangeFormat.GroupChatMember gm = new DataExchangeFormat.GroupChatMember(newContact, sipUri, true); 
				members.add(gm);
				// Refresh list of contacts:
				groupParticipants.setAdapter(new MembersAdapter());
				groupParticipants.setVisibility(View.VISIBLE);
				noMembers.setVisibility(View.INVISIBLE);
				// Test if next button should be activated
				testDone();
			}
		}
		else if (id == R.id.clearGroupNameField)	// clear GroupName Edit Button clicked
		{
			groupName.setText("");
			// disable next button
			testDone();
		}
		else if (id == R.id.clearMemberField)		// clear New Member EditText
			newParticipant.setText("");
		else if (id == R.id.radio_none)				// Radio button no encryption selected
		{
			encryptionChoice = ENC_NONE;
			// Enable next button?
			testDone();
		}
		else if (id == R.id.radio_EncAES)			// Radio button AES encryption selected
		{
			encryptionChoice = ENC_AES;
			// Enable next button?
			testDone();
		}
		// Next button clicked. Should proceed to GroupChatMessaginFragment
		else if (id == R.id.next)					
		{
			//Check UI data and create group => Make MessagingFragment Visible
			
			LinphoneManager lm = LinphoneManager.getInstance();
			LinphoneCore lc = lm.getLc();
			// get user sip:
			String usersip = lc.getDefaultProxyConfig().getIdentity();
			// get user name
			String username = usersip.substring(0, usersip.indexOf('@'));
			members.add(new DataExchangeFormat.GroupChatMember(username, usersip, true));
			
			// Determine Encryption type:
			EncryptionType et;
			if (encryptionChoice.equals(ENC_AES))
				et = EncryptionType.AES256;
			else
				et = EncryptionType.None;
			
			// Create group
			LinphoneGroupChatManager lGm = LinphoneGroupChatManager.getInstance();
			
				// Create groupchat and Get new GroupChat id
				String groupId;
				Exception exc = null;
				
					try 
					{
						groupId = lGm.createGroupChat(groupNameString, usersip, members, et);
						
						GroupChatActivity activity =  (GroupChatActivity) getActivity();
						Bundle extras = new Bundle();
						extras.putString("groupID", groupId);
						extras.putString("groupName", groupNameString);
						// Replace this fragment with GroupChatMessagingFragment for the newly created group
						activity.changeFragment("gcMessagingFragment", extras);
					} catch (GroupChatSizeException | InvalidGroupNameException
							| GroupChatExistsException e) {
						AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
			            builder1.setMessage(e.getMessage());
			            builder1.setCancelable(true);
			            builder1.setPositiveButton("OK",
			                    new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int id) {
			                    dialog.cancel();
			                }
			            });
			            AlertDialog alert11 = builder1.create();
			            alert11.show();
						e.printStackTrace();
					}
					
		}
		
	}
	
	/**
	 * Method to test conditions for enabling next button
	 * Conditions: GroupName not empty && EncryptionType Selected && At least one member
	 */
	private void testDone() 
	{
		groupNameString = groupName.getText().toString();
		if (!members.isEmpty() && !groupNameString.isEmpty() && !encryptionChoice.isEmpty())
			next.setEnabled(true);
		else
			next.setEnabled(false);
		
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
	
	/**
	 * Event handler for click events on ListView items
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		String member = (String) view.getTag();
		for (int k = 0; k < members.size(); ++k)
			if (members.get(k).name.equals(member))
				members.remove(k);
		
		refreshParticipantsList();
		
	}
	
	/**
	 * Method to update groupParticipants ListView
	 */
	private void refreshParticipantsList() 
	{
		if (members.isEmpty())
		{
			groupParticipants.setVisibility(View.GONE);
			noMembers.setVisibility(View.VISIBLE);
		}
		else
		{
			groupParticipants.setVisibility(View.VISIBLE);
			noMembers.setVisibility(View.GONE);
			groupParticipants.setAdapter(new MembersAdapter());
		}
		testDone();
		
	}

	/**
	 * Adapter for groupParticipants ListView
	 * Adds list elements according to List of members
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
			
			String contact = members.get(position).name;
			view.setTag(contact);
			
			TextView sipUri = (TextView) view.findViewById(R.id.sipUri);
			sipUri.setText(contact);
			
			ImageView delete = (ImageView) view.findViewById(R.id.delete);
			delete.setVisibility(view.VISIBLE);
			
			return view;
		}
		
	}
}
