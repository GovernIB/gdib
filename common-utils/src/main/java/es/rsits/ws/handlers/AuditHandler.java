package es.rsits.ws.handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import es.rsits.ws.audit.WSAudit;
import es.rsits.ws.audit.type.AuditData;
import es.rsits.ws.exception.AuditDataBaseException;
import es.rsits.ws.utils.WSUtils;
@Component
@Scope(value = "request")
public class AuditHandler extends SpringBeanAutowiringSupport  implements SOAPHandler<SOAPMessageContext>{

	private static final Logger LOGGER = Logger.getLogger(AuditHandler.class);

	private long time;

	private AuditData auditData;

	@Value("$base{gdib.audit.active}")
	private Boolean activeHandler;

	@Value("$base{audit.db.driver}")
    private String db_audit_driver;
	@Value("$base{audit.db.url}")
    private String db_audit_url;
	@Value("$base{audit.db.username}")
    private String db_audit_username;
	@Value("$base{audit.db.password}")
    private String db_audit_password;

	@Autowired
	private WSAudit wsAudit;

	private static final String GDIB_HEADER = "gdibHeader";
	private static final String GDIB_AUDIT = "gdibAudit";
	private static final String GDIB_SECURITY = "gdibSecurity";
	private static final String GDIB_SECURITY_FIELD_USER = "user";
	private static final String GDIB_AUDIT_FIELD_APPLICATION = "application";
	private static final String GDIB_AUDIT_FIELD_OPERATION_ESB = "esbOperation";
	private static final String GDIB_AUDIT_FIELD_APPLICANT = "applicant";
	private static final String GDIB_AUDIT_FIELD_APPLICANT_NAME = "name";
	private static final String GDIB_AUDIT_FIELD_APPLICANT_DOCUMENT = "document";
	private static final String GDIB_AUDIT_FIELD_PUBLIC_SERVANT = "publicServant";
	private static final String GDIB_AUDIT_FIELD_PUBLIC_SERVANT_NAME = "name";
	private static final String GDIB_AUDIT_FIELD_PUBLIC_SERVANT_DOCUMENT = "document";
	private static final String GDIB_AUDIT_FIELD_PUBLIC_SERVANT_ORGANIZATION = "organization";
	private static final String GDIB_AUDIT_FIELD_EXPEDIENT = "fileUid";

