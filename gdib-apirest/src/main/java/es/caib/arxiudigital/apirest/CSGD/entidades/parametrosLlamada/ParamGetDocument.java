package es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentId;

public class ParamGetDocument {
	
	private DocumentId documentId;
	private String content;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public DocumentId getDocumentId() {
		return documentId;
	}

	public void setDocumentId(DocumentId documentId) {
		this.documentId = documentId;
	}

	
	
}
