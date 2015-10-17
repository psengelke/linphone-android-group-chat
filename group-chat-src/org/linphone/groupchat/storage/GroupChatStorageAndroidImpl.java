package org.linphone.groupchat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.linphone.LinphoneService;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage.MessageDirection;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMessage.MessageState;
import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;
import org.linphone.groupchat.exception.GroupChatExistsException;
import org.linphone.groupchat.exception.GroupDoesNotExistException;
import org.linphone.groupchat.exception.MemberDoesNotExistException;
import org.linphone.groupchat.exception.MemberExistsException;

import java.lang.Override;
import java.lang.String;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class uses {@link org.linphone.groupchat.storage.interfaces.GroupChatStorage} instance.
 *
 * @author David Breetzke
 * @author Jessica Lessev
 */

class GroupChatStorageAndroidImpl implements GroupChatStorage {

	private GroupChatHelper helper;
	
    private GroupChatStorageAndroidImpl(Context c){
    	
    	helper = new GroupChatHelper(c);
    }

    public void close(){}
    
    @Override
	public void createGroupChat(GroupChatData data) throws GroupChatExistsException {

		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (groupExists(data.group_id)) throw new GroupChatExistsException("Group already exists with the same name.");

		// create group
		ContentValues values = new ContentValues();
		values.put(GroupChatHelper.Groups.groupId, data.group_id);
		values.put(GroupChatHelper.Groups.groupName, data.group_name);
		values.put(GroupChatHelper.Groups.adminId, data.admin);
		values.put(GroupChatHelper.Groups.encryptionType, data.encryption_type.ordinal());
		values.put(GroupChatHelper.Groups.secretKey,"");
		db.insert(GroupChatHelper.Groups.tableName, null, values);
		db.close();

		// add members
		Iterator<GroupChatMember> it = data.members.iterator();
		while (it.hasNext()){
			try {
				addMember(data.group_id, it.next());
			} catch (GroupDoesNotExistException | MemberExistsException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addMember(String id, GroupChatMember member) 
			throws GroupDoesNotExistException,  MemberExistsException {

		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		if (MemberExists(member.sip , id)) throw new MemberExistsException();

		ContentValues values = new ContentValues();
		
		values.put(GroupChatHelper.Members.name, member.name);
		values.put(GroupChatHelper.Members.sipAddress, member.sip);
		values.put(GroupChatHelper.Members.groupId, id);
		
		db.insert(GroupChatHelper.Members.tableName, null, values);
		db.close();
	}

	@Override
	public void markChatAsRead(String id) throws GroupDoesNotExistException {

		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		String queryDelete = "UPDATE " + GroupChatHelper.Messages.tableName + " SET " 
				+ GroupChatHelper.Messages.messageState
				+ " = 1 WHERE " + GroupChatHelper.Messages.groupId + " = '" + id + "'";
		db.execSQL(queryDelete);
	}

	@Override
	public void saveTextMessage(String id, GroupChatMessage message) throws GroupDoesNotExistException {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");
		
		String queryMember = "Select * from " +  GroupChatHelper.Members.tableName + " where " + GroupChatHelper.Members.sipAddress
				+ " = '" + message.sender + "' and " + GroupChatHelper.Members.groupId + "='" + id + "'";
		Cursor c1 = db.rawQuery(queryMember, null);
		// Retrieve memberId for message.sender which is a sipAddress
		int memberId = 0;
		if (c1.moveToFirst())
			memberId = c1.getInt(c1.getColumnIndex(GroupChatHelper.Members.id));
//			else
//				throw new MemberDoesNotExistException();
		Log.e("memberId retrieved in saveTextMessage", "" + memberId);
		ContentValues values = new ContentValues();

		values.put(GroupChatHelper.Messages.messageText, message.message);
		values.put(GroupChatHelper.Messages.memberId, memberId);
		values.put(GroupChatHelper.Messages.groupId, id);
		values.put(GroupChatHelper.Messages.messageState, message.state.ordinal());
		values.put(GroupChatHelper.Messages.messageDirection, message.direction.ordinal());
		values.put(GroupChatHelper.Messages.timeSent, message.time.getTime());
		
		db.insert(GroupChatHelper.Messages.tableName, null, values);
		db.close();
	}
	
	@Override
	public void saveImageMessage(String id, GroupChatMessage message) throws GroupDoesNotExistException {
		
		// TODO Auto-generated method stub
	}

	@Override
	public void saveVoiceRecording(String id, GroupChatMessage message) throws GroupDoesNotExistException {
		
		// TODO Auto-generated method stub
	}

	@Override
	public LinkedList<GroupChatMessage> getMessages(String id) throws GroupDoesNotExistException {

		SQLiteDatabase db = helper.getReadableDatabase();
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");
		
		String query = "SELECT * FROM "+ GroupChatHelper.Messages.tableName + " msg," 
				+ GroupChatHelper.Members.tableName + " mem WHERE " + "msg."+ GroupChatHelper.Messages.memberId 
				+ "=mem." + GroupChatHelper.Members.id+" AND mem."+GroupChatHelper.Members.groupId + "=" + "'"
				+ id + "'" ;
		Cursor c = db.rawQuery(query, null);
		
		LinkedList<GroupChatMessage> el = new LinkedList<>();
		

		//SimpleDateFormat format = new SimpleDateFormat ("d MMM HH:mm", Locale.ENGLISH);	
		while (c.moveToNext()){
			GroupChatMessage temp = new GroupChatMessage();
			temp.id = c.getInt(c.getColumnIndex(GroupChatHelper.Messages.id));
			temp.message = c.getString(c.getColumnIndex(GroupChatHelper.Messages.messageText));
			temp.sender = c.getString(c.getColumnIndex(GroupChatHelper.Members.sipAddress));
			temp.state = MessageState.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Messages.messageState))];
			temp.direction = MessageDirection.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Messages.messageDirection))];
			temp.time= new Date(); //TODO mock date, need to convert properly using simple date format
			
			el.add(temp);
		}
		
		c.close();
		db.close();
		
		return el;			
		
	}
	
	@Override
	public LinkedList<GroupChatMessage> getMessages(String id, int limit) throws GroupDoesNotExistException {
		
		SQLiteDatabase db = helper.getReadableDatabase();
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");
		
		String query = "SELECT * FROM "+ GroupChatHelper.Messages.tableName + " msg," 
				+ GroupChatHelper.Members.tableName + " mem WHERE " + "msg."+ GroupChatHelper.Messages.memberId 
				+ "=mem." + GroupChatHelper.Members.id+" AND mem."+GroupChatHelper.Members.groupId + "=" + "'"
				+ id + "'" ;
		Cursor c = db.rawQuery(query, null);
		
		LinkedList<GroupChatMessage> el = new LinkedList<>();

		//SimpleDateFormat format = new SimpleDateFormat ("MMMM d, yyyy", Locale.ENGLISH);	
		int i=0;
		while (c.moveToNext() && i < limit){
			
			GroupChatMessage temp = new GroupChatMessage();
			temp.id = c.getInt(c.getColumnIndex(GroupChatHelper.Messages.id));
			temp.message = c.getString(c.getColumnIndex(GroupChatHelper.Messages.messageText));
			temp.sender = c.getString(c.getColumnIndex(GroupChatHelper.Members.sipAddress));
			temp.state = MessageState.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Messages.messageState))];
			temp.direction = MessageDirection.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Messages.messageDirection))];
			temp.time= new Date(); //TODO mock date, need to convert properly using simple date format
			
			el.add(temp);
			i++;
		}
		
		c.close();
		db.close();
		
		return el;
	}

	@Override
	public LinkedList<GroupChatData> getChatList(){
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String query = "SELECT * FROM " + GroupChatHelper.Groups.tableName;
		Cursor c = db.rawQuery(query, null);

		LinkedList<GroupChatData> el = new LinkedList<>();
		while (c.moveToNext()) {
			
			GroupChatData temp = new GroupChatData();
			temp.group_id = c.getString(c.getColumnIndex(GroupChatHelper.Groups.groupId));
			temp.group_name = c.getString(c.getColumnIndex(GroupChatHelper.Groups.groupName));
			temp.encryption_type = EncryptionType.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Groups.encryptionType))];
			temp.admin = c.getString(c.getColumnIndex(GroupChatHelper.Groups.adminId));	
			try {
				temp.members = getMembers(temp.group_id);
			} catch (GroupDoesNotExistException e) {
				e.printStackTrace(); // this should not happen as each id retrieved from the database is valid.
			}
			
			el.add(temp);
		}
		
		c.close();
		db.close();
		
		return el;
	}

	@Override
	public LinkedList<String> getChatIdList() {
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String query = "SELECT "+GroupChatHelper.Groups.groupId+" FROM Groups";
		Cursor c = db.rawQuery(query, null);

		LinkedList<String> el = new LinkedList<>();
		while (c.moveToNext()){
			el.add(c.getString(c.getColumnIndex(GroupChatHelper.Groups.groupId)));		
		}
		 
        c.close();
        db.close();
        
		return el;
	}

	public LinkedList<GroupChatMember> getMembers(String groupId) throws GroupDoesNotExistException {
		
		SQLiteDatabase db = helper.getReadableDatabase();
		if (!groupExists(groupId)) throw new GroupDoesNotExistException("Group does not exist.");
		
		String query = "SELECT * FROM " + GroupChatHelper.Members.tableName + " WHERE Members.group_id='"+groupId + "'";
		Cursor c=db.rawQuery(query, null);

		LinkedList<GroupChatMember> el=new LinkedList<>();
		while (c.moveToNext()){
			
			el.add(new GroupChatMember(
					c.getString(c.getColumnIndex(GroupChatHelper.Members.name)), 
					c.getString(c.getColumnIndex(GroupChatHelper.Members.sipAddress)), 
					Boolean.valueOf(c.getString(c.getColumnIndex(GroupChatHelper.Members.pending)))
			));
		}

		c.close();
		db.close();
		
		return el;
	}
	
	@Override
	public int getUnreadMessageCount(String id) throws GroupDoesNotExistException{
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		String query = "SELECT * FROM " + GroupChatHelper.Messages.tableName 
				+" WHERE "+ GroupChatHelper.Messages.groupId
				+ "='" + id + "' AND Messages.message_state=0";
		Cursor c=db.rawQuery(query, null);
			
		return c.getCount();
	}

	@Override
	public void setGroupName(String groupId, String name)  throws GroupDoesNotExistException {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (!groupExists(groupId)) throw new GroupDoesNotExistException("Group does not exist.");

		String query="UPDATE " + GroupChatHelper.Groups.tableName + " SET " 
				+ GroupChatHelper.Groups.groupName + "='"+name+"' WHERE "
				+ GroupChatHelper.Groups.groupId + "='"+groupId+"'";
		db.execSQL(query);
	}

	@Override
	public void setSecretKey(String id, String key) throws GroupDoesNotExistException {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		String query = "UPDATE " + GroupChatHelper.Groups.tableName + " SET " + GroupChatHelper.Groups.secretKey + "="+ key +" WHERE " + GroupChatHelper.Groups.groupId + " = '"+id+"'";
		db.execSQL(query);		
	}

	@Override
	public String getSecretKey(String id) throws GroupDoesNotExistException {
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		String query = "SELECT * From " + GroupChatHelper.Groups.tableName + " WHERE " 
				+ GroupChatHelper.Groups.groupId + " = '" + id+"'";
		Cursor c=db.rawQuery(query, null);
		String secretKey = "";
		if(c.moveToNext()){
			secretKey = c.getString(c.getColumnIndex(GroupChatHelper.Groups.secretKey));
		}
		
		c.close();
		db.close();
		
		return secretKey;
	}

	@Override
	public void setEncryptionType(String id, EncryptionType type) throws GroupDoesNotExistException {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		String query = "UPDATE " + GroupChatHelper.Groups.tableName + " SET " + GroupChatHelper.Groups.encryptionType
				+ "="+ type.ordinal() +" WHERE " + GroupChatHelper.Groups.groupId + " = '"+id+"'";
		db.execSQL(query);
	}

	@Override
	public EncryptionType getEncryptionType(String id) throws GroupDoesNotExistException {
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		String query = "SELECT " + GroupChatHelper.Groups.encryptionType + " From " + GroupChatHelper.Groups.tableName
				+ " Where " + GroupChatHelper.Groups.groupId + " = '" + id+"'";
		
		Cursor c=db.rawQuery(query, null);
		if (c.moveToNext()){
			
			EncryptionType encryption_type = 
					EncryptionType.values()[c.getInt(c.getColumnIndex(GroupChatHelper.Groups.encryptionType))];
			
			c.close();
			db.close();
			
			return encryption_type;
		}
		
		return EncryptionType.None; // should never happen, but safer than returning null
	}

	@Override
	public void setMemberStatus(String id, GroupChatMember member)  
			throws GroupDoesNotExistException, MemberDoesNotExistException {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		String query="UPDATE " + GroupChatHelper.Members.tableName  
				+ " SET " + GroupChatHelper.Members.pending + "='"+member.pending
				+"' WHERE " + GroupChatHelper.Members.sipAddress + "='"+member.sip+"' AND " 
				+ GroupChatHelper.Members.groupId + "='"+id+"'";
		db.execSQL(query);
	}

	@Override
	public void setAdmin(String id, GroupChatMember member) throws GroupDoesNotExistException, MemberDoesNotExistException {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		String query = "UPDATE " + GroupChatHelper.Groups.tableName + " SET " + GroupChatHelper.Groups.adminId
				+ "= (SELECT Members._id FROM " + GroupChatHelper.Members.tableName +" WHERE Members.sip_address = '"+member.sip+"')"
				+" WHERE " + GroupChatHelper.Members.groupId + " = '"+id+"'";
		db.execSQL(query);
	}

	@Override
	public void removeMember(String id, GroupChatMember member) 
			throws GroupDoesNotExistException, MemberDoesNotExistException {
		
		SQLiteDatabase db = helper.getWritableDatabase();
	
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		String query="DELETE FROM " + GroupChatHelper.Members.tableName + " WHERE " + GroupChatHelper.Members.sipAddress
				+ "='"+member.sip+"' AND " + GroupChatHelper.Members.groupId + "='"+id+"'";
		
		db.execSQL(query);
	}

	@Override
	public void deleteMessages(String id) throws GroupDoesNotExistException{
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("Group does not exist.");

		String query="DELETE FROM " + GroupChatHelper.Messages.tableName + " WHERE " + GroupChatHelper.Messages.groupId 
				+" = '" + id + "'";
		db.execSQL(query);
	}

	@Override
	public void deleteChat(String id) throws GroupDoesNotExistException {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if (!groupExists(id)) throw new GroupDoesNotExistException("The group does not exist or has been deleted!");

		// delete all messages
		deleteMessages(id);

		//delete all members
		String query = "DELETE FROM " + GroupChatHelper.Members.tableName + " WHERE Members.group_id = '"+id+ "'" ;
		db.execSQL(query);

		//delete group info
		String queryDeleteGroup = "DELETE FROM " + GroupChatHelper.Groups.tableName + " WHERE Groups.group_id = '"+id+ "'" ;
		db.execSQL(queryDeleteGroup); 
	}
	
	/**
	 * Queries the database to test if a member exists for a group or not.
	 * @param memberId The member being validated.
	 * @param GroupId The group for which the member's existence is being checked.
	 * @return True if the member exists, else false.
	 */
	private boolean MemberExists(String memberId, String GroupId) 
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String queryCheck = "SELECT * FROM " + GroupChatHelper.Members.tableName 
				+ " WHERE "+ GroupChatHelper.Members.sipAddress +" ='"+ memberId + "' AND " 
				+ GroupChatHelper.Members.groupId +"='"+ GroupId + "'";
		Cursor cCheck = db.rawQuery(queryCheck, null);
		if (cCheck.getCount()>0) return true;
		
		return false;
	}
	
	/**
	 * Checks whether or not a group exists.
	 * @param id The group's id.
	 * @return True if the group exists,  else false.
	 */
	private boolean groupExists(String id) {
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String query = "SELECT * FROM " + GroupChatHelper.Groups.tableName +" WHERE "
				+ GroupChatHelper.Groups.groupId +" ='"+ id + "'" ;
		Cursor c = db.rawQuery(query, null);
		
		if (c.moveToNext()) return true;

		return false;
	}

	/**
	 * http://androidhive.info/2011/11/android-sqlite-database-tutorial is helpful.
	 * {@link SQLiteOpenHelper} for {@link GroupChatStorage}.
	 * 
	 * @author David Breetzke
	 */
	private class GroupChatHelper extends SQLiteOpenHelper {
		
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
			private static final String secretKey = "secret_Key";
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
			private static final String groupId = "group_id";
			private static final String groupIdType = " VARCHAR(255) ";
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

		@Override
		public void onCreate(SQLiteDatabase db){
			String createGroupsTable = "CREATE TABLE " + Groups.tableName + "("
					+ Groups.id + " " + Groups.idType + ", " +  Groups.groupId + " " + Groups.groupIdType + ", "
					+ Groups.groupName + " " + Groups.groupNametype + ", " +  Groups.encryptionType 
					+ " " + Groups.encryptionTypeType + ", " +  Groups.adminId + " " + Groups.adminIdType + ", " 
					+  Groups.secretKey + " " + Groups.secretKeyType + " )";

			String createMessagesTable = "CREATE TABLE " + Messages.tableName + "("
					+ Messages.id + " "+ Messages.idType +", " +  Messages.messageText + " " + Messages.messageTextType 
					+ "," + Messages.memberId + " " + Messages.memberIdType + " ," + Messages.groupId + " "
					+ Messages.groupIdType + ", " +  Messages.messageState 
					+ " " + Messages.messageStateType + ", " + Messages.messageDirection + " " 
					+ Messages.messageDirectionType +", " + Messages.timeSent + " " + Messages.timeSentType + " )";

			String createMembersTable = "CREATE TABLE " + Members.tableName + "(" + Members.id + " " + Members.idType 
					+ ", " +  Members.name + " " + Members.nameType + ", " + Members.sipAddress + " " + Members.sipAddressType 
					+ ", " + Members.groupId + " " + Members.groupIdType + ", " + Members.pending + " " + Members.pendingType + " )";

			String createAttachmentsTable = "CREATE TABLE " + Attachments.tableName + "("
					+ Attachments.id + " " + Attachments.idType + ", " +  Attachments.file + " " + Attachments.fileType 
					+ ", " + Attachments.messageId  + Attachments.messageIdType  + " )";


			db.execSQL(createGroupsTable);
			db.execSQL(createMessagesTable);
			db.execSQL(createMembersTable);
			db.execSQL(createAttachmentsTable);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			
			db.execSQL("DROP TABLE IF EXISTS " + Groups.tableName + ", " + Messages.tableName + ", "
					+ Members.tableName + ", " + Attachments.tableName );
			
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

        private static final GroupChatStorageAndroidImpl INSTANCE = 
        		new GroupChatStorageAndroidImpl(LinphoneService.instance().getApplicationContext());
    }
}
