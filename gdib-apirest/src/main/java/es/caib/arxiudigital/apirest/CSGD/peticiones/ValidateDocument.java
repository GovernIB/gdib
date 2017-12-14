package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamValidateDoc;

public class ValidateDocument {
	private Request<ParamValidateDoc> validateDocRequest;

	public Request<ParamValidateDoc> getValidateDocRequest() {
		return validateDocRequest;
	}

	public void setValidateDocRequest(Request<ParamValidateDoc> validateDocRequest) {
		this.validateDocRequest = validateDocRequest;
	}



}
