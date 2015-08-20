package org.linphone.groupchat.storage;

import android.database.sqlite.SQLiteOpenHelper;

import org.linphone.groupchat.interfaces.GroupChatStorage;

/**
 *
 * @author David Breetzke
 *
 *	This class uses {@link org.linphone.interfaces.GroupChatStorage} instance.
 */

public class GroupChatStorageAndroidImpl implements GroupChatStorage {

    private class GroupChatHelper extends SQLiteOpenHelper{} // this class implements a class provided by the android sdk, SQLiteOpenHelper
}
