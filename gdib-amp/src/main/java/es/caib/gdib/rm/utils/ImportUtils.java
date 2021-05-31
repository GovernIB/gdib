package es.caib.gdib.rm.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.ImporterContentCache;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.caib.gdib.rm.RMImportPackageHandler;
import es.caib.gdib.rm.RMImportPackageHandlerFactory;
import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.ws.common.types.Content;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.SearchResults;
import es.caib.gdib.ws.exception.GdibException;

public class ImportUtils {

	private static final Logger LOGGER = Logger.getLogger(ImportUtils.class);

	private NodeService nodeService;
	private ImporterService importerService;
	private SearchService searchService;

	private RMImportPackageHandlerFactory rmImportPackageHandlerFactory;

	private GdibUtils utils;
	private String destDir;

	public NodeRef importExpedient(NodeRef expediente) throws GdibException {
		LOGGER.debug("Comenzamos la importacion del expediente del RM al DM");
		return execute(expediente);
	}

	public NodeRef importExpedientWithTarget(NodeRef expediente, NodeRef target) throws GdibException {
		return execute(expediente, target);
	}

	public SearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	private NodeRef execute(NodeRef expediente) throws GdibException {
		return execute(expediente, null);
	}

	private NodeRef execute(NodeRef expediente, NodeRef target) throws GdibException {
		String expedientName = (String) nodeService.getProperty(expediente, ConstantUtils.PROP_NAME);

		LOGGER.debug("Obtenemos la localizacion donde se va a crear el expediente en el DM");
		// location donde importar(abrir expediente) el nodo en el DM
		NodeRef destRef = null;
		if (target == null)
			destRef = getLocationExpedientDM(expediente);
		else
			destRef = target;
//		Location location = new Location(destDir);

		Location location = new Location(destRef);

		LOGGER.debug("Configuro la importacion del expediente");
		RMImportPackageHandler importHandler = rmImportPackageHandlerFactory.getRMImportPackageHandler();
		importHandler.setRoot(expediente);

		LOGGER.debug("Realizo la importacion del expediente");
		importerService.importView(importHandler, location, CREATE_NEW, null);
		LOGGER.debug("Terminada importacion del expediente");

		NodeRef openExpedient = nodeService.getChildByName(destRef, ContentModel.ASSOC_CONTAINS, expedientName);
		LOGGER.debug("Obtengo el expediente nuevo creado. (" + openExpedient.getId() + ")");
		LOGGER.debug("Añado propiedad expediente original al nuevo expediente creado");
		if (target == null) {
			// TODO Trabajo de reopen File
			nodeService.setProperty(openExpedient, ConstantUtils.PROP_EXP_REAPERTURA_QNAME,
					expediente.getStoreRef() + "/" + expediente.getId());
			//
			Serializable expedientes = nodeService.getProperty(expediente, ConstantUtils.PROP_EXP_REABIERTO_QNAME);
			List<String> lint = new ArrayList<String>();
			if (expedientes != null) {
				if (expedientes instanceof List) {
					lint = (List<String>) expedientes;
				} else {
					lint.add(openExpedient.getStoreRef() + "/" + openExpedient.getId());
				}
			} else {
				lint.add(openExpedient.getStoreRef() + "/" + openExpedient.getId());
			}
			nodeService.setProperty(expediente, ConstantUtils.PROP_EXP_REABIERTO_QNAME, (Serializable) lint);
		}
		return openExpedient;
	}

	/**
	 * Recupero la ruta donde se debe de abrir el expediente en el DM. Este dato lo
	 * sacamos del xml descriptor de la exportacion de alfresco. Con esa ruta
	 * buscamos el nodo donde abrir el expediente
	 *
	 * @param expedient a reabrir el expediente del RM al DM
	 * @return nodo donde se va a reabrir el expediente
	 * @throws GdibException
	 */
	private NodeRef getLocationExpedientDM(NodeRef expedient) throws GdibException {
		NodeRef expedienteDM = null;
		try {
			// obtenemos el nodo que contiene el XML descriptor del contenido del expediente
			LOGGER.debug("Obtengo el xml descriptor con la estructura de carpetas del expediente");
			NodeRef xmlRef = getXMLDescriptorFromExpedient(expedient);
			if (xmlRef != null)
				LOGGER.debug(
						" NodeRef xmlRef = getXMLDescriptorFromExpedient(expedient); xmlRef = " + xmlRef.toString());
			else
				LOGGER.debug("xMlRef Is null");
			Content contentXmlDescriptor = utils.getContent(xmlRef, ConstantUtils.PROP_CONTENT);
			if (contentXmlDescriptor != null)
				LOGGER.debug(
						" Content contentXmlDescriptor = utils.getContent(xmlRef, ConstantUtils.PROP_CONTENT); contentXmlDescriptor= "
								+ contentXmlDescriptor.toString());
			else
				LOGGER.debug("contentXmlDescriptor is null ");

			// parseamos el xml
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			LOGGER.debug("calling factory new instance");

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			LOGGER.debug("calling documentBuilder from factory");

			Document doc = dBuilder.parse(contentXmlDescriptor.getData().getInputStream());
			LOGGER.debug("Doc = dbBuilder.parse");

			doc.getDocumentElement().normalize();
			LOGGER.debug("doc.getelement.normalize");
			String path = doc.getElementsByTagName("view:exportOf").item(0).getTextContent();
			LOGGER.debug("path= " + path);
			// buscamos el path dentro de alfresco
			LOGGER.debug("Busco la ruta donde se va a reabrir el expediente");
			expedienteDM = searchPath(path);
			if (expedienteDM != null)
				LOGGER.debug(expedienteDM.toString());
			else
				LOGGER.debug("expedientDM is null.");
		} catch (ParserConfigurationException e) {
			throw new GdibException(
					"Ha ocurrido un error leyendo el xml descriptor de la abertura del expediente del RM "
							+ expedient.getId() + ". " + e.getMessage());
		} catch (SAXException e) {
			throw new GdibException(
					"Ha ocurrido un error leyendo el xml descriptor de la abertura del expediente del RM "
							+ expedient.getId() + ". " + e.getMessage());
		} catch (IOException e) {
			throw new GdibException(
					"Ha ocurrido un error leyendo el xml descriptor de la abertura del expediente del RM "
							+ expedient.getId() + ". " + e.getMessage());
		}
		return expedienteDM;
	}

