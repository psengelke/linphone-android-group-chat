package org.linphone.groupchat.test;

import java.util.Iterator;
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

		assertEquals(
				"Adam,adam@linphone.org", 
				MessageParser.stringifyAdminChange(new GroupChatMember("Adam", "adam@linphone.org"))
		);
	}

	public void testParseInitialContactMessage() {

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
		
		InitialContactInfo parsed_info = 
				MessageParser.parseInitialContactMessage(MessageParser.stringifyInitialContactMessage(info));
		
		assertEquals(parsed_info.secret_key, info.secret_key);
		assertEquals(parsed_info.public_key, info.public_key);
		assertEquals(parsed_info.group.group_id, info.group.group_id);
		assertEquals(parsed_info.group.group_name, info.group.group_name);
		assertEquals(parsed_info.group.encryption_type, info.group.encryption_type);
		
		assertEquals(parsed_info.group.members.size(), info.group.members.size());
		Iterator<GroupChatMember> it2 = parsed_info.group.members.iterator();
		Iterator<GroupChatMember> it1 = info.group.members.iterator();
		while (it2.hasNext() && it1.hasNext()) {
			GroupChatMember member1 = (GroupChatMember) it1.next();
			GroupChatMember member2 = (GroupChatMember) it2.next();
			assertEquals(member2.name, member1.name);
			assertEquals(member2.sip, member1.sip);
		}
	}

	public void testParseMemberUpdateMessage() {
		fail("Not yet implemented");
	}

	public void testParseAdminChange() {

		GroupChatMember orig = new GroupChatMember("Adam", "adam@linphone.org");
		GroupChatMember parsed = MessageParser.parseAdminChange(MessageParser.stringifyAdminChange(orig));
		assertEquals(orig.name, parsed.name);
		assertEquals(orig.sip, parsed.sip);
	}

}