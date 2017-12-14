package es.caib.invoices.ws;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import es.caib.invoices.ws.alfresco.WSAlfrescoUtils;
import es.rsits.ws.auth.WSAuthentication;
import es.rsits.ws.exception.WSException;

/**
 * Implementacion de la interfaz {@link CaibWebServiceSoapPort}
 *
 * @author <a href="mailto:luis.fernandezprado@ricoh.es">Luis Fernandez Prado (LFP)</a>
 *
 */


@WebService(serviceName = "CaibService", portName = "CaibServiceSoapPort",
	targetNamespace = "http://www.ricoh.es/ws/caib",
	endpointInterface = "es.caib.invoices.ws.CaibWebServiceSoapPort")
public class CaibWebServiceSoapPortImpl extends SpringBeanAutowiringSupport implements CaibWebServiceSoapPort {
	
	private final String INVOICENUMBER = "invoiceNumber";
	private final String PROVIDERID = "providerId";
	private final String PROVIDERNAME = "providerName";
	private final String SERIALNUMBER = "serialNumber";
	private final String ISSUEDATE = "issueDate";
	private final String INVOICETOTAL = "invoiceTotal";
	private final String DIR3 = "dir3";
	private final String DOCID = "docid";
	private final String ARCHIV_ID = "archiv_id";
	
	private final String QNAME_URI = "http://www.caib.es/invoices/model/content/1.0";
	
	private final QName QInvoiceNumber = QName.createQNameWithValidLocalName(QNAME_URI, INVOICENUMBER);
	private final QName QProviderId = QName.createQNameWithValidLocalName(QNAME_URI, PROVIDERID);
	private final QName QProviderName = QName.createQNameWithValidLocalName(QNAME_URI, PROVIDERNAME);
	private final QName QSerialNumber = QName.createQNameWithValidLocalName(QNAME_URI, SERIALNUMBER);
	private final QName QIssueDate = QName.createQNameWithValidLocalName(QNAME_URI, ISSUEDATE);
	private final QName QInvoiceTotal = QName.createQNameWithValidLocalName(QNAME_URI, INVOICETOTAL);
	private final QName QDir3 = QName.createQNameWithValidLocalName(QNAME_URI, DIR3);
	private final QName QDocid = QName.createQName(QNAME_URI, DOCID);
	private final QName QArchivid = QName.createQName(QNAME_URI, ARCHIV_ID);
			

    @Resource
    private WebServiceContext wsctx;
    
    @Autowired
    private WSAlfrescoUtils wSAlfrescoUtils;

    @Override
    public UpdateMetadataResponse updateMetadata(UpdateMetadataRequest parameters) throws WSException{
		// con la ayuda del servicio 'wsAutehntication' se comprueba el usuario y password que llegan en la peticion soap
		// si hay algun error, se lanza una excepcion
    	try{
		String objectid = parameters.getArcDocId();
		NodeRef nodeRef = wSAlfrescoUtils.findNode(objectid);
		if ( nodeRef == null )
			throw new WSException("No se ha encontrado ningún nodo para el identificador "+objectid);
		Map<QName,Serializable> properties = formatProperties(parameters);	
		wSAlfrescoUtils.addProperties(nodeRef, properties );	
			return new UpdateMetadataResponse(1);
    	}catch(Exception excpt){
    		return new UpdateMetadataResponse(0);
    	}
    }  
    
    private Map<QName,Serializable> formatProperties(UpdateMetadataRequest metadatos){
    	HashMap<QName,Serializable> ret = new HashMap<QName,Serializable>();
    	ret.put(QInvoiceNumber,metadatos.getInvoiceNumber());
    	ret.put(QInvoiceTotal, metadatos.getInvoiceTotal());
    	ret.put(QDir3, metadatos.getDir3());
    	ret.put(QIssueDate, metadatos.getIssueDate());
    	ret.put(QProviderId, metadatos.getProviderId());
    	ret.put(QProviderName, metadatos.getProviderName());
    	ret.put(QSerialNumber, metadatos.getSerialNumber());
    	ret.put(QDocid, metadatos.getDocid());
    	ret.put(QArchivid, metadatos.getArchivid());
    	return ret;
    }
    public WebServiceContext getWsctx() {
	return wsctx;
    }

    public void setWsctx(WebServiceContext wsctx) {
	this.wsctx = wsctx;
    }
}
