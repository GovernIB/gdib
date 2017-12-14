package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class CreateChildFile {
	private Request<ParamCreateChildFile> createChildFileRequest;

	public Request<ParamCreateChildFile> getCreateChildFileRequest() {
		return createChildFileRequest;
	}

	public void setCreateChildFileRequest(Request<ParamCreateChildFile> createChildFileRequest) {
		this.createChildFileRequest = createChildFileRequest;
	}


}
