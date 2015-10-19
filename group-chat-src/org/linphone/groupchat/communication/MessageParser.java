package org.linphone.groupchat.communication;

import java.util.Iterator;

import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.communication.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.communication.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.communication.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.encryption.MessagingStrategy.EncryptionType;

/**
 * This class provides functionality for parsing string message objects and converting such 
 * objects to strings such that they may be passed in messages.
 * 
 * @author Paul Engelke
 */
public class MessageParser {
	
	private static final String SEPARATOR = ",";
	private static final String SEPARATOR2 = ";";

	private MessageParser(){}
	
	/* stringify functions */
	
	/**
	 * Converts the passed {@link InitialContactInfo} object to a formatted string.
	 * @param info The object to be converted.
	 * @return A formatted string.
	 */
	public static String stringifyInitialContactInfo(InitialContactInfo info){
		
		String message = "" + info.secret_key 
				+ SEPARATOR + info.public_key 
				+ SEPARATOR + info.group.group_id 
				+ SEPARATOR + info.group.group_name 
				+ SEPARATOR + info.group.admin 
				+ SEPARATOR + info.group.encryption_type.ordinal();
		
		Iterator<GroupChatMember> it = info.group.members.iterator();
		while (it.hasNext()) {
			GroupChatMember member = (GroupChatMember) it.next();
			message += SEPARATOR + member.name + SEPARATOR + member.sip + SEPARATOR + member.pending;
		}
		
		return message;
	}
	
	/**
	 * Converts {@link GroupChatData} into a formatted string.
	 * @param group The group object to be converted.
	 * @return The formated string.
	 */
	public static String stringifyGroupChatData(GroupChatData group){
		
		String message = "" + group.group_id
				+ SEPARATOR + group.group_name
				+ SEPARATOR + group.admin
				+ SEPARATOR + group.encryption_type.ordinal();
		
		Iterator<GroupChatMember> it = group.members.iterator();
		while (it.hasNext()){
			GroupChatMember m = it.next();
			message += SEPARATOR + m.name + SEPARATOR + m.sip + SEPARATOR + m.pending;
		}
		
		return message;
	}

	/**
	 * Converts the passed {@link MemberUpdateInfo} object to a formatted string.
	 * @param info The object to be converted.
	 * @return A formatted string.
	 */
	public static String stringifyMemberUpdateInfo(MemberUpdateInfo info){
		
		String message = "";
		
		Iterator<GroupChatMember> it = info.added.iterator();
		GroupChatMember member;
		
		// stringify added members
		if (it.hasNext()){
			member = it.next();
			message += member.name + SEPARATOR + member.sip + SEPARATOR + member.pending;
		}
		while (it.hasNext()) {
			member = (GroupChatMember) it.next();
			message += SEPARATOR + member.name + SEPARATOR + member.sip + SEPARATOR + member.pending;
		}
		message += SEPARATOR2;
		
		// stringify removed members
		it = info.removed.iterator();
		if (it.hasNext()){
			member = it.next();
			message += member.name + SEPARATOR + member.sip + SEPARATOR + member.pending;
		}
		while (it.hasNext()) {
			member = (GroupChatMember) it.next();
			message += SEPARATOR + member.name + SEPARATOR + member.sip + SEPARATOR + member.pending;
		}
		message += SEPARATOR2;
		
		// stringify confirmed members
		it = info.confirmed.iterator();
		if (it.hasNext()){
			member = it.next();
			message += member.name + SEPARATOR + member.sip + SEPARATOR + member.pending;
		}
		while (it.hasNext()) {
			member = (GroupChatMember) it.next();
			message += SEPARATOR + member.name + SEPARATOR + member.sip + SEPARATOR + member.pending;
		}
		
		return message;
	}

	/**
	 * Converts the passed {@link GroupChatMember} to a formatted string.
	 * @param member The object to be converted.
	 * @return A formatted string.
	 */
	public static String stringifyGroupChatMember(GroupChatMember member){
		
		return member.name + SEPARATOR + member.sip + SEPARATOR + member.pending;
	}
	
