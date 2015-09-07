package com.example.groupchatui;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

public class GroupChatActivity extends FragmentActivity
{
	private static GroupChatActivity instance;
	private static final String GROUP_CHAT_CREATION_FRAGMENT = "gcCreationFragment";
	private static final String GROUP_CHAT_INFO_FRAGMENT = "gcInfoFragment";
	private static final String GROUP_CHAT_MSG_FRAGMENT = "gcMessagingFragment";
	private static final String GROUP_CHAT_SETTINGS_FRAGMENT = "gcSettingsFragment";
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
		
		if (frag.equals(GROUP_CHAT_CREATION_FRAGMENT))
		{
			GroupChatCreationFragment gFragment = new GroupChatCreationFragment();
			//gFragment.setArguments(extras);
			getSupportFragmentManager().beginTransaction().add(R.id.gcactivity, gFragment, GROUP_CHAT_CREATION_FRAGMENT).commit();
			
			FragmentManager fm = getSupportFragmentManager();
			gcCreationFragment = (GroupChatCreationFragment) fm.findFragmentByTag(GROUP_CHAT_CREATION_FRAGMENT);

		    // If the Fragment is non-null, then it is currently being
		    // retained across a configuration change.
			if (gcCreationFragment == null) {
				gcCreationFragment = new GroupChatCreationFragment();
				//gcCreationFragment.setArguments(extras);
				fm.beginTransaction().add(R.id.gcactivity, gcCreationFragment, GROUP_CHAT_CREATION_FRAGMENT).commit();
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
	
	public void changeFragment(String fragment)
	{
		if (fragment.equals(GROUP_CHAT_SETTINGS_FRAGMENT))
		{
			GroupChatSettingsFragment gFragment = new GroupChatSettingsFragment();
			
			FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
			tr.replace(R.id.gcactivity, gFragment, GROUP_CHAT_SETTINGS_FRAGMENT);
			tr.addToBackStack(GROUP_CHAT_SETTINGS_FRAGMENT);
			tr.commit();
			
			FragmentManager fm = getSupportFragmentManager();
			gcSettingsFragment = (GroupChatSettingsFragment) fm.findFragmentByTag(GROUP_CHAT_SETTINGS_FRAGMENT);
			
		    // If the Fragment is non-null, then it is currently being
		    // retained across a configuration change.
			if (gcSettingsFragment == null) {
				gcSettingsFragment = new GroupChatSettingsFragment();
				fm.beginTransaction().replace(R.id.gcactivity, gcSettingsFragment, GROUP_CHAT_SETTINGS_FRAGMENT)
					.addToBackStack(GROUP_CHAT_SETTINGS_FRAGMENT).commit();
		    }
		}
		else if (fragment.equals(GROUP_CHAT_MSG_FRAGMENT))
		{
//			GroupChatSettingsFragment gFragment = new GroupChatSettingsFragment();
//			
//			FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
//			tr.replace(R.id.gcactivity, gFragment, GROUP_CHAT_SETTINGS_FRAGMENT);
//			tr.addToBackStack(GROUP_CHAT_SETTINGS_FRAGMENT);
//			tr.commit();
			
			FragmentManager fm = getSupportFragmentManager();
			gcMessagingFragment = (GroupChatMessagingFragment) fm.findFragmentByTag(GROUP_CHAT_MSG_FRAGMENT);
			
		    // If the Fragment is non-null, then it is currently being
		    // retained across a configuration change.
			if (gcMessagingFragment == null) {
				gcMessagingFragment = new GroupChatMessagingFragment();
				fm.beginTransaction().replace(R.id.gcactivity, gcMessagingFragment, GROUP_CHAT_MSG_FRAGMENT).commit();
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
