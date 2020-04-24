package es.caib.gdib.rm.utils;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.domain.node.ContentDataWithId;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.view.ExporterCrawlerParameters;
import org.alfresco.service.cmr.view.ExporterException;
import org.alfresco.service.cmr.view.ExporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.gdib.rm.RMExportPackageHandler;
import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.CuadroClasificacionUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;


public class ExportUtils {

	private static final Logger LOGGER =  Logger.getLogger(ExportUtils.class);
	private static final String DOC_PREFIX = "DOC_";

    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private ContentService contentService;
    private ExporterService exporterService;
    private CuadroClasificacionUtils ccUtils;

    // Utilidades
    @Autowired
    private GdibUtils utils;

	// Parametros de la exportacion
    private String destDir;
    private String tmpDir;

	public NodeRef exportExpediente(NodeRef expediente) throws GdibException {
		LOGGER.debug("Comenzamos el proceso de exportacion del expediente del DM al RM");
		NodeRef rmExpedient = execute(expediente);
		return rmExpedient;
	}

	private NodeRef execute(NodeRef expediente) throws GdibException {
		Location location = new Location(expediente);

		// configuro la exportacion
		LOGGER.debug("Configurando los parametros de la exportacion");
		ExporterCrawlerParameters parameters = new ExporterCrawlerParameters();
		parameters.setExportFrom(location);
		parameters.setCrawlSelf(true);
		parameters.setCrawlChildNodes(true);

		RMExportPackageHandler exportHandler = null;

		try {

			LOGGER.debug("Realizo la exportacion");
			// realizo la exportacion(cerrar expediente) de alfresco
			exportHandler = new RMExportPackageHandler();
			exporterService.exportView(exportHandler, parameters, null);

			LOGGER.debug("Muevo el expediente del DM al RM");
			// muevo el expediente a al RM, recuperando el expediente creado en el RM
			NodeRef rmExpedient = moveToRM(expediente, exportHandler);
//			moveToTemp(expediente, exportHandler);

			return rmExpedient;

		} catch (ExporterException e) {
			throw new GdibException("Ha ocurrido un error durante la exportacion de nodos a RM. " + e.getMessage(),e);
		} catch (Throwable e) {
			throw new GdibException("Ha ocurrido un error durante la exportacion de nodos a RM. " + e.getMessage(),e);
		}
	}

	/**
	 * Creamos un nodo para guardar el contenido del XML descriptor de la exportacion
	 * Este xml guarda la estructura de carpetas y ficheros que contiene el expediente antes
	 * de mover el expediente del DM al RM
	 *
	 * @param name nombre del fichero
	 * @param parentNode padre donde crear el nodo descriptor
	 * @param xmlContent contenido del XML
	 * @return el nodo creado
	 * @throws ContentIOException
	 * @throws UnsupportedEncodingException
	 */
	private NodeRef writeXMLexportDescriptor(String name, NodeRef parentNode, ByteArrayOutputStream xmlContent)
			throws ContentIOException, UnsupportedEncodingException {
		Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
		props.put(ContentModel.PROP_NAME, name);

		NodeRef node = this.nodeService.createNode(parentNode, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), ContentModel.TYPE_CONTENT, props)
				.getChildRef();

		ContentWriter writer = contentService.getWriter(node, ContentModel.PROP_CONTENT, true);
		writer.setMimetype(MimetypeMap.MIMETYPE_XML);
		writer.setEncoding("UTF-8");
		writer.putContent(xmlContent.toString("UTF-8"));

