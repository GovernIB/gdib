package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaFileSearch;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaGenerica;

public class EasyFileSearchResult {
	private RespuestaGenerica<RespuestaFileSearch> searchFilesResult;

	public RespuestaGenerica<RespuestaFileSearch> getSearchFilesResult() {
		return searchFilesResult;
	}

	public void setSearchFilesResult(RespuestaGenerica<RespuestaFileSearch> searchFilesResult) {
		this.searchFilesResult = searchFilesResult;
	}





}
