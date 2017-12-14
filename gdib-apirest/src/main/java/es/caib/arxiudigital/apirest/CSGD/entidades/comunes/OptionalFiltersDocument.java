package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

public class OptionalFiltersDocument {
	private List<String> name;
	private List<DatePeriod> custDate;
	private List<DatePeriod> lastModDate;
	private List<String> author;
	private List<String> appName;
	private List<String> eniId;
	private List<String> content;
	private List<String> mimetype;
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
	public List<DatePeriod> getLastModDate() {
		return lastModDate;
	}
	public void setLastModDate(List<DatePeriod> lastModDate) {
		this.lastModDate = lastModDate;
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
	public List<String> getContent() {
		return content;
	}
	public void setContent(List<String> content) {
		this.content = content;
	}
	public List<String> getMimetype() {
		return mimetype;
	}
	public void setMimetype(List<String> mimetype) {
		this.mimetype = mimetype;
	}
	public List<String> getDocSeries() {
		return docSeries;
	}
	public void setDocSeries(List<String> docSeries) {
		this.docSeries = docSeries;
	}
	
	
}
