package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

public class ValidateCertificateResults {
	private Result result;
	private List<CertificateField> certificateFields;
	
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public List<CertificateField> getCertificateFields() {
		return certificateFields;
	}
	public void setCertificateFields(List<CertificateField> certificateFields) {
		this.certificateFields = certificateFields;
	}
	
	
}
