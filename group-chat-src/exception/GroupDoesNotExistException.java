package exception;

public class GroupDoesNotExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public GroupDoesNotExistException(String message) {
		super(message);
	}
}
