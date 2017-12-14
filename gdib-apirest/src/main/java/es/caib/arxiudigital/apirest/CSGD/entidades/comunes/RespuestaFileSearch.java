package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

public class RespuestaFileSearch{
	
	private int totalNumberOfResults;
	private int pageNumber;
	private List<FileNode> files;
	
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
	public List<FileNode> getFiles() {
		return files;
	}
	public void setFiles(List<FileNode> files) {
		this.files = files;
	}

	
	


	
}
