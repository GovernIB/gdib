package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.*;

public class EasyFileSearch {
	private Request<ParamEasyFileSearch> searchFilesRequest;

	public Request<ParamEasyFileSearch> getSearchFilesRequest() {
		return searchFilesRequest;
	}

	public void setSearchFilesRequest(Request<ParamEasyFileSearch> searchFilesRequest) {
		this.searchFilesRequest = searchFilesRequest;
	}

}
