package core.exceptions;

/**
 * Exception si commande non permise par le role courant
 *
 */
public class IllegalCommandException extends InvalidCommandException {

	public IllegalCommandException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8904785980248427274L;

}
