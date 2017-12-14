package es.rsits.ws.audit.type;

import java.util.Date;

public class AuditData {

	private String username;
	private String application;
	private String operType;
	private String operation;
	private String esbOperation;
	private String authType;
	private Date executionDate;
	private String executionTime;
	private String MAC;
	private String IP;

	// OPTIONAL DATA
	private String applicantName;
	private String applicantDocument;
	private String publicServantName;
	private String publicServantDocument;
	private String publicServantOrganization;
	// private String procedureExpedient;
	private String expedient;
	// private String documentarySeries;

	// ERROR
	public String code;
	public String message;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getEsbOperation() {
		return esbOperation;
	}

	public void setEsbOperation(String esbOperation) {
		this.esbOperation = esbOperation;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public String getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}

	public String getMAC() {
		return MAC;
	}

	public void setMAC(String mAC) {
		MAC = mAC;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getApplicantDocument() {
		return applicantDocument;
	}

	public void setApplicantDocument(String applicantDocument) {
		this.applicantDocument = applicantDocument;
	}

	public String getPublicServantName() {
		return publicServantName;
	}

	public void setPublicServantName(String publicServantName) {
		this.publicServantName = publicServantName;
	}

	public String getPublicServantDocument() {
		return publicServantDocument;
	}

	public void setPublicServantDocument(String publicServantDocument) {
		this.publicServantDocument = publicServantDocument;
	}

	public String getPublicServantOrganization() {
		return publicServantOrganization;
	}

	public void setPublicServantOrganization(String publicServantOrganization) {
		this.publicServantOrganization = publicServantOrganization;
	}

	public String getExpedient() {
		return expedient;
	}

	public void setExpedient(String expedient) {
		this.expedient = expedient;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
