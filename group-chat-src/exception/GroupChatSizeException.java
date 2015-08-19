package exception;

public class GroupChatSizeException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public GroupChatSizeException(String message) {
		super(message);
	}
}
