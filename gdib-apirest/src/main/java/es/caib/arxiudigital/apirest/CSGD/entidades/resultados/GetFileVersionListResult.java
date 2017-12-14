package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaGenericaLista;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.VersionNode;

public class GetFileVersionListResult {
	private RespuestaGenericaLista<VersionNode> getFileVersionListResult;

	public RespuestaGenericaLista<VersionNode> getGetFileVersionListResult() {
		return getFileVersionListResult;
	}

	public void setGetFileVersionListResult(RespuestaGenericaLista<VersionNode> getFileVersionListResult) {
		this.getFileVersionListResult = getFileVersionListResult;
	}



	
}
