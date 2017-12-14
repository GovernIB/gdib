package es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentNode;

public class ParamCreateDraftDocument {
	private String parent;
	private String retrieveNode;
	private DocumentNode document;
	
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getRetrieveNode() {
		return retrieveNode;
	}
	public void setRetrieveNode(String retrieveNode) {
		this.retrieveNode = retrieveNode;
	}
	public DocumentNode getDocument() {
		return document;
	}
	public void setDocument(DocumentNode document) {
		this.document = document;
	}
}
