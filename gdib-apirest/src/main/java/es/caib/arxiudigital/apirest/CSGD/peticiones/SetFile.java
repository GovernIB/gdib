package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class SetFile {
	private Request<ParamSetFile> setFileRequest;

	public Request<ParamSetFile> getSetFileRequest() {
		return setFileRequest;
	}

	public void setSetFileRequest(Request<ParamSetFile> setFileRequest) {
		this.setFileRequest = setFileRequest;
	}

}
