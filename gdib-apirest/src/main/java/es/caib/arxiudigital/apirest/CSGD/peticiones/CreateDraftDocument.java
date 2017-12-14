package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class CreateDraftDocument {
	private Request<ParamCreateDraftDocument> createDraftDocumentRequest;

	public Request<ParamCreateDraftDocument> getCreateDraftDocumentRequest() {
		return createDraftDocumentRequest;
	}

	public void setCreateDraftDocumentRequest(Request<ParamCreateDraftDocument> createDraftDocumentRequest) {
		this.createDraftDocumentRequest = createDraftDocumentRequest;
	}

}
