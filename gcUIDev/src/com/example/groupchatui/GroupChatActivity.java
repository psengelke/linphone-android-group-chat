package com.example.groupchatui;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

public class GroupChatActivity extends FragmentActivity
{
	private static GroupChatActivity instance;
	private static final String GROUP_CHAT_FRAGMENT = "gcCreationFragment";
	private static final String GROUP_CHAT_INFO_FRAGMENT = "gcInfoFragment";
	private static final String GROUP_CHAT_MSG_FRAGMENT = "gcMessagingFragment";
	private GroupChatMessagingFragment gcMessagingFragment;
	private GroupChatSettingsFragment gcSettingsFragment;
	private GroupChatCreationFragment gcCreationFragment;
	
	static final boolean isInstanciated() {
		return instance != null;
	}
	
	@Override
	/*
	 * Please modify as needed. Just included code that might be needed here...
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupchat_activity);
		String frag = getIntent().getExtras().getString("fragment");
		
		
		instance = this;
		Bundle extras = new Bundle();
		// extras.putString("SipUri", getIntent().getExtras().getString("SipUri"));
		
		if (frag.equals(GROUP_CHAT_FRAGMENT))
		{
			GroupChatCreationFragment gFragment = new GroupChatCreationFragment();
			//gFragment.setArguments(extras);
			getSupportFragmentManager().beginTransaction().add(R.id.gcactivity, gFragment, GROUP_CHAT_FRAGMENT).commit();
			
			FragmentManager fm = getSupportFragmentManager();
			gcCreationFragment = (GroupChatCreationFragment) fm.findFragmentByTag(GROUP_CHAT_FRAGMENT);

		    // If the Fragment is non-null, then it is currently being
		    // retained across a configuration change.
			if (gcCreationFragment == null) {
				gcCreationFragment = new GroupChatCreationFragment();
				//gcCreationFragment.setArguments(extras);
				fm.beginTransaction().add(R.id.gcactivity, gcCreationFragment, GROUP_CHAT_FRAGMENT).commit();
		    }
		}
		else if (frag.equals(GROUP_CHAT_MSG_FRAGMENT))
		{
			String whichGroup = getIntent().getExtras().getString("groupClicked");
			GroupChatMessagingFragment gFragment = new GroupChatMessagingFragment();
			//gFragment.setArguments(extras);
			getSupportFragmentManager().beginTransaction().add(R.id.gcactivity, gFragment, GROUP_CHAT_MSG_FRAGMENT).commit();
			
			FragmentManager fm = getSupportFragmentManager();
			gcMessagingFragment = (GroupChatMessagingFragment) fm.findFragmentByTag(GROUP_CHAT_MSG_FRAGMENT);

		    // If the Fragment is non-null, then it is currently being
		    // retained across a configuration change.
			if (gcMessagingFragment == null) {
				gcMessagingFragment = new GroupChatMessagingFragment();
				//gcCreationFragment.setArguments(extras);
				fm.beginTransaction().add(R.id.gcactivity, gcMessagingFragment, GROUP_CHAT_MSG_FRAGMENT).commit();
		    }
		}
		
	}
	
	public void onRadioButtonClicked(View view)
	{
		
	}
	
	public void onBackPressed()
	{
		super.onBackPressed();
		
	}

	
}
