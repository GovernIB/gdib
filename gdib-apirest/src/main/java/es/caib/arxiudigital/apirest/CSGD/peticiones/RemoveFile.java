package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class RemoveFile {
	private Request<ParamNodeId> removeFileRequest;

	public Request<ParamNodeId> getRemoveFileRequest() {
		return removeFileRequest;
	}

	public void setRemoveFileRequest(Request<ParamNodeId> removeFileRequest) {
		this.removeFileRequest = removeFileRequest;
	}



}
