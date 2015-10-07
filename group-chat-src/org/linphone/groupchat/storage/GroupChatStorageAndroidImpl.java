package org.linphone.groupchat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;

import org.linphone.LinphoneService;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.MemberDoesNotExistException;

import java.lang.Override;
import java.lang.String;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

/**
 * This class uses {@link org.linphone.groupchat.storage.interfaces.GroupChatStorage} instance.
 *
 * @author David Breetzke
 * @author Jessica Lessev
 */

class GroupChatStorageAndroidImpl implements GroupChatStorage {

	private GroupChatHelper helper;
	private Context context;
    public GroupChatStorageAndroidImpl(){
    	helper = new GroupChatHelper(LinphoneService.instance().getApplicationContext());
    }

    public void close(){}
    
    /***********************************************************************************************************************/    
    @Override
	public void createGroupChat(GroupChatData data) {
		
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GroupChatHelper.Groups.groupId, data.group_id);
        values.put(GroupChatHelper.Groups.groupName, data.group_name);
        values.put(GroupChatHelper.Groups.adminId, data.admin);
        values.put(GroupChatHelper.Groups.encryptionType, data.encryption_type.ordinal());
        values.put(GroupChatHelper.Groups.secretKey,"");

        db.insert(GroupChatHelper.Groups.tableName, null, values);
        db.close();
        
        Iterator<GroupChatMember> it = data.members.iterator();
        while (it.hasNext()){
        	addMember(data.group_id, it.next());
        }
	}
    /***********************************************************************************************************************/
    
    /***********************************************************************************************************************/

   	@Override
   	public void addMember(String id, GroupChatMember member) {

   		SQLiteDatabase db = helper.getWritableDatabase();
           ContentValues values = new ContentValues();

           values.put(GroupChatHelper.Members.name, member.name);
           values.put(GroupChatHelper.Members.sipAddress, member.sip);
           values.put(GroupChatHelper.Members.groupId, id);
           
           db.insert(GroupChatHelper.Groups.tableName, null, values);
           db.close();
   		
   	}
   	
   	
   	/***********************************************************************************************************************/
   	/***********************************************************************************************************************/
	
	public void markChatAsRead(String groupId){
		
		String query = "Update Messages SET message_state = 0 WHERE Messages.member_id = (SELECT Members._id FROM Members WHERE Members.group_id = '"+groupId + "')" ;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(query);       
	}
	

	/***********************************************************************************************************************/
	/***********************************************************************************************************************/
	 @Override
		public void saveTextMessage(String id, GroupChatMessage message) {
	        SQLiteDatabase db = helper.getWritableDatabase();
	        ContentValues values = new ContentValues();
	        
	        values.put(GroupChatHelper.Messages.messageText, message.message);
	        values.put(GroupChatHelper.Messages.memberId, message.sender);
	        values.put(GroupChatHelper.Messages.messageState, message.state.ordinal());
	        values.put(GroupChatHelper.Messages.messageDirection, message.direction.ordinal());
	        values.put(GroupChatHelper.Messages.timeSent, message.time.getTime());

	        // Inserting Row
	        db.insert(GroupChatHelper.Messages.tableName, null, values);
	        db.close(); // Closing database connection
			
		}
	 

		/***********************************************************************************************************************/

		/***********************************************************************************************************************/
		@Override
		public LinkedList<GroupChatMessage> getMessages(String id) {
			
			SQLiteDatabase db = helper.getReadableDatabase();
			String query = "SELECT * FROM Messages WHERE Messages.member_id = (SELECT Members._id FROM Members WHERE Members.group_id = '"+id + "')" ;
			Cursor c = db.rawQuery(query, null);
			
			LinkedList<GroupChatMessage> el = new LinkedList<>();
			GroupChatMessage temp = new GroupChatMessage();
			
			SimpleDateFormat format = new SimpleDateFormat ("MMMM d, yyyy", Locale.ENGLISH);	
			Date d=null;
			 if(c.moveToFirst()){
		            do{	          
		               temp.id = c.getInt(c.getColumnIndex(GroupChatHelper.Messages.id));
		               temp.message = c.getString(c.getColumnIndex(GroupChatHelper.Messages.messageText));
		               temp.sender = c.getString(c.getColumnIndex(GroupChatHelper.Messages.memberId));
		               temp.state = MessageState.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Messages.messageState))];
		               temp.direction = MessageDirection.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Messages.messageDirection))];
		               try {
						d=format.parse(c.getString(c.getColumnIndex(GroupChatHelper.Messages.timeSent)));
			               temp.time= d;
						} catch (ParseException e) {
							e.printStackTrace();
						}
		               el.add(temp);
		            }while(c.moveToNext());
		        }
		        c.close();
		        db.close();
			return el;
		}

		/***********************************************************************************************************************/   	 

		/***********************************************************************************************************************/
		/**
		 * @return A list of group information containers.
		 */
		
	    public LinkedList<GroupChatData> getChatList(){
	    	SQLiteDatabase db = helper.getReadableDatabase();
			String query = "SELECT * FROM Groups" ;
			Cursor c = db.rawQuery(query, null);
			
			LinkedList<GroupChatData> el = new LinkedList<>();
			GroupChatData temp = new GroupChatData();
			
			 if(c.moveToFirst()){
		            do{	          
		               temp.group_id = c.getString(c.getColumnIndex(GroupChatHelper.Groups.groupId));
		               temp.group_name = c.getString(c.getColumnIndex(GroupChatHelper.Groups.groupName));
		               temp.encryption_type = EncryptionType.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Groups.encryptionType))];
		               temp.admin = c.getString(c.getColumnIndex(GroupChatHelper.Groups.adminId));		               		               
		               el.add(temp);
		            }while(c.moveToNext());
		        }
		        c.close();
		        db.close();
			return el;
	    }
	    
	    /***********************************************************************************************************************/
    /**
     * @return A list of group IDs for existing group chats.
     */

		@Override
		public LinkedList<String> getChatIdList() {
			// TODO Auto-generated method stub
			return null;
		}