		return node;
	}

	// Metodo de prueba cuando se hacia la exportacion hacia una carpeta de Alfresco y no al RM.
	/**
	 * Mueve un expediente hacia una carpeta temporal configurada por properties. Este metodo es para realizar pruebas
	 * de exportacion del expediente del DM al RM, pero sin utilizar el RM para evitar fallos debido al modulo RM de alfresco.
	 *
	 * @param expediente
	 * @param exportHandler
	 * @throws GdibException
	 */
	private void moveToTemp(NodeRef expediente, RMExportPackageHandler exportHandler) throws GdibException{
		NodeRef nodeDestino = utils.idToNodeRef(destDir);
		NodeRef nodeTmp = utils.idToNodeRef(tmpDir);
		String expedienteName = (String) nodeService.getProperty(expediente, ConstantUtils.PROP_NAME);

		try {
			// Creamos la carpeta temporal del expediente
			FileInfo tmpParentFileInfo = fileFolderService.create(nodeTmp, "tmp_" + expedienteName, ContentModel.TYPE_FOLDER);

			// Movemos a una carpeta temporal renombrando todos los nodos
			for(NodeRef nodeRef : exportHandler.getListNodeRefsToMove()){
				String newName = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_ID_QNAME);
				newName = StringUtils.isEmpty(newName) ? nodeRef.getId() : newName;

				fileFolderService.copy(nodeRef, tmpParentFileInfo.getNodeRef(), newName);
			}

			// Movemos el exp al destino (Necesitamos conservar el uid del expediente)
			FileInfo newExpediente = fileFolderService.copy(expediente, nodeDestino, null);

			// Movemos los nodos de la carpeta temporal al expediente
			for(NodeRef nodeRef : exportHandler.getListNodeRefsToMove()){
				fileFolderService.move(nodeRef, expediente, null);
			}

			// Escribimos el descriptor XML
			writeXMLexportDescriptor(expedienteName + ".xml", newExpediente.getNodeRef(), (ByteArrayOutputStream) exportHandler.getXML_outputStream());

			//Borramos la carpetata temporal
			nodeService.deleteNode(tmpParentFileInfo.getNodeRef());

		} catch (FileExistsException | FileNotFoundException | ContentIOException | UnsupportedEncodingException e) {
			throw new GdibException("Ha ocurrido un error moviendo los ficheros de exportacion a RM. " + e.getMessage(),e);
		}
	}

	/**
	 * Muevo un expediente del DM al RM. Respetando todos los datos (incluido los UUID)
	 *
	 * @param expediente
	 * @param exportHandler
	 * @throws GdibException
	 */
	private NodeRef moveToRM(NodeRef expediente, RMExportPackageHandler exportHandler) throws GdibException {
		Boolean moveNodeToRM;
		NodeRef nodeTmp = utils.idToNodeRef(tmpDir);
		List<String> processedNodes = new ArrayList<String>();
		try {
			// Creamos la carpeta temporal del expediente
			LOGGER.debug("Creo una carpeta temporal para mover el expediente");
			String expedienteName = (String) nodeService.getProperty(expediente, ConstantUtils.PROP_NAME);
			FileInfo tmpParentFileInfo = fileFolderService.create(nodeTmp, "tmp_" + expedienteName,
					ContentModel.TYPE_FOLDER);

			// Movemos a una carpeta temporal renombrando todos los nodos
			LOGGER.debug("Renombramos los nombres que colisionen.");
			List<String> colision = new ArrayList<String>();
			// A la hora de renombrar, tengo que tener encuenta si es un documento del expediente, si es
			// el indice electronico interno o de intercambio, o si es un documento de un expediente de exportacion (indice para remision cerrado)
			for (NodeRef nodeRef : exportHandler.getListNodeRefsToMove()) {
				// parsear el nombre con expresion regular para detectar si viene

				// indice electronico interno (indice-eni:id-fechahora.xml) indice-ES_123456789_2016_EXP_50d800a8a96a4c2cb224b1d9453ab0b9-201605051200.xml
				//			--> nombre final el nombre del nodo sin tocar
				// indice electronico de intercambio (indice-int-eni:id-fechahora.xml) indice-int-ES_123456789_2016_EXP_50d800a8a96a4c2cb224b1d9453ab0b9-201605051200.xml
				//			--> nombre final el nombre del nodo sin tocar
				// documentos
				//			--> DOC_+UUID Alfresco

				String nodeId = nodeRef.getId();
				System.out.println("Nodo a copiar " + nodeId);
				if(!processedNodes.contains(nodeId)){
					QName nodeType = nodeService.getType(nodeRef);
					String name = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_NAME);
					String eniId = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_ID_QNAME);
					String newName = "";
					moveNodeToRM = Boolean.FALSE;
					
					newName = getNewName(colision,name, eniId);
					
					System.out.println("Nodo no copiado aun. Tipo del nodo: " + nodeType.toString() + "; eni:id -> " + eniId);

					if (utils.isType(nodeType, ConstantUtils.TYPE_DOCUMENTO_QNAME)){
						/*
						int posExtension = name.lastIndexOf(".");
						if ( posExtension != -1 ){
							newName = name.substring(0, posExtension)+"_"+eniId+name.substring(posExtension,name.length());
						}else{
							newName = name+eniId;
						}*/
						
						//newName = DOC_PREFIX + nodeId.replaceAll("-", ConstantUtils.BLANK);
						moveNodeToRM = Boolean.TRUE;
					} else if(utils.isType(nodeType, ConstantUtils.TYPE_FILE_INDEX_QNAME)){
						// indie electronico interno o de intercambio
						// newName = name.toString();
						moveNodeToRM = Boolean.TRUE;
					}

					LOGGER.warn("Nodo a copiar " + name + ". Nuevo nombre: " + newName);
					System.out.println("Nodo a copiar " + name + ". Nuevo nombre: " + newName);
					if(moveNodeToRM){
						fileFolderService.move(nodeRef, tmpParentFileInfo.getNodeRef(), newName);
						processedNodes.add(nodeId);
					}
				}
			}

			// Movemos el exp al destino (Necesitamos conservar el uid del expediente)
			LOGGER.debug("Movemos el expediente al RM");
			NodeRef rmSeries = ccUtils.getDocumentarySeries((String) nodeService.getProperty(expediente, ConstantUtils.PROP_COD_CLASIFICACION_QNAME));
			NodeRef rmExpedient = createRMExpedient(expediente, rmSeries);

			// Movemos los nodos de la carpeta temporal al expediente
			LOGGER.debug("Movemos los documentos de la carpeta temporal al expediente creado en el RM");
			for (NodeRef nodeRef : exportHandler.getListNodeRefsToMove()) {
				createRMRecord(nodeRef, rmExpedient);
			}

			// Escribimos el descriptor XML
			LOGGER.debug("Creamos el archivo xml descriptor de la estructura del expediente antes de cerrarlo");
			writeXMLexportDescriptor(expedienteName + ".xml", expediente,
					(ByteArrayOutputStream) exportHandler.getXML_outputStream());

			// Borramos la carpetata temporal
			LOGGER.debug("Borramos la carpeta temporal");
			nodeService.deleteNode(tmpParentFileInfo.getNodeRef());

			return rmExpedient;

		} catch (FileExistsException | FileNotFoundException | ContentIOException | UnsupportedEncodingException e) {
			throw new GdibException("Ha ocurrido un error moviendo los ficheros de exportacion a RM. " + e.getMessage(),e);
		}
	}

