package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * Tipo de datos compuesto que representa un campo o dato de un certificado electrónico.
 * 
 * @author u104848
 *
 */
public class CertificateField {
	private String alias;
	private String value;
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
