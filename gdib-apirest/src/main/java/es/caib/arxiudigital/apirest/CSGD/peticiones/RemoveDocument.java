package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class RemoveDocument {
	private Request<ParamNodeId> removeDocumentRequest;

	public Request<ParamNodeId> getRemoveDocumentRequest() {
		return removeDocumentRequest;
	}

	public void setRemoveDocumentRequest(Request<ParamNodeId> removeDocumentRequest) {
		this.removeDocumentRequest = removeDocumentRequest;
	}

}
