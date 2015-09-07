package org.linphone.groupchat.encryption;

import java.util.Iterator;
import java.util.LinkedList;

import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.interfaces.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;

/**
 * 
 * @author Paul Engelke
 * 
 * This class provides functionality for parsing string message objects and converting such 
 * objects to strings such that they may be passed in messages.
 *
 */
public class MessageParser {
	
	private static final String SEPARATOR = ",";

	private MessageParser(){}
	
	/* stringify functions */
	
	/**
	 * Converts the passed object to a formatted string.
	 * @param info The object to be converted.
	 * @return A formatted string.
	 */
	public static String stringifyInitialContactMessage(InitialContactInfo info){
		
		String message = "" + info.secret_key 
				+ SEPARATOR + info.public_key 
				+ SEPARATOR + info.group.group_id 
				+ SEPARATOR + info.group.group_name 
				+ SEPARATOR + info.group.admin 
				+ SEPARATOR + info.group.encryption_type.ordinal();
		
		Iterator<GroupChatMember> it = info.group.members.iterator();
		while (it.hasNext()) {
			GroupChatMember member = (GroupChatMember) it.next();
			message += SEPARATOR + member.name + SEPARATOR + member.sip;
		}
		
		return message;
	}

	/**
	 * Converts the passed object to a formatted string.
	 * @param info The object to be converted.
	 * @return A formatted string.
	 */
	public static String stringifyMemberUpdateMessage(MemberUpdateInfo info){
		
		return null;
	}

	/**
	 * Converts the passed object to a formatted string.
	 * @param info The object to be converted.
	 * @return A formatted string.
	 */
	public static String stringifyAdminChange(String message){
		
		return null;
	}
	
	/* parse functions */
	
	/**
	 * Parses a message containing information about the initial contact communication session.
	 * @param message The message to be parsed.
	 * @return A structured object containing the information.
	 */
	public static InitialContactInfo parseInitialContactMessage(String message){

		String[] parts = message.split(SEPARATOR);
		InitialContactInfo info = new InitialContactInfo();
		
		info.secret_key = Long.parseLong(parts[0]);
		info.public_key = Long.parseLong(parts[1]);
		
		info.group = new GroupChatData();
		info.group.group_id = parts[2];
		info.group.group_name = parts[3];
		info.group.admin = parts[4];
		info.group.encryption_type = EncryptionType.values()[Integer.parseInt(parts[5])];
		
		info.group.members = new LinkedList<>();
		int i = 6;
		while (i < parts.length){
			
			GroupChatMember member = new GroupChatMember();
			member.name = parts[i++];
			member.sip = parts[i++];
		}
		
		return info;
	}
	
	/**
	 * Parses a message containing member additions and removals.
	 * @param message The message to be parsed.
	 * @return A structured object containing the data.
	 */
	public static MemberUpdateInfo parseMemberUpdateMessage(String message){
		
		return null;
	}
	
	public static GroupChatMember parseAdminChange(String message){
		
		return null;
	}
}
