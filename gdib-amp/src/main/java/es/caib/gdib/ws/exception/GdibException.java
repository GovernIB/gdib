package es.caib.gdib.ws.exception;

import javax.xml.ws.WebFault;

@WebFault(name = "GdibException", targetNamespace = "http://www.caib.es/gdib/repository/ws")
public class GdibException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -7581993808604957156L;

	private GdibExceptionInfo faultInfo;

	public GdibException(){
		super();
	}

	public GdibException(String message){
		super(message.replaceAll("[\\s\\']", " ").replaceAll("[^\\wçñàáèéíïòóúüÇÑÀÁÈÉÍÏÒÓÚÜ()\\-,\\.·\\s\\:\\-\\/\\{\\}]", "").trim());
	}

	public GdibException(String message, Throwable cause){
		super(message.replaceAll("[\\s\\']", " ").replaceAll("[^\\wçñàáèéíïòóúüÇÑÀÁÈÉÍÏÒÓÚÜ()\\-,\\.·\\s\\:\\-\\/\\{\\}]", "").trim(), cause);
	}

	public GdibException(final String message, final GdibExceptionInfo faultInfo) {
		super(message.replaceAll("[\\s\\']", " ").replaceAll("[^\\wçñàáèéíïòóúüÇÑÀÁÈÉÍÏÒÓÚÜ()\\-,\\.·\\s\\:\\-\\/\\{\\}]", "").trim());
		this.faultInfo = faultInfo;
	}

	public GdibException(final String message, final GdibExceptionInfo faultInfo, final Throwable cause) {
		super(message.replaceAll("[\\s\\']", " ").replaceAll("[^\\wçñàáèéíïòóúüÇÑÀÁÈÉÍÏÒÓÚÜ()\\-,\\.·\\s\\:\\-\\/\\{\\}]", "").trim(), cause);
		this.faultInfo = faultInfo;
	}

	public GdibException(GdibTransactionException e) {
		super(e.getMessage().replaceAll("[\\s\\']", " ").replaceAll("[^\\wçñàáèéíïòóúüÇÑÀÁÈÉÍÏÒÓÚÜ()\\-,\\.·\\s\\:\\-\\/\\{\\}]", "").trim(), e.getCause());		
		this.faultInfo = e.getFaultInfo();
	}

	public GdibExceptionInfo getFaultInfo() {
		return this.faultInfo;
	}

}
