package es.caib.gdib.ws.impl.authtrans;

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

import es.caib.gdib.ws.common.types.CertSearchResults;
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
	
	@Override
	public String createNode(final Node node, final String parentId, final GdibHeader gdibHeader) throws GdibException {
    	long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<String> callback = new RetryingTransactionCallback<String>()
        {
           public String execute() throws Throwable
           {                            
        	   return getBean().createNode(node, parentId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           String ret = txnHelper.doInTransaction(callback);
           LOGGER.info("createNode("+ret+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
	}

	@Override
	public Node createAndGetNode(final Node node, final String parentId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Node> callback = new RetryingTransactionCallback<Node>()
        {
           public Node execute() throws Throwable
           {                            
        	   return getBean().createAndGetNode(node, parentId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           Node ret = txnHelper.doInTransaction(callback);
           LOGGER.info("createAndGetNode("+ret+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
	}

	@Override
	public void modifyNode(final Node node, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {                            
        	   getBean().modifyNode(node, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("modifyNode securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
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
        	   return getBean().getNode(nodeId, withContent, withSign, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           Node ret = txnHelper.doInTransaction(callback);
           LOGGER.info("getNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public void moveNode(final String nodeId, final String newParent, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {                            
        	   getBean().moveNode(nodeId, newParent, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("moveNode("+nodeId+" => "+newParent+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public SearchResults searchNode(final String luceneSearch, final int pagina, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<SearchResults> callback = new RetryingTransactionCallback<SearchResults>()
        {
           public SearchResults execute() throws Throwable
           {                            
        	   return getBean().searchNode(luceneSearch, pagina, gdibHeader);        	   
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           SearchResults res = txnHelper.doInTransaction(callback);
           LOGGER.info("searchNode securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return res;
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public void removeNode(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {                            
        	   getBean().removeNode(nodeId, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("removeNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
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
        	   return getBean().linkNode(parentId, nodeId, linkMode, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           String ret = txnHelper.doInTransaction(callback);
           LOGGER.info("linkNode("+ret+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public DataHandler foliateNode(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<DataHandler> callback = new RetryingTransactionCallback<DataHandler>()
        {
           public DataHandler execute() throws Throwable
           {                            
        	   return getBean().foliateNode(nodeId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           DataHandler ret = txnHelper.doInTransaction(callback);
           LOGGER.info("foliateNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public DataHandler exportNode(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<DataHandler> callback = new RetryingTransactionCallback<DataHandler>()
        {
           public DataHandler execute() throws Throwable
           {                            
        	   return getBean().exportNode(nodeId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           DataHandler ret = txnHelper.doInTransaction(callback);
           LOGGER.info("exportNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public List<NodeVersion> getNodeVersionList(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<List<NodeVersion>> callback = new RetryingTransactionCallback<List<NodeVersion>>()
        {
           public List<NodeVersion> execute() throws Throwable
           {                            
        	   return getBean().getNodeVersionList(nodeId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           List<NodeVersion> ret = txnHelper.doInTransaction(callback);
           LOGGER.info("getNodeVersionList("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
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
        	   getBean().authorizeNode(nodeIds, authorities, permission, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("authorizeNode("+nodeIds.toString()+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
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
        	   getBean().removeAuthority(nodeIds, authorities, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("removeAuthority("+nodeIds.toString()+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public void lockNode(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {                            
        	   getBean().lockNode(nodeId, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("lockNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public void unlockNode(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {                            
        	   getBean().unlockNode(nodeId, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("unlockNode("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public String getTicket(GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
		doAuthentication(gdibHeader);
		String ret = getBean().getTicket(gdibHeader);
		LOGGER.info("getTicket securizado ejecutado en: "+ (System.currentTimeMillis()-initMill)+ " ms." );
		return ret;
	}

	@Override
	public MigrationInfo getMigrationInfo(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<MigrationInfo> callback = new RetryingTransactionCallback<MigrationInfo>()
        {
           public MigrationInfo execute() throws Throwable
           {                            
        	   return getBean().getMigrationInfo(nodeId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           MigrationInfo ret = txnHelper.doInTransaction(callback);
           LOGGER.info("getMigrationInfo("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public String getCSV(GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
		doAuthentication(gdibHeader);
		String ret = getBean().getCSV(gdibHeader);
		LOGGER.info("getCSV securizado ejecutado en: "+ (System.currentTimeMillis()-initMill)+ " ms." );
		return ret;		
	}

	@Override
	public String openFile(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<String> callback = new RetryingTransactionCallback<String>()
        {
           public String execute() throws Throwable
           {                            
        	   return getBean().openFile(nodeId, gdibHeader);
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           String ret = txnHelper.doInTransaction(callback);
           LOGGER.info("openFile("+ret+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
           return ret;
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }

	@Override
	public void closeFile(final String nodeId, final GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {                            
        	   getBean().closeFile(nodeId, gdibHeader);
        	   return null;
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           txnHelper.doInTransaction(callback);
           LOGGER.info("closeFile("+nodeId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }
    }
	@Override
	public CertSearchResults recountFilesByCert(String certId, GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
        RetryingTransactionCallback<CertSearchResults> callback = new RetryingTransactionCallback<CertSearchResults>()
        {
           public CertSearchResults execute() throws Throwable
           {                            
        	   return getBean().recountFilesByCert(certId, gdibHeader);
        	   
           }
        };        
        try
        {
           doAuthentication(gdibHeader);
           CertSearchResults res;
           res = txnHelper.doInTransaction(callback);
           LOGGER.info("Busqueda de certificados ("+certId+") securizado ejecutado en: "+(System.currentTimeMillis()-initMill)+" ms.");

           return res;
           
        } catch (Exception e) {
            if (e.getCause() instanceof GdibException) {
                GdibException ex = ((GdibException) e.getCause());
                LOGGER.error("Se ha producido la excepcion: " + ex.getMessage(), ex);
                throw (GdibException) e.getCause();
            }
            LOGGER.error("Se ha producido la excepcion: " + e.getMessage(), e);
            throw e;
        }	
    }
	
	public RepositoryServiceSoapPortImpl getBean(){
		//return (RepositoryServiceSoapPortImpl) context.getBean("RepositoryServiceSoapPortImpl");
		return (RepositoryServiceSoapPortImpl) context.getBean("repositoryServiceSoap");		
	}
	
	private void doAuthentication(GdibHeader gdibHeader) throws GdibException{
		GdibSecurity security = gdibHeader.getGdibSecurity();
		doAuthentication(security.getUser(),security.getPassword());
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
		    LOGGER.error("Error logueando: "+ae);
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
