package es.gob.afirma.utils;

/** 
 * <p>Class that represents an exception generated on the execution of some utility operation over a signature.</p>
 * 
 * @author RICOH
 *
 */
public class UtilsException extends Exception {

	/**
	 * Class serial version.
	 */
	private static final long serialVersionUID = 7781697797349685100L;

	/**
	 * Constructor method for the class UtilsException.java.
	 */
	public UtilsException() {
		super();
	}

	/**
	 * Constructor method for the class UtilsException.java.
	 * @param message Error message.
	 */
	public UtilsException(String message) {
		super(message);
	}

	/**
	 * Constructor method for the class UtilsException.java.
	 * @param cause Error cause.
	 */
	public UtilsException(Throwable cause) {
		super(cause);

	}

	/**
	 * Constructor method for the class UtilsException.java.
	 * @param message Error message.
	 * @param cause Error cause.
	 */
	public UtilsException(String message, Throwable cause) {
		super(message, cause);
	}

}
