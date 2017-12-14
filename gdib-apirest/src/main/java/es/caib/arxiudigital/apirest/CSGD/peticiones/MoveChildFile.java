package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeID_TargetParent;

public class MoveChildFile {
	private Request<ParamNodeID_TargetParent> MoveChildFileRequest;

	public Request<ParamNodeID_TargetParent> getMoveChildFileRequest() {
		return MoveChildFileRequest;
	}

	public void setMoveChildFileRequest(Request<ParamNodeID_TargetParent> moveChildFileRequest) {
		MoveChildFileRequest = moveChildFileRequest;
	}




}
