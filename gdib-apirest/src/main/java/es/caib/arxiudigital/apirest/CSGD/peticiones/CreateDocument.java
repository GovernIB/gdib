package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class CreateDocument {
	private Request<ParamCreateDocument> createDocumentRequest;

	public Request<ParamCreateDocument> getCreateDocumentRequest() {
		return createDocumentRequest;
	}

	public void setCreateDocumentRequest(Request<ParamCreateDocument> createDocumentRequest) {
		this.createDocumentRequest = createDocumentRequest;
	}
	
	
}