private static String getNewName(List<String> colision, String name, String eni) {		
		String newName=name;
		if ( colision.contains(name) ){
			int posExtension = name.lastIndexOf(".");
			if ( posExtension != -1 ){
				newName = name.substring(0, posExtension)+"_"+eni+name.substring(posExtension,name.length());
			}else{
				newName = name+"_"+eni;
			}
			newName = getNewName(colision,newName, eni);
		}		
		colision.add(newName);		
		return newName;
	}

	/**
	 * Crea una copia de un Nodo expediente de alfresco en el RM. Respetando todos los datos inlcuido el UUID.
	 * Tambien se crea la estructura de carpetas con la fecha de incorporacion del expediente el RM
	 *
	 * @param expedient
	 *            nodo expediente que tengo que replicar en el RM
	 * @param rmSeries
	 *            nodo padre donde crear el expediente
	 * @return el nodo expediente creado en el RM
	 * @throws GdibException
	 */
	private NodeRef createRMExpedient(NodeRef expedient, NodeRef rmSeries) throws GdibException {
		LOGGER.debug("Creamos el nodo expediente en el RM, pasandole todos los metadatos del expediente a la carpeta del RM");
		// Creo la estructura de carpetas año, mes, dia en RM para guardar el expediente
		String[] dateTreeFolder = new SimpleDateFormat("yyyy/MM/dd").format(new Date()).split("/");
		for (String folder : dateTreeFolder) {
			NodeRef folderRef = fileFolderService.searchSimple(rmSeries, folder);
			if(folderRef != null)
				rmSeries = folderRef;
			else{
				Map<QName, Serializable> props = new HashMap<QName, Serializable>();
				props.put(ContentModel.PROP_NAME, folder);
				props.put(ContentModel.PROP_TITLE, folder);
				QName name = utils.createNameQName(folder);
				rmSeries = nodeService.createNode(rmSeries, ContentModel.ASSOC_CONTAINS, name,
						RecordsManagementModel.TYPE_RECORD_CATEGORY, props).getChildRef();
			}
		}

		// recupero la informacion del nodo expediente
		// aspectos, properties, nombre y uuid
		List<QName> aspects = utils.transformListStringToQname(utils.getAspects(expedient));
		List<Property> properties = utils.getProperties(expedient);
		String uuid = (String) nodeService.getProperty(expedient, ConstantUtils.PROP_NODE_UUID);
		String expedientName = (String) nodeService.getProperty(expedient, ConstantUtils.PROP_NAME);

		// elimino el nodo para tener libre el uuid
		nodeService.deleteNode(expedient);

		// creo el nuevo nodo en el RM
		QName nameQname = utils.createNameQName(expedientName);
		QName type = RecordsManagementModel.TYPE_RECORD_FOLDER;
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		props.put(ContentModel.PROP_NODE_UUID, uuid);
		props.put(ContentModel.PROP_NAME, expedientName);
		props.put(ContentModel.PROP_TITLE, expedientName);
		props.put(ContentModel.PROP_AUTO_VERSION_PROPS, true);
		// guardo las propiedades del nodo previo
		props.putAll(utils.transformMapStringToQname(properties));
		// TODO Verificar con Iñaki, cuando se crean los nodos en RM, la fecha de creacion
		props.remove(ContentModel.PROP_CREATED);
		ChildAssociationRef createdChildRef = nodeService.createNode(rmSeries, ContentModel.ASSOC_CONTAINS, nameQname,
				type, props);
		NodeRef rmExpedient = createdChildRef.getChildRef();
		// le aplico los datos recuperados anteriormente
		utils.addAspects(rmExpedient, aspects);

		// devuelvo la copia del expediente en el RM
		return rmExpedient;
	}

	/**
	 * Crea una copia de un Nodo documento de alfresco en el RM. Respetando
	 * todos los datos inlcuidos y el UUID
	 *
	 * @param nodeRef
	 *            nodo documento que tengo que replicar en el RM
	 * @param rmExpedient
	 *            nodo expediente donde tengo que crear el documento
	 * @throws GdibException
	 */
	private void createRMRecord(NodeRef nodeRef, NodeRef rmExpedient) throws GdibException {
		// recupero toda la informacion del nodo
		// aspectos, properties, contenido y firma, nombre y uuid
		// TODO parsear los documentos que son de tipo thumbnail
		if(nodeService.exists(nodeRef) && !nodeService.getType(nodeRef).equals(ContentModel.TYPE_THUMBNAIL))
		{
			List<QName> aspects = utils.transformListStringToQname(utils.getAspects(nodeRef));
			List<Property> properties = utils.getProperties(nodeRef);
			QName type = nodeService.getType(nodeRef);
			String uuid = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_NODE_UUID);
			String nodeRefName = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_NAME);
			ContentDataWithId content = (ContentDataWithId) nodeService.getProperty(nodeRef, ConstantUtils.PROP_CONTENT);
			ContentDataWithId signature = (ContentDataWithId) nodeService.getProperty(nodeRef, ConstantUtils.PROP_FIRMA_QNAME);

			// elimino el nodo para tener libre el uuid
			nodeService.deleteNode(nodeRef);

			// creo el nuevo nodo en el RM
			QName nameQname = utils.createNameQName(nodeRefName);
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(ContentModel.PROP_NODE_UUID, uuid);
			props.put(ContentModel.PROP_NAME, nodeRefName);
			props.put(ContentModel.PROP_TITLE, nodeRefName);
			props.put(ConstantUtils.PROP_CONTENT, content);
			props.put(ConstantUtils.PROP_FIRMA_QNAME, signature);
			props.put(ContentModel.PROP_AUTO_VERSION_PROPS, true);
			// guardo las propiedades del nodo previo
			props.putAll(utils.transformMapStringToQname(properties));

			ChildAssociationRef createdChildRef = nodeService.createNode(rmExpedient, ContentModel.ASSOC_CONTAINS,
					nameQname, type, props);
			NodeRef docRM = createdChildRef.getChildRef();

			// le aplico los datos recuperados anteriormente
			utils.addAspects(docRM, aspects);
		}
	}

	public ExporterService getExporterService() {
		return exporterService;
	}

	public void setExporterService(ExporterService exporterService) {
		this.exporterService = exporterService;
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public String getDestDir() {
		return destDir;
	}

	public void setDestDir(String destDir) {
		this.destDir = destDir;
	}

	public GdibUtils getUtils() {
		return utils;
	}

	public void setUtils(GdibUtils utils) {
		this.utils = utils;
	}

	public FileFolderService getFileFolderService() {
		return fileFolderService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public void setCcUtils(CuadroClasificacionUtils ccUtils) {
		this.ccUtils = ccUtils;
	}
}
