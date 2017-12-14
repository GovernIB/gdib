package es.rsits.ws.exception;

import javax.xml.ws.WebFault;

/**
 * Excepcion a utilizar en los metodos del servicio web de CAIB
 *
 * @author <a href="mailto:luis.fernandezprado@ricoh.es>Luis Fernandez Prado (LFP)</a>
 *
 */
@WebFault(name="ServiceError", targetNamespace="http://www.caib.es/invoices/ws")
public class WSException extends Exception {

    private static final long serialVersionUID = -6647544772732631047L;

    /**
     *
     */
    public WSException() {

    }

    /**
     * @param message
     */
    public WSException(String message) {
	super(message);
    }

    /**
     * @param cause
     */
    public WSException(Throwable cause) {
	super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public WSException(String message, Throwable cause) {
	super(message, cause);
    }
}
