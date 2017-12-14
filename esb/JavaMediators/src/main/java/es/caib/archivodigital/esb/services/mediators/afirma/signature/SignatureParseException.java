package es.caib.archivodigital.esb.services.mediators.afirma.signature;

public class SignatureParseException extends Exception {

	private static final long serialVersionUID = -2317318156658919109L;

	public SignatureParseException(){
		super();
    }

    public SignatureParseException(String message) {
        super(message);
    }

    public SignatureParseException(Throwable cause) {
        super(cause);
    }

    public SignatureParseException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
