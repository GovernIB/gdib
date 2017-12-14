package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamGrantPermissions;

public class GrantPermissionsOnFolders {
	private Request<ParamGrantPermissions> grantPermissionsOnFoldersRequest;

	public Request<ParamGrantPermissions> getGrantPermissionsOnFoldersRequest() {
		return grantPermissionsOnFoldersRequest;
	}

	public void setGrantPermissionsOnFoldersRequest(
			Request<ParamGrantPermissions> grantPermissionsOnFoldersRequest) {
		this.grantPermissionsOnFoldersRequest = grantPermissionsOnFoldersRequest;
	}

	


}
