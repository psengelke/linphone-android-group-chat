package org.linphone.groupchat.test;

import java.util.Iterator;
import java.util.LinkedList;

import org.linphone.groupchat.encryption.MessageParser;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatData;
import org.linphone.groupchat.interfaces.DataExchangeFormat.GroupChatMember;
import org.linphone.groupchat.interfaces.DataExchangeFormat.InitialContactInfo;
import org.linphone.groupchat.interfaces.DataExchangeFormat.MemberUpdateInfo;
import org.linphone.groupchat.interfaces.EncryptionHandler.EncryptionType;

import junit.framework.TestCase;

/**
 * 
 * Test class for {@link MessageParser}
 * 
 * @author Paul Engelke
 *
 */
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
		info.group.members.add(new GroupChatMember("John", "john@linphone.org", true));
		info.group.members.add(new GroupChatMember("Bob", "bob@linphone.org", false));
		info.group.members.add(new GroupChatMember("Jess", "Jess@linphone.org", false));
		
		assertEquals(
				"98232,"
				+ "89624,"
				+ "sip@linphone.org:GroupName,"
				+ "Group Name,"
				+ "admin@linphone.org,"
				+ "0,"
				+ "John,john@linphone.org,true,"
				+ "Bob,bob@linphone.org,false,"
				+ "Jess,Jess@linphone.org,false", 
				MessageParser.stringifyInitialContactInfo(info)
		);
	}

	public void testStringifyMemberUpdateMessage() {

		MemberUpdateInfo info = new MemberUpdateInfo();
		info.added = new LinkedList<>();
		info.added.add(new GroupChatMember("Steve", "steve@linphone.org", false));
		info.added.add(new GroupChatMember("Bob", "bob@linphone.org", false));
		info.removed = new LinkedList<>();
		info.removed.add(new GroupChatMember("Ginger", "souless@linphone.org", false));
		info.removed.add(new GroupChatMember("Scott", "gibson@linphone.org", false));
		info.confirmed.add(new GroupChatMember("Fred", "freddy@linphone.org", false));
		info.confirmed.add(new GroupChatMember("Fred", "freddy@linphone.org", false));
		
		String test = MessageParser.stringifyMemberUpdateInfo(info);
		assertEquals(
				"Steve,steve@linphone.org,false,"
				+ "Bob,bob@linphone.org,false;"
				+ "Ginger,souless@linphone.org,false,"
				+ "Scott,gibson@linphone.org,false;"
				+ "Fred,freddy@linphone.org,false,"
				+ "Fred,freddy@linphone.org,false",
				test
		);
	}

	public void testStringifyAdminChange() {

		assertEquals(
				"Adam,adam@linphone.org,false", 
				MessageParser.stringifyGroupChatMember(new GroupChatMember("Adam", "adam@linphone.org", false))
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
		info.group.members.add(new GroupChatMember("John", "john@linphone.org", false));
		info.group.members.add(new GroupChatMember("Bob", "bob@linphone.org", false));
		info.group.members.add(new GroupChatMember("Jess", "Jess@linphone.org", false));
		
		InitialContactInfo parsed_info = 
				MessageParser.parseInitialContactInfo(MessageParser.stringifyInitialContactInfo(info));
		
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
			assertEquals(member1.name, member2.name);
			assertEquals(member1.sip, member2.sip);
			assertEquals(member1.pending, member2.pending);
		}
	}

	public void testParseMemberUpdateMessage() {

		MemberUpdateInfo info = new MemberUpdateInfo();
		info.added = new LinkedList<>();
		info.added.add(new GroupChatMember("Steve", "steve@linphone.org", false));
		info.added.add(new GroupChatMember("Bob", "bob@linphone.org", false));
		info.removed = new LinkedList<>();
		info.removed.add(new GroupChatMember("Ginger", "souless@linphone.org", false));
		info.removed.add(new GroupChatMember("Scott", "gibson@linphone.org", false));
		info.confirmed.add(new GroupChatMember("Fred", "freddy@linphone.org", false));
		info.confirmed.add(new GroupChatMember("Fred", "freddy@linphone.org", false));
		
		MemberUpdateInfo parsed = 
				MessageParser.parseMemberUpdateInfo(MessageParser.stringifyMemberUpdateInfo(info));
		
		assertEquals(info.added.size(), parsed.added.size());
		assertEquals(info.removed.size(), info.removed.size());
		
		Iterator<GroupChatMember> it1 = parsed.added.iterator();
		Iterator<GroupChatMember> it2 = parsed.added.iterator();
		while (it1.hasNext()){
			
			GroupChatMember a = it1.next();
			GroupChatMember e = it2.next();
			assertEquals(e.name, a.name);
			assertEquals(e.sip, a.sip);
			assertEquals(e.pending, a.pending);
		}
		
		it1 = parsed.removed.iterator();
		it2 = parsed.removed.iterator();
		while (it1.hasNext()){
			
			GroupChatMember a = it1.next();
			GroupChatMember e = it2.next();
			assertEquals(e.name, a.name);
			assertEquals(e.sip, a.sip);
			assertEquals(e.pending, a.pending);
		}
		
		it1 = parsed.confirmed.iterator();
		it2 = parsed.confirmed.iterator();
		while (it1.hasNext()){
			
			GroupChatMember a = it1.next();
			GroupChatMember e = it2.next();
			assertEquals(e.name, a.name);
			assertEquals(e.sip, a.sip);
			assertEquals(e.pending, a.pending);
		}
	}

	public void testParseAdminChange() {

		GroupChatMember orig = new GroupChatMember("Adam", "adam@linphone.org", false);
		GroupChatMember parsed = MessageParser.parseGroupChatMember(MessageParser.stringifyGroupChatMember(orig));
		assertEquals(orig.name, parsed.name);
		assertEquals(orig.sip, parsed.sip);
		assertEquals(orig.pending, parsed.pending);
	}

}
