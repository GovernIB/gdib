package es.caib.invoices.ws.alfresco;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;


public class WSAlfrescoUtilsImpl implements WSAlfrescoUtils {

	private final QName QASPECTINVOICE = QName.createQName("{http://www.caib.es/invoices/model/content/1.0}invoice");
	
	private String findPrefix;	
	private String findId;	
	private NodeService nodeService;
	private SearchService searchService;
	
	public void setFindPrefix(String findPrefix) {
		this.findPrefix = findPrefix;
	}

	public void setFindId(String findId) {
		this.findId = findId;
	}

	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}	
	
	@Override
	public NodeRef findNode(String id) {
		String strQuery="+@"+findPrefix+"\\:"+findId+":\""+id+"\" AND +TYPE:\"cm:content\"";
		System.out.println("Query: " + strQuery);
	    ResultSet results = null;
	    NodeRef ret = null;
	    try {	      
	    	results = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_LUCENE, strQuery);
	    	if ( results != null && results.length() > 0  && results.getChildAssocRef(0) != null)
	    		ret = results.getChildAssocRef(0).getChildRef();
	    } finally {
		      if (results != null) { results.close(); }
		}
	    return ret;
	}

	@Override
	public void addProperties(NodeRef id, Map<QName, Serializable> properties) {
		if ( ! nodeService.hasAspect(id,QASPECTINVOICE)){
			nodeService.addAspect(id, QASPECTINVOICE, properties);
		}else{
			nodeService.addProperties(id, properties);
		}
	}

}
