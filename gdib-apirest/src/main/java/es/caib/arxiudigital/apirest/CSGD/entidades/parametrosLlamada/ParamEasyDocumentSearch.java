package es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.OptionalFiltersDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RequiredFiltersDocument;

public class ParamEasyDocumentSearch {
	
	private RequiredFiltersDocument requiredFilters;
	private OptionalFiltersDocument optionalFilters;
	private int pageNumber;
	public RequiredFiltersDocument getRequiredFilters() {
		return requiredFilters;
	}
	public void setRequiredFilters(RequiredFiltersDocument requiredFilters) {
		this.requiredFilters = requiredFilters;
	}
	public OptionalFiltersDocument getOptionalFilters() {
		return optionalFilters;
	}
	public void setOptionalFilters(OptionalFiltersDocument optionalFilters) {
		this.optionalFilters = optionalFilters;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	

	
}
