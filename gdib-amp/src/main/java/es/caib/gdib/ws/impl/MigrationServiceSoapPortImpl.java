package es.caib.gdib.ws.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.annotation.Resource;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.surf.util.ISO8601DateFormat;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.ExUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.ws.common.types.DataNodeTransform;
import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.MigrationID;
import es.caib.gdib.ws.common.types.MigrationNode;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.common.types.SearchResults;
import es.caib.gdib.ws.common.types.header.GdibSecurity;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.MigrationServiceSoapPort;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;

@WebService(serviceName = "MigrationService", portName = "GdibMigrationServiceSoapPort",
targetNamespace = "http://www.caib.es/gdib/migration/ws",
endpointInterface = "es.caib.gdib.ws.iface.MigrationServiceSoapPort")
public class MigrationServiceSoapPortImpl extends SpringBeanAutowiringSupport implements MigrationServiceSoapPort {

	private static final Logger LOGGER =  Logger.getLogger(MigrationServiceSoapPortImpl.class);
	
	String caibModelURI;
	String caibModelDocument;
	String caibModelExp;

	// TYPE:"{http://www.alfresco.org/model/content/1.0}content"
	String QUERY_TYPE_MIGRATED = "TYPE:\"" + ConstantUtils.TYPE_DOCUMENTO_MIGRADO_QNAME.toString() + "\"";
	String QUERY_TYPE_EXP_ENI = "TYPE:\"" + ConstantUtils.TYPE_EXPEDIENTE_QNAME + "\"";
	// @cm\:name:*anan*
	String QUERY_CSV = "@"+ConstantUtils.ENI_PREFIX + "\\:" + ConstantUtils.PROP_CSV + ":%s";
	String QUERY_ID = "@"+ConstantUtils.ENI_PREFIX + "\\:" + ConstantUtils.PROP_ID + ":%s";
	String QUERY_VALCERT_ID = "@"+ConstantUtils.ENI_PREFIX + "\\:" + ConstantUtils.PROP_APP_TRAMITE_DOC+":%S AND "
							+ "@"+ConstantUtils.CAIB_PREFIX + "\\:" + ConstantUtils.PROP_CODIGO_EXTERNO+":%s";

	private UserTransaction usrTrx;
	
	@Autowired
	private NodeService nodeService;
	@Autowired
	private SearchService searchService;
	@Autowired
	@Qualifier("repositoryServiceSoap")
	private RepositoryServiceSoapPort gdibRepositoryService;
	@Autowired
    private AuthenticationService authenticationService;
	@Autowired
	private TransactionService transactionService;

	@Autowired
	private GdibUtils utils;
	@Autowired
	private ExUtils exUtils;
	
	public void setAuthenticationService(AuthenticationService authenticationService){
		this.authenticationService = authenticationService;
	}
	
	public void setSearchService(SearchService searchService){
		this.searchService = searchService;
	}
	
	public void setTransactionService (TransactionService transactionService){
		this.transactionService = transactionService;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setExUtils(ExUtils exUtils){
		this.exUtils = exUtils;
	}

	public void setCaibModelURI(String caibModelURI) {
		this.caibModelURI = caibModelURI;
	}

	public void setCaibModelDocument(String caibModelDocument) {
		this.caibModelDocument = caibModelDocument;
	}

	public void setCaibModelExp(String caibModelExp) {
		this.caibModelExp = caibModelExp;
	}

	public void setGdibRepositoryService(RepositoryServiceSoapPort gdibRepositoryService) {
		this.gdibRepositoryService = gdibRepositoryService;
	}

	public void setGdibUtils(GdibUtils gdibUtils) {
		this.utils = gdibUtils;
	}

	private MigrationNode nodeToMigrationNode(Node node, boolean withContent, boolean withSign, boolean withMigrationSign) throws GdibException{
		MigrationNode ret = new MigrationNode();

		ret.setId(node.getId());
		ret.setName(node.getName());
		ret.setType(node.getType());
		ret.setAspects(node.getAspects());
		ret.setProperties(node.getProperties());

		if  (withContent){
			ret.setContent( utils.getContent(utils.idToNodeRef(node.getId())));
		}else{
			ret.setContent(null);
		}

		NodeRef parent = nodeService.getPrimaryParent( utils.idToNodeRef(node.getId()) ).getParentRef();
		if (withSign && node.getSign() != null){
			ret.setValcertSign(node.getSign());
		}else if (withSign){
			NodeRef firmaValcert =  nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS ,  node.getName() + ConstantUtils.FIRMA_VALCERT );
			try {
				ret.setValcertSign( utils.getContent(firmaValcert).getData() );
			} catch (GdibException e) {
				ret.setValcertSign(null);
			}
		}else{
			ret.setValcertSign(null);
		}


		NodeRef firmaMigracion = null;
		NodeRef zipFirmaMigracion = null;
		if (withMigrationSign){
			firmaMigracion =  nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS ,  node.getName() + ConstantUtils.FIRMA_MIGRACION);
			zipFirmaMigracion =  nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS ,  node.getName() + ConstantUtils.FIRMA_MIGRACION_ZIP);

