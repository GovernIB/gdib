package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.*;

public class CreateFolderResult {
	private RespuestaGenerica<FolderNode> createFolderResult;

	public RespuestaGenerica<FolderNode> getCreateFolderResult() {
		return createFolderResult;
	}

	public void setCreateFolderResult(RespuestaGenerica<FolderNode> createFolderResult) {
		this.createFolderResult = createFolderResult;
	}

}
