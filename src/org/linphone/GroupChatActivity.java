package org.linphone;

/**
 * @class GroupChatActivity
 * @author Izak Blom
 * The GroupChatActivity instantiates and swaps respective User Interface Fragments.
 * It also serves as a communication channel between Fragments
 */

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

public class GroupChatActivity extends FragmentActivity
{
	private static GroupChatActivity instance;
	private static final String GROUP_CHAT_CREATION_FRAGMENT = "gcCreationFragment";
	private static final String GROUP_CHAT_MSG_FRAGMENT = "gcMessagingFragment";
	private static final String GROUP_CHAT_SETTINGS_FRAGMENT = "gcSettingsFragment";
	private GroupChatMessagingFragment gcMessagingFragment;
	private GroupChatSettingsFragment gcSettingsFragment;
	private GroupChatCreationFragment gcCreationFragment;
	
	static final boolean isInstanciated() {
		return instance != null;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupchat_activity);
		// frag passed in intent indicates which fragment to instantiate upon Activity creation
		String frag = getIntent().getExtras().getString("fragment");
		
		
		instance = this;
		Bundle extras = new Bundle();
		// extras.putString("SipUri", getIntent().getExtras().getString("SipUri"));
		
		// When create group button clicked from ChatListFragment
		if (frag.equals(GROUP_CHAT_CREATION_FRAGMENT))
		{
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
		// When a groupchat is selected from ChatListFragment
		else if (frag.equals(GROUP_CHAT_MSG_FRAGMENT))
		{
			FragmentManager fm = getSupportFragmentManager();
			gcMessagingFragment = (GroupChatMessagingFragment) fm.findFragmentByTag(GROUP_CHAT_MSG_FRAGMENT);

		    // If the Fragment is non-null, then it is currently being
		    // retained across a configuration change.
			if (gcMessagingFragment == null) {
				gcMessagingFragment = new GroupChatMessagingFragment();
				gcMessagingFragment.setArguments(getIntent().getExtras());
				//gcCreationFragment.setArguments(extras);
				fm.beginTransaction().add(R.id.gcactivity, gcMessagingFragment, GROUP_CHAT_MSG_FRAGMENT).commit();
		    }
		}
		
	}
	
	/**
	 * Method to change currently visible Fragment. Called from Fragment as getActivity().changeFragment(..)
	 * @param fragment Indicates which fragment to change to. Should match one of the static variables above
	 * @param extras Parameters to be passed to the fragment upon creation
	 */
	public void changeFragment(String fragment, Bundle extras)
	{
		if (fragment.equals(GROUP_CHAT_SETTINGS_FRAGMENT))
		{
			FragmentManager fm = getSupportFragmentManager();
			gcSettingsFragment = (GroupChatSettingsFragment) fm.findFragmentByTag(GROUP_CHAT_SETTINGS_FRAGMENT);

		    // If the Fragment is non-null, then it is currently being
		    // retained across a configuration change.
			if (gcSettingsFragment == null) {
				gcSettingsFragment = new GroupChatSettingsFragment();
				gcSettingsFragment.setArguments(extras);
				// .addToBackStack to ensure previously shown fragment is made visible when back pressed from GroupChatSettingsFragment
				fm.beginTransaction().replace(R.id.gcactivity, gcSettingsFragment, GROUP_CHAT_SETTINGS_FRAGMENT).addToBackStack(null).commit();
		    }
		}
		else if (fragment.equals(GROUP_CHAT_MSG_FRAGMENT))
		{
			FragmentManager fm = getSupportFragmentManager();
			gcMessagingFragment = (GroupChatMessagingFragment) fm.findFragmentByTag(GROUP_CHAT_MSG_FRAGMENT);
			
		    // If the Fragment is non-null, then it is currently being
		    // retained across a configuration change.
			if (gcMessagingFragment == null) {
				gcMessagingFragment = new GroupChatMessagingFragment();
				gcMessagingFragment.setArguments(extras);
				// No addToBackStack. Change to GroupChatMessagingFragment occurs only when next clicked on GroupChatCreationFragment.
				// Back navigation should not return to creation screen. 
				fm.beginTransaction().replace(R.id.gcactivity, gcMessagingFragment, GROUP_CHAT_MSG_FRAGMENT).commit();
		    }
		}
	}
	
	/**
	 * Empty method needed when radio buttons used even if event not handled here
	 * @param view
	 */
	public void onRadioButtonClicked(View view)
	{
		
	}
	
	/**
	 * Back navigation from activity
	 */
	public void onBackPressed()
	{
		super.onBackPressed();
	}

	
}
