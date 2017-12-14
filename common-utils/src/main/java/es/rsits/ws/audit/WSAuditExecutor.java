package es.rsits.ws.audit;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import es.rsits.ws.audit.dao.WSAuditDAO;
import es.rsits.ws.audit.type.AuditData;
import es.rsits.ws.exception.DAOException;

public class WSAuditExecutor implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(WSAuditExecutor.class);

	private WSAuditDAO auditDAO;

	private AuditData auditData;

	private String username;
	private String application;
	private String operationType;
	private String operation;
	private String esbOperation;
	private Date executionDate;
	private String executionTime;
	private String authType;
	private String ip;
	private String mac;

	private String applicantname;
	private String applicantdocument;
	private String publicServantName;
	private String publicServantDocument;
	private String publicServantOrganization;
	private String expedient;

	private String code;
	private String message;

	public WSAuditExecutor(){

	}

	public WSAuditExecutor(AuditData auditData) {
		super();
		this.username = auditData.getUsername();
		this.application = auditData.getApplication();
		this.operation = auditData.getOperation();
		this.operationType = auditData.getOperType();
		this.esbOperation = auditData.getEsbOperation();
		this.executionDate = auditData.getExecutionDate();
		this.executionTime = auditData.getExecutionTime();
		this.authType = auditData.getAuthType();
		this.ip = auditData.getIP();
		this.mac = auditData.getMAC();

		this.applicantname = auditData.getApplicantName();
		this.applicantdocument = auditData.getApplicantDocument();
		this.publicServantName = auditData.getPublicServantName();
		this.publicServantDocument = auditData.getPublicServantDocument();
		this.publicServantOrganization = auditData.getPublicServantOrganization();
		this.expedient = auditData.getExpedient();
		this.code = auditData.getCode();
		this.message = auditData.getMessage();

		this.auditData = auditData;
	}

	@Override
	public void run(){
		if(StringUtils.isEmpty(code)){
			try {
				auditDAO.auditOperation(auditData);
				// si falla la validacion se lanza una excepcion indicando que faltan datos
				validateAuditData();

			} catch (DAOException e) {
				// si hay fallo en la auditoria con la base de datos que hacer??
				LOGGER.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage(), e);
			}
		}else{
			try {
				auditDAO.auditError(auditData);
				// si falla la validacion se lanza una excepcion indicando que faltan datos
				validateAuditData();

			} catch (DAOException e) {
				// si hay fallo en la auditoria con la base de datos que hacer??
				LOGGER.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage(), e);
			}
		}

	}

	/**
	 * Valido los datos obligatorios de auditoria, en caso de faltar algun dato obligatorio
	 *
	 * @return booleano para indicar si la validacion es valida
	 */
	private void validateAuditData() {
		validateAuditData(auditData.getUsername(), "username");
		validateAuditData(auditData.getApplication(), "application");
		validateAuditData(auditData.getOperType(), "operationType");
		validateAuditData(auditData.getOperation(), "operation");
		validateAuditData(auditData.getEsbOperation(), "esbOperation");
		validateAuditData(auditData.getExecutionDate(), "executionDate");
		validateAuditData(auditData.getExecutionTime(), "executionTime");
		validateAuditData(auditData.getAuthType(), "authenticationType");
		validateAuditData(auditData.getMAC(), "MAC");
		validateAuditData(auditData.getIP(), "IP");
	}

	private void validateAuditData(String value, String field){
		if(StringUtils.isEmpty(value))
			throw new RuntimeException("["+field+"] missing data for Audit. ");
	}

	private void validateAuditData(Date value, String field){
		if(value==null)
			throw new RuntimeException("["+field+"] missing data for Audit. ");
	}

	public WSAuditDAO getAuditDAO() {
		return auditDAO;
	}

	public void setAuditDAO(WSAuditDAO auditDAO) {
		this.auditDAO = auditDAO;
	}

	public AuditData getAuditData() {
		return auditData;
	}

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

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
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

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getApplicantname() {
		return applicantname;
	}

	public void setApplicantname(String applicantname) {
		this.applicantname = applicantname;
	}

	public String getApplicantdocument() {
		return applicantdocument;
	}

	public void setApplicantdocument(String applicantdocument) {
		this.applicantdocument = applicantdocument;
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
