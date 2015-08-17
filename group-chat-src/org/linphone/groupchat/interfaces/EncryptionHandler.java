package org.linphone.groupchat.interfaces;

import java.lang.String;

/**
 *
 * @author David Breetzke
 *
 *	This class serves as an interface for handling encryption and is used by a
 *	{@link LinphoneGroupChatRoom} instance.
 */

public interface EncryptionHandler {

    public static enum EncryptionType{
        None, AES256
    }

    public static encrpyt(String message, long key){

    }
    public static decrypt(String message){

    }

    public static long getPublicKey(){

    }

    public static EncryptionType getEncryptionType(){

    }


}
