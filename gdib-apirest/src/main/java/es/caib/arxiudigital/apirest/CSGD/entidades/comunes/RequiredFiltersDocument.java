package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

public class RequiredFiltersDocument {
	private String name;
	private DatePeriod custDate;
	private DatePeriod lastModDate;
	private String author;
	private String appName;
	private String eniId;
	private String content;
	private String mimetype;
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
	public DatePeriod getLastModDate() {
		return lastModDate;
	}
	public void setLastModDate(DatePeriod lastModDate) {
		this.lastModDate = lastModDate;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public String getDocSeries() {
		return docSeries;
	}
	public void setDocSeries(String docSeries) {
		this.docSeries = docSeries;
	}
	
	
}
