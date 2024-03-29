package org.linphone;

/*
ChatListFragment.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.core.LinphoneGroupChatManager;
import org.linphone.groupchat.core.LinphoneGroupChatRoom;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.IsAdminException;
import org.linphone.mediastream.Log;






import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Sylvain Berfini
 */
public class ChatListFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private LayoutInflater mInflater;
	private List<String> mConversations, mDrafts;
	private ListView chatList;
	private TextView edit, ok, newDiscussion, noChatHistory, groupChat, groupsTab, chatsTab;
	private ImageView clearFastChat;
	private EditText fastNewChat;
	private boolean isEditMode = false;
	private boolean useLinphoneStorage;
	private boolean displayGroupChats;
	
	private LinkedList<LinphoneGroupChatRoom> groups;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("ChatlistFragment onCreate", "ChatListFragment onCreate");
		mInflater = inflater;
		displayGroupChats = false;
		View view = inflater.inflate(R.layout.chatlist, container, false);
		chatList = (ListView) view.findViewById(R.id.chatList);
		chatList.setOnItemClickListener(this);
		registerForContextMenu(chatList);
		
		groups = LinphoneGroupChatManager.getInstance().getGroupChatList();
		
		noChatHistory = (TextView) view.findViewById(R.id.noChatHistory);
		
		
		
		if (!displayGroupChats)
		{
			chatList.setVisibility(View.GONE);
			noChatHistory.setVisibility(View.VISIBLE);
		}
		edit = (TextView) view.findViewById(R.id.edit);
		edit.setOnClickListener(this);
		
		
		newDiscussion = (TextView) view.findViewById(R.id.newDiscussion);
		newDiscussion.setOnClickListener(this);
		
		groupChat = (TextView) view.findViewById(R.id.newGroupDiscussion);
		groupChat.setOnClickListener(this);
		
		groupsTab = (TextView) view.findViewById(R.id.allGroups);
		groupsTab.setOnClickListener(this);
		
		chatsTab = (TextView) view.findViewById(R.id.allChats);
		chatsTab.setOnClickListener(this);
		
		groupsTab.setEnabled(!displayGroupChats);
		chatsTab.setEnabled(displayGroupChats);
		
		ok = (TextView) view.findViewById(R.id.ok);
		ok.setOnClickListener(this);
		
		clearFastChat = (ImageView) view.findViewById(R.id.clearFastChatField);
		clearFastChat.setOnClickListener(this);
		
		fastNewChat = (EditText) view.findViewById(R.id.newFastChat);
		
		return view;
	}
	
	private void hideAndDisplayMessageIfNoChat() {
		if (displayGroupChats)
		{
			if (groups.isEmpty())
			{
				noChatHistory.setVisibility(View.VISIBLE);
				chatList.setVisibility(View.GONE);
			}
			else
			{
				noChatHistory.setVisibility(View.GONE);
				chatList.setVisibility(View.VISIBLE);
				chatList.setAdapter(new GroupListAdapter());
			}
				
		}
		else
		{
			if (mConversations.size() == 0 && mDrafts.size() == 0) {
				noChatHistory.setVisibility(View.VISIBLE);
				chatList.setVisibility(View.GONE);
			} else {
				noChatHistory.setVisibility(View.GONE);
				chatList.setVisibility(View.VISIBLE);
				chatList.setAdapter(new ChatListAdapter(useLinphoneStorage));
			}
		}
		
	}
	
	public void refresh() {
		mConversations = LinphoneActivity.instance().getChatList();
		mDrafts = LinphoneActivity.instance().getDraftChatList();
		mConversations.removeAll(mDrafts);
		hideAndDisplayMessageIfNoChat();
	}
	
	private boolean isVersionUsingNewChatStorage() {
		try {
			Context context = LinphoneActivity.instance();
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode >= 2200;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		//Check if the is the first time we show the chat view since we use liblinphone chat storage
		useLinphoneStorage = getResources().getBoolean(R.bool.use_linphone_chat_storage);
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LinphoneActivity.instance());
		boolean updateNeeded = prefs.getBoolean(getString(R.string.pref_first_time_linphone_chat_storage), true);
		updateNeeded = updateNeeded && !isVersionUsingNewChatStorage();
		if (useLinphoneStorage && updateNeeded) {
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog pd;
                @Override
                protected void onPreExecute() {
                         pd = new ProgressDialog(LinphoneActivity.instance());
                         pd.setTitle(getString(R.string.wait));
                         pd.setMessage(getString(R.string.importing_messages));
                         pd.setCancelable(false);
                         pd.setIndeterminate(true);
                         pd.show();
                }
                @Override
                protected Void doInBackground(Void... arg0) {
                        try {
                        	if (importAndroidStoredMessagedIntoLibLinphoneStorage()) {
                				prefs.edit().putBoolean(getString(R.string.pref_first_time_linphone_chat_storage), false).commit();
                				LinphoneActivity.instance().getChatStorage().restartChatStorage();
                			}
                        } catch (Exception e) {
                               e.printStackTrace();
                        }
                        return null;
                 }
                 @Override
                 protected void onPostExecute(Void result) {
                         pd.dismiss();
                 }
			};
        	task.execute((Void[])null);
		}
		
		if (LinphoneActivity.isInstanciated()) {
			LinphoneActivity.instance().selectMenu(FragmentsAvailable.CHATLIST);
			LinphoneActivity.instance().updateChatListFragment(this);
			
			if (getResources().getBoolean(R.bool.show_statusbar_only_on_dialer)) {
				LinphoneActivity.instance().hideStatusBar();
			}
		}
		
		refresh();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, getString(R.string.delete));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (!displayGroupChats)
		{
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			if (info == null || info.targetView == null) {
				return false;
			}
			String sipUri = (String) info.targetView.getTag();
			
			LinphoneActivity.instance().removeFromChatList(sipUri);
			mConversations = LinphoneActivity.instance().getChatList();
			mDrafts = LinphoneActivity.instance().getDraftChatList();
			mConversations.removeAll(mDrafts);
			hideAndDisplayMessageIfNoChat();
			return true;
		}
		else
		{
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			if (info == null || info.targetView == null) {
				return false;
			}
			final LinphoneGroupChatRoom group = (LinphoneGroupChatRoom) info.targetView.getTag();
			
			AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage("Are you sure you want to leave group " + group.getName());
            builder1.setCancelable(true);
			builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                
				public void onClick(DialogInterface dialog, int id) {
					leaveGroup(groups.indexOf(group));
                }
            });
            builder1.setNegativeButton("No", 
            		new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
            AlertDialog alert11 = builder1.create();
            alert11.show();
			
			hideAndDisplayMessageIfNoChat();
			
			
			return true;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.clearFastChatField) {		// Clear textbox button
			fastNewChat.setText("");
		}
		else if (id == R.id.ok) {				// OK button click after edit
			edit.setVisibility(View.VISIBLE);
			ok.setVisibility(View.GONE);
			isEditMode = false;
			hideAndDisplayMessageIfNoChat();	// refresh contactsList
		}
		else if (id == R.id.edit) {				// Enable edit functionality
			edit.setVisibility(View.GONE);
			ok.setVisibility(View.VISIBLE);
			isEditMode = true;
			hideAndDisplayMessageIfNoChat();	// refresh contacts list
		}
		else if (id == R.id.newDiscussion) {	// New Chat button clicked
			String sipUri = fastNewChat.getText().toString();
			if (sipUri.equals("")) {
				LinphoneActivity.instance().displayContacts(true);
			} else {
				if (!LinphoneUtils.isSipAddress(sipUri)) {
					if (LinphoneManager.getLc().getDefaultProxyConfig() == null) {
						return;
					}
					sipUri = sipUri + "@" + LinphoneManager.getLc().getDefaultProxyConfig().getDomain();
				}
				if (!LinphoneUtils.isStrictSipAddress(sipUri)) {
					sipUri = "sip:" + sipUri;
				}
				LinphoneActivity.instance().displayChat(sipUri);
			}
		}
		else if (id == R.id.newGroupDiscussion)		// New GroupChatButton clicked
		{
			// Start GroupChatActivity with Intent gcCreationFragment to start 
			// GroupChatCreation Fragment
			Intent intent = new Intent(getActivity(), GroupChatActivity.class);
			Bundle b = new Bundle();
			b.putString("fragment", "gcCreationFragment");
			intent.putExtras(b);
			startActivity(intent);
		}
		else if (id == R.id.allGroups)		// All Groups tab clicked
		{
			displayGroupChats = true;
			toggleContactsTab();
			chatList.setVisibility(View.VISIBLE);
			refreshGroupList();
		}
		else if (id == R.id.allChats)		// All Chats tab clicked
		{
			displayGroupChats = false;
			toggleContactsTab();
			noChatHistory.setVisibility(View.VISIBLE);
			refresh();
		}
	}
	
	public void refreshGroupList()
	{
		groups = LinphoneGroupChatManager.getInstance().getGroupChatList();
		hideAndDisplayMessageIfNoChat();
	}
	
	/**
	 * Enable and disable groupchat and chats tab as needed
	 * Also change fastNewChat edit text and hint
	 */
	private void toggleContactsTab()
	{
		if (displayGroupChats)
		{
			groupsTab.setEnabled(false);
			chatsTab.setEnabled(true);
			fastNewChat.setText("");
			fastNewChat.setHint(R.string.group_list_name);
		}
		else
		{
			groupsTab.setEnabled(true);
			chatsTab.setEnabled(false);
			fastNewChat.setText("");
			fastNewChat.setHint(R.string.new_fast_chat);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, final int position, long id) {
		if (displayGroupChats)
		{
			if (!isEditMode) {
				//Make Appropriate GroupChatMessagingFragment appear
				Intent intent = new Intent(getActivity(), GroupChatActivity.class);
				Bundle b = new Bundle();
				b.putString("fragment", "gcMessagingFragment");
				LinphoneGroupChatRoom group = (LinphoneGroupChatRoom) view.getTag();
				b.putString("groupID", group.getGroupId());
				b.putString("groupName", group.getName());
				intent.putExtras(b);
				startActivity(intent);
			}
			else	// Leave this group
			{
				String group = ((LinphoneGroupChatRoom) view.getTag()).getName();
				// Alert to confirm leave group
				AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
	            builder1.setMessage("Are you sure you want to leave group " + group);
	            builder1.setCancelable(true);
	            builder1.setPositiveButton("Yes",
	                    new DialogInterface.OnClickListener() {
	                
					public void onClick(DialogInterface dialog, int id) {
						leaveGroup(position);	// Leave this group
	                }
	            });
	            builder1.setNegativeButton("No", 
	            		new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
	            AlertDialog alert11 = builder1.create();
	            alert11.show();
				
				hideAndDisplayMessageIfNoChat();
			}
			
		}
		else
		{
			String sipUri = (String) view.getTag();
			
			if (LinphoneActivity.isInstanciated() && !isEditMode) {
				LinphoneActivity.instance().displayChat(sipUri);
			} else if (LinphoneActivity.isInstanciated()) {
				LinphoneActivity.instance().removeFromChatList(sipUri);
				LinphoneActivity.instance().removeFromDrafts(sipUri);
				
				mConversations = LinphoneActivity.instance().getChatList();
				mDrafts = LinphoneActivity.instance().getDraftChatList();
				mConversations.removeAll(mDrafts);
				hideAndDisplayMessageIfNoChat();
				
				LinphoneActivity.instance().updateMissedChatCount();
			}
		}

	}
	
	/**
	 * Method to leave a chosen group
	 * @param position The position in the ListView of Groups indicating group to leave
	 */
	protected void leaveGroup(int position) {
		try {
			LinphoneGroupChatManager.getInstance().deleteGroupChat(groups.get(position).getGroupId());
			hideAndDisplayMessageIfNoChat();
		} catch (GroupDoesNotExistException e) {
			e.printStackTrace();
		} catch (IsAdminException e) {
			// Display an alert to inform the user to assign a new admin
			AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage("Please assign a new admin before leaving this group");
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

	private boolean importAndroidStoredMessagedIntoLibLinphoneStorage() {
		Log.w("Importing previous messages into new database...");
		try {
			ChatStorage db = LinphoneActivity.instance().getChatStorage();
			List<String> conversations = db.getChatList();
			for (int j = conversations.size() - 1; j >= 0; j--) {
				String correspondent = conversations.get(j);
				LinphoneChatRoom room = LinphoneManager.getLc().getOrCreateChatRoom(correspondent);
				for (ChatMessage message : db.getMessages(correspondent)) {
					LinphoneChatMessage msg = room.createLinphoneChatMessage(message.getMessage(), message.getUrl(), message.getStatus(), Long.parseLong(message.getTimestamp()), true, message.isIncoming());
					if (message.getImage() != null) {
						String path = saveImageAsFile(message.getId(), message.getImage());
						if (path != null)
							msg.setExternalBodyUrl(path);
					}
					msg.store();
				}
				db.removeDiscussion(correspondent);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private String saveImageAsFile(int id, Bitmap bm) {
		try {
			String path = Environment.getExternalStorageDirectory().toString();
			if (!path.endsWith("/"))
				path += "/";
			path += "Pictures/";
			File directory = new File(path);
			directory.mkdirs();
			
			String filename = getString(R.string.picture_name_format).replace("%s", String.valueOf(id));
			File file = new File(path, filename);
			
			OutputStream fOut = null;
			fOut = new FileOutputStream(file);

			bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
			
			return path + filename;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	class ChatListAdapter extends BaseAdapter {
		private boolean useNativeAPI;
		
		ChatListAdapter(boolean useNativeAPI) {
			this.useNativeAPI = useNativeAPI;
		}
		
		public int getCount() {
			return mConversations.size() + mDrafts.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			
			if (convertView != null) {
				view = convertView;
			} else {
				view = mInflater.inflate(R.layout.chatlist_cell, parent, false);
				
			}
			String contact;
			boolean isDraft = false;
			if (position >= mDrafts.size()) {
				contact = mConversations.get(position - mDrafts.size());
			} else {
				contact = mDrafts.get(position);
				isDraft = true;
			}
			view.setTag(contact);
			int unreadMessagesCount = LinphoneActivity.instance().getChatStorage().getUnreadMessageCount(contact);
			
			LinphoneAddress address;
			try {
				address = LinphoneCoreFactory.instance().createLinphoneAddress(contact);
			} catch (LinphoneCoreException e) {
				Log.e("Chat view cannot parse address",e);
				return view;
			}
			Contact lContact = ContactsManager.getInstance().findContactWithAddress(getActivity().getContentResolver(), address);

			String message = "";
			if (useNativeAPI) {
				LinphoneChatRoom chatRoom = LinphoneManager.getLc().getOrCreateChatRoom(contact);
				LinphoneChatMessage[] history = chatRoom.getHistory(20);
				if (history != null && history.length > 0) {
					for (int i = history.length - 1; i >= 0; i--) {
						LinphoneChatMessage msg = history[i];
						if (msg.getText() != null && msg.getText().length() > 0 && msg.getFileTransferInformation() == null) {
							message = msg.getText();
							break;
						}
					}
				}
			} else {
				List<ChatMessage> messages = LinphoneActivity.instance().getChatMessages(contact);
				if (messages != null && messages.size() > 0) {
					int iterator = messages.size() - 1;
					ChatMessage lastMessage = null;
					
					while (iterator >= 0) {
						lastMessage = messages.get(iterator);
						if (lastMessage.getMessage() == null) {
							iterator--;
						} else {
							iterator = -1;
						}
					}
					message = (lastMessage == null || lastMessage.getMessage() == null) ? "" : lastMessage.getMessage();
				}
			}
			TextView lastMessageView = (TextView) view.findViewById(R.id.lastMessage);
			lastMessageView.setText(message);
			
			TextView sipUri = (TextView) view.findViewById(R.id.sipUri);
			sipUri.setSelected(true); // For animation

			if (getResources().getBoolean(R.bool.only_display_username_if_unknown)) {
				sipUri.setText(lContact == null ? address.getUserName() : lContact.getName());
			} else {
				sipUri.setText(lContact == null ? address.asStringUriOnly() : lContact.getName());
			}

			if (isDraft) {
				view.findViewById(R.id.draft).setVisibility(View.VISIBLE);
			}
			
			
			ImageView delete = (ImageView) view.findViewById(R.id.delete);
			TextView unreadMessages = (TextView) view.findViewById(R.id.unreadMessages);
			
			if (unreadMessagesCount > 0) {
				unreadMessages.setVisibility(View.VISIBLE);
				unreadMessages.setText(String.valueOf(unreadMessagesCount));
			} else {
				unreadMessages.setVisibility(View.GONE);
			}
			
			if (isEditMode) {
				delete.setVisibility(View.VISIBLE);
			} else {
				delete.setVisibility(View.INVISIBLE);
			}
			
			return view;
		}
	}
	
	/**
	 * Adapter for list of groups
	 * @author Izak Blom
	 *
	 */
	class GroupListAdapter extends BaseAdapter
	{

		public GroupListAdapter()
		{
			groups = LinphoneGroupChatManager.getInstance().getGroupChatList();
		}
		
		@Override
		public int getCount() {
			return groups.size();
		}

		@Override
		public Object getItem(int position) {
			return groups.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			
			if (convertView != null)
				view = convertView;
			else
				view = mInflater.inflate(R.layout.chatlist_cell, parent, false);
			
			LinphoneGroupChatRoom group = groups.get(position);
			view.setTag(group);
			
			TextView groupName = (TextView) view.findViewById(R.id.sipUri);
			groupName.setText(group.getName());
			
			ImageView delete = (ImageView) view.findViewById(R.id.delete);
			
			try {
				GroupChatMessage lastMessage = group.getHistory().getLast();
				TextView lastMsg = (TextView) view.findViewById(R.id.lastMessage);
				String senderName = lastMessage.sender;
				senderName = senderName.substring(senderName.indexOf(':') + 1, senderName.indexOf('@'));
				lastMsg.setText(senderName + ": " + lastMessage.message);
				lastMsg.setVisibility(View.VISIBLE);
			} catch (NoSuchElementException e){}
			
			TextView unread = (TextView) view.findViewById(R.id.unreadMessages);
			int unreadMessages = group.getUnreadMessagesCount();

			if (unreadMessages > 0)
			{
				unread.setVisibility(View.VISIBLE);
				unread.setText(String.valueOf(unreadMessages));
				
			}
			
			
			if (isEditMode)
				delete.setVisibility(View.VISIBLE);
			else
				delete.setVisibility(View.INVISIBLE);
			
			return view;
		}
		
	}
}


