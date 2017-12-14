package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

public class RespuestaDocumentSearch{
	
	private int totalNumberOfResults;
	private int pageNumber;
	private List<DocumentNode> documents;
	
	public int getTotalNumberOfResults() {
		return totalNumberOfResults;
	}
	public void setTotalNumberOfResults(int totalNumberOfResults) {
		this.totalNumberOfResults = totalNumberOfResults;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public List<DocumentNode> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentNode> documents) {
		this.documents = documents;
	}

	
}
