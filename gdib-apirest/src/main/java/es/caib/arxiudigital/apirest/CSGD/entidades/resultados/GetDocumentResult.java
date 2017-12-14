package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaGenerica;

public class GetDocumentResult {
	private RespuestaGenerica<DocumentNode> getDocumentResult;

	public RespuestaGenerica<DocumentNode> getGetDocumentResult() {
		return getDocumentResult;
	}

	public void setGetDocumentResult(RespuestaGenerica<DocumentNode> getDocumentResult) {
		this.getDocumentResult = getDocumentResult;
	}
	
	
}
