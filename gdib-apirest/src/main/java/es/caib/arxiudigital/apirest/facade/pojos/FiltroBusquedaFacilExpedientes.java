package es.caib.arxiudigital.apirest.facade.pojos;

public class FiltroBusquedaFacilExpedientes {

	private String name;
	private IntervaloFechas custDate;
	private IntervaloFechas closingDate;
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
	public IntervaloFechas getCustDate() {
		return custDate;
	}
	public void setCustDate(IntervaloFechas custDate) {
		this.custDate = custDate;
	}
	public IntervaloFechas getClosingDate() {
		return closingDate;
	}
	public void setClosingDate(IntervaloFechas closingDate) {
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
