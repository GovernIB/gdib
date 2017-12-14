package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class UnlockDocument {
	private Request<ParamNodeId> lockDocumentRequest;

	public Request<ParamNodeId> getUnlockDocumentRequest() {
		return lockDocumentRequest;
	}

	public void setUnlockDocumentRequest(Request<ParamNodeId> lockDocumentRequest) {
		this.lockDocumentRequest = lockDocumentRequest;
	}

	
	
}
