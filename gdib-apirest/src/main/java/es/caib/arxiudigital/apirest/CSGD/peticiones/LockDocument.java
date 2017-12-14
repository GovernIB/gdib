package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class LockDocument {
	private Request<ParamNodeId> lockDocumentRequest;

	public Request<ParamNodeId> getLockDocumentRequest() {
		return lockDocumentRequest;
	}

	public void setLockDocumentRequest(Request<ParamNodeId> lockDocumentRequest) {
		this.lockDocumentRequest = lockDocumentRequest;
	}

	
	
}