/*	@Override
	public LinkedList<String> getChatIdList() {
		SQLiteDatabase db = helper.getReadableDatabase();
		String query = "SELECT * FROM Groups" ;
		Cursor c = db.rawQuery(query, null);
		
		LinkedList<String> el = new LinkedList<>();
		GroupChatData temp = new GroupChatData();
		
		 if(c.moveToFirst()){
	            do{	          
	               temp.group_id = c.getString(1);		               		               
	               el.add(temp);
	            }while(c.moveToNext());
	        }
	        c.close();
	        db.close();
		return el;
	}*/

	
    public LinkedList<GroupChatMember> getMembers(String groupId){
    	SQLiteDatabase db = helper.getReadableDatabase();
    	String query = "select * from members where members.group_id="+groupId + ")";
    	Cursor c=db.rawQuery(query, null);
    	
    	LinkedList<GroupChatMember> el=new LinkedList<>();
    	if(c.moveToFirst()){
            do{	          	               		               
               el.add(new GroupChatMember(c.getString(c.getColumnIndex(GroupChatHelper.Members.id)), c.getString(c.getColumnIndex(GroupChatHelper.Members.sipAddress)), Boolean.valueOf(c.getString(c.getColumnIndex(GroupChatHelper.Members.pending)))));
            }while(c.moveToNext());
        }
    	c.close();
    	db.close();
    	return el;
    }
    

	@Override
	public LinkedList<GroupChatMessage> getMessages(String id, int limit) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String query = "SELECT * FROM Messages WHERE Messages.member_id = (SELECT Members._id FROM Members WHERE Members.group_id = '"+id + "')" ;
		Cursor c = db.rawQuery(query, null);
		
		LinkedList<GroupChatMessage> el = new LinkedList<>();
		GroupChatMessage temp = new GroupChatMessage();
		
		SimpleDateFormat format = new SimpleDateFormat ("MMMM d, yyyy", Locale.ENGLISH);	
		Date d=null;
		int i=0;
		 if(c.moveToFirst()){
	            do{	          
	               temp.id = c.getInt(c.getColumnIndex(GroupChatHelper.Messages.id));
	               temp.message = c.getString(c.getColumnIndex(GroupChatHelper.Messages.messageText));
	               temp.sender = c.getString(c.getColumnIndex(GroupChatHelper.Messages.memberId));
	               temp.state = MessageState.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Messages.messageState))];
	               temp.direction = MessageDirection.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Messages.messageDirection))];
	               try {
					d=format.parse(c.getString(c.getColumnIndex(GroupChatHelper.Messages.timeSent)));
		               temp.time= d;
					} catch (ParseException e) {
						e.printStackTrace();
					}
	               el.add(temp);
	               i++;
	               if (i==limit)
	            	   break;
	            }while(c.moveToNext());
	        }
	        c.close();
	        db.close();
		return el;
	}

	@Override
	public int getUnreadMessageCount(String id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String query = "SELECT * FROM Messages WHERE Messages.member_id = (SELECT Members._id FROM Members WHERE Members.group_id = '"+id + "') and messages.message_state=0";
		Cursor c=db.rawQuery(query, null);
		c.moveToFirst();
		int count=0;
		if (c!=null)
			while (c.moveToNext())
				count++;
		return count;
	}
	
	@Override
	public void updateGroupName(String groupId, String name) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String query="update Groups set" + GroupChatHelper.Groups.groupName + "='"+name+"' where " + GroupChatHelper.Groups.groupId + "='"+groupId+"'";
		db.execSQL(query);
	}
	
	@Override
	public void setSecretKey(String id, String key) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String query = "Update Groups SET secret_Key="+ key +" Where group_id = '"+id+"'";
		db.execSQL(query);		
	}
	
	@Override
	public String getSecretKey(String id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String query = "SELECT secret_Key From Groups Where group_id = '" + id+"'";
		Cursor c=db.rawQuery(query, null);
		// error here:
		c.moveToFirst();
		String secretKey = c.getString(c.getColumnIndex(GroupChatHelper.Groups.secretKey));
		return secretKey;
	}
	
	@Override
	public void setEncryptionType(String id, EncryptionType type) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String query = "Update Groups SET encryption_type="+ type.ordinal() +" Where group_id = '"+id+"'";
		db.execSQL(query);		
	}
	
	@Override
	public EncryptionType getEncryptionType(String id){
		SQLiteDatabase db = helper.getReadableDatabase();
		String query = "SELECT encryption_type From Groups Where group_id = '" + id+"'";
		Cursor c=db.rawQuery(query, null);
		c.moveToFirst();
		EncryptionType encryption_type = EncryptionType.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Groups.encryptionType))];
		return encryption_type;
	}
	
	@Override
	public void updateMemberStatus(String id, GroupChatMember member) throws MemberDoesNotExistException {
		SQLiteDatabase db = helper.getWritableDatabase();
		String query="update Members set pending='"+member.pending+"' where sip_address='"+member.sip+"' and where group_id='"+id+"'";
		db.execSQL(query);
	}

	@Override
	public void updateAdmin(String id, GroupChatMember member) throws GroupDoesNotExistException {
		SQLiteDatabase db = helper.getWritableDatabase();
		String query = "Update Groups SET admin_id= (Select Members._id From Members Where Members.sip_address = '"+member.sip+"')"+" Where group_id = '"+id+"'";
		db.execSQL(query);		
	}

	@Override
	public void removeMember(String id, GroupChatMember member) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String query="delete from Members where sip_address='"+member.sip+"' and where group_id='"+id+"'";
		db.execSQL(query);
	}

	@Override
	public void deleteMessages(String id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String query="delete FROM Messages WHERE Messages.member_id = (SELECT Members._id FROM Members WHERE Members.group_id = '"+id + "')";
		db.execSQL(query);
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
    public void deleteChat(String groupIdToDelete) throws GroupDoesNotExistException {
        SQLiteDatabase db = helper.getWritableDatabase();
        
        //Delete from messages table
        String querySelect = "Select * From Messages WHERE Messages.member_id = (SELECT Members._id FROM Members WHERE Members.group_id = '"+groupIdToDelete+ "')" ;
        Cursor c = db.rawQuery(querySelect,null);
        if(c.moveToFirst()){
        	String queryDelete = "Delete From Messages WHERE Messages.member_id = (SELECT Members._id FROM Members WHERE Members.group_id = '"+groupIdToDelete+ "')" ;
        	db.execSQL(queryDelete);
        }
        else
        {
        	throw new GroupDoesNotExistException("Group could not be found in the database!");
        }
        
      //Delete from Members table
        String query = "Delete From Members WHERE Members.group_id = '"+groupIdToDelete+ "'" ;
        Cursor c = db.rawQuery(querySelect,null);
        if(c.moveToFirst()){
        	String queryDelete = "Delete From Messages WHERE Messages.member_id = (SELECT Members._id FROM Members WHERE Members.group_id = '"+groupIdToDelete+ "')" ;
        	db.execSQL(queryDelete);
        }
        else
        {
        	throw new GroupDoesNotExistException("Group could not be found in the database!");
        }
        
        
        
        
        
        
        
        
        
        //delete groupIdToDelete from Groups table
        if( db.delete(GroupChatHelper.Groups.tableName, GroupChatHelper.Groups.groupId + " =?",
        		new String[]{groupIdToDelete}) ==0) {
            
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
        private class Groups{
            private static final String tableName = "Groups";
            private static final String id = "_id";
            private static final String idType = " INTEGER PRIMARY KEY AUTOINCREMENT ";
            private static final String groupId = "group_id";
            private static final String groupIdType = " VARCHAR(255) ";
            private static final String groupName = "group_name";
            private static final String groupNametype = " VARCHAR(50) ";
            private static final String encryptionType = "encryption_type";
            private static final String encryptionTypeType = " INTEGER "; 
            private static final String adminId = "admin_id";
            private static final String adminIdType = " INTEGER ";
            private static final String secretKey = " secret_Key ";
            private static final String secretKeyType = " VARCHAR(50) ";
            
        }

        //Messages Table
        private class Messages{
            private static final String tableName = "Messages";
            private static final String id = "_id";
            private static final String idType = " INTEGER PRIMARY KEY AUTOINCREMENT ";
            private static final String messageText = "message_text";
            private static final String messageTextType = " TEXT ";
            private static final String memberId = "member_id";
            private static final String memberIdType = " INTEGER "; 
            private static final String messageState = "message_state";
            private static final String messageStateType = " INTEGER ";
            private static final String messageDirection = "message_direction";
            private static final String messageDirectionType = " INTEGER "; 
            private static final String timeSent = "time_sent";
            private static final String timeSentType = "DATETIME"; 
        }

        //Members Table
        private class Members{
            private static final String tableName = "Members";
            private static final String id = "_id";
            private static final String idType = " INTEGER PRIMARY KEY AUTOINCREMENT ";
            private static final String name = "name";
            private static final String nameType =" VARCHAR(50) ";
            private static final String sipAddress = "sip_address";
            private static final String sipAddressType = " VARCHAR(50) ";
            private static final String pending = "pending";
            private static final String pendingType = " BOOLEAN ";
            private static final String groupId = "group_id";
            private static final String groupIdType = " INTEGER ";
        }

        //Attachments Table
        private class Attachments{
            private static final String tableName = "Attachments";
            private static final String id = "_id";
            private static final String idType = " INTEGER PRIMARY KEY AUTOINCREMENT ";
            private static final String file = "file";
            private static final String fileType = " BLOB ";
            private static final String messageId = "message_id";
            private static final String messageIdType = " INTEGER";
        }


        private GroupChatHelper(Context context){
            super(context, GROUPCHAT_DB_NAME, null, GROUPCHAT_DB_VERSION);
        }

        //creating tables
        @Override
        public void onCreate(SQLiteDatabase db){
            String createGroupsTable = "CREATE TABLE " + Groups.tableName + "("
                    + Groups.id + " " + Groups.idType + ", " +  Groups.groupId + " " + Groups.groupIdType + ", "
                    + Groups.groupName + " " + Groups.groupNametype + ", " +  Groups.encryptionType 
                    + " " + Groups.encryptionTypeType + ", " +  Groups.adminId + " " + Groups.adminIdType + ", " +  Groups.secretKey + " " + Groups.secretKeyType + " )";
            
            String createMessagesTable = "CREATE TABLE " + Messages.tableName + "("
                    + Messages.id + " "+ Messages.idType +", " +  Messages.messageText + " " + Messages.messageTextType 
                    + "," + Messages.memberId + " " + Messages.memberIdType + "," +  Messages.messageState 
                    + " " + Messages.messageStateType + ", " + Messages.messageDirection + " " 
                    + Messages.messageDirectionType +", " + Messages.timeSent + " " + Messages.timeSentType + " )";
            
            String createMembersTable = "CREATE TABLE " + Members.tableName + "(" + Members.id + " " + Members.idType 
            		+ ", " +  Members.name + " " + Members.nameType + ", " + Members.sipAddress + " " + Members.sipAddressType 
            		+ ", " + Members.groupId + " " + Members.groupIdType + " )";
            
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
