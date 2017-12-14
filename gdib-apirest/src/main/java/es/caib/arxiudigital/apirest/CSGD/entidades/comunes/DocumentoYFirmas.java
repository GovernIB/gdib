package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

/**
 * Clase con ele objeto devuelto por validateDocument.
 * No aparece en los objetos comunes del documento de RICOH
 * 
 * @author u104848
 *
 */
public class DocumentoYFirmas {
	private DocumentNode document;
	private List<SignatureDetailedInfo> signaturesDetailedInfo;
	
	public DocumentNode getDocument() {
		return document;
	}
	public void setDocument(DocumentNode document) {
		this.document = document;
	}
	public List<SignatureDetailedInfo> getSignaturesDetailedInfo() {
		return signaturesDetailedInfo;
	}
	public void setSignaturesDetailedInfo(List<SignatureDetailedInfo> signaturesDetailedInfo) {
		this.signaturesDetailedInfo = signaturesDetailedInfo;
	}
	
	
}
