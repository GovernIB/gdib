package es.rsits.ws.exception;

import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebFault;

@WebFault(name = "ProtocolServiceError", targetNamespace = "http://www.caib.es/invoices/ws")
public class WSProtocolRuntimeException extends ProtocolException {

	/**
	 *
	 */
	private static final long serialVersionUID = 9043477486287551272L;

	/**
	*
	*/
	public WSProtocolRuntimeException() {

	}

	/**
	 * @param message
	 */
	public WSProtocolRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public WSProtocolRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
