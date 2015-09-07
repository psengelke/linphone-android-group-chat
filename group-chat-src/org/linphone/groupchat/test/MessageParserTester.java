package org.linphone.groupchat.test;

import java.util.LinkedList;

import org.linphone.groupchat.encryption.MessageParser;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;

import junit.framework.TestCase;

public class MessageParserTester extends TestCase {

	public MessageParserTester(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testStringifyInitialContactMessage() {
		
		InitialContactInfo info = new InitialContactInfo();
		info.secret_key = 98232;
		info.public_key = 89624;
		info.group = new GroupChatData();
		info.group.group_id = "sip@linphone.org:GroupName";
		info.group.group_name = "Group Name";
		info.group.admin = "admin@linphone.org";
		info.group.encryption_type = EncryptionType.None;
		info.group.members = new LinkedList<>();
		info.group.members.add(new GroupChatMember("John", "john@linphone.org"));
		info.group.members.add(new GroupChatMember("Bob", "bob@linphone.org"));
		info.group.members.add(new GroupChatMember("Jess", "Jess@linphone.org"));
		
		assertEquals(
				"98232,"
				+ "89624,"
				+ "sip@linphone.org:GroupName,"
				+ "Group Name,"
				+ "admin@linphone.org,"
				+ "0,"
				+ "John,john@linphone.org,"
				+ "Bob,"
				+ "bob@linphone.org,"
				+ "Jess,"
				+ "Jess@linphone.org", 
				MessageParser.stringifyInitialContactMessage(info)
		);
		
		//fail("Not yet implemented");
	}

	public void testStringifyMemberUpdateMessage() {
		fail("Not yet implemented");
	}

	public void testStringifyAdminChange() {
		fail("Not yet implemented");
	}

	public void testParseInitialContactMessage() {
		fail("Not yet implemented");
	}

	public void testParseMemberUpdateMessage() {
		fail("Not yet implemented");
	}

	public void testParseAdminChange() {
		fail("Not yet implemented");
	}

}
