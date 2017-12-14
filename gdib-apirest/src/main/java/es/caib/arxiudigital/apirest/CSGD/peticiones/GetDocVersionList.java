package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class GetDocVersionList {
	private Request<ParamNodeId> getDocVersionListRequest;

	public Request<ParamNodeId> getGetDocVersionListRequest() {
		return getDocVersionListRequest;
	}

	public void setGetDocVersionListRequest(Request<ParamNodeId> getDocVersionListRequest) {
		this.getDocVersionListRequest = getDocVersionListRequest;
	}

	
}
