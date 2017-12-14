package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class LockFolder {
	private Request<ParamNodeId> lockFolderRequest;

	public Request<ParamNodeId> getLockFolderRequest() {
		return lockFolderRequest;
	}

	public void setLockFolderRequest(Request<ParamNodeId> lockFolderRequest) {
		this.lockFolderRequest = lockFolderRequest;
	}


	
	
}