	public static String stringifyEncryptionType(EncryptionType type){
		
		return type.ordinal()+"";
	}
	
	/* parse functions */
	
	/**
	 * Parses a message containing an {@link InitialContactInfo} object.
	 * @param message The message to be parsed.
	 * @return A structured object containing the information.
	 */
	public static InitialContactInfo parseInitialContactInfo(String message){

		String[] parts = message.split(SEPARATOR);
		InitialContactInfo info = new InitialContactInfo();
		
		info.secret_key = parts[0];
		info.public_key = parts[1];
		
		info.group = new GroupChatData();
		info.group.group_id = parts[2];
		info.group.group_name = parts[3];
		info.group.admin = parts[4];
		info.group.encryption_type = EncryptionType.values()[Integer.parseInt(parts[5])];
		
		int i = 6;
		while (i < parts.length){
			
			info.group.members
				.add(new GroupChatMember(parts[i++], parts[i++], Boolean.parseBoolean(parts[i++])));
		}
		
		return info;
	}
	
	/**
	 * Parses a string containing a group chat 
	 * @param message The message to be parsed.
	 * @return A structured object containing the data.
	 */
	public static GroupChatData parseGroupChatData(String message){
		
		String[] parts = message.split(SEPARATOR);
		GroupChatData group = new GroupChatData();
		
		group.group_id = parts[0];
		group.group_name = parts[1];
		group.admin = parts[2];
		group.encryption_type = EncryptionType.values()[(Integer.parseInt(parts[3]))];
		
		int i = 4;
		while (i < parts.length){
			
			group.members.add(new GroupChatMember(parts[i++], parts[i++], Boolean.parseBoolean(parts[i++])));
		}
		
		return group;
	}
	
	/**
	 * Parses a message containing a {@link MemberUpdateInfo} object.
	 * @param message The message to be parsed.
	 * @return A structured object containing the data.
	 */
	public static MemberUpdateInfo parseMemberUpdateInfo(String message){
		
		MemberUpdateInfo info = new MemberUpdateInfo();
		
		String[] lists = message.split(SEPARATOR2), added, removed, confirmed, 
				empty = {""};
		
		try {added = lists[0].split(SEPARATOR);} catch (ArrayIndexOutOfBoundsException e){added = empty;}
		try {removed = lists[1].split(SEPARATOR);} catch (ArrayIndexOutOfBoundsException e){removed = empty;}
		try {confirmed = lists[2].split(SEPARATOR);} catch (ArrayIndexOutOfBoundsException e){confirmed = empty;}
		
		int i = 0;
		try {
			while (i < added.length){
				info.added.add(new GroupChatMember(added[i++], added[i++], Boolean.parseBoolean(added[i++])));
			}
		} catch (ArrayIndexOutOfBoundsException e){} // added was empty
		
		i = 0;
		try {
			while (i < removed.length){
				info.removed.add(new GroupChatMember(removed[i++], removed[i++], Boolean.parseBoolean(removed[i++])));
			}
		} catch (ArrayIndexOutOfBoundsException e){} // removed was empty
		
		
		i = 0;
		try {
			while (i < confirmed.length){
				info.confirmed.add(new GroupChatMember(confirmed[i++], confirmed[i++], Boolean.parseBoolean(confirmed[i++]))); 
			}
		} catch (ArrayIndexOutOfBoundsException e){} // confirmed was empty
		
		return info;
	}
	
	public static EncryptionType parseEncryptionType(String message){
		
		try {
			return EncryptionType.values()[Integer.parseInt(message)];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
			return EncryptionType.None;
		}
	}
	
	/**
	 * Parses a message containing a {@link GroupChatMember}.
	 * @param message The message to be parsed.
	 * @return A structured object containing data.
	 */
	public static GroupChatMember parseGroupChatMember(String message){
		
		String[] admin = message.split(SEPARATOR);
		
		return new GroupChatMember(admin[0], admin[1], Boolean.parseBoolean(admin[2]));
	}
}