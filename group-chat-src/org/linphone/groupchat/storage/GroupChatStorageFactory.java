package org.linphone.groupchat.storage;

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
			case SQLite :
			default : return GroupChatStorageAndroidImpl.getInstance();
		}
	}
}
