package exception;

/**
 * Represents a problem executing an operating system command 
 */
public class CommandExecutionException extends RuntimeException {
	
	private static final long serialVersionUID = 5954245630027306759L;
	
	public CommandExecutionException(Throwable cause) {
		super(cause);
	}
	
	public CommandExecutionException(String message) {
		super(message);
	}
	
}
