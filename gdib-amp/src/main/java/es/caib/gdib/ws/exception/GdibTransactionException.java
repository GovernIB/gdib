package es.caib.gdib.ws.exception;

import javax.xml.ws.WebFault;

public class GdibTransactionException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -7581993808604957156L;

	private GdibExceptionInfo faultInfo;

	public GdibTransactionException(){
		super();
	}

	public GdibTransactionException(String message){
		super(message);
	}

	public GdibTransactionException(String message, Throwable cause){
		super(message,cause);
	}

	public GdibTransactionException(final String message, final GdibExceptionInfo faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
	}

	public GdibTransactionException(final String message, final GdibExceptionInfo faultInfo, final Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public GdibExceptionInfo getFaultInfo() {
		return this.faultInfo;
	}

}
