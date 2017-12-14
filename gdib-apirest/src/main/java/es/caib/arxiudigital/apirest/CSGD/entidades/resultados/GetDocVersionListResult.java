package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaGenericaLista;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.VersionNode;

public class GetDocVersionListResult {
	private RespuestaGenericaLista<VersionNode> getDocVersionListResult;

	public RespuestaGenericaLista<VersionNode> getGetDocVersionListResult() {
		return getDocVersionListResult;
	}

	public void setGetDocVersionListResult(RespuestaGenericaLista<VersionNode> getDocVersionListResult) {
		this.getDocVersionListResult = getDocVersionListResult;
	}


}
