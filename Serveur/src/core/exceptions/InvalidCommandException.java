package core.exceptions;

import tools.IO;

/**
 * Exception père des commandes invalides reçues du client
 *
 */
public class InvalidCommandException extends Exception {

	public InvalidCommandException(String message) {
		super(message); // maybe, remplacer ici les " " par "_"?
		IO.traceDebug("InvalidCommandException: "+ message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3835326239959660003L;
	
	
}
