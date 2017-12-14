package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

public class OptionalFiltersFile {
	private List<String> name;
	private List<DatePeriod> custDate;
	private List<DatePeriod> closingDate;
	private List<String> author;
	private List<String> appName;
	private List<String> eniId;
	private List<String> applicants;
	private List<String> docSeries;
	
	public List<String> getName() {
		return name;
	}
	public void setName(List<String> name) {
		this.name = name;
	}
	public List<DatePeriod> getCustDate() {
		return custDate;
	}
	public void setCustDate(List<DatePeriod> custDate) {
		this.custDate = custDate;
	}
	public List<DatePeriod> getClosingDate() {
		return closingDate;
	}
	public void setClosingDate(List<DatePeriod> closingDate) {
		this.closingDate = closingDate;
	}
	public List<String> getAuthor() {
		return author;
	}
	public void setAuthor(List<String> author) {
		this.author = author;
	}
	public List<String> getAppName() {
		return appName;
	}
	public void setAppName(List<String> appName) {
		this.appName = appName;
	}
	public List<String> getEniId() {
		return eniId;
	}
	public void setEniId(List<String> eniId) {
		this.eniId = eniId;
	}
	public List<String> getApplicants() {
		return applicants;
	}
	public void setApplicants(List<String> applicants) {
		this.applicants = applicants;
	}
	public List<String> getDocSeries() {
		return docSeries;
	}
	public void setDocSeries(List<String> docSeries) {
		this.docSeries = docSeries;
	}
	
	
}
