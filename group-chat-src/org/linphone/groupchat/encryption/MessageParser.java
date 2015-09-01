package org.linphone.groupchat.encryption;

import org.linphone.groupchat.interfaces.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.interfaces.DataExchangeFormat.MemberUpdateInfo;

public class MessageParser {

	private MessageParser(){}
	
	/**
	 * Parses a message containing information about the initial contact communication session.
	 * @param message The message to be parsed.
	 * @return A structured object containing the information.
	 */
	public static InitialContactInfo parseInitialContactMessage(String message){

		// parse message, could contain a public key, secret key, contact details
		
		return null;
	}
	
	/**
	 * Parses a message containing member additions and removals.
	 * @param message The message to be parsed.
	 * @return A structured object containing the data.
	 */
	public static MemberUpdateInfo parseMemberUpdateMessage(String message){
		
		return null;
	}
}
