package es.caib.invoices.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import es.caib.invoices.commons.InvoicesConstants;

public class InvoicesRequestDto {
	
	@JsonProperty(InvoicesConstants.ARCDOCID_JSON)
	@NotBlank(message = "'arcDocId' is null or empty")
	private String arcDocId;
	
	@JsonProperty(InvoicesConstants.INVOICENUMBER_JSON)
	@NotBlank(message = "'invoiceNumber' is null or empty")
	private String invoiceNumber;
	
	@JsonProperty(InvoicesConstants.PROVIDERNAME_JSON)
	@NotBlank(message = "'providerName' is null or empty")
	private String providerName;
	
	@JsonProperty(InvoicesConstants.PROVIDERID_JSON)
	@NotBlank(message = "'providerId' is null or empty")
	private String providerId;
	
	@JsonProperty(InvoicesConstants.SERIALNUMBER_JSON)
	private String serialNumber;
	
	@JsonProperty(InvoicesConstants.ISSUEDATE_JSON)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date issueDate;
	
	@JsonProperty(InvoicesConstants.INVOICETOTAL_JSON)
	@NotBlank(message = "'invoiceTotal' is null or empty")
	private String invoiceTotal;
	
	@JsonProperty(InvoicesConstants.DIR3_JSON)
	@NotBlank(message = "'dir3' is null or empty")
	private String dir3;
	
	@JsonProperty(InvoicesConstants.DOCID_JSN)
	@NotBlank(message = "'docid' is null or empty")
	private String docid;
	
	@JsonProperty(InvoicesConstants.ARCHIVID_JSON)
	@NotBlank(message = "'archivid' is null or empty")
	private String archivid;

	public InvoicesRequestDto(@NotBlank(message = "'arcDocId' is null or empty") String arcDocId,
			@NotBlank(message = "'invoiceNumber' is null or empty") String invoiceNumber,
			@NotBlank(message = "'providerName' is null or empty") String providerName,
			@NotBlank(message = "'providerId' is null or empty") String providerId,
			String serialNumber, Date issueDate,
			@NotBlank(message = "'invoiceTotal' is null or empty") String invoiceTotal,
			@NotBlank(message = "'dir3' is null or empty") String dir3,
			@NotBlank(message = "'docid' is null or empty") String docid,
			@NotBlank(message = "'archivid' is null or empty") String archivid) {
		super();
		this.arcDocId = arcDocId;
		this.invoiceNumber = invoiceNumber;
		this.providerName = providerName;
		this.providerId = providerId;
		this.serialNumber = serialNumber;
		this.issueDate = issueDate;
		this.invoiceTotal = invoiceTotal;
		this.dir3 = dir3;
		this.docid = docid;
		this.archivid = archivid;
	}

	public InvoicesRequestDto() {
		super();
	}

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

	@Override
	public String toString() {
		return "InvoicesRequestDto [arcDocId=" + arcDocId + ", invoiceNumber=" + invoiceNumber + ", providerName="
				+ providerName + ", providerId=" + providerId + ", serialNumber=" + serialNumber + ", issueDate="
				+ issueDate + ", invoiceTotal=" + invoiceTotal + ", dir3=" + dir3 + ", docid=" + docid + ", archivid="
				+ archivid + "]";
	}

	
	
}
