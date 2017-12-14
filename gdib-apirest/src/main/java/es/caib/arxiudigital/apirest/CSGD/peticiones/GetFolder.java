package es.caib.arxiudigital.apirest.CSGD.peticiones;


import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class GetFolder {
	private Request<ParamNodeId> getFolderRequest;

	public Request<ParamNodeId> getGetFolderRequest() {
		return getFolderRequest;
	}

	public void setGetFolderRequest(Request<ParamNodeId> getFolderRequest) {
		this.getFolderRequest = getFolderRequest;
	}


}
