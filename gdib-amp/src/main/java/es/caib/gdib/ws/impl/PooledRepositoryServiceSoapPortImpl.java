package es.caib.gdib.ws.impl;

import java.util.List;

import javax.activation.DataHandler;
import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.MigrationInfo;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.NodeVersion;
import es.caib.gdib.ws.common.types.SearchResults;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;

@WebService(serviceName = "RepositoryService", portName = "GdibRepositoryServiceSoapPort",
targetNamespace = "http://www.caib.es/gdib/repository/ws",
endpointInterface = "es.caib.gdib.ws.iface.RepositoryServiceSoapPort")
public class PooledRepositoryServiceSoapPortImpl extends SpringBeanAutowiringSupport implements RepositoryServiceSoapPort {
	@Autowired
    private WebApplicationContext context;

	@Override
	public String createNode(Node node, String parentId, GdibHeader gdibHeader) throws GdibException {
		return getBean().createNode(node, parentId, gdibHeader);
	}

	@Override
	public Node createAndGetNode(Node node, String parentId, GdibHeader gdibHeader) throws Exception {		
		return getBean().createAndGetNode(node, parentId, gdibHeader);
	}

	@Override
	public void modifyNode(Node node, GdibHeader gdibHeader) throws GdibException {
		getBean().modifyNode(node, gdibHeader);
	}

	@Override
	public Node getNode(String nodeId, boolean withContent, boolean withSign, GdibHeader gdibHeader)
			throws GdibException {		
		return getBean().getNode(nodeId, withContent, withSign, gdibHeader);		
	}

	@Override
	public void moveNode(String nodeId, String newParent, GdibHeader gdibHeader) throws GdibException {
		getBean().moveNode(nodeId, newParent, gdibHeader);
	}

	@Override
	public SearchResults searchNode(String luceneSearch, int pagina, GdibHeader gdibHeader) throws GdibException {
		return getBean().searchNode(luceneSearch, pagina, gdibHeader);
	}

	@Override
	public void removeNode(String nodeId, GdibHeader gdibHeader) throws GdibException {
		getBean().removeNode(nodeId, gdibHeader);
	}

	@Override
	public String linkNode(String parentId, String nodeId, String linkMode, GdibHeader gdibHeader)
			throws GdibException {
		return getBean().linkNode(parentId, nodeId, linkMode, gdibHeader);
	}

	@Override
	public DataHandler foliateNode(String nodeId, GdibHeader gdibHeader) throws GdibException {
		return getBean().foliateNode(nodeId, gdibHeader);
	}

	@Override
	public DataHandler exportNode(String nodeId, GdibHeader gdibHeader) throws GdibException {
		return getBean().exportNode(nodeId, gdibHeader);
	}

	@Override
	public List<NodeVersion> getNodeVersionList(String nodeId, GdibHeader gdibHeader) throws GdibException {
		return getBean().getNodeVersionList(nodeId, gdibHeader);
	}

	@Override
	public void authorizeNode(List<String> nodeIds, List<String> authorities, String permission, GdibHeader gdibHeader)
			throws GdibException {
		getBean().authorizeNode(nodeIds, authorities, permission, gdibHeader);
	}

	@Override
	public void removeAuthority(List<String> nodeIds, List<String> authorities, GdibHeader gdibHeader)
			throws GdibException {
		getBean().removeAuthority(nodeIds, authorities, gdibHeader);
	}

	@Override
	public void lockNode(String nodeId, GdibHeader gdibHeader) throws GdibException {
		getBean().lockNode(nodeId, gdibHeader);
	}

	@Override
	public void unlockNode(String nodeId, GdibHeader gdibHeader) throws GdibException {
		getBean().unlockNode(nodeId, gdibHeader);
	}

	@Override
	public String getTicket(GdibHeader gdibHeader) throws GdibException {
		return getBean().getTicket(gdibHeader);
	}

	@Override
	public MigrationInfo getMigrationInfo(String nodeId, GdibHeader gdibHeader) throws GdibException {
		return getBean().getMigrationInfo(nodeId, gdibHeader);
	}

	@Override
	public String getCSV(GdibHeader gdibHeader) throws GdibException {
		return getBean().getCSV(gdibHeader);
	}

	@Override
	public String openFile(String nodeId, GdibHeader gdibHeader) throws GdibException {
		return getBean().openFile(nodeId, gdibHeader);
	}

	@Override
	public void closeFile(String nodeId, GdibHeader gdibHeader) throws GdibException {
		getBean().closeFile(nodeId, gdibHeader);
	}
	
	private RepositoryServiceSoapPort getBean(){		
		return (RepositoryServiceSoapPort) context.getBean("AuthTransRepo");		
	}
	/**
	 * 
	 * transactionService.getRetryingTransactionHelper()
   .doInTransaction(
         new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {
            public Boolean execute()
                  throws Throwable {

               boolean success= method(var);
               if (!success){
                  // Retry and rollback if always problem
                  throw  new IllegalStateException("Problem during moveFilesAndAddMeta");
               }
               return true;
            }
         });
	 * 
	 * 
	 * 
	 * */
	
}