			ret.getProperties().add(new Property(ConstantUtils.PROP_CSV_QNAME,(String)nodeService.getProperty(firmaMigracion, ConstantUtils.PROP_CSV_QNAME)));
			ret.getProperties().add(new Property(ConstantUtils.PROP_TIPO_FIRMA_QNAME,(String)nodeService.getProperty(firmaMigracion, ConstantUtils.PROP_TIPO_FIRMA_QNAME)));
			ret.getProperties().add(new Property(ConstantUtils.PROP_PERFIL_FIRMA_QNAME,(String)nodeService.getProperty(firmaMigracion, ConstantUtils.PROP_PERFIL_FIRMA_QNAME)));

			if ( firmaMigracion != null ){
				ret.setSign( utils.getContent(firmaMigracion).getData() );
			}else{
				throw exUtils.firmaMigracionNotFound(node.getId());
			}
			if ( zipFirmaMigracion != null ){
				ret.setZipContent(utils.getContent(zipFirmaMigracion).getData());
			}else{
				throw exUtils.zipMigracionNotFound(node.getId());
			}

		}else{
			ret.setSign(null);
		}

		return ret;
	}

	/**
	 * Valido los datos del MigrationId que se utilizaran para realizar la
	 * busqueda. La busqueda es por CSV o por Aplicacion Tramite y id externo.
	 * Solo pueden venir datos de una busqueda sino es error
	 *
	 * @param migrationId
	 *            parametro que contiene los datos para la busqueda
	 * @throws GdibException
	 */
	private void validateMigratonId(MigrationID migrationId) throws GdibException{
		if(migrationId == null)
			throw exUtils.nullParamException("migrationId");

		if(StringUtils.isEmpty(migrationId.getAppId())){
			throw exUtils.checkParamsException("AppId", "null or empty");
		}else if(StringUtils.isEmpty(migrationId.getExternalId())){
			throw exUtils.checkParamsException("ExternalId", "null or empty");
		}
	}
	
	private MigrationNode _internal_getMigrationNode(MigrationID migrationId, boolean withContent, boolean withSign, boolean withMigrationSign) throws GdibException{
		// 			Validar los parametros de entrada, solo puede venir datos para realizar una busqueda
		this.validateMigratonId(migrationId);

		// realizar la busqueda del nodo
    	String luceneSearch = String.format("+"+QUERY_TYPE_MIGRATED + " AND " + QUERY_VALCERT_ID,
    			migrationId.getAppId(), migrationId.getExternalId());
    	// Para que no haya paginacion en los resultados se pasa el parametro 0
    	LOGGER.debug("query lucene:"+luceneSearch);
    	SearchResults result = _internal_searchNode(luceneSearch, 0);

    	// si la busqueda devuelve mas de un resultado, error
    	if(result.getNumResultados() > 1)
    		throw exUtils.migratedNodeExceptionDuplicated(migrationId.getAppId(), migrationId.getExternalId());

    	// 0 Resultados => Excepcion.
    	if ( result.getNumResultados() == 0)
    		throw exUtils.migrationNodeNotFoundException(migrationId);

    	Node node = result.getResultados().get(0);

    	// recuperar el nodo y realizar controles de validacion
        // compruebo que el nodo este en el repositorio de migracion
    	utils.inMigrationPath(utils.toNodeRef(node.getId()));

        // miro si el nodo ha sido transformado
        if ( utils.contains( node.getAspects(), ConstantUtils.ASPECT_TRANSFORMADO_QNAME) ){
          	// recupero el uuid del nodo migrado en el DM para mostrarlo en el mensaje de error
           	String newNodeId = utils.getProperty(utils.filterCalculatedProperties(node.getProperties()), ConstantUtils.PROP_TRANSFORM_UUID_QNAME);
          	throw exUtils.migratedNodeException(migrationId.getAppId(), migrationId.getExternalId(),newNodeId);
        }
        // transformo el nodo a nodo de migracion	        
        MigrationNode ret = nodeToMigrationNode(node, withContent, withSign, withMigrationSign);
        return ret;
	}

	@Override
	public MigrationNode getMigrationNode(MigrationID migrationId, boolean withContent, boolean withSign,
			boolean withMigrationSign, GdibHeader gdibHeader) throws GdibException {    		
			MigrationNode ret = _internal_getMigrationNode(migrationId,withContent,withSign,withMigrationSign);	        
	        return ret;
	}

	/**
	 * Añado al nodo de transformacion los aspectos requeridos para crearlo
	 *
	 * @param node
	 *            de transformacion a crear
	 */
	private void addRequiredTransformNodeAspects(Node node){
		List<String> aspects = node.getAspects();
		aspects.add(ConstantUtils.ASPECT_INTEROPERABLE_QNAME.toString());
		aspects.add(ConstantUtils.ASPECT_TRANSFERIBLE_QNAME.toString());
		aspects.add(ConstantUtils.ASPECT_FIRMADO_QNAME.toString());
		aspects.add(ConstantUtils.ASPECT_TRANSFORMADO_QNAME.toString());
	}

	/**
	 * Completo los metadatos del nodo transformado
	 *
	 * @param node
	 *            nodo transformado para crear un nodo nuevo en DM
	 * @param mNode
	 *            nodo de migracion
	 * @throws GdibException
	 */
	private void addPreTranformMetadata(Node node, MigrationNode mNode) throws GdibException{

		// añado el aspecto de transformado a la lista de aspectos del nuevo nodo
		node.getAspects().add(ConstantUtils.ASPECT_TRANSFORMADO_QNAME.toString());
		
		List<Property> mProperties = mNode.getProperties();
		List<Property> properties = node.getProperties();
		Date transformDate = new Date();
		//properties.put(ConstantUtils.PROP_FECHA_TRANSFORMACION_QNAME.toString(), new SimpleDateFormat("yyyy-MM-dd").format(transformDate));
		properties.add(new Property(ConstantUtils.PROP_FECHA_TRANSFORMACION_QNAME.toString(), ISO8601DateFormat.format(transformDate)));
		properties.add(new Property(ConstantUtils.PROP_FECHA_MIGRACION_VALCERT_QNAME.toString(), utils.getProperty(mProperties, ConstantUtils.PROP_FECHA_MIGRACION_QNAME)));
		properties.add(new Property(ConstantUtils.PROP_CODIGO_VALCERT_QNAME.toString(), utils.getProperty(mProperties, ConstantUtils.PROP_CODIGO_QNAME)));
		properties.add(new Property(ConstantUtils.PROP_TIPO_DOCUMENTAL_VALCERT_QNAME.toString(), utils.getProperty(mProperties, ConstantUtils.PROP_TIPO_DOCUMENTAL_QNAME)));
		properties.add(new Property(ConstantUtils.PROP_CODIGO_EXTERNO_VALCERT_QNAME.toString(), utils.getProperty(mProperties, ConstantUtils.PROP_CODIGO_EXTERNO_QNAME)));
		properties.add(new Property(ConstantUtils.PROP_CLASE_VALCERT_QNAME.toString(), utils.getProperty(mProperties, ConstantUtils.PROP_CLASE_QNAME)));
		
		/*
		properties.put(ConstantUtils.PROP_FECHA_TRANSFORMACION_QNAME.toString(), ISO8601DateFormat.format(transformDate));
		properties.put(ConstantUtils.PROP_FECHA_MIGRACION_VALCERT_QNAME.toString(), utils.getProperty(mProperties, ConstantUtils.PROP_FECHA_MIGRACION_QNAME));
		properties.put(ConstantUtils.PROP_CODIGO_VALCERT_QNAME.toString(), utils.getProperty(mProperties, ConstantUtils.PROP_CODIGO_QNAME));
		properties.put(ConstantUtils.PROP_TIPO_DOCUMENTAL_VALCERT_QNAME.toString(), utils.getProperty(mProperties, ConstantUtils.PROP_TIPO_DOCUMENTAL_QNAME));
		properties.put(ConstantUtils.PROP_CODIGO_EXTERNO_VALCERT_QNAME.toString(), utils.getProperty(mProperties, ConstantUtils.PROP_CODIGO_EXTERNO_QNAME));
		properties.put(ConstantUtils.PROP_CLASE_VALCERT_QNAME.toString(), utils.getProperty(mProperties, ConstantUtils.PROP_CLASE_QNAME));
		*/
	}

	/**
	 * Completo los metadatos del nodo transformado, rellenando los metadatos de
	 * tipo contenido que necesitan que el nodo ya este creado
	 *
	 * @param uuid
	 *            del nodo transformado para crear un nodo nuevo en DM
	 * @param mNode
	 *            nodo de migracion
	 * @throws GdibException
	 */
	private void addPostTranformMetadata(String uuid, MigrationNode mNode) throws GdibException{
		NodeRef node = utils.toNodeRef(uuid);
		 try{
			 utils.setDataHandler(node, ConstantUtils.PROP_FIRMAVALCERT_QNAME, mNode.getValcertSign(), MimetypeMap.MIMETYPE_BINARY);
         }catch(IOException exception){
             throw exUtils.setSignException(uuid, exception);
         }
		 try{
			 utils.setDataHandler(node, ConstantUtils.PROP_ZIPMIGRACION_QNAME, mNode.getZipContent(), MimetypeMap.MIMETYPE_ZIP);
         }catch(IOException exception){
             throw exUtils.setSignException(uuid, exception);
         }
	}

	/**
	 * Transformo un nodo de migracion a un nodo de transformacion, insertandole
	 * nuevos metadatos, los metadatos se mergean con los ya existentes
	 *
	 * @param mNode
	 *            nodo de migracion
	 * @param metadata
	 *            nuevos metadatos
	 * @return
	 * @throws GdibException
	 */
	private MigrationNode migrationNodeToTransformNode(MigrationNode mNode, List<Property> metadata) throws GdibException{
		MigrationNode node = new MigrationNode();
		node.setName(mNode.getName());
		node.setContent(mNode.getContent());
		node.setSign(mNode.getSign());
		node.setType(ConstantUtils.TYPE_DOCUMENTO_QNAME.toString());
		node.setZipContent(mNode.getZipContent());
		node.setProperties(mNode.getProperties());

		// los metadatos que ya tuviera se ignoran, por lo que aplico los nuevos y no los mergeo
		List<Property> nodeMetadata = new ArrayList<Property>();		
		nodeMetadata.addAll(utils.transformListPropertyStringToQname(utils.filterCalculatedProperties(mNode.getProperties())));		
		//nodeMetadata.addAll(utils.transformListPropertyStringToQname(metadata));
		// Añado props especificadas en la llamda, sobreescribiendo las del nodo original.
		LOGGER.debug("TOTAL METADATA NEW PROPERTIES : "+metadata.size());
		LOGGER.debug("<<<<<<<<<<<Lista Metadatos recibidos>>>>>>>>>>>>>> ");
		for(Property prop : metadata)
		{
			LOGGER.debug("Prop "+prop.getQname());
			LOGGER.debug("Value "+prop.getValue());
			
		}
		LOGGER.debug("<<<<<<<<<<<<Lista nodeMetadata >>>>>>>");
		
		for(Property prop : nodeMetadata)
		{
			LOGGER.debug("Prop "+prop.getQname());
			LOGGER.debug("Value "+prop.getValue());
			
		}
		for(Property prop:metadata){
			LOGGER.debug("trying nodeMetadata.indexOf("+prop.getQname()+")");
			int pos = nodeMetadata.indexOf(prop);
			LOGGER.debug("CHECKING INDEX = "+ pos);
			if ( pos != -1 ){
				nodeMetadata.get(pos).setValue(prop.getValue());
			}else{
				nodeMetadata.add(prop);
			}
		}
		
		node.setProperties(nodeMetadata);

		// incluyo los aspectos necesarios
		addRequiredTransformNodeAspects(node);

		return node;
	}

	/**
	 * busco el expediente donde se va a insertar en nuevo nodo transformado en
	 * el DM.
	 * Obtener el uuid del nodo padre del documento, correspondiente al
	 * expediente al que pertenece el documento transformado Si no existe el
	 * expediente, se debe lanzar una excepción
	 *
	 * @param fileNumber
	 *            identificador del expediente, eni:id
	 * @return uuid del expediente
	 * @throws GdibException
	 */
	private String getExpedientFolder(String fileNumber) throws GdibException{
		//Realizar una búsqueda en el repositorio.
		String query = "@cm\\:name:\"" + fileNumber+"\"";
		SearchResults bus = _internal_searchNode(query, 0);
		if ( bus.getNumResultados() == 1 ){
			return bus.getResultados().get(0).getId();
		}
		if ( bus.getNumResultados() == 0 )
			throw new GdibException("Imposible encontrar el expediente");
		throw new GdibException("Hay más de un expediente con el ID:" + fileNumber);
	}
	
	private String getExpedientFolderOld(String fileNumber) throws GdibException{
		if(StringUtils.isEmpty(fileNumber))
			throw exUtils.checkParamsException("fileNumber (eni:id)", "null or empty");

		String uuid = "";
		// no hace falta obtener solo la parte del uuid en el eni:id
		String id = fileNumber.substring(fileNumber.length()-ConstantUtils.UUID_LENGTH_WITHOUT_DASH);
		// uuid pattern (8-4-4-4-12)
		Matcher mat = ConstantUtils.GENERATE_UUID_PATTERN.matcher(id);
		if (mat.matches()) {
			uuid += mat.group(1) + "-";
			uuid += mat.group(2) + "-";
			uuid += mat.group(3) + "-";
			uuid += mat.group(4) + "-";
			uuid += mat.group(5);
		}

		return uuid;
	}

	/**
	 * Bloquear el nodo transformado
	 *
	 * */
	@Override
	public String transformNode(DataNodeTransform datanodetransform, String fileNumber,  GdibHeader gdibHeader)
			throws GdibException {
		
			// fileNumber --> ES_A04003003_2015_EXP_5c49e60af512dc9bc52eebdefb608b
			LOGGER.debug("RECEIVING METADATA >>");
			for(Property prop : datanodetransform.getMetadata())
				LOGGER.debug(""+prop.getQname()+ " " + prop.getValue());
			
			// obtengo el nodo a migrado
			final MigrationNode mNode = this._internal_getMigrationNode(datanodetransform.getMigrationId(), true, true, true);
	
			// obtengo el uuid donde dejar el nodo transformado
			String parentId = fileNumber;//getExpedientFolder(fileNumber);
	
			// preparo el Nodo para crearlo
	
			// completar los metadatos del nodo migracion con los nuevos
			MigrationNode node = migrationNodeToTransformNode(mNode, datanodetransform.getMetadata());
	
			// relleno las propiedades del aspecto de transformacion
			addPreTranformMetadata(node, mNode);
	
			// creo el nuevo nodo node, parentId
			
			final String uuid = ((RepositoryServiceSoapPortImpl) gdibRepositoryService)._createNode(node,parentId,gdibHeader);
	
			// relleno las propiedades de transformacion que necesitan que el nodo exista
			addPostTranformMetadata(uuid, mNode);
			
			RunAsWork<?> raw = new RunAsWork<Object>() {
	            public Object doWork() throws Exception {
	    			// añado al nodo en el repositorio de migracion el aspecto de transformado y el uuid del nuevo nodo
	    			// creado en el DM
	    			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
	    			properties.put(ConstantUtils.PROP_TRANSFORM_UUID_QNAME, uuid);
	            	utils.addAspect(utils.toNodeRef(mNode.getId()), ConstantUtils.ASPECT_TRANSFORMADO_QNAME, properties);	
	    			// Bloquear el nodo transformado
	    			utils.lockNode(utils.toNodeRef(mNode.getId()));
	                return null;
	               }
	           };
           //Run as admin
           AuthenticationUtil.runAs(raw, "admin");
			
			
			
			return uuid;

	}
	

	
	
	private SearchResults _internal_searchNode(String luceneQuery, int pagina) throws GdibException{
		long initMill = System.currentTimeMillis();
		SearchResults res = new SearchResults();
		SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        searchParameters.setQuery(luceneQuery);
        ResultSet nodes = searchService.query(searchParameters);
        ArrayList<Node> resultado = new ArrayList<Node>();
        long numResultados = nodes.getNumberFound();
        if ( numResultados > 10 )
        	numResultados = 10;
        
        for ( int i = 0; i < numResultados; i++){        	
        		resultado.add(getNode(nodes.getNodeRef(i)));        	
        }
        //Componer la respuesta
        res.setNumPaginas(1);
        res.setNumResultados(numResultados);
        res.setResultados(resultado);
        long endMill = System.currentTimeMillis();
        return res;
	}


	private Node getNode(NodeRef nodeRef) throws GdibException {
		return ((RepositoryServiceSoapPortImpl) gdibRepositoryService)._internal_getNode(nodeRef, false, false);		
	}	
	

}