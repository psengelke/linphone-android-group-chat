package com.example.groupchatui;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class GroupChatActivity extends FragmentActivity
{
	private static final String GROUP_CHAT_FRAGMENT = "gcCreationFragment";
	private GroupChatMessagingFragment gcMessagingFragment;
	private GroupChatSettingsFragment gcSettingsFragment;
	private GroupChatCreationFragment gcCreationFragment;
	
	@Override
	/*
	 * Please modify as needed. Just included code that might be needed here...
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupchat_activity);
		
		Bundle extras = new Bundle();
		// extras.putString("SipUri", getIntent().getExtras().getString("SipUri"));
		
		
		GroupChatCreationFragment gFragment = new GroupChatCreationFragment();
		//gFragment.setArguments(extras);
		getSupportFragmentManager().beginTransaction().add(R.id.gcactivity, gFragment, GROUP_CHAT_FRAGMENT).addToBackStack(null).commit();
		
		FragmentManager fm = getSupportFragmentManager();
		gcCreationFragment = (GroupChatCreationFragment) fm.findFragmentByTag(GROUP_CHAT_FRAGMENT);

	    // If the Fragment is non-null, then it is currently being
	    // retained across a configuration change.
		if (gcCreationFragment == null) {
			gcCreationFragment = new GroupChatCreationFragment();
			//gcCreationFragment.setArguments(extras);
			fm.beginTransaction().add(R.id.gcactivity, gcCreationFragment, GROUP_CHAT_FRAGMENT).addToBackStack(null).commit();
	    }
	}
	
	public void onBackPressed()
	{
		super.onBackPressed();
		
	}
}
