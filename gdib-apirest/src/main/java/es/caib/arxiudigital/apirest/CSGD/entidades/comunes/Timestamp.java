package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import es.caib.arxiudigital.apirest.constantes.TiposSelloDeTiempo;

/**
 * Tipo de datos compuesto que representa un sello de tiempo incluido en una firma electrónica.
 * 
 * @author u104848
 *
 */
public class Timestamp {
	private TiposSelloDeTiempo type;
	private String genTime;
	private ValidateCertificateResults validateCertificateResults;
	


	public TiposSelloDeTiempo getType() {
		return type;
	}
	public void setType(TiposSelloDeTiempo type) {
		this.type = type;
	}
	public String getGenTime() {
		return genTime;
	}
	public void setGenTime(String genTime) {
		this.genTime = genTime;
	}
	public ValidateCertificateResults getValidateCertificateResults() {
		return validateCertificateResults;
	}
	public void setValidateCertificateResults(ValidateCertificateResults validateCertificateResults) {
		this.validateCertificateResults = validateCertificateResults;
	}
	
	
}