	public void initIt() throws Exception {
		/*try {
			if (! checkAuditTableExist()){
				createAuditTable();
			}
		} catch (AuditDataBaseException e) {
			LOGGER.error(e.getMessage(),e);
		}*/
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		// compruebo si el handler esta activa en la property del modulo ws-amp

		if(this.activeHandler.booleanValue()){
			Boolean outboundProperty = (Boolean)
					context.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);

			if (outboundProperty.booleanValue()) {
				// relleno el tiempo de ejecucion y realizo la auditoria de la operacion
				long endTime = new Date().getTime();
				auditData.setExecutionTime(String.valueOf(endTime - time) + " ms");
				LOGGER.debug("handleMessage :: outboundProperty : insert in BBDD");
				wsAudit.auditOperation(auditData);
			} else {
				// relleno el objeto de auditoria con los datos de la request.
				LOGGER.debug("handleMessage :: inboundProperty : getData from request");
			    getAuditData(context);
			}
		}
		return true;
	}

	/**
	 * Relleno el objeto {@link AuditData} con la informacion que viene de la request
	 * y con datos calculados
	 *
	 * @param context
	 */
	private void getAuditData(SOAPMessageContext context){
		LOGGER.debug("Extract audit properties from Soap Body");
		auditData = new AuditData();
		try{
			SOAPMessage soapMsg = context.getMessage();
	        SOAPBody soapBody = soapMsg.getSOAPBody();
	        SOAPElement operationNode = WSUtils.getSOAPElement(soapBody, null);
	        SOAPElement elementHeader = WSUtils.getSOAPElement(operationNode, GDIB_HEADER);
	        SOAPElement elementAudit = WSUtils.getSOAPElement(elementHeader, GDIB_AUDIT);
	        SOAPElement elementSecurity = WSUtils.getSOAPElement(elementHeader, GDIB_SECURITY);

//	        String username = WSUtils.getInfoSecurityHeader(context, WSUtils.QNAME_WSSE_USERNAME);
	        String username = WSUtils.getSOAPElementValue(elementSecurity, GDIB_SECURITY_FIELD_USER);
	        auditData.setUsername(username);
	        auditData.setApplication(WSUtils.getSOAPElementValue(elementAudit, GDIB_AUDIT_FIELD_APPLICATION));
	        String operation = operationNode.getLocalName();
	        auditData.setOperation(operation);
	        auditData.setOperType(WSUtils.AUDIT_OPERATION_TYPE.get(operation));
	        auditData.setEsbOperation(WSUtils.getSOAPElementValue(elementAudit, GDIB_AUDIT_FIELD_OPERATION_ESB));

	        auditData.setAuthType(username!=null?WSUtils.USERNAME:WSUtils.ANONYMOUS);
	        auditData.setIP(WSUtils.getIPAddress());
	        auditData.setMAC(WSUtils.getMACAddress());
	        Date now = new Date();
	        auditData.setExecutionDate(now);
	        time = now.getTime();

	        // saco los datos de la cabecera de Gdib
	        SOAPElement applicant = WSUtils.getSOAPElement(elementAudit, GDIB_AUDIT_FIELD_APPLICANT);
	        auditData.setApplicantName(WSUtils.getSOAPElementValue(applicant, GDIB_AUDIT_FIELD_APPLICANT_NAME));
	        auditData.setApplicantDocument(WSUtils.getSOAPElementValue(applicant, GDIB_AUDIT_FIELD_APPLICANT_DOCUMENT));
	        SOAPElement functionary = WSUtils.getSOAPElement(elementAudit, GDIB_AUDIT_FIELD_PUBLIC_SERVANT);
	        auditData.setPublicServantName(WSUtils.getSOAPElementValue(functionary, GDIB_AUDIT_FIELD_PUBLIC_SERVANT_NAME));
	        auditData.setPublicServantDocument(WSUtils.getSOAPElementValue(functionary, GDIB_AUDIT_FIELD_PUBLIC_SERVANT_DOCUMENT));
	        auditData.setPublicServantOrganization(WSUtils.getSOAPElementValue(functionary, GDIB_AUDIT_FIELD_PUBLIC_SERVANT_ORGANIZATION));
	        auditData.setExpedient(WSUtils.getSOAPElementValue(elementAudit, GDIB_AUDIT_FIELD_EXPEDIENT));

		}catch(Exception e){
			LOGGER.error(e.getMessage(),e);
		}
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		if(this.activeHandler.booleanValue()){
			LOGGER.debug("Enter - handleFault!!");
			try{
				SOAPMessage soapMsg = context.getMessage();
		        SOAPBody soapBody = soapMsg.getSOAPBody();

		        LOGGER.debug("Extract fault properties from Soap Body");
		        SOAPElement fault = WSUtils.getSOAPElement(soapBody, "Fault");
		        SOAPElement faultDetail = WSUtils.getSOAPElement(fault, "detail");
		        SOAPElement exception = WSUtils.getSOAPElement(faultDetail, "GdibException");
		        String code = (WSUtils.getSOAPElement(exception, "code") != null)?WSUtils.getSOAPElement(exception, "code").getValue():"";
		        String message = (WSUtils.getSOAPElement(exception, "message")!=null) ?WSUtils.getSOAPElement(exception, "message").getValue():"";
		        if ( auditData == null )
		        	auditData = new AuditData();
		        auditData.setCode(code);
		        auditData.setMessage(message);

		        long endTime = new Date().getTime();
				auditData.setExecutionTime(String.valueOf(endTime - time) + " ms");

		        wsAudit.auditError(auditData);

			}catch(SOAPException e){
				LOGGER.error(e.getMessage(),e);
			}
		}
		return true;
	}

	@Override
	public void close(MessageContext context) {
		LOGGER.debug("Enter - close!!");
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	public WSAudit getWsAudit() {
		return wsAudit;
	}

	public void setWsAudit(WSAudit wsAudit) {
		this.wsAudit = wsAudit;
	}

	public void setActiveHandler(Boolean activeHandler) {
		this.activeHandler = activeHandler;
	}

	private boolean checkAuditTableExist() throws AuditDataBaseException{
		boolean res = false;

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			conn = getDBConnection();

			if(conn != null){
				StringBuffer stb_SELECT = new StringBuffer();
				stb_SELECT.append("SELECT EXISTS");
				stb_SELECT.append("    (");
				stb_SELECT.append("    SELECT 1 FROM information_schema.tables ");
				stb_SELECT.append("    WHERE table_schema='public' AND ");
				stb_SELECT.append("          table_name='gdibauditoria'");
				stb_SELECT.append("    )");

//				SELECT EXISTS (
//						   SELECT 1
//						   FROM   information_schema.tables
//						   WHERE  table_catalog='alfresco'
//						   AND	  table_schema = 'public',
//						   AND    table_name = 'gdibauditoriaa'
//						);

				st = conn.createStatement();
				rs =  st.executeQuery(stb_SELECT.toString());

				while(rs.next()){
					res = rs.getBoolean(1);
				}
			}

		} catch (SQLException e) {
			LOGGER.error("Ha fallado la conexion con la bddd de auditoria.");
			throw  new AuditDataBaseException(e);
		} finally {
			try {
				st.close();
				conn.close();
				rs.close();
			} catch (SQLException e) {
				LOGGER.error("No se podido cerrar la conexion a base de datos.");
				throw  new AuditDataBaseException(e);
			}
		}

		return res;
	}
	private void createAuditTable() throws AuditDataBaseException{
		Connection conn = null;
		Statement st = null;

		try {
			conn = getDBConnection();

			if(conn != null){
				StringBuffer stb_CREATE = new StringBuffer();

				stb_CREATE.append("CREATE TABLE gdibauditoria");
				stb_CREATE.append("    (");
				stb_CREATE.append("    id serial NOT NULL,");
				stb_CREATE.append("    username character varying(50),");
				stb_CREATE.append("    application character varying(100),");
				stb_CREATE.append("    operationtype character varying(15),");
				stb_CREATE.append("    operation character varying(50),");
				stb_CREATE.append("    esboperation character varying(50),");
				stb_CREATE.append("    executiondate date,");
				stb_CREATE.append("    executiontime character varying(10),");
				stb_CREATE.append("    authenticationtype character varying(10),");
				stb_CREATE.append("    ip character varying(20),");
				stb_CREATE.append("    mac character varying(20),");
				stb_CREATE.append("    applicantdocument character varying(150),");
				stb_CREATE.append("    applicantname character varying(150),");
				stb_CREATE.append("    publicservantdocument character varying(150),");
				stb_CREATE.append("    publicservantname character varying(150),");
				stb_CREATE.append("    publicservantorganization character varying(150),");
				stb_CREATE.append("    expedient character varying(150),");
				stb_CREATE.append("    CONSTRAINT gdibauditoria_pkey PRIMARY KEY (id)");
				stb_CREATE.append("    )");
				stb_CREATE.append("WITH (");
				stb_CREATE.append("    OIDS=FALSE");
				stb_CREATE.append("    )");

				stb_CREATE.append("CREATE TABLE gdibauditoria_error");
				stb_CREATE.append("    (");
				stb_CREATE.append("    id serial NOT NULL,");
				stb_CREATE.append("    code character varying(15),");
				stb_CREATE.append("    message character varying(300),");
				stb_CREATE.append("    username character varying(50),");
				stb_CREATE.append("    application character varying(100),");
				stb_CREATE.append("    operationtype character varying(15),");
				stb_CREATE.append("    operation character varying(50),");
				stb_CREATE.append("    esboperation character varying(50),");
				stb_CREATE.append("    executiondate date,");
				stb_CREATE.append("    executiontime character varying(10),");
				stb_CREATE.append("    authenticationtype character varying(10),");
				stb_CREATE.append("    ip character varying(20),");
				stb_CREATE.append("    mac character varying(20),");
				stb_CREATE.append("    applicantdocument character varying(150),");
				stb_CREATE.append("    applicantname character varying(150),");
				stb_CREATE.append("    publicservantdocument character varying(150),");
				stb_CREATE.append("    publicservantname character varying(150),");
				stb_CREATE.append("    publicservantorganization character varying(150),");
				stb_CREATE.append("    expedient character varying(150),");
				stb_CREATE.append("    CONSTRAINT gdibauditoria_error_pkey PRIMARY KEY (id)");
				stb_CREATE.append("    )");
				stb_CREATE.append("WITH (");
				stb_CREATE.append("    OIDS=FALSE");
				stb_CREATE.append("    )");

				st = conn.createStatement();

				st.execute(stb_CREATE.toString());

				conn.commit();

			} else{
				throw  new AuditDataBaseException("La conexion a la base de datos se ha generado nula.");
			}

		} catch (SQLException e) {
			LOGGER.error("Ha fallado la conexion con la bddd de auditoria.");
			throw  new AuditDataBaseException(e);
		} finally {
			try {
				st.close();
				conn.close();
			} catch (SQLException e) {
				LOGGER.error("No se podido cerrar la conexion a base de datos.");
				throw  new AuditDataBaseException(e);
			}
		}
	}

	private Connection getDBConnection() throws AuditDataBaseException{
		Connection conn = null;

		Properties props = new Properties();
		props.setProperty("user", getDb_audit_username());
		props.setProperty("password", getDb_audit_password());


		try {
			Class.forName(getDb_audit_driver());
			conn = DriverManager.getConnection(getDb_audit_url(), props);
			conn.setAutoCommit(false);

		} catch (ClassNotFoundException e) {
			LOGGER.error("DataBase Driver no encontrado en el Class Path del servidor para la conexion con la base de datos de auuditoria.");
			throw  new AuditDataBaseException(e);
		} catch (SQLException e) {
			LOGGER.error("Ha fallado la conexion con la bddd de auditoria.");
			throw  new AuditDataBaseException(e);
		}

		return conn;
	}

	public String getDb_audit_url() {
		return db_audit_url;
	}

	public void setDb_audit_url(String db_audit_url) {
		this.db_audit_url = db_audit_url;
	}

	public String getDb_audit_username() {
		return db_audit_username;
	}

	public void setDb_audit_username(String db_audit_username) {
		this.db_audit_username = db_audit_username;
	}

	public String getDb_audit_password() {
		return db_audit_password;
	}

	public void setDb_audit_password(String db_audit_password) {
		this.db_audit_password = db_audit_password;
	}

	public String getDb_audit_driver() {
		return db_audit_driver;
	}

	public void setDb_audit_driver(String db_audit_driver) {
		this.db_audit_driver = db_audit_driver;
	}

}
