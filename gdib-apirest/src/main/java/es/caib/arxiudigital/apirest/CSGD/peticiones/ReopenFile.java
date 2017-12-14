package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class ReopenFile {
	private Request<ParamNodeId> reopenFileRequest;

	public Request<ParamNodeId> getReopenFileRequest() {
		return reopenFileRequest;
	}

	public void setReopenFileRequest(Request<ParamNodeId> reopenFileRequest) {
		this.reopenFileRequest = reopenFileRequest;
	}



	
}
