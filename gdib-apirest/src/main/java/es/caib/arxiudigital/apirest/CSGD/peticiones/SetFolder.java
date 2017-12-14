package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class SetFolder {
	private Request<ParamSetFolder> setFolderRequest;

	public Request<ParamSetFolder> getSetFolderRequest() {
		return setFolderRequest;
	}

	public void setSetFolderRequest(Request<ParamSetFolder> setFolderRequest) {
		this.setFolderRequest = setFolderRequest;
	}


}
