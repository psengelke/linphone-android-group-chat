package org.linphone.groupchat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import org.linphone.core.LinphoneChatMessage;
import org.linphone.groupchat.core.LinphoneGroupChatManager.GroupChatMember;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.interfaces.GroupChatStorage.GroupChatData;
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

	@Override
	public void createGroupChat(GroupChatData data) {
		// TODO Auto-generated method stub
		
	}


    public void deleteChat(String groupIdToDelete) throws GroupDoesNotExistException {
        SQLiteDatabase db = GroupChatHelper.getWritableDatabase();
        //delete groupIdToDelete from Groups table
        if( db.delete(GroupChatHelper.Groups.tableName, GroupChatHelper.Groups.groupId, + " =?",
                groupIdToDelete) ==0) {
            throw new GroupDoesNotExistException("Group could not be found in the database!");
        }
        //remove all members associated with groupIdToDelete from Members table
        db.delete(GroupChatHelper.Members.tableName, GroupChatHelper.Members.groupId, + " =?",
                groupIdToDelete);
    }

    // TODO from is a string and is the sip address of the sender --------------------------------->
    public void saveTextMessage(String from, String message, MessageDirection direction,
                                MessageState status, long time){

        SQLiteDatabase db = GroupChatHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(GroupChatHelper.Messages.memberId, from);
        values.put(GroupChatHelper.Messages.messageText, message); // store message

        // Inserting Row
        db.insert(GroupChatHelper.Messages.tableName, null, values);
        db.close(); // Closing database connection
    }

	@Override
	public void saveImageMessage(String from, Bitmap image, String url, long time) {
		// TODO Auto-generated method stub
		
	}

	// TODO
	public void saveVoiceRecording(String from, /*Bitstream voiceNote,*/ long time){}

	/**
	 * @return A list of group information containers.
	 */
    public LinkedList<GroupChatData> getChatList(){
    	return null;
    }
    
    /**
     * @return A list of group IDs for existing group chats.
     */
	@Override
	public LinkedList<String> getChatIdList() {
		// TODO Auto-generated method stub
		return null;
	}

    public void markChatAsRead(String groupId){}

    public LinkedList<GroupChatMember> getMembers(String groupId){
    	return null; // TODO 
    }

	@Override
	public void updateEncryptionType(String id, EncryptionType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LinkedList<LinphoneChatMessage> getMessages(String id) {
		// TODO Auto-generated method stub
		return null;
	}

    

    // A note on database tables:
    
    // The  _id field should be auto-incremented by the DBMS and not inserted when a new row is added.


    //http://androidhive.info/2011/11/android-sqlite-database-tutorial is helpful
    // this class extends a class provided by the android sdk, SQLiteOpenHelper
    private class GroupChatHelper extends SQLiteOpenHelper{
        private static final int GROUPCHAT_DB_VERSION = 1;

        private static final String GROUPCHAT_DB_NAME = "GroupChatStorageDatabase";

        //Groups Table
        public class Groups{
            private static final String tableName = "Groups";
            private static final String id = "_id";
            private static final String groupId = "group_id";
            private static final String groupName = "group_name";
            private static final String encryptionType = "encryption_type";
            private static final String adminId = "admin_id";
        }

        //Messages Table
        public class Messages{
            private static final String tableName = "Messages";
            private static final String id = "_id";
            private static final String messageText = "message_text";
            private static final String memberId = "member_id";
            private static final String messageState = "message_state";
            private static final String messageDirection = "message_direction";
            private static final String timeSent = "time_sent";
        }

        //Members Table
        public class Members{
            private static final String tableName = "Members";
            private static final String id = "_id";
            private static final String name = "name";
            private static final String sipAddress = "sip_address";
            private static final String publicKey = "public_key"; // this field will no longer be needed 
            private static final String groupId = "group_id";
        }

        //Attachments Table
        public class Attachments{
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
                    + Groups.id + " INTEGER(10) PRIMARY KEY," +  Groups.groupId + " VARCHAR(255),"
                    + Groups.groupName + " VARCHAR(50)," +  Groups.encryptionType + " VARCHAR(50),"
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
            db.execSQL(createAttachmentsTable);
        }

        //upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            //drop old tables
            db.execSQL("DROP TABLE IF EXISTS " + Groups.tableName + ", " + Messages.tableName + ", "
            + Members.tableName + ", " + Attachments.tableName );
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
