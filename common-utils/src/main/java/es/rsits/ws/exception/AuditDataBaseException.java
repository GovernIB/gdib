package es.rsits.ws.exception;

public class AuditDataBaseException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuditDataBaseException() {
    }

    public AuditDataBaseException(String message) {
    	super(message);
    }

    public AuditDataBaseException(Throwable cause) {
    	super(cause);
    }
}
