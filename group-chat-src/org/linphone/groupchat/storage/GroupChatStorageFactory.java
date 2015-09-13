package org.linphone.groupchat.storage;

import org.linphone.groupchat.interfaces.GroupChatStorage;

/**
 * 
 * This factory class provides a service for creating or getting storage instances.
 * 
 * @author Paul Engelke
 *
 */
public class GroupChatStorageFactory {

	public static enum StorageAdapterType {
		SQLite
	}
	
	public static GroupChatStorage getOrCreateGroupChatStorage(StorageAdapterType type){
		
		switch (type) {
			default : return GroupChatStorageAndroidImpl.getInstance();
		}
	}
}
