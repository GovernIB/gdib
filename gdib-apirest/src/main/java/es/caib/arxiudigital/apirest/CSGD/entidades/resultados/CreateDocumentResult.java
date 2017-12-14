package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaGenerica;

public class CreateDocumentResult {
	private RespuestaGenerica<DocumentNode> createDocumentResult;

	public RespuestaGenerica<DocumentNode> getCreateDocumentResult() {
		return createDocumentResult;
	}

	public void setCreateDocumentResult(RespuestaGenerica<DocumentNode> createDocumentResult) {
		this.createDocumentResult = createDocumentResult;
	}

}
