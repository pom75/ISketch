package core.exceptions;

/**
 * Exception si commande inconnue
 * 
 */
public class UnknownCommandException extends InvalidCommandException {

	public UnknownCommandException(String message) {
		super("Commande inconnue: " + message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6521801332001232825L;

}
