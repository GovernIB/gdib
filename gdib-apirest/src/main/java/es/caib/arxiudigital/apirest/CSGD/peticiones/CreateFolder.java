package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class CreateFolder {
	private Request<ParamCreateFolder> createFolderRequest;

	public Request<ParamCreateFolder> getCreateFolderRequest() {
		return createFolderRequest;
	}

	public void setCreateFolderRequest(Request<ParamCreateFolder> createFolderRequest) {
		this.createFolderRequest = createFolderRequest;
	}



}
