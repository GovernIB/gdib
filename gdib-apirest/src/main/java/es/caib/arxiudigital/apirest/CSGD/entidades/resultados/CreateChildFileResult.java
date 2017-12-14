package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.*;

public class CreateChildFileResult {
	private RespuestaGenerica<FileNode> createChildFileResult;

	public RespuestaGenerica<FileNode> getCreateChildFileResult() {
		return createChildFileResult;
	}

	public void setCreateChildFileResult(RespuestaGenerica<FileNode> createChildFileResult) {
		this.createChildFileResult = createChildFileResult;
	}

}
