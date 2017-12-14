package es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada;

public class ParamSearch {
	private String query;
	private Integer pageNumber;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

}
