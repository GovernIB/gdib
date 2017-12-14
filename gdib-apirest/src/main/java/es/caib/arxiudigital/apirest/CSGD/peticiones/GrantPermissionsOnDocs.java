package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamGrantPermissions;

public class GrantPermissionsOnDocs {
	private Request<ParamGrantPermissions> grantPermissionsOnDocsRequest;

	public Request<ParamGrantPermissions> getGrantPermissionsOnDocsRequest() {
		return grantPermissionsOnDocsRequest;
	}

	public void setGrantPermissionsOnDocsRequest(Request<ParamGrantPermissions> grantPermissionsOnDocsRequest) {
		this.grantPermissionsOnDocsRequest = grantPermissionsOnDocsRequest;
	}


}
