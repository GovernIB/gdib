package es.caib.gdib.schedulejobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.ws.exception.GdibException;

/**
 * Esta clase se encarga de realziar la logica de negocio del cronjob.
 *
 * En el fichero "procesos.properties" esta la configuracion que se utiliza en
 * este job
 *
 * Solo se ejecuta si la propertie "migr.expire.active" esta activa
 *
 * Compruebo una lista de tipos de documento (migr.expire.type_doc) que tengan
 * una propiedad (migr.expire.attrib) sea menor o igual a la fecha actual y le
 * pongo el valor(migr.expire.attribvalue) a otra propiedad
 * (migr.expire.changeattrib)
 *
 * @author Ricoh
 *
 */
public class ExpiredDocuments {

	private static final Logger LOGGER = Logger.getLogger(ExpiredDocuments.class);

	public static final String STRING_SPLIT = ",";

	private NodeService nodeService;
	private SearchService searchService;
	private DictionaryService dictionaryService;

	private String typeDoc;
	private String attrib;
	private String changeAttrib;
	private String attribValue;
	private String luceneQuery;
	private boolean active;

	/**
	 * Executer implementation
	 *
	 * @throws IOException
	 */
	public void execute() {
		// compruebo si el job esta activo, por la property "migr.expire.active"
		if (active) {
			LOGGER.info("Lanzando el cronjob - Expired Documents Job");
			try {
				run();
			} catch (GdibException e) {
				LOGGER.error("Error durante el proceso de expurgo de documentos. " + e.getMessage());
				// TODO se hace algo si falla el cronjob??
			}
			LOGGER.info("El cronjob ha finalizado");
		} else {
			LOGGER.info("El cronjob no esta activo");
		}
	}

	private void run() throws GdibException{
		List<NodeRef> nodes = null;

		// realizo un split de la lista de tipos de documento que se van a modificar
		String[] typeDocArray = typeDoc.split(STRING_SPLIT);
		for (int i = 0; i < typeDocArray.length; i++) {
			String[] attribArray = attrib.split(STRING_SPLIT);
			String[] changeAttribArray = changeAttrib.split(STRING_SPLIT);
			String[] attribValueArray = attribValue.split(STRING_SPLIT);

			// realizo la busqueda
			LOGGER.debug("Realizando la busqueda de documentos");
			nodes = searchDocsToCalculateExpire(typeDocArray[i].trim(), attribArray[i].trim(), changeAttribArray[i].trim(),
					attribValueArray[i].trim());

			// verificar que se haya encontrado algun nodo a modificar
			if (!CollectionUtils.isEmpty(nodes)) {
				for (NodeRef node : nodes) {
					LOGGER.debug("Modificando el nodo con Id[" + node.getId() + "]");
					try {
						// por cada nodo actualizo la propiedad con el valor que viene en el propoerties
						updateAttribute(node, changeAttribArray[i].trim(), attribValueArray[i].trim());
						LOGGER.info("Nodo[" + node.getId() + "] - modificada la propiedad[" + changeAttribArray[i].trim()
								+ "] - con el valor[" + attribValueArray[i].trim() + "]");
					} catch (GdibException edjEx) {
						LOGGER.error("No se ha podido actualizar la propiedad el nodo. " + edjEx.getMessage(),
								edjEx);
						throw new GdibException("No se ha podido actualizar la propiedad el nodo. " + edjEx.getMessage(),
								edjEx);
					}
				}
			}else{
				LOGGER.info("No se ha encontrado ningun documento del tipo["+typeDocArray[i].trim()+"]."
						+ " Atributo de consulta["+attribArray[i].trim()+"]."
						+ " Atributo de modificar["+changeAttribArray[i].trim()+"] y valor["+attribValueArray[i].trim()+"]");
			}
		}
	}

	/**
	 * Actualizo una propiedad de un nodo, que propiedad y el valor estaran
	 * identificadas en el fichero properties
	 *
	 * @param nodeRef
	 *            nodo a modificar
	 * @param changeAttrib
	 *            propiedad del nodo a modificar
	 * @param attribValue
	 *            valor con el que modificar la propiedad
	 * @throws ExpiredDocumentJobException
	 *             si ocurre un error al parsear la propiedad que esta en el
	 *             fichero properties
	 * @throws GdibException
	 */
	private void updateAttribute(NodeRef nodeRef, String changeAttrib, String attribValue) throws GdibException{
		QName attribu = GdibUtils.createQName(changeAttrib);
		if(dictionaryService.getProperty(attribu) != null){
			nodeService.setProperty(nodeRef, attribu, attribValue);
		}else{
			throw new GdibException("El atributo [" +attribu.toString()+ "] no existe en el modelo.");
		}
	}

	/**
	 * Realiza una busqueda de nodos con lucene
	 *
	 * @param typeDoc
	 *            tipos de documentos a filtrar
	 * @param attrib
	 *            atributo por el que voy a filtrar, tipo fecha
	 * @param changeAttrib
	 *            atributo por el que voy a filtar, tipo string
	 * @param attribValue
	 *            valor con el que voy a comparar el parametro anterior
	 * @return lista denodos
	 */
	@SuppressWarnings("resource")
	private List<NodeRef> searchDocsToCalculateExpire(final String typeDoc, final String attrib,
			final String changeAttrib, final String attribValue) {
		// construyo la query
		final StringBuilder query = new StringBuilder(250);
		Formatter formatter = new Formatter(query);
		formatter.format(luceneQuery, typeDoc, attrib.split(":")[0], attrib.split(":")[1], changeAttrib.split(":")[0], changeAttrib.split(":")[1], attribValue).toString();
		query.trimToSize();
		LOGGER.debug("Query: " + query);

		// incluyo los parametros de la busqueda
		final SearchParameters params = new SearchParameters();
		params.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		params.setLanguage(SearchService.LANGUAGE_LUCENE);
		params.setQuery(query.toString());

		List<NodeRef> nodeList = new ArrayList<NodeRef>();
		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(params);
			if (resultSet != null && resultSet.length() != 0) {
				nodeList = resultSet.getNodeRefs();
				LOGGER.debug(resultSet.length() + " results found");
			}
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return nodeList;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setTypeDoc(String typeDoc) {
		this.typeDoc = typeDoc;
	}

	public void setAttrib(String attrib) {
		this.attrib = attrib;
	}

	public void setChangeAttrib(String changeAttrib) {
		this.changeAttrib = changeAttrib;
	}

	public void setAttribValue(String attribValue) {
		this.attribValue = attribValue;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setLuceneQuery(String luceneQuery) {
		this.luceneQuery = luceneQuery;
	}

}
