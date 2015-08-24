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

    public GroupChatStorageAndroidImpl(){}


    public void close(){}

    //public void updateMessageStatus(String to, String message, MessageState status);

    //public void updateMessageStatus(String to, String id, MessageState status);

    public void saveTextMessage(String from, String message, MessageDirection direction,
                                MessageState status, long time){}

    public void saveImageMessage(String from, Bitmap image, String url, long time){}

    // not sure bitstream exists
    public void saveVoiceRecording(String from, Bitstream voiceNote, long time){}

    public LinkedList<LinphoneChatMessage> getMessage(String id){}

    public LinkedList<String> getChatList(){}

    //maybe make return Boolean? -- could do, we have to look at return types where possible as well
    // as exceptions for testability and system control
    // and stability
    public void deleteChat(String groupId){}

    public void markChatAsRead(String groupId){}

    public GroupChatMember getMembers(String groupId){}

    public void updateEncryptionType(String id, EncryptionHandler.EncryptionType type){}




    



    //http://androidhive.info/2011/11/android-sqlite-database-tutorial is helpful
    private class GroupChatHelper extends SQLiteOpenHelper{// this class implements a class provided by the android sdk, SQLiteOpenHelper
        private static final int GROUPCHAT_DB_VERSION = 1;

        private static final String GROUPCHAT_DB_NAME = "GroupChatStorageDatabase";

        //add table names as Strings
        private static final String TABLE_GROUPS = "Groups";
        private static final String TABLE_MESSAGES = "Messages";
        private static final String TABLE_MEMBERS = "Members";
        private static final String TABLE_ATTACHMENTS = "Attachments";
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


	/* -- Singleton Declarations -- */
    /**
     * Getter method for the singleton.
     * @return The {@link GroupChatStorageAndroidImpl} singleton instance.
     */
    public static GroupChatStorageAndroidImpl getInstance(){

        return InstanceHolder.INSTANCE;
    }

    /**
     * This class provides a thread-safe lazy initialisation of the singleton.
     */
    private static class InstanceHolder {

        private static final GroupChatStorageAndroidImpl INSTANCE = new GroupChatStorageAndroidImpl();
    }
}
