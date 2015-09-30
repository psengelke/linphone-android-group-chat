package org.linphone.groupchat.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class GroupChatActivity extends FragmentActivity
{
	private static final String GROUP_CHAT_FRAGMENT = "";
	private GroupChatMessagingFragment groupChatFragment;
	private GroupChatSettingsFragment groupChatSettingsFragment;
	
	@Override
	/*
	 * Please modify as needed. Just included code that might be needed here...
	 */
	public void onCreate(Bundle savedInstanceState) {
		/*super.onCreate(savedInstanceState);
		setContentView(R.layout.groupchat_activity);
		
		Bundle extras = new Bundle();
		// extras.putString("SipUri", getIntent().getExtras().getString("SipUri"));
		
		
		GroupChatMessagingFragment gFragment = new GroupChatMessagingFragment();
		gFragment.setArguments(extras);
		getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, gFragment, "GroupChatFragment").commit();
		
		FragmentManager fm = getSupportFragmentManager();
		groupChatFragment = (GroupChatMessagingFragment) fm.findFragmentByTag(GROUP_CHAT_FRAGMENT);

	    // If the Fragment is non-null, then it is currently being
	    // retained across a configuration change.
		if (groupChatFragment == null) {
			groupChatFragment = new GroupChatMessagingFragment();
			groupChatFragment.setArguments(extras);
			fm.beginTransaction().add(R.id.fragmentContainer, groupChatFragment, GROUP_CHAT_FRAGMENT).commit();
	    }*/
	}
}
