package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class SearchDocs {
	private Request<ParamSearch> searchDocsRequest;

	public Request<ParamSearch> getSearchDocsRequest() {
		return searchDocsRequest;
	}

	public void setSearchDocsRequest(Request<ParamSearch> searchDocsRequest) {
		this.searchDocsRequest = searchDocsRequest;
	}


}
