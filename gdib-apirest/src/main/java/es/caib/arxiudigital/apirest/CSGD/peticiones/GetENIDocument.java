package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class GetENIDocument {
	private Request<ParamNodeId> getENIDocRequest;

	public Request<ParamNodeId> getGetENIDocRequest() {
		return getENIDocRequest;
	}

	public void setGetENIDocRequest(Request<ParamNodeId> getENIDocRequest) {
		this.getENIDocRequest = getENIDocRequest;
	}




}
