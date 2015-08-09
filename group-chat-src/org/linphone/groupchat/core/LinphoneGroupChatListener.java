package org.linphone.groupchat.core;

import java.nio.ByteBuffer;

import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.EcCalibratorStatus;
import org.linphone.core.LinphoneCore.GlobalState;
import org.linphone.core.LinphoneCore.LogCollectionUploadState;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCore.RemoteProvisioningState;
import org.linphone.core.LinphoneCoreListener;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneInfoMessage;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PublishState;
import org.linphone.core.SubscriptionState;

/**
 * 
 * @author Paul Engelke
 *
 *	This class serves as an intercepter for group chat messages and defers those messages 
 *	to the {@link LinphoneGroupChatManager} instance. It also serves as a wrapper class for 
 *	the {@link LinphoneManager} instance, to which responsibility for it's purpose as a 
 *	{@link LinphoneCoreListener} is delegated.
 */
public class LinphoneGroupChatListener  implements LinphoneCoreListener {
	
	private LinphoneCoreListener linphone_manager;
	private LinphoneGroupChatManager chat_manager;
	
	private LinphoneGroupChatListener() {
		
	}
	
	/**
	 * Getter method for the singleton.
	 * @return The {@link LinphoneCoreListener} singleton instance.
	 */
	public static LinphoneGroupChatListener getInstance(){
		
		return InstanceHolder.INSTANCE;
	}
	
	/**
	 * This class provides a thread-safe lazy initialisation of the singleton.
	 */
	private static class InstanceHolder {
		
		private static final LinphoneGroupChatListener INSTANCE = new LinphoneGroupChatListener();
	}

	/* LinphoneCoreListener implementation*/
	
	@Override
	public void authInfoRequested(LinphoneCore lc, String realm, String username, String Domain) {

	}

	@Override
	public void callStatsUpdated(LinphoneCore lc, LinphoneCall call, LinphoneCallStats stats) {
	
	}

	@Override
	public void newSubscriptionRequest(LinphoneCore lc, LinphoneFriend lf, String url) {

	}

	@Override
	public void notifyPresenceReceived(LinphoneCore lc, LinphoneFriend lf) {

	}

	@Override
	public void textReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneAddress from, String message) {

	}

	@Override
	public void dtmfReceived(LinphoneCore lc, LinphoneCall call, int dtmf) {

	}

	@Override
	public void notifyReceived(LinphoneCore lc, LinphoneCall call, LinphoneAddress from, byte[] event) {

	}

	@Override
	public void transferState(LinphoneCore lc, LinphoneCall call, State new_call_state) {

	}

	@Override
	public void infoReceived(LinphoneCore lc, LinphoneCall call, LinphoneInfoMessage info) {

	}

	@Override
	public void subscriptionStateChanged(LinphoneCore lc, LinphoneEvent ev, SubscriptionState state) {

	}

	@Override
	public void publishStateChanged(LinphoneCore lc, LinphoneEvent ev, PublishState state) {

	}

	@Override
	public void show(LinphoneCore lc) {

	}

	@Override
	public void displayStatus(LinphoneCore lc, String message) {

	}

	@Override
	public void displayMessage(LinphoneCore lc, String message) {

	}

	@Override
	public void displayWarning(LinphoneCore lc, String message) {

	}

	@Override
	public void fileTransferProgressIndication(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content,
			int progress) {

	}

	@Override
	public void fileTransferRecv(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, byte[] buffer,
			int size) {

	}

	@Override
	public int fileTransferSend(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content,
			ByteBuffer buffer, int size) {

		return 0;
	}

	@Override
	public void globalState(LinphoneCore lc, GlobalState state, String message) {

		
	}

	@Override
	public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, RegistrationState state, String smessage) {

	}

	@Override
	public void configuringStatus(LinphoneCore lc, RemoteProvisioningState state, String message) {

	}

	@Override
	public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {

	}

	@Override
	public void callState(LinphoneCore lc, LinphoneCall call, State state, String message) {

	}

	@Override
	public void callEncryptionChanged(LinphoneCore lc, LinphoneCall call, boolean encrypted,
			String authenticationToken) {

	}

	@Override
	public void notifyReceived(LinphoneCore lc, LinphoneEvent ev, String eventName, LinphoneContent content) {

	}

	@Override
	public void isComposingReceived(LinphoneCore lc, LinphoneChatRoom cr) {

	}

	@Override
	public void ecCalibrationStatus(LinphoneCore lc, EcCalibratorStatus status, int delay_ms, Object data) {

	}

	@Override
	public void uploadProgressIndication(LinphoneCore lc, int offset, int total) {

	}

	@Override
	public void uploadStateChanged(LinphoneCore lc, LogCollectionUploadState state, String info) {

	}

}
