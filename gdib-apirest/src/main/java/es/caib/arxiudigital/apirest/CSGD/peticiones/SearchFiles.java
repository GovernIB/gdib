package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class SearchFiles {
	private Request<ParamSearch> searchFilesRequest;

	public Request<ParamSearch> getSearchFilesRequest() {
		return searchFilesRequest;
	}

	public void setSearchFilesRequest(Request<ParamSearch> searchFilesRequest) {
		this.searchFilesRequest = searchFilesRequest;
	}


}
