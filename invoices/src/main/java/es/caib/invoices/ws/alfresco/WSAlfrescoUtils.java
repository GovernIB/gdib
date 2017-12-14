package es.caib.invoices.ws.alfresco;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public interface WSAlfrescoUtils {
	NodeRef findNode(String id);
	void addProperties(NodeRef id, Map<QName,Serializable> properties);
}
