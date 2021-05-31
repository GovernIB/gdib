package es.caib.gdib.utils;

import java.sql.Date;

/*
 * 
 * CREATE TABLE GDIB.CERTIFICATES (
SERIALNUMBER VARCHAR2(50) NOT NULL,
SUBJECTDN VARCHAR2(250 CHAR) NOT NULL,
ISSUERDN VARCHAR2(250 CHAR) NOT NULL,
NOTBEFORE DATE NOT NULL,
NOTAFTER DATE NOT NULL,
NUMINDICES NUMBER(20,0) NOT NULL, -- indica el numero de indices que
existen con este certificado
CONSTRAINT GDIBCERTIFICATES_PK PRIMARY KEY (SERIALNUMBER)
)
 * */
public class Certificate {

	private String serialNumber;
	private String subjectDN;
	private String issuerDN;
	private Date notBefore;
	private Date notAfter;
	private int numIndices;
	public Certificate(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public Certificate() {
	}
	public Certificate(String serialNumber,int indices)
	{
		this.serialNumber = serialNumber;
		this.numIndices = indices;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getSubjectDN() {
		return subjectDN;
	}
	public void setSubjectDN(String subjectDN) {
		this.subjectDN = subjectDN;
	}
	public String getIssuerDN() {
		return issuerDN;
	}
	public void setIssuerDN(String issuerDN) {
		this.issuerDN = issuerDN;
	}
	public Date getNotBefore() {
		return notBefore;
	}
	public void setNotBefore(Date nofBefore) {
		this.notBefore = nofBefore;
	}
	public Date getNotAfter() {
		return notAfter;
	}
	public void setNotAfter(Date nofAfter) {
		this.notAfter = nofAfter;
	}
	public int getNumIndices() {
		return numIndices;
	}
	public void setNumIndices(int numIndices) {
		this.numIndices = numIndices;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((issuerDN == null) ? 0 : issuerDN.hashCode());
		result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
		result = prime * result + ((subjectDN == null) ? 0 : subjectDN.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Certificate)) {
			return false;
		}
		Certificate other = (Certificate) obj;
		if (issuerDN == null) {
			if (other.issuerDN != null) {
				return false;
			}
		} else if (!issuerDN.equals(other.issuerDN)) {
			return false;
		}
		if (serialNumber == null) {
			if (other.serialNumber != null) {
				return false;
			}
		} else if (!serialNumber.equals(other.serialNumber)) {
			return false;
		}
		if (subjectDN == null) {
			if (other.subjectDN != null) {
				return false;
			}
		} else if (!subjectDN.equals(other.subjectDN)) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return "Certificate [serialNumber=" + serialNumber + ", subjectDN=" + subjectDN + ", issuerDN=" + issuerDN
				+ ", notBefore=" + notBefore + ", notAfter=" + notAfter + ", numIndices=" + numIndices + "]";
	}
	
	
}
