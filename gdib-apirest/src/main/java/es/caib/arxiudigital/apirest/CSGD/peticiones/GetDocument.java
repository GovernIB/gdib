package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class GetDocument {
	private Request<ParamGetDocument> getDocumentRequest;

	public Request<ParamGetDocument> getGetDocumentRequest() {
		return getDocumentRequest;
	}

	public void setGetDocumentRequest(Request<ParamGetDocument> getDocumentRequest) {
		this.getDocumentRequest = getDocumentRequest;
	}
	
}
