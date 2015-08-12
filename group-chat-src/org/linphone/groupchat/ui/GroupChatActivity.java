package org.linphone.groupchat.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class GroupChatActivity extends FragmentActivity
{
	private static final String GROUP_CHAT_FRAGMENT;
	private GroupChatMessagingFragment groupChatFragment;
	private GroupChatSettingsFragment groupChatSettingsFragment;
	
	@Override
	/*
	 * Please modify as needed. Just included code that might be needed here...
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_chat_activity);
		
		Bundle extras = new Bundle();
		// extras.putString("SipUri", getIntent().getExtras().getString("SipUri"));
		
		
		GroupChatFragment gFragment = new GroupChatFragment();
		gFragment.setArguments(extras);
		getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment, "GroupChatFragment").commit();
		
		FragmentManager fm = getSupportFragmentManager();
		groupChatFragment = (GroupChatFragment) fm.findFragmentByTag(GROUP_CHAT_FRAGMENT);

	    // If the Fragment is non-null, then it is currently being
	    // retained across a configuration change.
		if (groupChatFragment == null) {
			groupChatFragment = new GroupChatFragment();
			groupChatFragment.setArguments(extras);
			fm.beginTransaction().add(R.id.fragmentContainer, groupChatFragment, GROUP_CHAT_FRAGMENT).commit();
	    }
	}
}
