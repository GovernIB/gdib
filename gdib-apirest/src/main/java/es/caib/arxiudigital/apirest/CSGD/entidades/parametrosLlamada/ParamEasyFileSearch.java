package es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.OptionalFiltersFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RequiredFiltersFile;

public class ParamEasyFileSearch {
	
	private RequiredFiltersFile requiredFilters;
	private OptionalFiltersFile optionalFilters;
	private Integer pageNumber = null;
	
	public RequiredFiltersFile getRequiredFilters() {
		return requiredFilters;
	}
	public void setRequiredFilters(RequiredFiltersFile requiredFilters) {
		this.requiredFilters = requiredFilters;
	}
	public OptionalFiltersFile getOptionalFilters() {
		return optionalFilters;
	}
	public void setOptionalFilters(OptionalFiltersFile optionalFilters) {
		this.optionalFilters = optionalFilters;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	
}
