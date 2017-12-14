package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class CreateFile {
	private Request<ParamCreateFile> createFileRequest;

	public Request<ParamCreateFile> getCreateFileRequest() {
		return createFileRequest;
	}

	public void setCreateFileRequest(Request<ParamCreateFile> createFileRequest) {
		this.createFileRequest = createFileRequest;
	}
}
