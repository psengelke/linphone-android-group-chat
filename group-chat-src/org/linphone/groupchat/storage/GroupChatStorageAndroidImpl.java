package org.linphone.groupchat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import org.linphone.LinphoneService;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.MemberDoesNotExistException;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;
import org.linphone.groupchat.interfaces.GroupChatStorage;

import java.lang.Override;
import java.lang.String;
import java.security.PrivateKey;
import java.util.LinkedList;

/**
 * This class uses {@link org.linphone.interfaces.GroupChatStorage} instance.
 *
 * @author David Breetzke
 */

class GroupChatStorageAndroidImpl implements GroupChatStorage {

	private GroupChatHelper helper;
	private Context context;
    public GroupChatStorageAndroidImpl(){
    	helper = new GroupChatHelper(LinphoneService.instance().getApplicationContext());
    }

    public void close(){}
    
    @Override
	public void saveTextMessage(String id, GroupChatMessage message) {
		// TODO Auto-generated method stub
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(GroupChatHelper.Messages.memberId, from);
        values.put(GroupChatHelper.Messages.messageText, message.message); // store message

        // Inserting Row
        db.insert(GroupChatHelper.Messages.tableName, null, values);
        db.close(); // Closing database connection
		
	}

	@Override
	public void saveImageMessage(String id, GroupChatMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveVoiceRecording(String id, GroupChatMessage message) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public LinkedList<GroupChatMessage> getMessages(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
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
	
    public LinkedList<GroupChatMember> getMembers(String groupId){
    	return null; // TODO 
    }
    
    @Override
	public void createGroupChat(GroupChatData data) {
		
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GroupChatHelper.Groups.groupId, data.group_id);
        values.put(GroupChatHelper.Groups.groupName, data.group_name);
        values.put(GroupChatHelper.Groups.adminId, data.admin);
        values.put(GroupChatHelper.Groups.encryptionType, data.encryption_type.ordinal());

        db.insert(GroupChatHelper.Groups.tableName, null, values);
        db.close();

		// TODO loop through data.members and add them to members table
	}

	@Override
	public void addMember(String id, GroupChatMember member) {
		// TODO Auto-generated method stub
		
	}
	
	public void markChatAsRead(String groupId){}

	@Override
	public void updateGroupName(String grouId, String name) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateSecretKey(String id, Long key) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateEncryptionType(String id, EncryptionType type) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateMemberStatus(String id, GroupChatMember member) throws MemberDoesNotExistException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAdmin(String id, GroupChatMember member) throws GroupDoesNotExistException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeMember(String id, GroupChatMember member) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void deleteChat(String groupIdToDelete) throws GroupDoesNotExistException {
        SQLiteDatabase db = helper.getWritableDatabase();
        //delete groupIdToDelete from Groups table
        if( db.delete(GroupChatHelper.Groups.tableName, GroupChatHelper.Groups.groupId + " =?",
        		new String[]{groupIdToDelete}) ==0) {
            throw new GroupDoesNotExistException("Group could not be found in the database!");
        }
        //remove all members associated with groupIdToDelete from Members table
        db.delete(GroupChatHelper.Members.tableName, GroupChatHelper.Members.groupId + " =?",
                new String[]{groupIdToDelete});

        // TODO delete messages from Message Table. ?????? Should message table have groupId key????
    }



	// TODO
	public void saveVoiceRecording(String from, /*Bitstream voiceNote,*/ long time){}


	
	
    //http://androidhive.info/2011/11/android-sqlite-database-tutorial is helpful
    // this class extends a class provided by the android sdk, SQLiteOpenHelper
    private class GroupChatHelper extends SQLiteOpenHelper{
        private static final int GROUPCHAT_DB_VERSION = 1;

        private static final String GROUPCHAT_DB_NAME = "GroupChatStorageDatabase";

        //Groups Table
        public class Groups{
            private static final String tableName = "Groups";
            private static final String id = "_id";
            private static final String idType = " INTEGER(10) PRIMARY KEY AUTOINCREMENT ";
            private static final String groupId = "group_id";
            private static final String groupIdType = " VARCHAR(255) ";
            private static final String groupName = "group_name";
            private static final String groupNametype = " VARCHAR(50) ";
            private static final String encryptionType = "encryption_type";
            private static final String encryptionTypeType = " INTEGER(1) "; 
            private static final String adminId = "admin_id";
            private static final String adminIdType = " INTEGER(10) "; 
        }

        //Messages Table
        public class Messages{
            private static final String tableName = "Messages";
            private static final String id = "_id";
            private static final String idType = " INTEGER(10) PRIMARY KEY AUTOINCREMENT ";
            private static final String messageText = "message_text";
            private static final String messageTextType = " TEXT ";
            private static final String memberId = "member_id";
            private static final String memberIdType = " INTEGER(10) "; 
            private static final String messageState = "message_state";
            private static final String messageStateType = " INTEGER(1) ";
            private static final String messageDirection = "message_direction";
            private static final String messageDirectionType = " INTEGER(1) "; 
            private static final String timeSent = "time_sent";
            private static final String timeSentType = "DATETIME"; 
        }

        //Members Table
        public class Members{
            private static final String tableName = "Members";
            private static final String id = "_id";
            private static final String idType = " INTEGER(10) PRIMARY KEY AUTOINCREMENT ";
            private static final String name = "name";
            private static final String nameType =" VARCHAR(50) ";
            private static final String sipAddress = "sip_address";
            private static final String sipAddressType = " VARCHAR(50) ";
            private static final String publicKey = "public_key"; // this field will no longer be needed
            private static final String publicKeyType = " INTEGER(10) ";
            private static final String groupId = "group_id";
            private static final String groupIdType = " INTEGER(10) ";
        }

        //Attachments Table
        public class Attachments{
            private static final String tableName = "Attachments";
            private static final String id = "_id";
            private static final String idType = " INTEGER(10) PRIMARY KEY AUTOINCREMENT ";
            private static final String file = "file";
            private static final String fileType = " BLOB ";
            private static final String messageId = "message_id";
            private static final String messageIdType = " INTEGER(10)";
        }


        public GroupChatHelper(Context context){
            super(context, GROUPCHAT_DB_NAME, null, GROUPCHAT_DB_VERSION);
        }

        //creating tables
        @Override
        public void onCreate(SQLiteDatabase db){
            String createGroupsTable = "CREATE TABLE " + Groups.tableName + "("
                    + Groups.id + " " + Groups.idType + ", " +  Groups.groupId + " " + Groups.groupIdType + ", "
                    + Groups.groupName + " " + Groups.groupNametype + ", " +  Groups.encryptionType 
                    + " " + Groups.encryptionTypeType + ", " +  Groups.adminId + " " + Groups.adminIdType + " )";
            
            String createMessagesTable = "CREATE TABLE " + Messages.tableName + "("
                    + Messages.id + " "+ Messages.idType +", " +  Messages.messageText + " " + Messages.messageTextType 
                    + "," + Messages.memberId + " " + Messages.memberIdType + "," +  Messages.messageState 
                    + " " + Messages.messageStateType + ", " + Messages.messageDirection + " " 
                    + Messages.messageDirectionType +", " + Messages.timeSent + " " + Messages.timeSentType + " )";
            
            String createMembersTable = "CREATE TABLE " + Members.tableName + "(" + Members.id + " " + Members.idType 
            		+ ", " +  Members.name + " " + Members.nameType + ", " + Members.sipAddress + " " + Members.sipAddressType 
            		+ ", " +  Members.publicKey + " " + Members.publicKeyType + ", " + Members.groupId + " " + Members.groupIdType + " )";
            
            String createAttachmentsTable = "CREATE TABLE " + Attachments.tableName + "("
                    + Attachments.id + " " + Attachments.idType + ", " +  Attachments.file + " " + Attachments.fileType 
                    + ", " + Attachments.messageId  + Attachments.messageIdType  + " )";


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
