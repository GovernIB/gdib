package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeID_TargetParent;

public class MoveFolder {
	private Request<ParamNodeID_TargetParent> moveFolderRequest;

	public Request<ParamNodeID_TargetParent> getMoveFolderRequest() {
		return moveFolderRequest;
	}

	public void setMoveFolderRequest(Request<ParamNodeID_TargetParent> moveFolderRequest) {
		this.moveFolderRequest = moveFolderRequest;
	}

}
