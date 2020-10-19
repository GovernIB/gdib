package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import org.apache.commons.lang3.StringEscapeUtils;

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
		//Se hace el escapeado para evitar problemas con caracteres especiales
		this.document = StringEscapeUtils.escapeXml(document);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
