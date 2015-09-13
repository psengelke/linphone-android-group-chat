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
 *	This class serves as an intercepter for group chat messages and defers those messages 
 *	to the {@link LinphoneGroupChatManager} instance. It also serves as a wrapper class for 
 *	the {@link LinphoneManager} instance, to which responsibility for it's purpose as a 
 *	{@link LinphoneCoreListener} is delegated.
 *
 * @author Paul Engelke
 */
public class LinphoneGroupChatListener  implements LinphoneCoreListener {
	
	private LinphoneManager linphone_manager;
	private LinphoneGroupChatManager chat_manager;
	
	private LinphoneGroupChatListener() {
		linphone_manager = LinphoneManager.getInstance();
		chat_manager = LinphoneGroupChatManager.getInstance();
	}
	
	/**
	 * Getter for the {@link LinphoneCore} instance.
	 * @return
	 */
	public static LinphoneCore getLinphoneCore(){
		
		return LinphoneManager.getLc();
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
		linphone_manager.authInfoRequested(lc, realm, username, Domain);
	}

	@Override
	public void callStatsUpdated(LinphoneCore lc, LinphoneCall call, LinphoneCallStats stats) {
		linphone_manager.callStatsUpdated(lc, call, stats);
	}

	@Override
	public void newSubscriptionRequest(LinphoneCore lc, LinphoneFriend lf, String url) {
		linphone_manager.newSubscriptionRequest(lc, lf, url);
	}

	@Override
	public void notifyPresenceReceived(LinphoneCore lc, LinphoneFriend lf) {
		linphone_manager.notifyPresenceReceived(lc, lf);
	}

	@Override
	public void textReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneAddress from, String message) {
		linphone_manager.textReceived(lc, cr, from, message);
	}

	@Override
	public void dtmfReceived(LinphoneCore lc, LinphoneCall call, int dtmf) {
		linphone_manager.dtmfReceived(lc, call, dtmf);
	}

	@Override
	public void notifyReceived(LinphoneCore lc, LinphoneCall call, LinphoneAddress from, byte[] event) {
		linphone_manager.notifyReceived(lc, call, from, event);
	}

	@Override
	public void transferState(LinphoneCore lc, LinphoneCall call, State new_call_state) {
		linphone_manager.transferState(lc, call, new_call_state);
	}

	@Override
	public void infoReceived(LinphoneCore lc, LinphoneCall call, LinphoneInfoMessage info) {
		linphone_manager.infoReceived(lc, call, info);
	}

	@Override
	public void subscriptionStateChanged(LinphoneCore lc, LinphoneEvent ev, SubscriptionState state) {
		linphone_manager.subscriptionStateChanged(lc, ev, state);
	}

	@Override
	public void publishStateChanged(LinphoneCore lc, LinphoneEvent ev, PublishState state) {
		linphone_manager.publishStateChanged(lc, ev, state);
	}

	@Override
	public void show(LinphoneCore lc) {
		linphone_manager.show(lc);
	}

	@Override
	public void displayStatus(LinphoneCore lc, String message) {
		linphone_manager.displayStatus(lc, message);
	}

	@Override
	public void displayMessage(LinphoneCore lc, String message) {
		linphone_manager.displayMessage(lc, message);
	}

	@Override
	public void displayWarning(LinphoneCore lc, String message) {
		linphone_manager.displayWarning(lc, message);
	}

	@Override
	public void fileTransferProgressIndication(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content,
			int progress) {
		linphone_manager.fileTransferProgressIndication(lc, message, content, progress);
	}

	@Override
	public void fileTransferRecv(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, byte[] buffer,
			int size) {
		linphone_manager.fileTransferRecv(lc, message, content, buffer, size);
	}

	@Override
	public int fileTransferSend(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content,
			ByteBuffer buffer, int size) {

		return linphone_manager.fileTransferSend(lc, message, content, buffer, size);
	}

	@Override
	public void globalState(LinphoneCore lc, GlobalState state, String message) {
		linphone_manager.globalState(lc, state, message);
	}

	@Override
	public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, RegistrationState state, String smessage) {
		linphone_manager.registrationState(lc, cfg, state, smessage);
	}

	@Override
	public void configuringStatus(LinphoneCore lc, RemoteProvisioningState state, String message) {
		linphone_manager.configuringStatus(lc, state, message);
	}

	@Override
	public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {
		
		if (message.getCustomHeader(LinphoneGroupChatRoom.MSG_HEADER_GROUP_ID) != null){
			chat_manager.handleMessage(lc, cr, message);
		} else {
			linphone_manager.messageReceived(lc, cr, message);
		}
	}

	@Override
	public void callState(LinphoneCore lc, LinphoneCall call, State state, String message) {
		linphone_manager.callState(lc, call, state, message);
	}

	@Override
	public void callEncryptionChanged(LinphoneCore lc, LinphoneCall call, boolean encrypted,
			String authenticationToken) {
		linphone_manager.callEncryptionChanged(lc, call, encrypted, authenticationToken);
	}

	@Override
	public void notifyReceived(LinphoneCore lc, LinphoneEvent ev, String eventName, LinphoneContent content) {
		linphone_manager.notifyReceived(lc, ev, eventName, content);
	}

	@Override
	public void isComposingReceived(LinphoneCore lc, LinphoneChatRoom cr) {
		linphone_manager.isComposingReceived(lc, cr);
	}

	@Override
	public void ecCalibrationStatus(LinphoneCore lc, EcCalibratorStatus status, int delay_ms, Object data) {
		linphone_manager.ecCalibrationStatus(lc, status, delay_ms, data);
	}

	@Override
	public void uploadProgressIndication(LinphoneCore lc, int offset, int total) {
		linphone_manager.uploadProgressIndication(lc, offset, total);
	}

	@Override
	public void uploadStateChanged(LinphoneCore lc, LogCollectionUploadState state, String info) {
		linphone_manager.uploadStateChanged(lc, state, info);
	}

}