	/**
	 * Obtenemos el nodo que contiene el xml descriptor del expediente. Este fichero
	 * es un .xml con el nombre del expediente
	 *
	 * @param expedient donde buscar el xml descriptor
	 * @return nodo que contiene el xml descriptor
	 */
	private NodeRef getXMLDescriptorFromExpedient(NodeRef expedient) {
		String expedientName = (String) nodeService.getProperty(expedient, ConstantUtils.PROP_NAME);
		//LOGGER.debug(expedientName);

		List<ChildAssociationRef> listNodes = nodeService.getChildAssocs(expedient);
		//LOGGER.debug(listNodes.toString());
		for (ChildAssociationRef childAssoc : listNodes) {
			//if (childAssoc != null)
			//	LOGGER.debug(childAssoc.toString());
		//	else
			//	LOGGER.debug("node nul");
			NodeRef son = childAssoc.getChildRef();
		//	LOGGER.debug("son.toString=" + son.toString());
			String sonName = (String) nodeService.getProperty(son, ConstantUtils.PROP_NAME);
			//LOGGER.debug("SonName  = " + sonName);
		//	LOGGER.debug("trying sonName(" + sonName + " contains expedientName(" + expedientName + ")");
			if (sonName.contains(expedientName)) {
				return son;
			}
		}
		//LOGGER.debug("returned nul");
		return null;
	}

	/**
	 * Realizamos una busqueda lucene para buscar el nodo que corresponde con la
	 * ruta
	 *
	 *
	 * @param path ruta donde se debe reabrir el expediente
	 * @return nodo donde reabrir el expediente
	 * @throws GdibException si
	 */
	private NodeRef searchPath(String path) throws GdibException {
		// elimino la ultima parte del path, pues es el expediente en si
		path = path.substring(0, path.lastIndexOf("/"));
		String luceneSearch = String.format("PATH:\"/" + path + "\"");
		ArrayList<Node> resultado = new ArrayList<Node>();
		SearchParameters searchParameters = new SearchParameters();
		searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		searchParameters.setQuery(luceneSearch);

		ResultSet nodes = searchService.query(searchParameters);
		long numResultados = nodes.getNumberFound();
		if (numResultados > 1)
			throw new GdibException("Existe un mas de un nodo donde reabrir el expediente. Ruta (" + path + ")");
		if (numResultados < 1)
			throw new GdibException("No existe la ruta donde reabrir el expediente. Ruta (" + path + ")");

		return nodes.getNodeRef(0);
	}

	private static ImporterBinding CREATE_NEW = new ImporterBinding() {
		@Override
		public UUID_BINDING getUUIDBinding() {
			return UUID_BINDING.CREATE_NEW;
		}

		@Override
		public String getValue(String key) {
			return null;
		}

		@Override
		public boolean allowReferenceWithinTransaction() {
			return false;
		}

		@Override
		public QName[] getExcludedClasses() {
			return null;
		}

		@Override
		public ImporterContentCache getImportConentCache() {
			return null;
		}
	};

	public void setImporterService(ImporterService importerService) {
		this.importerService = importerService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDestDir(String destDir) {
		this.destDir = destDir;
	}

	public void setUtils(GdibUtils utils) {
		this.utils = utils;
	}

	public void setRmImportPackageHandlerFactory(RMImportPackageHandlerFactory rmImportPackageHandlerFactory) {
		this.rmImportPackageHandlerFactory = rmImportPackageHandlerFactory;
	}

}
