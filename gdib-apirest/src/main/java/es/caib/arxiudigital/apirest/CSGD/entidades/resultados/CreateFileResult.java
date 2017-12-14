package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FileNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaGenerica;

public class CreateFileResult {
	private RespuestaGenerica<FileNode> createFileResult;

	public RespuestaGenerica<FileNode> getCreateFileResult() {
		return createFileResult;
	}

	public void setCreateFileResult(RespuestaGenerica<FileNode> createFileResult) {
		this.createFileResult = createFileResult;
	}


	
}
