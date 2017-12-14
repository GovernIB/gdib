package es.caib.arxiudigital.apirest.CSGD.peticiones;


import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class GetFile {
	private Request<ParamNodeId> getFileRequest;

	public Request<ParamNodeId> getGetFileRequest() {
		return getFileRequest;
	}

	public void setGetFileRequest(Request<ParamNodeId> getFileRequest) {
		this.getFileRequest = getFileRequest;
	}
}
