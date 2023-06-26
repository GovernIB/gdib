package es.caib.invoices.ws;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.invoices.commons.InvoicesConstants;
import es.caib.invoices.dto.InvoicesRequestDto;
import es.caib.invoices.validator.ObjectValidatorImpl;

public class InvoicesWebscript extends DeclarativeWebScript{
	
	private static Logger logger = LoggerFactory.getLogger(InvoicesWebscript.class);
	
	private NodeService nodeService;
	private SearchService searchService;
	private ObjectMapper mapper;
	private ObjectValidatorImpl validator;

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		
		int code = 200;
		String message = null;
		InvoicesRequestDto dto = null;
		
		try {
			String bodyStr = req.getContent().getContent();
			logger.debug("body: {}", bodyStr);
			//Convertimos el cuerpo a objeto
			dto = mapper.readValue(bodyStr, InvoicesRequestDto.class);
			logger.debug("DTO created: {}", dto.toString());
			//Validamos el objeto: si falla lanza ValidationException
			validator.validateObject(dto);
			logger.debug("DTO validated");
		} catch (IOException | NullPointerException e) {
			logger.error("Fallo al procesar el cuerpo de la petición: {}", e.getMessage(), e);
			message = "Error en el cuerpo de la petición: " + e.getMessage();
			code = 400;;
		} catch (ValidationException e) {
			logger.error("Fallo de validación de parámetros: {}", e.getMessage());
			message = "Fallos de validación: " + e.getMessage();
			code = 400;
		}

		if(code != 200) {
			status.setCode(code, message);
			return null;
		} else {
			String objectid = dto.getArcDocId();
			logger.info("Buscando nodo con sapid: {}", objectid);
			NodeRef nodeRef = findNode(objectid);
			if(nodeRef == null) {
				logger.info("Nodo con connexasArchivelink:sapid:{} no encontrado", objectid);
				code = 404;
				message= "No se ha encontrado contenido con sapid: " + objectid;
				status.setCode(code, message);
				return null;
			}
			logger.info("NodeRef found: {}", nodeRef.getId());
			Map<QName,Serializable> properties = formatProperties(dto);
			logger.info("Adding properties to the node");
			addProperties(nodeRef, properties);
			logger.info("Properties set - HttpStatus OK");
			status.setCode(code, "OK");
		}
			
		return null;
	}
	
    private Map<QName,Serializable> formatProperties(InvoicesRequestDto dto){
    	HashMap<QName,Serializable> ret = new HashMap<QName,Serializable>();
    	ret.put(InvoicesConstants.QInvoiceNumber,dto.getInvoiceNumber());
    	ret.put(InvoicesConstants.QInvoiceTotal, dto.getInvoiceTotal());
    	ret.put(InvoicesConstants.QDir3, dto.getDir3());
    	ret.put(InvoicesConstants.QIssueDate, dto.getIssueDate());
    	ret.put(InvoicesConstants.QProviderId, dto.getProviderId());
    	ret.put(InvoicesConstants.QProviderName, dto.getProviderName());
    	ret.put(InvoicesConstants.QSerialNumber, dto.getSerialNumber());
    	ret.put(InvoicesConstants.QDocid, dto.getDocid());
    	ret.put(InvoicesConstants.QArchivid, dto.getArchivid());
    	return ret;
    }
	
	private NodeRef findNode(String id) {
		String query = "=connexasArchivelink:sapid:" +id + " AND TYPE:\"cm:content\"";
		logger.debug("Query: " + query);
	    ResultSet results = null;
	    NodeRef ret = null;
	    try {	      
	    	results = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_FTS_ALFRESCO, query);
	    	if ( results != null && results.length() > 0  && results.getChildAssocRef(0) != null)
	    		ret = results.getChildAssocRef(0).getChildRef();
		} finally {
		      if (results != null) { results.close(); }
		}
	    return ret;
	}

	private void addProperties(NodeRef id, Map<QName, Serializable> properties) {
		if ( ! nodeService.hasAspect(id,InvoicesConstants.QASPECTINVOICE)){
			nodeService.addAspect(id, InvoicesConstants.QASPECTINVOICE, properties);
		}else{
			nodeService.addProperties(id, properties);
		}
	}
	

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public void setValidator(ObjectValidatorImpl validator) {
		this.validator = validator;
	}


}
