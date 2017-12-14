package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class SetDocument {
	private Request<ParamSetDocument> setDocumentRequest;

	public Request<ParamSetDocument> getSetDocumentRequest() {
		return setDocumentRequest;
	}

	public void setSetDocumentRequest(Request<ParamSetDocument> setDocumentRequest) {
		this.setDocumentRequest = setDocumentRequest;
	}


}
