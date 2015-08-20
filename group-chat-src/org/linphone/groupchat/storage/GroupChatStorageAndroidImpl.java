package org.linphone.groupchat.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.linphone.groupchat.interfaces.GroupChatStorage;

import java.lang.Override;
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

        //add table names as Strings
        //private static final String TABLE_A = "NameForTableA";
        //add column names under each table name, as Strings
        //These can be used literally in onCreate without being declared here, but when DBs get large
        //this helps readability


        public GroupChatHelper(Context context){
            super(context, GROUPCHAT_DB_NAME, null, GROUPCHAT_DB_VERSION);
        }

        //creating tables
        @Override
        public void onCreate(SQLiteDatabase db){
            String CREATE_TABLE_A = "CREATE TABLE "/*+ Table_Name*/;
            db.execSQL(CREATE_TABLE_A);
        }

        //upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            //drop old table
            db.execSQL("DROP TABLE IF EXISTS "/*+ Table_Name*/);
            //create tables again
            onCreate(db);
        }


    }
}
