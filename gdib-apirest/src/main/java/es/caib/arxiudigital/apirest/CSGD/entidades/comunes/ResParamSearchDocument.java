package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

public class ResParamSearchDocument{
	private List<DocumentNode>  documents;
	private Integer pageNumber;
	private Integer totalNumberOfResults;
	public List<DocumentNode> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentNode> documents) {
		this.documents = documents;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getTotalNumberOfResults() {
		return totalNumberOfResults;
	}
	public void setTotalNumberOfResults(Integer totalNumberOfResults) {
		this.totalNumberOfResults = totalNumberOfResults;
	}

}
