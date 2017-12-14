package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.*;

public class SearchDocsResult {
	private RespuestaGenerica<ResParamSearchDocument> searchDocumentsResult;

	public RespuestaGenerica<ResParamSearchDocument> getSearchDocumentsResult() {
		return searchDocumentsResult;
	}

	public void setSearchDocumentsResult(RespuestaGenerica<ResParamSearchDocument> searchDocumentsResult) {
		this.searchDocumentsResult = searchDocumentsResult;
	}




}
