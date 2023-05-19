package es.caib.invoices.commons;

import org.alfresco.service.namespace.QName;

public class InvoicesConstants {

	//JSON request
	public static final String ARCDOCID_JSON = "arcDocId";
	public static final String INVOICENUMBER_JSON = "invoiceNumber";
	public static final String PROVIDERID_JSON = "providerId";
	public static final String PROVIDERNAME_JSON = "providerName";
	public static final String SERIALNUMBER_JSON = "serialNumber";
	public static final String ISSUEDATE_JSON = "issueDate";
	public static final String INVOICETOTAL_JSON = "invoiceTotal";
	public static final String DIR3_JSON = "dir3";
	public static final String DOCID_JSN = "docid";
	public static final String ARCHIVID_JSON = "archivid";
	
	//Modelo de ACS de SAP
	public static final String QNAME_URI = "http://www.caib.es/invoices/model/content/1.0";

	public static final String INVOICE = "invoice";
	public static final String INVOICENUMBER = "invoiceNumber";
	public static final String PROVIDERID = "providerId";
	public static final String PROVIDERNAME = "providerName";
	public static final String SERIALNUMBER = "serialNumber";
	public static final String ISSUEDATE = "issueDate";
	public static final String INVOICETOTAL = "invoiceTotal";
	public static final String DIR3 = "dir3";
	public static final String DOCID = "docid";
	public static final String ARCHIV_ID = "archiv_id";
	//Aspeto
	public static final QName QASPECTINVOICE = QName.createQName(QNAME_URI, INVOICE);
	//Propiedades
	public static final QName QInvoiceNumber = QName.createQName(QNAME_URI, INVOICENUMBER);
	public static final QName QProviderId = QName.createQName(QNAME_URI, PROVIDERID);
	public static final QName QProviderName = QName.createQName(QNAME_URI, PROVIDERNAME);
	public static final QName QSerialNumber = QName.createQName(QNAME_URI, SERIALNUMBER);
	public static final QName QIssueDate = QName.createQName(QNAME_URI, ISSUEDATE);
	public static final QName QInvoiceTotal = QName.createQName(QNAME_URI, INVOICETOTAL);
	public static final QName QDir3 = QName.createQName(QNAME_URI, DIR3);
	public static final QName QDocid = QName.createQName(QNAME_URI, DOCID);
	public static final QName QArchivid = QName.createQName(QNAME_URI, ARCHIV_ID);
	

	
	
}
