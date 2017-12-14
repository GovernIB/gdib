package es.caib.invoices.ws;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Clase utilizada para encapsular los parametros de entrada del metodo de 'updateMetadata' del web service de CAIB
 *
 * @author <a href="mailto:luis.fernandezprado@ricoh.es>Luis Fernandez Prado (LFP)</a>
 *
 */
@XmlRootElement(name = "updateMetadataRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateMetadataRequestType", propOrder = { "arcDocId","invoiceNumber","providerId","providerName","serialNumber","issueDate","invoiceTotal","dir3","docid","archivid" })
public class UpdateMetadataRequest {

	@XmlElement(required = true)
	private String arcDocId;
	
	public String getArcDocId() {
		return arcDocId;
	}

	public void setArcDocId(String arcDocId) {
		this.arcDocId = arcDocId;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getInvoiceTotal() {
		return invoiceTotal;
	}

	public void setInvoiceTotal(String invoiceTotal) {
		this.invoiceTotal = invoiceTotal;
	}

	public String getDir3() {
		return dir3;
	}

	public void setDir3(String dir3) {
		this.dir3 = dir3;
	}
	
	

	public String getDocid() {
		return docid;
	}

	public void setDocid(String docid) {
		this.docid = docid;
	}

	public String getArchivid() {
		return archivid;
	}

	public void setArchivid(String archivid) {
		this.archivid = archivid;
	}




	@XmlElement(required = true)
	private String invoiceNumber;
	
	@XmlElement(required = true)
	private String providerName;
	
	@XmlElement(required = true)
	private String providerId;
	
	@XmlElement(required = true)
	private String serialNumber;
	
	@XmlElement(required = true)
	private Date issueDate;
	
	@XmlElement(required = true)
	private String invoiceTotal;
	
	@XmlElement(required = true)
	private String dir3;
	
	@XmlElement(required = true)
	private String docid;
	
	@XmlElement(required = true)
	private String archivid;
	
	public UpdateMetadataRequest(){
		super();
	}
		
    public UpdateMetadataRequest(String arcDocId, String invoiceNumber, String providerName, String providerId,
    		String serialNumber, Date issueDate, String invoiceTotal, String dir3, String docid, String archivid) {
    	super();
    	this.arcDocId = arcDocId;
    	this.invoiceNumber = invoiceNumber;
    	this.providerId = providerId;
    	this.providerName = providerName;
    	this.serialNumber = serialNumber;
    	this.issueDate = issueDate;
    	this.invoiceTotal = invoiceTotal;
    	this.dir3 = dir3;
    	this.docid = docid;
    	this.archivid = archivid;
    }
}
