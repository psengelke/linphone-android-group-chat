package org.linphone.groupchat.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.groupchat.interfaces.GroupChatStorage;

import java.lang.Override;
import java.lang.String;
import java.security.PrivateKey;
import java.util.LinkedList;

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

    public LinkedList<GroupChatMember> getMembers(String groupId){}

    public void updateEncryptionType(String id, EncryptionHandler.EncryptionType type){}

    public void createGroupChat(String groupId, String groupName,  EncryptionType encryptionType,
                                LinkedList<GroupChatMember> memberList){}

    public void updateMemberPublicKey(){}








    //http://androidhive.info/2011/11/android-sqlite-database-tutorial is helpful
    private class GroupChatHelper extends SQLiteOpenHelper{// this class implements a class provided by the android sdk, SQLiteOpenHelper
        private static final int GROUPCHAT_DB_VERSION = 1;

        private static final String GROUPCHAT_DB_NAME = "GroupChatStorageDatabase";

        //Groups Table
        private class Groups{
            private static final String tableName = "Groups";
            private static final String id = "_id";
            private static final String groupId = "group_id";
            private static final String groupName = "group_name";
            private static final String encryptionType = "encryption_type";
            private static final String adminId = "admin_id";
        }

        //Messages Table
        private class Messages{
            private static final String tableName = "Messages";
            private static final String id = "_id";
            private static final String messageText = "message_text";
            private static final String memberId = "member_id";
            private static final String messageState = "message_state";
            private static final String messageDirection = "message_direction";
            private static final String timeSent = "time_sent";
        }

        //Members Table
        private class Members{
            private static final String tableName = "Members";
            private static final String id = "_id";
            private static final String name = "name";
            private static final String sipAddress = "sip_address";
            private static final String publicKey = "public_key";
            private static final String groupId = "group_id";
        }

        //Attachments Table
        private class Attachments{
            private static final String tableName = "Attachments";
            private static final String id = "_id";
            private static final String file = "file";
            private static final String messageId = "message_id";
        }


        public GroupChatHelper(Context context){
            super(context, GROUPCHAT_DB_NAME, null, GROUPCHAT_DB_VERSION);
        }

        //creating tables
        @Override
        public void onCreate(SQLiteDatabase db){
            String createGroupsTable = "CREATE TABLE " + Groups.tableName + "("
                    + group.id + " INTEGER(10) PRIMARY KEY," +  Groups.groupId + " VARCHAR(255),"
                    + Groups.name + " VARCHAR(50)," +  Groups.encryptionType + " VARCHAR(50),"
                    +  Groups.adminId + " INTEGER(10) " + ")";
            String createMessagesTable = "CREATE TABLE " + Messages.tableName + "("
                    + Messages.id + " INTEGER(10) PRIMARY KEY," +  Messages.messageText + " TEXT,"
                    + Messages.memberId + " INTEGER(10)," +  Messages.messageState + " INTEGER(1),"
                    + Messages.messageDirection + " INTEGER(1)," + Messages.timeSent + " DATETIME "
                    + ")";
            String createMembersTable = "CREATE TABLE " + Members.tableName + "("
                    + Members.id + " INTEGER(10) PRIMARY KEY," +  Members.name + " VARCHAR(50),"
                    + Members.sipAddress + " VARCHAR(50)," +  Members.publicKey + " INTEGER(10),"
                    + Members.groupId + " INTEGER(10)" + ")";
            String createAttachmentsTable = "CREATE TABLE " + Attachments.tableName + "("
                    + Attachments.id + " INTEGER(10) PRIMARY KEY," +  Attachments.file + " BLOB,"
                    + Attachments.messageId  + " INTEGER(10)" + ")";




            db.execSQL(createGroupsTable);
            db.execSQL(createMessagesTable);
            db.execSQL(createMembersTable);
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
    public static GroupChatStorage getInstance(){

        return InstanceHolder.INSTANCE;
    }

    /**
     * This class provides a thread-safe lazy initialisation of the singleton.
     */
    private static class InstanceHolder {

        private static final GroupChatStorageAndroidImpl INSTANCE = new GroupChatStorageAndroidImpl();
    }
}
