package es.caib.gdib.ws.impl.authtrans;

import java.util.Arrays;
import java.util.List;

import javax.activation.DataHandler;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.security.AuthenticationService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.MigrationInfo;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.NodeVersion;
import es.caib.gdib.ws.common.types.SearchResults;
import es.caib.gdib.ws.common.types.header.GdibSecurity;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;
import es.caib.gdib.ws.impl.RepositoryServiceSoapPortImpl;

@Component(value="AuthTransRepo")
@Scope("request")
public class AuthTransRepositoryServiceSoapPortImpl extends SpringBeanAutowiringSupport implements RepositoryServiceSoapPort {
	
	private static final Logger LOGGER =  Logger.getLogger(AuthTransRepositoryServiceSoapPortImpl.class);
	
	@Autowired
    private WebApplicationContext context;
	//@Autowired
	private RetryingTransactionHelper txnHelper;
	//@Autowired
    private AuthenticationService authenticationService;
	
    private Integer reintent = 0;
	@Override
	public String createNode(final Node node, final String parentId, final GdibHeader gdibHeader) throws GdibException {
    	long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<String> callback = new RetryingTransactionCallback<String>()
        {
           public String execute() throws Throwable
           {
        	   reintent++;
        	   return getBean().createNode(node, parentId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           List<Class<?>> extraExceptions = Arrays.asList(org.alfresco.service.cmr.model.FileExistsException.class);
		   txnHelper.setExtraExceptions(extraExceptions);
		   txnHelper.setMaxRetries(10);
		   txnHelper.setMinRetryWaitMs(1000);
           String ret = txnHelper.doInTransaction(callback);
           LOGGER.info("createNode("+ret+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent create node nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
	}

	@Override
	public Node createAndGetNode(final Node node, final String parentId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Node> callback = new RetryingTransactionCallback<Node>()
        {
           public Node execute() throws Throwable
           {                     
        	   reintent++;
        	   return getBean().createAndGetNode(node, parentId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           List<Class<?>> extraExceptions = Arrays.asList(org.alfresco.service.cmr.model.FileExistsException.class);
		   txnHelper.setExtraExceptions(extraExceptions);
		   txnHelper.setMaxRetries(10);
		   txnHelper.setMinRetryWaitMs(1000);
           Node ret = txnHelper.doInTransaction(callback);
           LOGGER.info("createAndGetNode("+ret+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent createAndGetNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
	}

	@Override
	public void modifyNode(final Node node, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {
        	   reintent++;
        	   getBean().modifyNode(node, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("modifyNode securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent modifyNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public Node getNode(final String nodeId, final boolean withContent, final boolean withSign, final GdibHeader gdibHeader)
			throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Node> callback = new RetryingTransactionCallback<Node>()
        {
           public Node execute() throws Throwable
           {                          
        	   reintent++;
        	   return getBean().getNode(nodeId, withContent, withSign, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           Node ret = txnHelper.doInTransaction(callback);
           LOGGER.info("getNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent getNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public void moveNode(final String nodeId, final String newParent, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {              
        	   reintent++;
        	   getBean().moveNode(nodeId, newParent, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("moveNode("+nodeId+" => "+newParent+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent moveNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public SearchResults searchNode(final String luceneSearch, final int pagina, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<SearchResults> callback = new RetryingTransactionCallback<SearchResults>()
        {
           public SearchResults execute() throws Throwable
           {             
        	   reintent++;
        	   return getBean().searchNode(luceneSearch, pagina, gdibHeader);        	   
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           SearchResults res = txnHelper.doInTransaction(callback);
           LOGGER.info("searchNode securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return res;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent searchNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public void removeNode(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {                 
        	   reintent++;
        	   getBean().removeNode(nodeId, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("removeNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent removeNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public String linkNode(final String parentId, final String nodeId, final String linkMode, final GdibHeader gdibHeader)
			throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<String> callback = new RetryingTransactionCallback<String>()
        {
           public String execute() throws Throwable
           {     
        	   reintent++;
        	   return getBean().linkNode(parentId, nodeId, linkMode, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           String ret = txnHelper.doInTransaction(callback);
           LOGGER.info("linkNode("+ret+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent linkNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public DataHandler foliateNode(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<DataHandler> callback = new RetryingTransactionCallback<DataHandler>()
        {
           public DataHandler execute() throws Throwable
           {        
        	   reintent++;
        	   return getBean().foliateNode(nodeId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           DataHandler ret = txnHelper.doInTransaction(callback);
           LOGGER.info("foliateNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent foliateNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public DataHandler exportNode(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<DataHandler> callback = new RetryingTransactionCallback<DataHandler>()
        {
           public DataHandler execute() throws Throwable
           {                            
        	   reintent++;
        	   return getBean().exportNode(nodeId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           DataHandler ret = txnHelper.doInTransaction(callback);
           LOGGER.info("exportNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent exportNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public List<NodeVersion> getNodeVersionList(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<List<NodeVersion>> callback = new RetryingTransactionCallback<List<NodeVersion>>()
        {
           public List<NodeVersion> execute() throws Throwable
           {             
        	   reintent++;
        	   return getBean().getNodeVersionList(nodeId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           List<NodeVersion> ret = txnHelper.doInTransaction(callback);
           LOGGER.info("getNodeVersionList("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent getNodeVersionList nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public void authorizeNode(final List<String> nodeIds, final List<String> authorities, final String permission, final GdibHeader gdibHeader)
			throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {                      
        	   reintent++;
        	   getBean().authorizeNode(nodeIds, authorities, permission, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("authorizeNode("+nodeIds.toString()+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent autorizeNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public void removeAuthority(final List<String> nodeIds, final List<String> authorities, final GdibHeader gdibHeader)
			throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {     
        	   reintent++;
        	   getBean().removeAuthority(nodeIds, authorities, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("removeAuthority("+nodeIds.toString()+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent removeAuthority nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public void lockNode(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {           
        	   reintent++;
        	   getBean().lockNode(nodeId, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("lockNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent lockNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public void unlockNode(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {                    
        	   reintent++;
        	   getBean().unlockNode(nodeId, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("unlockNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent unlockNode nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public String getTicket(GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
		try {
			doAuthentication(gdibHeader);
			String ret = getBean().getTicket(gdibHeader);
			LOGGER.info("getTicket securizado ejecutado en: "+ (System.currentTimeMillis()-initMill)+ " ms." );
			return ret;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
	}

	@Override
	public MigrationInfo getMigrationInfo(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<MigrationInfo> callback = new RetryingTransactionCallback<MigrationInfo>()
        {
           public MigrationInfo execute() throws Throwable
           {               
        	   reintent++;
        	   return getBean().getMigrationInfo(nodeId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           MigrationInfo ret = txnHelper.doInTransaction(callback);
           LOGGER.info("getMigrationInfo("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent getMigrationInfo nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public String getCSV(GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
		try {
			doAuthentication(gdibHeader);
			String ret = getBean().getCSV(gdibHeader);
			LOGGER.info("getCSV securizado ejecutado en: "+ (System.currentTimeMillis()-initMill)+ " ms." );
			if (ret == null || "".equals(ret))
				throw new GdibException("El csv es null");
			return ret;		
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
	}

	@Override
	public String openFile(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<String> callback = new RetryingTransactionCallback<String>()
        {
           public String execute() throws Throwable
           {                          
        	   reintent++;
        	   return getBean().openFile(nodeId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           String ret = txnHelper.doInTransaction(callback);
           LOGGER.info("openFile("+ret+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent openfile nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
    }

	@Override
	public void closeFile(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {                       
        	   reintent++;
        	   getBean().closeFile(nodeId, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           List<Class<?>> extraExceptions = Arrays.asList(GdibException.class);
		   txnHelper.setExtraExceptions(extraExceptions);
		   txnHelper.setMaxRetries(10);
		   txnHelper.setMinRetryWaitMs(1000);
           txnHelper.doInTransaction(callback);
           LOGGER.info("closeFile("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
        	LOGGER.error(String.format("reintent closeFile nº: %s", reintent));
        	//LOGGER.error(e);
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
        }
	}
	
	public RepositoryServiceSoapPortImpl getBean(){
		//return (RepositoryServiceSoapPortImpl) context.getBean("RepositoryServiceSoapPortImpl");
		return (RepositoryServiceSoapPortImpl) context.getBean("repositoryServiceSoap");		
	}
	
	private void doAuthentication(GdibHeader gdibHeader) throws GdibException{
	    try {
			GdibSecurity security = gdibHeader.getGdibSecurity();
			doAuthentication(security.getUser(),security.getPassword());
        } catch (GdibException ge) {
            LOGGER.error("Se ha producido la Excepcion: " + ge.getMessage(), ge);
            throw ge;
        } catch (Exception e) {
            LOGGER.error("Se ha producido la excepcion generica: " + e.getMessage(), e);
            throw new GdibException(e.getMessage());
		}
	}

	public void doAuthentication(String username, String password) throws GdibException {
		try {
			// si el usuario viene vacio, se tiene que validar el ticket de autenticacion de alfresco
			if(username.isEmpty()){
				if(password==null)
					throw new GdibException("You need authentication to perfom this operation");
				authenticationService.validate(password);
			}else{
				// y sino realizar la autenticacion normal de usuario y password
				if(password.isEmpty()){
					throw new GdibException("Username and Password are mandatory");
				}
				// login con usuario y password
				authenticationService.authenticate(username, password.toCharArray());
				LOGGER.debug("Autenticacion realizada");
			}
		}catch (AuthenticationException ae){
		    LOGGER.error("Error logueando: " + ae.getMessage(), ae);
			throw new GdibException(ae.getMessage());
		} catch (Exception e) {
		    LOGGER.error("Error generico logueando: " + e.getMessage(), e);
		    throw new GdibException(e.getMessage());
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
