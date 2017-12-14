package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

/**
 * Tipo de datos compuesto que representa el resultado de validación de una firma 
 * electrónica por los servicios de repositorio y migración de la capa CSGD.
 * 
 * @author u104848
 *
 */
public class SignatureDetailedInfo {
	private DSSResult result;
	private SignatureFormat signatureFormat;
	private List<ValidateCertificateResults> validateCertificateResults;
	private List<Timestamp> timestamps;
	
	public DSSResult getResult() {
		return result;
	}
	public void setResult(DSSResult result) {
		this.result = result;
	}
	public SignatureFormat getSignatureFormat() {
		return signatureFormat;
	}
	public void setSignatureFormat(SignatureFormat signatureFormat) {
		this.signatureFormat = signatureFormat;
	}
	public List<ValidateCertificateResults> getValidateCertificateResults() {
		return validateCertificateResults;
	}
	public void setValidateCertificateResults(List<ValidateCertificateResults> validateCertificateResults) {
		this.validateCertificateResults = validateCertificateResults;
	}
	public List<Timestamp> getTimestamps() {
		return timestamps;
	}
	public void setTimestamps(List<Timestamp> timestamps) {
		this.timestamps = timestamps;
	}
	
	
}
