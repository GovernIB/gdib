package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class CloseFile {
	private Request<ParamNodeId> closeFileRequest;

	public Request<ParamNodeId> getCloseFileRequest() {
		return closeFileRequest;
	}

	public void setCloseFileRequest(Request<ParamNodeId> closeFileRequest) {
		this.closeFileRequest = closeFileRequest;
	}



}
