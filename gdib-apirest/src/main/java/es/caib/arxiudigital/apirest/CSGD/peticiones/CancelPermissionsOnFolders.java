package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCancelPermissions;

public class CancelPermissionsOnFolders {
	private Request<ParamCancelPermissions> cancelPermissionsOnFoldersRequest;

	public Request<ParamCancelPermissions> getCancelPermissionsOnFoldersRequest() {
		return cancelPermissionsOnFoldersRequest;
	}

	public void setCancelPermissionsOnFoldersRequest(Request<ParamCancelPermissions> cancelPermissionsOnFoldersRequest) {
		this.cancelPermissionsOnFoldersRequest = cancelPermissionsOnFoldersRequest;
	}




}
