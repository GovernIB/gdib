package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaGenerica;

public class CreateDraftDocumentResult {
	private RespuestaGenerica<DocumentNode> createDraftDocumentResult;

	public RespuestaGenerica<DocumentNode> getCreateDraftDocumentResult() {
		return createDraftDocumentResult;
	}

	public void setCreateDraftDocumentResult(RespuestaGenerica<DocumentNode> createDraftDocumentResult) {
		this.createDraftDocumentResult = createDraftDocumentResult;
	}



}
