package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class RemoveFolder {
	private Request<ParamNodeId> removeFolderRequest;

	public Request<ParamNodeId> getRemoveFolderRequest() {
		return removeFolderRequest;
	}

	public void setRemoveFolderRequest(Request<ParamNodeId> removeFolderRequest) {
		this.removeFolderRequest = removeFolderRequest;
	}

}
