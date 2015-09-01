package org.linphone.groupchat.storage;

import org.linphone.groupchat.interfaces.GroupChatStorage;

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
