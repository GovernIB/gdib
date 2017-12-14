package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamDispatchDocument;

public class DispatchDocument {
	
	private Request<ParamDispatchDocument> dispatchDocumentRequest;

	public Request<ParamDispatchDocument> getDispatchDocumentRequest() {
		return dispatchDocumentRequest;
	}

	public void setDispatchDocumentRequest(Request<ParamDispatchDocument> dispatchDocumentRequest) {
		this.dispatchDocumentRequest = dispatchDocumentRequest;
	}


}
