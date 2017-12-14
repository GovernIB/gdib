package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * Tipo de datos compuesto que representa la información de identificación de una persona.
 * 
 * @author u104848
 *
 */
public class PersonIdentAuditInfo {
	private String document;
	private String name;
	
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
