package es.caib.gdib.ws.impl.authtrans;


import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.apache.axis.utils.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import es.caib.gdib.ws.common.types.DataNodeTransform;
import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.MigrationID;
import es.caib.gdib.ws.common.types.MigrationNode;
import es.caib.gdib.ws.common.types.header.GdibSecurity;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.MigrationServiceSoapPort;
import es.caib.gdib.ws.impl.MigrationServiceSoapPortImpl;

@Component(value="AuthTransMigr")
@Scope("request")
public class AuthTransMigrationServiceSoapPortImpl extends SpringBeanAutowiringSupport
		implements MigrationServiceSoapPort {
	
	private static final Logger LOGGER =  Logger.getLogger(AuthTransMigrationServiceSoapPortImpl.class);
	
	@Autowired
    private WebApplicationContext context;
	//@Autowired
		private RetryingTransactionHelper txnHelper;
		//@Autowired
	    private AuthenticationService authenticationService;

	@Override
	public MigrationNode getMigrationNode(final MigrationID migrationId, final boolean withContent, final boolean withSign,
		final boolean withMigrationSign, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<MigrationNode> callback = new RetryingTransactionCallback<MigrationNode>()
        {
           public MigrationNode execute() throws Throwable
           {                            
        	   return getBean().getMigrationNode(migrationId, withContent, withSign, withMigrationSign, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           MigrationNode ret = txnHelper.doInTransaction(callback);
           LOGGER.info("getMigrationNode("+migrationId.getAppId()+"/"+migrationId.getExternalId()+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        }
        catch (Exception e)
        {
        	if ( e.getCause() instanceof GdibException ){
        		throw (GdibException) e.getCause();
        	}
           throw e;
        }finally{
        	
        }		
	}

	@Override
	public String transformNode(final DataNodeTransform datanodetransform, final String fileNumber, final GdibHeader gdibHeader)
			throws GdibException {
			long initMill = System.currentTimeMillis();
	        RetryingTransactionCallback<String> callback = new RetryingTransactionCallback<String>()
	        {
	           public String execute() throws Throwable
	           {                            
	        	   return getBean().transformNode(datanodetransform, fileNumber, gdibHeader);
	           }
	        };        
	        try
	        {
	           doAuthentication(gdibHeader);
	           String ret = txnHelper.doInTransaction(callback);
	           LOGGER.info("getMigrationNode("+datanodetransform.getMigrationId().getAppId()+"/"+datanodetransform.getMigrationId().getExternalId()+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
	           return ret;
	        }
	        catch (Exception e)
	        {
	        	if ( e.getCause() instanceof GdibException ){
	        		throw (GdibException) e.getCause();
	        	}
	           throw e;
	        }finally{
	        	
	        }		
	}
	
	private MigrationServiceSoapPortImpl getBean(){
		return (MigrationServiceSoapPortImpl) context.getBean("migrationServiceSoap");
	}
	
	private void doAuthentication(GdibHeader gdibHeader) throws GdibException{
		GdibSecurity security = gdibHeader.getGdibSecurity();
		doAuthentication(security.getUser(),security.getPassword());
	}
	private void doAuthentication(String username, String password) throws GdibException {
		try {
			// si el usuario viene vacio, se tiene que validar el ticket de autenticacion de alfresco
			if(StringUtils.isEmpty(username)){
				if(password==null)
					throw new GdibException("You need authentication to perfom this operation");
				authenticationService.validate(password);
			}else{
				// y sino realizar la autenticacion normal de usuario y password
				if(StringUtils.isEmpty(password)){
					throw new GdibException("Username and Password are mandatory");
				}
				// login con usuario y password
				authenticationService.authenticate(username, password.toCharArray());
			}
		}catch (AuthenticationException ae){
			throw new GdibException(ae.getMessage());
		}
	}

	public void setContext(WebApplicationContext context) {
		this.context = context;
	}

	public void setTxnHelper(RetryingTransactionHelper txnHelper) {
		this.txnHelper = txnHelper;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	
}
