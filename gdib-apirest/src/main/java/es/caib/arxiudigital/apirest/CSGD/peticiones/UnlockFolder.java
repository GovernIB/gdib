package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class UnlockFolder {
	private Request<ParamNodeId> unlockFolderRequest;

	public Request<ParamNodeId> getUnlockFolderRequest() {
		return unlockFolderRequest;
	}

	public void setUnlockFolderRequest(Request<ParamNodeId> unlockFolderRequest) {
		this.unlockFolderRequest = unlockFolderRequest;
	}

	
	
}
