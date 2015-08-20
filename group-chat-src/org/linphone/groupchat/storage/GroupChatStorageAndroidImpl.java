package org.linphone.groupchat.storage;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import org.linphone.groupchat.interfaces.GroupChatStorage;

import java.lang.String;

/**
 *
 * @author David Breetzke
 *
 *	This class uses {@link org.linphone.interfaces.GroupChatStorage} instance.
 */

public class GroupChatStorageAndroidImpl implements GroupChatStorage {

    //http://androidhive.info/2011/11/android-sqlite-database-tutorial is helpful
    private class GroupChatHelper extends SQLiteOpenHelper{// this class implements a class provided by the android sdk, SQLiteOpenHelper
        private static final int GROUPCHAT_DB_VERSION = 1;

        private static final String GROUPCHAT_DB_NAME = "GroupChatStorageDatabase";

        //add tables and name them

        public GroupChatHelper(Context context){
            super(context, GROUPCHAT_DB_NAME, null, GROUPCHAT_DB_VERSION);
        }



    }
}
