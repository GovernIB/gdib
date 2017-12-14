package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

public class ErrorBus{
	
	private MensajeError exception;
	
	public MensajeError getException() {
		return exception;
	}

	public void setException(MensajeError exception) {
		this.exception = exception;
	}

	
	public String toString() { 
		return "{\n exception:\n"+exception+"\n }";
	}
	

}
