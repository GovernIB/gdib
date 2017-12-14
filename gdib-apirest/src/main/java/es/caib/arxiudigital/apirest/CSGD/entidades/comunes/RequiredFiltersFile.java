package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

public class RequiredFiltersFile {
	private String name;
	private DatePeriod custDate;
	private DatePeriod closingDate;
	private String author;
	private String appName;
	private String eniId;
	private String applicants;
	private String docSeries;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DatePeriod getCustDate() {
		return custDate;
	}
	public void setCustDate(DatePeriod custDate) {
		this.custDate = custDate;
	}
	public DatePeriod getClosingDate() {
		return closingDate;
	}
	public void setClosingDate(DatePeriod closingDate) {
		this.closingDate = closingDate;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getEniId() {
		return eniId;
	}
	public void setEniId(String eniId) {
		this.eniId = eniId;
	}
	public String getApplicants() {
		return applicants;
	}
	public void setApplicants(String applicants) {
		this.applicants = applicants;
	}
	public String getDocSeries() {
		return docSeries;
	}
	public void setDocSeries(String docSeries) {
		this.docSeries = docSeries;
	}
	

}
