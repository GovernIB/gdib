package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class LockFile {
	private Request<ParamNodeId> lockFileRequest;

	public Request<ParamNodeId> getLockFileRequest() {
		return lockFileRequest;
	}

	public void setLockFileRequest(Request<ParamNodeId> lockFileRequest) {
		this.lockFileRequest = lockFileRequest;
	}


	
}
