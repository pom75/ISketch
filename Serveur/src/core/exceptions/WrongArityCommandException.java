package core.exceptions;

/**
 * Exception si nombre arguments incorrects
 *
 */
public class WrongArityCommandException extends InvalidCommandException {

	public WrongArityCommandException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2876417535856518363L;


}
