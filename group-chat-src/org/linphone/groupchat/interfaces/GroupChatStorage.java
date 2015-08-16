package org.linphone.groupchat.interfaces;


import android.graphics.Bitmap;

import org.linphone.core.LinphoneChatMessage;

import java.lang.String;
import java.util.LinkedList;

/**
 *
 * @author David Breetzke
 *
 *	This class serves as an interface for interaction between the persistence tool and a
 *	{@link LinphoneGroupChatRoom} instance.
 */

public interface GroupChatStorage {

    public static GroupChatStorage.getInstance(){
    }

    public static void close(){
    }

    public static void updateMessageStatus(String to, String message, MessageState status){}

    //id is int here, but String elsewhere??
    public static void updateMessageStatus(String to, int id, MessageState status){}

    public static void saveTextMessage(String from, String message, MessageDirection direction,
                                       MessageState status, long time){}

    public static void saveImageMessage(String from, Bitmap image, String url, long time){}

    public static void saveVoiceRecording(String from, Bitstream voiceNote, long time){}

    public static LinkedList<LinphoneChatMessage> getMessage(String id){}

    public static LinkedList<String> getChatList();

    //maybe make return Boolean?
    public static void deleteChat(String id){}

    public static void markChatAsRead(String id){}

    //No GroupChatMember type exists
    public static GroupChatMember getMembers(String id){}

    public static void updateEncryptionType(String id, EncryptionType type)
}
