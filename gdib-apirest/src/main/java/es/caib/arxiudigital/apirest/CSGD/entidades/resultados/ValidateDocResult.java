package es.caib.arxiudigital.apirest.CSGD.entidades.resultados;


import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentoYFirmas;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaGenericaDSS;

public class ValidateDocResult {
	private RespuestaGenericaDSS<DocumentoYFirmas> validateDocResult;

	public RespuestaGenericaDSS<DocumentoYFirmas> getValidateDocResult() {
		return validateDocResult;
	}

	public void setValidateDocResult(RespuestaGenericaDSS<DocumentoYFirmas> validateDocResult) {
		this.validateDocResult = validateDocResult;
	}



	
}
