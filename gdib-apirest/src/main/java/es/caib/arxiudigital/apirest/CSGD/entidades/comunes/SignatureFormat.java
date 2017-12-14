package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * Tipo de datos compuesto que representa el formato de una firma electrónica, expresado mediante URI’s DSS.
 * 
 * @author u104848
 *
 */
public class SignatureFormat {
	private String signatureType;
	private String signatureForm;
	
	public String getSignatureType() {
		return signatureType;
	}
	public void setSignatureType(String signatureType) {
		this.signatureType = signatureType;
	}
	public String getSignatureForm() {
		return signatureForm;
	}
	public void setSignatureForm(String signatureForm) {
		this.signatureForm = signatureForm;
	}
	
	
}
