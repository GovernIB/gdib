package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamGrantPermissions;

public class GrantPermissionsOnFiles {
	private Request<ParamGrantPermissions> grantPermissionsOnFilesRequest;

	public Request<ParamGrantPermissions> getGrantPermissionsOnFilesRequest() {
		return grantPermissionsOnFilesRequest;
	}

	public void setGrantPermissionsOnFilesRequest(Request<ParamGrantPermissions> grantPermissionsOnFilesRequest) {
		this.grantPermissionsOnFilesRequest = grantPermissionsOnFilesRequest;
	}


}
