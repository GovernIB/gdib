package es.caib.gdib.utils;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;

import javax.activation.DataHandler;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.lock.UnableToAquireLockException;
import org.alfresco.service.cmr.lock.UnableToReleaseLockException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionDoesNotExistException;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.QueryParser;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.extensions.surf.util.ISO8601DateFormat;
import org.w3c.dom.Element;

import es.caib.gdib.utils.iface.EniModelUtilsInterface;
import es.caib.gdib.ws.common.types.Content;
import es.caib.gdib.ws.common.types.EniSignatureType;
import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.MigrationNode;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.NodeVersion;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.common.types.SignatureValidationReport;
import es.caib.gdib.ws.common.types.ValidationStatus;
import es.caib.gdib.ws.common.types.header.GdibRestriction;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.SignatureService;

public class GdibUtils {

	private static final Logger LOGGER =  Logger.getLogger(GdibUtils.class);

    private static ExUtils exUtils;

    private String caibDocumentMetadata;
    private String caibExpedienteMetadata;
    private String noModifyCaibDocumentMetadata;
    private String noModifyCaibExpedienteMetadata;
    private String rootDM;
    private String _pathDM;
    private String rootMigration;
    private String _pathMigration;

    private static NamespaceService namespaceService;
    private FileFolderService fileFolderService;
    private DictionaryService dictionaryService;
    private ContentService contentService;
   // private ContentService unsecureContentService;
    private NodeService nodeService;
    private AuthenticationService authenticationService;
    private VersionService versionService;
    private SearchService searchService;
    private LockService lockService;
    private PermissionService permissionService;
    private AuthorityService authorityService;
    private CategoryService categoryService;

    private String rootCT;
    private String rootTemplate;
    private FilterPlaceholderProperties gdibAmpPropertiesAplicationFilter;
    private FilterPlaceholderProperties gdibAmpPropertiesESBOperationFilter;
    private Boolean repositoryDisableCheck;
    private CuadroClasificacionUtils ccUtils;
    private Boolean repositoryClassificationTableMode;
    private String registroCentralSeries;
    private Boolean inDMPathCheckActive;


    private static final SecureRandom secureRandom = new SecureRandom();

    private SignatureService signatureService;
    /**
     * Compruebo si el nodeId es valido
     *
     * @param nodeId uuid a validar
     * @return
     */
    public boolean validNodeId(String nodeId) {
        boolean res = true;

        if (StringUtils.isEmpty(nodeId))
            return false;
        /*
         * Comprobamos que el nodeId recibido coincide con alguna de las
         * siguientes opciones:
         * i- 	UUID: b24eeb92-aed8-439c-af4d-db25785b2fc4
         * ii- 	VERSION_UUID_PATTERN: 1.1@b24eeb92-aed8-439c-af4d-db25785b2fc4
         * iii-	PATH RELATIVO: b24eeb92-aed8-439c-af4d-db25785b2fc4/ruta/al/nodo
         */
        if (!ConstantUtils.UUID_PATTERN.matcher(nodeId).matches()
                & !ConstantUtils.VERSION_UUID_PATTERN.matcher(nodeId).matches()
                & !ConstantUtils.PATH_REL_PATTERN.matcher(nodeId).matches()) {
            return false;
        }

        return res;
    }

    /**
     * Compruebo si el parentId es valido.
     * * Compruebo si esta vacio o nulo
     * * Si cumple los dos formatos validos, uuid y ruta relativa
     *
     *
     * @param parentId uuid a validar
     * @return
     */
    public boolean validParentId(String parentId) {
        boolean res = true;

        if (StringUtils.isEmpty(parentId))
            return false;

        if (!ConstantUtils.UUID_PATTERN.matcher(parentId).matches()
                & !ConstantUtils.PATH_REL_PATTERN.matcher(parentId).matches()) {
            return false;
        }

        return res;
    }

    /**
     * Valida el uuid de un nodeRef de alfresco. Comprobando que cumple el
     * formato y que exista
     *
     * @param nodeId
     *            identificador del nodo
     * @return nodeRef del nodo de alfresco
     * @throws GdibException
     */
    public NodeRef checkNodeId(String nodeId) throws GdibException {
        if (!this.validNodeId(nodeId)) {
            throw exUtils.checkParamsException("nodeId", nodeId == null ? "null" : nodeId);
        }
        NodeRef ref = this.idToNodeRef(nodeId);
        exists(ref);
        return ref;
    }

    /**
     * Compruebo que el nodo esta en el path de la raiz del DM
     *
     * @param node
     *            nodo a verificar
     * @throws GdibException
     */
    public boolean inDMPath(NodeRef node) throws GdibException {
    	if(inDMPathCheckActive){
    		inPath(getPathDM(), node);
    	}
    	return true;
    }

    /* Devuelve el pathDM */
    private String getPathDM() {
    	if (!StringUtils.isEmpty(this.rootDM)) {
			this._pathDM = this.getPathFromUID(this.toNodeRef(this.rootDM));
		}
		return this._pathDM;
	}

	/**
     * Compruebo que el nodo esta en el path de la raiz del repositorio de migracion
     *
     * @param node
     *            nodo a verificar
     * @throws GdibException
     */
    public boolean inMigrationPath(NodeRef node) throws GdibException {
    	inPath(getPathMigration(), node);
    	return true;
    }

    /**
     * Devuelve el path migracion
     * */
	private String getPathMigration() {
		if (!StringUtils.isEmpty(this.rootMigration)) {
			this._pathMigration = this.getPathFromUID(this.toNodeRef(this.rootMigration));
		}
		return this._pathMigration;
	}

	private void inPath(String path, NodeRef node) throws GdibException {
		String pathFromUid = this.getPathFromUID(node);
		if (!pathFromUid.contains(path)) {
			throw exUtils.nodeNotInPath(node.getId(), path);
		}
	}

    /**
     * Valida una lista de nodeRef
     *
     * @param nodeIds
     * @return
     * @throws GdibException
     */
    public List<NodeRef> checkNodeIds(List<String> nodeIds) throws GdibException{
        List<NodeRef> noderefs = new ArrayList<NodeRef>();
        for(String nodeId : nodeIds){
            noderefs.add(checkNodeId(nodeId));
        }
        return noderefs;
    }

    /**
     * Recupera los documentos que pertenecen a un determinado expediente. Para
     * ello se construye y ejecuta una busqueda de tipo lucene sobre el node
     * "expediente" de nodos tipo "documento" a cualquier profundidad.
     *
     * @param expediente
     * @return
     */
    public ResultSet searchDocumentsBelongExpediente(NodeRef expediente) {
        return searchDocumentsBelongExpediente(expediente, false);
    }

    /**
     * Recupera los documentos que pertenecen a un determinado expediente. Para
     * ello se construye y ejecuta una busqueda de tipo lucene sobre el node
     * "expediente" de nodos tipo "documento" a cualquier profundidad.
     *
     * @param expediente
     *            NodeRef del expediente en cuestión
     * @param sortFoliado
     *            indica si el orden en la que se devuelven los resultados
     *            corresponde con el requerido para la operación de foliado.
     * @return
     */
    public ResultSet searchDocumentsBelongExpediente(NodeRef expediente, boolean sortFoliado) {
        String query = "";
        query += "+PATH:\"" + nodeService.getPath(expediente) + "//*\" ";
        query += "+TYPE:\"" + ConstantUtils.TYPE_DOCUMENTO_QNAME + "\" ";

        SearchParameters sp = new SearchParameters();
        sp.addStore(expediente.getStoreRef());
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        sp.setQuery(query);
        if (sortFoliado) {
            sp.addSort(QueryParser.escape("@" + ConstantUtils.PROP_FECHA_INICIO_QNAME), true);
        }

        ResultSet results = searchService.query(sp);

        return results;

    }

    /**
     * Realiza comprobaciones para que el nodo sea valido
     * <ul>
     * 	<li>Nombre: que sea valido</li>
     *  <li>Tipo de nodo: exista en el modelo</li>
     *  <li>Metadatos del nodo: que no tengan metadatos no existentes en el modelo de alfresco</li>
     *  <li>Aspectos del nodo: que no tengan aspectos no existentes en el modelo de alfresco</li>
     *  <li>Si el nodo es de tipo Expediente, no puede contener firma o contenido</li>
     * </ul>
     * @param node
     * @throws GdibException
     */
    public void checkNode(Node node) throws GdibException{
		if (node == null)
			throw exUtils.nullParamException("node");

		if (StringUtils.isEmpty(node.getName()))
			throw exUtils.nullParamException("node name");

		// elimino las propiedades calculadas y propiedades de borrar
		node.setProperties(this.filterRemoveMetadata(node.getProperties()));
		node.setProperties(this.filterCalculatedProperties(node.getProperties()));

         // elimino los aspectos a eliminar del nodo
         node.setAspects(this.filterRemoveAspects(node.getAspects()));

		// compruebo que el tipo del nodo exista
		this.checkNodeType(node.getType());

        // verifico si los metadatos del nodo existen en el dictionario de alfresco
        checkValidProperties(this.filterRemoveMetadata(node.getProperties()));

        // verifico si los aspectos del nodo existen en el dictionario de alfresco
        this.checkValidAspects(node.getAspects());

        // si el nodo es tipo carpeta no puede tener contenido o firma
        if(this.isType(node.getType(), ConstantUtils.TYPE_FOLDER)){
        	if (node.getContent() != null)
                throw exUtils.informParamException("node content");

            if (node.getSign() != null)
                throw exUtils.informParamException("node sign");
        }
    }

    /**
     * Valida la integridad de los datos del nodo.
     * - Que tenga los metadatos obligatorios necesarios.
     * - Si es documento fisico que tenga los metadatos necesarios y si no se valida la firma
     * - Y la integridad entre metadatos
     *   - si soporte digital, el nombre y formato de la extension son obligatorios
     *   - etc....
     * @param node
     * @throws GdibException
     */
    public void checkNodeIntegrity(Node node) throws GdibException{
    	// verifico que el nodo tiene unas propiedades obligatorias para existir
    	this.checkNodeMandatoryMetadata(node);

    	// si el documento es final tengo que realizar mas comprobaciones
    	if ( this.isType(node.getType(), ConstantUtils.TYPE_DOCUMENTO_QNAME ))
    	{
    		this.checkMetadataIntegrityDocument(node.getProperties());
    	}else if(this.isType(node.getType(), ConstantUtils.TYPE_EXPEDIENTE_QNAME )){
    		this.checkMetadataIntegrityExpedient(node.getProperties());
    	}

    }
    
   

	/**
	 * Se encarga de rellenar los metadatos que se tiene que rellenar automaticamente. Rellenando valores por defecto si no
	 * viene la informacion en la llamada.
	 *
	 * @param node
	 *            nodo a rellenar
	 * @throws GdibException
	 */
    public void fillNodeMetadata(Node node) throws GdibException{
        String v_nti = null;
        String categoria = null;

        if(node.getProperties()==null)
        	node.setProperties(new ArrayList<Property>());

        if ( this.isType(node.getType(), ConstantUtils.TYPE_DOCUMENTO_QNAME ) ){

        	// eni:app_tramite_doc
        	this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_APP_TRAMITE_DOC_QNAME, AuthenticationUtil.getFullyAuthenticatedUser());

        	v_nti = ConstantUtils.V_NTI_DOC;

        	categoria = ConstantUtils.CATEGORIA_DOC_SIMPLE;

        	// TODO eni:def_csv es un dato rellenado por nosotros (SRV) que valor se ha de meter?
        	//this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_DEF_CSV_QNAME, ConstantUtils.DEF_CSV_VALUE);

        	// eni:documento_vital
        	this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_DOCUMENTO_VITAL_QNAME, Boolean.FALSE.toString());

        }else if ( this.isType(node.getType(), ConstantUtils.TYPE_EXPEDIENTE_QNAME ) ){

        	// eni:app_tramite_exp
        	this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_APP_TRAMITE_EXP_QNAME, AuthenticationUtil.getFullyAuthenticatedUser());

        	v_nti = ConstantUtils.V_NTI_EXP;

        	categoria = ConstantUtils.CATEGORIA_EXPEDIENTE;
        }

        // eni:id
//        int index = node.getProperties().indexOf(new Property(ConstantUtils.PROP_ORGANO_QNAME));
//        if(!StringUtils.isEmpty(node.getId())
//        		&& !StringUtils.isEmpty(node.getType())
//        		&& index > -1){
//        	Property organo = node.getProperties().get(index);
//        	String eniId = this.calculateEniId(node.getId(), node.getType(), organo);
//        	this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_ID_QNAME, eniId);
//        }
        if ( this.isType(node.getType(), ConstantUtils.TYPE_DOCUMENTO_QNAME ) || this.isType(node.getType(), ConstantUtils.TYPE_EXPEDIENTE_QNAME )) {
	        // eni:v_nti
	        this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_V_NTI_QNAME, v_nti);

	        // eni:categoria
	        this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_CATEGORIA_QNAME, categoria);

	        // eni:estado_exp
	        this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_ESTADO_EXP_QNAME, ConstantUtils.ESTADO_EXP_E01);

	    	// eni:fase_archivo, si vacio, por defecto Archivo Activo
	        this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_FASE_ARCHIVO_QNAME, ConstantUtils.FASE_ARCHIVO_ACTIVO);

	        // eni:soporte si vacio, por defecto "Digital"
	        this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_SOPORTE_QNAME, ConstantUtils.SOPORTE_DIGITAL);

	        // eni:fecha_inicio. Si se esta creando un expediente sin informar el padre, cuando se calcula la estructura de carpetas
	        // para generar el expediente, se establece este valor en dicho paso
	        this.setValuePropertyIfEmpty(node.getProperties(), ConstantUtils.PROP_FECHA_INICIO_QNAME, ISO8601DateFormat.format(new Date()));
        }
    }

    private void setValuePropertyIfEmpty(List<Property> properties, QName property, String propertyValue) throws GdibException{
    	if(this.checkEmptyProperty(properties, property)){
    		int index = properties.indexOf(new Property(property));
        	if(index<0)
        		properties.add(new Property(property, propertyValue));
        	else
        		properties.get(index).setValue(propertyValue);
    	}
    }

    /**
     * Metodo para comprobar la integridad entre metadatos para los Expedientes
     *
     * - "eni:soporte" es digital, "eni:nombre_formato" y "eni:extension:formago" debe ser informado
     *
     * @param properties
     * @throws GdibException
     */
    private void checkMetadataIntegrityExpedient(List<Property> properties) throws GdibException{
    	// si "eni:soporte" es DISTINTO a digital, "eni:los_archivo_central" y "eni:loc_archivo_general" debe ser informado
		if (!ConstantUtils.SOPORTE_DIGITAL.equals(this.getProperty(properties, ConstantUtils.PROP_SOPORTE_QNAME))) {
			if(this.getProperty(properties, ConstantUtils.PROP_LOC_ARCHIVO_CENTRAL_QNAME) == null){
				throw exUtils.checkMetadataIntegrityException(ConstantUtils.PROP_SOPORTE_QNAME, "distinto a " + ConstantUtils.SOPORTE_DIGITAL, ConstantUtils.PROP_LOC_ARCHIVO_CENTRAL_QNAME);
			}
			if(this.getProperty(properties, ConstantUtils.PROP_LOC_ARCHIVO_GENERAL_QNAME) == null){
				throw exUtils.checkMetadataIntegrityException(ConstantUtils.PROP_SOPORTE_QNAME, "distinto a " + ConstantUtils.SOPORTE_DIGITAL, ConstantUtils.PROP_LOC_ARCHIVO_GENERAL_QNAME);
			}
		}
    }

    private void checkMetadataIntegrityDocument(List<Property> properties) throws GdibException{

    	// si "eni:soporte" es IGUAL digital, "eni:nombre_formato" y "eni:extension:formago" debe ser informado
		if (ConstantUtils.SOPORTE_DIGITAL.equals(this.getProperty(properties, ConstantUtils.PROP_SOPORTE_QNAME))
				|| this.getProperty(properties, ConstantUtils.PROP_SOPORTE_QNAME) == null) {
			String prop = this.getProperty(properties, ConstantUtils.PROP_NOMBRE_FORMATO_QNAME);
			if( StringUtils.isEmpty(prop) ){
				throw exUtils.checkMetadataIntegrityException(ConstantUtils.PROP_SOPORTE_QNAME, ConstantUtils.SOPORTE_DIGITAL, ConstantUtils.PROP_NOMBRE_FORMATO_QNAME);
			}
			prop = this.getProperty(properties, ConstantUtils.PROP_EXTENSION_FORMATO_QNAME);
			if( StringUtils.isEmpty(prop)){
				throw exUtils.checkMetadataIntegrityException(ConstantUtils.PROP_SOPORTE_QNAME, ConstantUtils.SOPORTE_DIGITAL, ConstantUtils.PROP_EXTENSION_FORMATO_QNAME);
			}
		}

		// si "eni:estado_elaboracoin" es EE02, EE03 o EE04 - "eni:id_origen" debe estar informado
		if ((ConstantUtils.ESTADO_ELABORACION_02.equals(this.getProperty(properties, ConstantUtils.PROP_ESTADO_ELABORACION_QNAME))
				/*|| ConstantUtils.ESTADO_ELABORACION_03.equals(this.getProperty(properties, ConstantUtils.PROP_ESTADO_ELABORACION_QNAME))*/
				|| ConstantUtils.ESTADO_ELABORACION_04.equals(this.getProperty(properties,	ConstantUtils.PROP_ESTADO_ELABORACION_QNAME)))
				&&
				this.getProperty(properties, ConstantUtils.PROP_ID_ORIGEN_QNAME) == null) {
			throw exUtils.checkMetadataIntegrityException(ConstantUtils.PROP_ESTADO_ELABORACION_QNAME,
					ConstantUtils.ESTADO_ELABORACION_02 + /*" or " + ConstantUtils.ESTADO_ELABORACION_03 +*/ " or " + ConstantUtils.ESTADO_ELABORACION_04,
					ConstantUtils.PROP_ID_ORIGEN_QNAME);
		}

    	// Tenemos una property donde se definen series documentales que son del "Registro General"
    	// Si la serie documental del nodo esta entre ellas. tengo que validar unas propiedades que
    	// son obligatorias del aspecto "gdib:trasladado"
    	List<String> registroCentralSeriesList = Arrays.asList(registroCentralSeries.split(ConstantUtils.CSV_SEPARATOR));
    	if(!CollectionUtils.isEmpty(registroCentralSeriesList)){
    		for (String centralRegistry : registroCentralSeriesList) {
				String cod_clasificacion = this.getProperty(properties, ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
				if(centralRegistry.equals(cod_clasificacion)){
					this.validateTrasladadoAspectMetadata(properties);
				}
			}
    	}
    }

    private void validateTrasladadoAspectMetadata(List<Property> properties) throws GdibException{
    	// validar gdib:fecha_traslado
    	if(checkEmptyProperty(properties, ConstantUtils.PROP_FECHA_TRASLADO_QNAME))
    		throw exUtils.checkMetadataValueException(ConstantUtils.PROP_FECHA_TRASLADO,  "null or empty");

    	// validar gdib:autor_traslado
    	if(checkEmptyProperty(properties, ConstantUtils.PROP_AUTOR_TRASLADO_QNAME))
    		throw exUtils.checkMetadataValueException(ConstantUtils.PROP_AUTOR_TRASLADO,  "null or empty");

    	// validar gdib:destino_traslado
    	if(checkEmptyProperty(properties, ConstantUtils.PROP_DESTINO_TRASLADO_QNAME))
    		throw exUtils.checkMetadataValueException(ConstantUtils.PROP_DESTINO_TRASLADO,  "null or empty");

    	// validar gdib:id_nodo_nueva_loc
    	if(checkEmptyProperty(properties, ConstantUtils.PROP_ID_NODO_NUEVA_LOC_QNAME))
    		throw exUtils.checkMetadataValueException(ConstantUtils.PROP_ID_NODO_NUEVA_LOC,  "null or empty");

    	// validar gdib:tipo_destino_traslado
    	if(checkEmptyProperty(properties, ConstantUtils.PROP_TIPO_DESTINO_QNAME))
    		throw exUtils.checkMetadataValueException(ConstantUtils.PROP_TIPO_DESTINO,  "null or empty");
    }

    /**
     * Verifico
     *  que el tipo del Nodo sea valido dentro del repositorio
     *
     * @param node
     *            a verificar
     * @throws GdibException
     */
    public void checkNodeType(final String type) throws GdibException {
        if (StringUtils.isEmpty(type))
            throw exUtils.nullParamException("node type");

        QName typeQ = createQName(type);
        if (dictionaryService.getType(typeQ) == null) {
            throw exUtils.checkParamsException("node Type", type);
        }
    }

    /**
     * Comprueba que todas las propiedades existan en el modelo de Alfresco y tengan un valor valido
     * en el caso de que sea una propiedad de tipo Lista
     *
     * @param properties
     *            a validar
     * @throws GdibException
     */
    public void checkValidProperties(final List<Property> properties) throws GdibException {
        // recorro la lista de propiedades del nodo
        for (Property prop : properties) {
        	String propKey = prop.getQname();
	        // transformo la propiedad a Qname
            QName qname = createQName(propKey);

	            // verifico si existe en el modelo
	            if (dictionaryService.getProperty(qname) == null)
	                throw exUtils.checkMetadataException(propKey);

	            // si el metadato es de tipo lista, que el valor este permitido
	            CaibConstraintValues values = CaibConstraintValues.getByName(qname.getLocalName());
	            if (values != null) {
	                if (!values.getValues().contains(prop.getValue())) {
	                    throw exUtils.checkMetadataValueException(qname.toString(), prop.getValue());
	                }
	            }
            }

    }

    /**
     * Verifica los metadatos obligatorios que debe de tener un nodo
     *
     * @param type tipo de nodo
     * @param properties metadatos a verificar
     * @param create indica si estamos en el paso de crear un nodo o no
     * @throws GdibException
     */
	public void checkNodeMandatoryMetadata(Node node) throws GdibException {
		// comprobacion para los nodos de tipo documento
		if (isType(node.getType(), ConstantUtils.TYPE_DOCUMENTO_QNAME)) {
			checkNodeMandatoryMetadata(node.getProperties(), Arrays.asList(caibDocumentMetadata.split(ConstantUtils.CSV_SEPARATOR)));
		}
		if (isType(node.getType(), ConstantUtils.TYPE_EXPEDIENTE_QNAME)) {
			// comprobacion para los nodos de tipo expediente
			checkNodeMandatoryMetadata(node.getProperties(), Arrays.asList(caibExpedienteMetadata.split(ConstantUtils.CSV_SEPARATOR)));
		}

	}

    private void checkNodeMandatoryMetadata(List<Property> list, List<String> mandatoryMetadataList) throws GdibException{
    	list = this.filterCalculatedProperties(list);
    	List<Property> newlist = transformListPropertyStringToQname(list);
		for (String metadata : mandatoryMetadataList) {
    		QName metadataQ = createQName(metadata);
    		int index = newlist.indexOf(new Property(metadataQ));
    		if(index == -1 || StringUtils.isEmpty(newlist.get(index).getValue())){
    			throw exUtils.checkMandatoryMetadataException(metadata);
    		}
    	}
    }

    public List<Property> transformListPropertyStringToQname(final List<Property> list) throws GdibException{
    	List<Property> res = new ArrayList<Property>();
    	for (Property prop : list) {
			res.add(new Property(createQName(prop.getQname()), prop.getValue()));
		}
    	return res;
    }

    /**
     * Trim a todos los elementos de un array.
     * */
    private String[] trim(String[] cadena){
    	for(int i =0; i < cadena.length;i++){
    		cadena[i] = cadena[i].trim();
    	}
    	return cadena;
    }

    /**
     * Transformo un mapa de propiedad List<Property> al formato de Alfresco <QName, Serializable> teniendo en cuenta si la propiedad es
     * multiple transformarla a una lista.
     *
     * @param list
     * @return
     * @throws GdibException
     */
    public Map<QName,Serializable> transformMapStringToQname(List<Property> list) throws GdibException {
        Map<QName,Serializable> props = new HashMap<QName,Serializable>();
        Iterator<Property> it = list.iterator();
        while( it.hasNext()){
            Property prop = it.next();
            //Ignoro las propiedades que se van a borrar.
            if ( !prop.getQname().startsWith(ConstantUtils.REMOVE_PROPERTY_TOKEN)){
	            QName qname = createQName(prop.getQname());
	            if(qname != null){
	            	//Si la propiedad es multiple construimos un array
	            	if(dictionaryService.getProperty(qname).isMultiValued()){
	            		String concatenedValue = prop.getValue().toString();
	            		List<String> values = null;
	            		if(concatenedValue.startsWith("[")){
	            			concatenedValue = concatenedValue.replace("[", "").replace("]", "");
	            			if(!StringUtils.isEmpty(concatenedValue)){
		            			values = Arrays.asList(trim(concatenedValue.split(",")));
		            		}
	            		}else{
		            		if(!StringUtils.isEmpty(concatenedValue)){
		            			values = Arrays.asList(trim(concatenedValue.split(ConstantUtils.CSV_SEPARATOR)));
		            		}
	            		}
	            		props.put( qname, (Serializable) values );
	            	} else{
	            		Serializable value = prop.getValue();
	            		props.put( qname, value  );
	            	}
	            }
            }
        }
        return props;
    }



    /**
     * Transformo una lista de String a Lista de Qname
     *
     * @param list
     * @return
     * @throws GdibException
     */
    public List<QName> transformListStringToQname(List<String> list) throws GdibException {
        List<QName> props = new ArrayList<QName>();
        for (String str : list) {
        	if(!StringUtils.isEmpty(str))
        		props.add(createQName(str));
        }
        return props;
    }

    public void exists(NodeRef node) throws GdibException{
        if(!nodeService.exists(node))
            throw exUtils.nodeNotFoundException("nodeId", node.getId());
    }

    public QName createNameQName(String name){
        return QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);
    }

    /**
     * Transformo la cadena de string a un valor QName.
     *<ul>
     * <li>Si el parametro es una propiedad de tipo calculada se devuelve null, para indicar que no se debe tener en cuanta</li>
     * <li>Si el parametro es una propiedad de eliminacion, se elimina el caracter de eliminacion '-' y se pasa el qname</li>
     *</ul>
     * @param s
     * @return
     * @throws GdibException
     */
    public static QName createQName(String s) throws GdibException{
        QName qname = null;
        try {
        	// ignorar los metadatos que son calculados
        	if (s.indexOf(ConstantUtils.CALCULATED_MODEL_PREFIX) != -1)
        		return null;
        	if (s.indexOf(ConstantUtils.REMOVE_PROPERTY_TOKEN) != -1)
        		s = s.substring(1);

            if (s.indexOf(ConstantUtils.NAMESPACE_BEGIN) != -1) {
                // viene con el URI
                qname = QName.createQName(s);
            } else {
                qname = QName.createQName(s, namespaceService);
            }
        } catch (NamespaceException ex) {
            // casos como "inventado:inventado"
        	// cambio la cadena vacio por comillas simples
        	if(ConstantUtils.BLANK.equals(s))
    			s = ConstantUtils.BLANK_TEXT;
            throw exUtils.invalidQnameException(s);
        }
        return qname;
    }

    public String convertValidPath(String path){
        Matcher matcher = ConstantUtils.START_PATH_PATTERN.matcher(path);
        path = matcher.replaceAll("");
        matcher = ConstantUtils.END_PATH_PATTERN.matcher(path);
        path = matcher.replaceAll("");
        if(path.equals(""))
            return "";
        String[] folders = ConstantUtils.PATH_PATTERN.split(path);
        path = "";

        //Reemplazar Sitios => st:sites
        if ( folders[0].trim().equalsIgnoreCase(ConstantUtils.SITE_FOLDER_NAME) )
            folders[0] = ConstantUtils.SITES_PATH;

        for (String folder : folders){
            if(folder.trim().contains(ConstantUtils.PREFIX_SEPARATOR)){
                String [] preffixAndName=folder.trim().split(ConstantUtils.PREFIX_SEPARATOR);
                path += ConstantUtils.PATH_SEPARATOR +
                        preffixAndName[0]+ ConstantUtils.PREFIX_SEPARATOR + ISO9075.encode(preffixAndName[1].trim());
            }else{
                path += ConstantUtils.PATH_SEPARATOR + ConstantUtils.ALFRESCO_CONTENT_MODEL_PREFIX + ConstantUtils.PREFIX_SEPARATOR + ISO9075.encode(folder.trim()); // /cm:<folder>
            }
        }
        return path;
    }

    /**
     * Paso un string a NodeRef, sin validar
     *
     * @param nodeId
     * @return
     */
    public NodeRef toNodeRef(String nodeId){
        return new NodeRef(ConstantUtils.SPACESSTORE_PREFIX + nodeId);
    }

    /**
     * Transformo el parametro nodeId a un NodeRef de alfresco. El valor del
     * nodeId puede llegar de cuatro formas distintas
     * <ol>
     * <li>Ruta relativa al nodo, desde el id de un nodo. Ej.: b24eeb92-aed8-439c-af4d-db25785b2fc4/ruta/al/nodo
     * <li>Version de un nodo. Ej.: 1.1@b24eeb92-aed8-439c-af4d-db25785b2fc4
     * <li>Ruta absoluta al nodo. Ej.: b24eeb92-aed8-439c-af4d-db25785b2fc4
     * </ol>
     *
     * @param nodeId
     *            string a tranformar en NodeRef
     * @return NodeRef, null si no cumple ningun de los casos anteriores o el caso de ruta relativa el nodo final no existe
     * @throws GdibException
     */
    public NodeRef idToNodeRef(String nodeId) throws GdibException {
    	// Orden: uid/path_relatico > version@uid > uid
    	Matcher mat = null;

    	if (nodeId == null || "".equals(nodeId))
    		return null;

    	mat = ConstantUtils.UUID_PATTERN.matcher(nodeId);
        if (mat.matches()) {
        	return idToNodeRefUuidPattern(nodeId);
        }

    	mat = ConstantUtils.PATH_REL_PATTERN.matcher(nodeId);
        if (mat.matches()) {
        	return idToNodeRefPathRelPattern(nodeId);
        }

        mat = ConstantUtils.VERSION_UUID_PATTERN.matcher(nodeId);
        if (mat.matches()) {
        	return idToNodeRefVersionPattern(nodeId);
        }

        return null;
    }

	/**
	 * Transfroma una cadena de texto a un uuid de alfresco. El formato de la cadena de texto
	 * (Ej.: b24eeb92-aed8-439c-af4d-db25785b2fc4/ruta/al/nodo) a un uuid de alfresco,
	 * si no existe devuelve null
	 *
	 * @param nodeId
	 *            cadena de texto con la ruta relativa a un nodo
	 * @return NodeRef de alfresco si existe, null si no existe
	 * @throws GdibException
	 */
	private NodeRef idToNodeRefPathRelPattern(String nodeId) throws GdibException {
		return this.idToNodeRefPathRelPattern(nodeId, false);
	}

	/**
	 * Transfroma una cadena de texto a un uuid de alfresco. El formato de la cadena de texto
	 * (Ej.: b24eeb92-aed8-439c-af4d-db25785b2fc4/ruta/al/nodo).
	 * Con el parametro de createPath se indica si el nodo final se debe crear si no existe o no.
	 *
	 * @param nodeId
	 *            cadena de texto con la ruta relativa a un nodo
	 * @param createPath
	 *            indica si el path hacia el nodo de la ruta relativa se debe de crear
	 * @return NodeRef de alfresco
	 * @throws GdibException
	 */
	private NodeRef idToNodeRefPathRelPattern(String nodeId, boolean createPath) throws GdibException {
		Matcher mat = ConstantUtils.PATH_REL_PATTERN.matcher(nodeId);
		if (mat.matches()) {
			// GROUP(1): uid padre
			// GROUP(2): ruta relativa
			String uid = mat.group(1);
			String rutaRel = mat.group(2);

			NodeRef uidPadre = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, uid);

			// el uuid del path relativo siempre tiene que ser un expediente
			if(nodeService.exists(uidPadre)){
				QName type = nodeService.getType(uidPadre);
				// me salto este paso si esta desactivado los check principales del repositorio
				if (!isType(nodeService.getType(uidPadre), ConstantUtils.TYPE_EXPEDIENTE_QNAME)
						&& this.repositoryDisableCheck.booleanValue() == false)
					throw exUtils.invalidTypeException(type.getLocalName());

				if (createPath) {
					return this.generateExpedientTree(uidPadre, this.splitPath(rutaRel));
				} else {
					// Si no existe el nodo devolvemos null => returnLastValidPath = false
					return getUIDFromPath(rutaRel, uidPadre, false);
				}
			}else{
				throw exUtils.nodeNotFoundException("parentId", uidPadre.getId());
			}
		}
		return null;
	}

	/**
	 * Transfroma una cadena de texto a un uuid de alfresco. El formato de la cadena de texto
	 * (Ej.: 1.1@b24eeb92-aed8-439c-af4d-db25785b2fc4).
	 *
	 * @param nodeId
	 *            cadena de texto con el uuid de un nodo de alfresco y con la version
	 * @return NodeRef de alfresco. Si no existe devuelve null
	 * @throws GdibException
	 */
	private NodeRef idToNodeRefVersionPattern(String nodeId) throws GdibException {
		Matcher mat = ConstantUtils.VERSION_UUID_PATTERN.matcher(nodeId);
		if (mat.matches()) {
			// GROUP(1): version
			// GROUP(2): uid
			String version = mat.group(1);
			String uid = mat.group(2);

			NodeRef node = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, uid);
			NodeRef nodeVersion = getVersion(node, version);
			return nodeVersion;
		}
		return null;
	}

	/**
	 * Transfroma una cadena de texto a un uuid de alfresco Formato de la cadena
	 * de texto Ej: b24eeb92-aed8-439c-af4d-db25785b2fc4
	 *
	 * @param nodeId
	 *            cadena de texto con uuid de un nodo de alfresco
	 * @return NodeRef de alfresco
	 */
	private NodeRef idToNodeRefUuidPattern(String nodeId) {
		Matcher mat = ConstantUtils.UUID_PATTERN.matcher(nodeId);
		if (mat.matches()) {
			// GROUP(1): uid
			String uid = mat.group(1);
			return new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, uid);
		}
		return null;
	}

    public NodeRef getUIDFromPath(String path){
        return getUIDFromPath(path,false);
    }

    public NodeRef getUIDFromPath(String path, NodeRef base){
        return getUIDFromPath(path,base,false);
    }

    public NodeRef getUIDFromPath(String path, boolean returnLastValidPath){
        path = path.trim();
        if ( ! checkPathBeginWithSlash(path) )
            return null;
        return getUIDFromPath(path.substring(1), this.getRootDM(), returnLastValidPath);
    }

    public NodeRef getRootDM(){
        return this.toNodeRef(rootDM);
    }

    public NodeRef getRootMigration(){
    	return this.toNodeRef(rootMigration);
    }

    public boolean checkPathBeginWithSlash(String path){
        if ( !path.startsWith("/") && !path.startsWith("\\"))
            return false;
        return true;
    }

    public NodeRef getUIDFromPath(String path, NodeRef base, boolean returnLastValidPath) {
        NodeRef ret = base;
        String [] hijos = null;
        hijos = splitPath(path);
        for (int i = 0 ; i < hijos.length ; i++ )
            if ( hijos[i]!= null && ! "".equals(hijos[i].trim() ) ){
                NodeRef previous=ret;
                ret = fileFolderService.searchSimple(ret, hijos[i]);
                if(ret == null){
                    return returnLastValidPath?previous:null;
                }
            }
        return ret;
    }

    public String[] splitPath(String path) {
        Matcher matcher = ConstantUtils.START_PATH_PATTERN.matcher(path);
        path = matcher.replaceAll("");
        matcher = ConstantUtils.END_PATH_PATTERN.matcher(path);
        path = matcher.replaceAll("");

        return ConstantUtils.PATH_PATTERN.split(path);
    }

    public boolean contains(Map<String,String> props, String property) throws GdibException{
        QName seed = createQName(property);
        Iterator<String> it = props.keySet().iterator();
        while (it.hasNext()){
            String key = it.next();
            QName compare = createQName(key);
            if ( compare.equals(seed))
                return true;
        }
        return false;
    }

    public boolean contains(List<String> qnames, QName qname) throws GdibException{
        Iterator<String> it = qnames.iterator();
        while (it.hasNext()){
            QName searchQname = createQName(it.next());
            if ( qname.equals(searchQname))
                return true;
        }
        return false;
    }

    public boolean contains(List<String> qnames, String qname) throws GdibException{
        QName searchQname = createQName(qname);
        return contains(qnames, searchQname);
    }

    private boolean checkEmptyProperty(List<Property> properties, QName property) throws GdibException{
    	int index = properties.indexOf(new Property(property));
    	if(index < 0)
    		return true;
    	String value = properties.get(index).getValue();
    	if (value == null || StringUtils.isEmpty(value))
			return true;
    	else
    		return false;
    }

    public String getProperty(NodeRef nodeRef, QName property)throws GdibException{
    	return this.getProperty( getProperties(nodeRef), property);
    }

    public String getProperty(List<Property> props,QName property) throws GdibException{
    	Iterator<Property> it = props.iterator();
        while (it.hasNext()){
            Property prop = it.next();
            QName compare = createQName(prop.getQname());
            if ( property.equals(compare))
                return prop.getValue().toString();
        }
        return null;
    }

    public String getProperty(Map<String,String> props, QName property) throws GdibException{
        Iterator<String> it = props.keySet().iterator();
        while (it.hasNext()){
            String key = it.next();
            QName compare = createQName(key);
            if ( property.equals(compare))
                return props.get(key);
        }
        return null;
    }

    public String getProperty(Map<String,String> props, String property) throws GdibException{
        QName prop = createQName(property);
        return getProperty(props,prop);
    }

    public String getProperty(List<Property> properties, String property) throws GdibException{
		QName prop = createQName(property);
		return getProperty(properties,prop);
	}

    public String getAspect(List<String> aspects, QName aspectQname) throws GdibException{
        for (String aspect : aspects) {
            QName compare = createQName(aspect);
            if ( compare.equals(aspectQname))
                return aspect;
        }
        return null;
    }

    public String getAspect(List<String> aspects, String aspect) throws GdibException{
        QName aspectQname = createQName(aspect);
        return getAspect(aspects,aspectQname);
    }

    /**
     * Obtengo el ContentReader de un campo cm:content del nodo.
     *
     * @param node
     * @return contentReader
     * @throws GdibException
     */
    public ContentReader getContentReader(NodeRef node, QName prop) {
        ContentReader reader = null;
        if(node != null){
            reader = contentService.getReader(node, prop);
        }
        return reader;
    }

    /**
     * Obtengo el {@link Content} del contenido del nodo
     *
     * @param node
     * @return content
     * @throws GdibException
     */
    public Content getContent(NodeRef node) throws GdibException {
       return getContent(node, ConstantUtils.PROP_CONTENT);
    }

    /**
     * Obtengo el {@link Content} del contenido del nodo
     *
     * @param node
     * @return content
     * @throws GdibException
     */
    public Content getContent(NodeRef node, Boolean withContent) throws GdibException {
        if(withContent==null) {
            return getContent(node, ConstantUtils.PROP_CONTENT);
        }else{
            return getContent(node, ConstantUtils.PROP_CONTENT,withContent);
        }
    }

    /**
     * Obtengo el {@link Content} de una propiedad de tipo content de un nodo
     *
     * @param node
     * @return content
     * @throws GdibException
     */
    public Content getContent(NodeRef node, QName qname, boolean withContent) throws GdibException {
        Content content = null;
        if(node != null){
            try {
                ContentReader reader = getContentReader(node, qname);
                if(reader!=null){
                    content = new Content();
                    if(withContent) {
                        DataHandler handler = new DataHandler(new InputStreamDataSource(reader.getContentInputStream()));
                        content.setData(handler);
                    }
                    if(isType(nodeService.getType(node),ConstantUtils.TYPE_DOCUMENTO_QNAME)) {
                        content.setByteSize(reader.getSize());
                        content.setEncoding(reader.getEncoding());
                        content.setMimetype(reader.getMimetype());
                    }
                }
            } catch (ContentIOException e) {
                LOGGER.error("Error en el getContent: "+e);
                throw exUtils.invalidContent(node.getId(),e);
            }
        }
        return content;
    }

    /**
     * Obtengo el {@link Content} de una propiedad de tipo content de un nodo
     *
     * @param node
     * @return content
     * @throws GdibException
     */
    public Content getContent(NodeRef node, QName qname) throws GdibException {
        Content content = null;
        if(node != null) {
            try {
                ContentReader reader = getContentReader(node, qname);
                if (reader != null) {
                    content = new Content();
                    DataHandler handler = new DataHandler(new InputStreamDataSource(reader.getContentInputStream()));
                    content.setData(handler);
                    content.setEncoding(reader.getEncoding());
                    content.setMimetype(reader.getMimetype());
                }
            } catch (ContentIOException e) {
                LOGGER.error("Se ha producido un error en el getContent: "+e);
                throw exUtils.invalidContent(node.getId(),e);
            }
        }
        return content;
    }

    /**
     * Obtengo el dataHandler de una propiedad tipo content
     *
     * @param node
     * @return
     * @throws GdibException
     */
    public DataHandler getDataHandler(NodeRef node, QName prop) throws GdibException {
        DataHandler data = null;
        if(node != null){
            ContentReader reader = getContentReader(node, prop);
            if(reader!=null){
                data = new DataHandler(new InputStreamDataSource(reader.getContentInputStream()));
            }
        }
        return data;
    }

    /**
     * Obtengo los aspectos del nodo. Se estan evitando recuperar los aspectos propios del sistema
     *
     * @param node
     * @return
     */
    public List<String> getAspects(NodeRef alfNode) {
        List<String> aspects = new ArrayList<String>();
        for (QName aspect : nodeService.getAspects(alfNode)) {
            // solo estoy metiendo los aspectos del modelo de gdib, evitando meter los aspectos del sistema
            if(!aspect.getNamespaceURI().contains(ConstantUtils.NS_SYSTEM_MODEL)){
                aspects.add(formatQname(aspect));
            }
        }
        return aspects;
    }

    /**
     * Obtengo las propiedades del nodo. Se estan evitando recuperar las propiedades propias del sistema
     *
     * @param node
     * @return mapa de propiedades del nodo
     * @throws GdibException
     */
	public Map<String, String> getPropertiesOld(NodeRef node) throws GdibException {
		Map<String, String> properties = new HashMap<String, String>();
		Map<QName, Serializable> propertiesNode = nodeService.getProperties(node);
		for (QName property : propertiesNode.keySet()) {
			// solo estoy metiendo las propiedades del modelo de gdib, evitando meter los aspectos del sistema
			Object prop = propertiesNode.get(property);
			if (!property.getNamespaceURI().contains(ConstantUtils.NS_SYSTEM_MODEL) && prop != null) {
				if (prop instanceof Date) {
					//properties.put(formatQname(property), new SimpleDateFormat("yyyy-MM-dd").format(prop));
					properties.put(formatQname(property), ISO8601DateFormat.format((Date) prop));
				} else {
					properties.put(formatQname(property), prop.toString());
				}
			}
		}
		return properties;
	}

	public List<Property> getProperties(NodeRef node) throws GdibException {
		List<Property> properties = new ArrayList<Property>();
		Map<QName, Serializable> propertiesNode = nodeService.getProperties(node);
		for (QName property : propertiesNode.keySet()) {
			// solo estoy metiendo las propiedades del modelo de gdib, evitando meter los aspectos del sistema
			Object prop = propertiesNode.get(property);
			if (!property.getNamespaceURI().contains(ConstantUtils.NS_SYSTEM_MODEL) && prop != null) {
				if (prop instanceof Date) {
					//properties.put(formatQname(property), new SimpleDateFormat("yyyy-MM-dd").format(prop));
					properties.add(new Property(formatQname(property), ISO8601DateFormat.format((Date) prop)));
				} else if (prop instanceof java.util.ArrayList){
					properties.add(new Property(formatQname(property), prop.toString().replace(", ", ",")));
				} else {
					properties.add(new Property(formatQname(property), prop.toString()));
				}
			}
		}
		return properties;
	}

    /**
     * Obtengo las propiedades calculadas del nodo
     *
     * @param node
     * @return mapa de propiedades calculadas del nodo
     * @throws GdibException
     */
    public List<Property> getPropertiesCalculated(NodeRef nodeRef) throws GdibException {
    	List<Property> propertiesCalculated = new ArrayList<Property>();
        /**
         * Propiedades calculadas:
         * - Tamaño contenido (En Mb)
         * - Ruta del nodo. (Ruta friendly) -
         * - Repositorio ( Migracion o DM o RM ) Sites
         * - TODO: Mas.
         *
         * Revisar el nombre de las propiedades calculadas
         */
        ContentReader cR = getContentReader(nodeRef, ConstantUtils.PROP_CONTENT);
        if (cR != null) {
            propertiesCalculated.add(new Property(formatQnameCalculated(ConstantUtils.CALCULATED_FILESIZE), String.valueOf(cR.getSize())));
        }
        String path = this.getPathFromUID(nodeRef);
        propertiesCalculated.add(new Property(formatQnameCalculated(ConstantUtils.CALCULATED_PATH), path));

        String[] site = path.split(ConstantUtils.PATH_SEPARATOR);
        propertiesCalculated.add(new Property(formatQnameCalculated(ConstantUtils.CALCULATED_SITE), site.length<4?"":site[3]));

        String primaryParent = nodeService.getPrimaryParent(nodeRef).getParentRef().getId();
        propertiesCalculated.add(new Property(formatQnameCalculated(ConstantUtils.CALCULATED_PARENT),primaryParent ));

        return propertiesCalculated;
    }

    public String getNodeType(NodeRef node) {
        QName type = null;
        if(node!=null){
            type = nodeService.getType(node);
        }
        return formatQname(type);
    }

    /**
     * Transformo el qname al formato definido en una property del proyecto. Puede ser {uri}name o prefix:name
     *
     * @param qname qname a transformar
     * @return cadena de texto con el valor
     */
    public String formatQname(QName qname){
        String value = "";
        if(qname != null){
            if(Boolean.valueOf(gdibAmpPropertiesAplicationFilter.getProperty(authenticationService.getCurrentUserName(), ConstantUtils.GDIB_REPOSITORY_QNAME_PREFIX))){
                String prefix = null;
                Collection<String> prefixList = namespaceService.getPrefixes(qname.getNamespaceURI());
                if(!CollectionUtils.isEmpty(prefixList) && prefixList.size() == 1){
                    prefix = prefixList.iterator().next();
                }
                value = prefix==null?"":prefix+ConstantUtils.PREFIX_SEPARATOR+qname.getLocalName();

            }else{
                value = ConstantUtils.NAMESPACE_BEGIN + qname.getNamespaceURI() + ConstantUtils.NAMESPACE_END + qname.getLocalName();
            }
        }
        return value;
    }

    /**
     * Transformo el string qname al formato definido en una property del proyecto. Puede ser {uri}name o prefix:name
     *
     * @param qname qname a transformar
     * @return cadena de texto con el valor
     */
    public String formatQname(String qname){
        String value = "";
        if(qname != null){
            String[] qnameArray = qname.split(ConstantUtils.PREFIX_SEPARATOR);
            if(Boolean.valueOf(gdibAmpPropertiesAplicationFilter.getProperty(authenticationService.getCurrentUserName(), ConstantUtils.GDIB_REPOSITORY_QNAME_PREFIX))){
                value = qname;
            }else{
                String uri = namespaceService.getNamespaceURI(qnameArray[0]);
                uri = uri==null?"":uri;
                value = ConstantUtils.NAMESPACE_BEGIN + uri + ConstantUtils.NAMESPACE_END + qnameArray[1];
            }
        }
        return value;
    }

    public String formatQnameCalculated(String name){
        String value = "";
        if(Boolean.valueOf(gdibAmpPropertiesAplicationFilter.getProperty(authenticationService.getCurrentUserName(), ConstantUtils.GDIB_REPOSITORY_QNAME_PREFIX))){
            value = ConstantUtils.CALCULATED_MODEL_PREFIX + name;
        }else{
            value = ConstantUtils.NAMESPACE_BEGIN + ConstantUtils.CALCULATED_URI + ConstantUtils.NAMESPACE_END + name;
        }
        return value;
    }

    /**
     * Añade una lista de aspectos al nodo recibido por parametro
     *
     * @param nodeId
     * @param aspects
     * @throws GdibException
     */
    public void addAspects(String nodeId, List<QName> aspects) throws GdibException {
        NodeRef node = this.idToNodeRef(nodeId);
        addAspects(node, aspects);
    }

    public boolean addAspects(NodeRef node, List<QName> aspects) throws GdibException {
    	 if(aspects.size() == 0){
         	return Boolean.FALSE;
         }
        for (QName aspect : aspects) {
            nodeService.addAspect(node, aspect, null);
        }
        return Boolean.TRUE;
    }

    public void addAspect(NodeRef node, QName aspect, Map<QName, Serializable> prop) throws GdibException {
        nodeService.addAspect(node, aspect, prop);
    }

    public boolean addProperties(NodeRef node, List<Property> properties) throws GdibException{
        Map<QName,Serializable> prop = this.transformMapStringToQname(properties);
        if(properties.size() == 0){
        	return Boolean.FALSE;
        }
        nodeService.addProperties(node, prop);
        return Boolean.TRUE;
    }

    public boolean removeProperties(NodeRef node, Map<String, String> properties) throws GdibException{
    	if(properties.size() == 0){
        	return Boolean.FALSE;
        }
        for (String removeKey : properties.keySet()) {
            nodeService.removeProperty(node, createQName(removeKey));
        }
        return Boolean.TRUE;
    }

    /**
     * Compare dos datahandlers para saber si el dos contenidos son iguales
     *
     * @param d1
     * @param d2
     * @return true si son iguales, false si no lo son
     * @throws GdibException
     */
    public boolean compareDataHandlers(DataHandler d1, DataHandler d2) throws GdibException{
		byte[] byteArray1;
		byte[] byteArray2;
		byteArray1 = getByteArrayFromHandler(d1);
		byteArray2 = getByteArrayFromHandler(d2);
		if (Arrays.equals(byteArray1, byteArray2)) {
			return true;
		} else {
			return false;
		}
    }

	/**
	 * Obtengo el ByteArray de un datahandler
	 *
	 * @param dataHandler
	 * @return byte[] del handler
	 * @throws GdibException
	 */
	public byte[] getByteArrayFromHandler(DataHandler dataHandler) throws GdibException {
		if(dataHandler == null){
			throw exUtils.getByteFromDataHandlerException("Datahandler is null", null);
		}
		byte[] byteArray = null;
		try {
			byteArray = IOUtils.toByteArray(dataHandler.getInputStream());
		} catch (IOException exception) {
			throw exUtils.getByteFromDataHandlerException(exception.getMessage(), exception);
		}
		return byteArray;
	}

    /**
     * Filtro un mapa de propiedades para eliminar las propiedades que son de eliminar
     * eliminar
     *
     * @param list
     * @return
     */
    public List<Property> filterRemoveMetadata(List<Property> list) {
        List<Property> filterProperties = new ArrayList<Property>();
        if(!CollectionUtils.isEmpty(list)){
	        for (Property prop : list) {
	            if (!prop.getQname().startsWith(ConstantUtils.REMOVE_PROPERTY_TOKEN))
	                filterProperties.add(new Property(prop.getQname(), prop.getValue()));
	        }
        }
        return filterProperties;
    }

    public List<String> filterRemoveAspects(List<String> list) {
        List<String> filterProperties = new ArrayList<String>();
        for (String key : list) {
            if (!key.startsWith(ConstantUtils.REMOVE_PROPERTY_TOKEN))
                filterProperties.add(key);
        }
        return filterProperties;
    }

	/**
	 * Obtengo las propiedades a actualizar del nodo. Si el valor viene a null
	 * es que son propiedades que se van a eliminar
	 *
	 * @param updateProperties
	 *            propiedades que llegan para actualizar
	 * @param originalProperties
	 *            propiedades del nodo originalmente
	 * @return mapa con las propiedades a actualizar
	 * @throws GdibException
	 */
	public Map<QName, Serializable> getModifyProperties(List<Property> updateProperties,
			List<Property> originalProperties) throws GdibException {
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		Map<QName, Serializable> originalPropertiesQname = this.transformMapStringToQname(originalProperties);
		Map<QName, Serializable> updatePropertiesQname = this.transformMapStringToQname(updateProperties);
		for ( Property prop : updateProperties) {
			// primero descarto las propiedades calculadas
			String keyUpdate = prop.getQname();
			if (!keyUpdate.contains(ConstantUtils.CALCULATED_PREFIX)
					&& !keyUpdate.contains(ConstantUtils.CALCULATED_URI)) {
				// si la propiedad es de borrado, meto la propiedad con valor null para que se elimine
				if (keyUpdate.startsWith(ConstantUtils.REMOVE_PROPERTY_TOKEN)) {
					properties.put(createQName(keyUpdate.substring(1)), null);
				} else {
					// sino comparo con el valor original para ver si se ha modificado
					QName qname = createQName(keyUpdate);
					if (!updatePropertiesQname.get(qname).equals(originalPropertiesQname.get(qname)))
						properties.put(qname, updatePropertiesQname.get(qname));
				}
			}
		}
		return properties;
	}

    /**
     * Filtro una lista de propiedades para descartar las que son calculadas
     *
     * @param list
     * @return
     */
    public List<Property> filterCalculatedProperties(List<Property> list) {
        List<Property> filterProperties = new ArrayList<Property>();
        if(!CollectionUtils.isEmpty(list)){
	        for (Property prop : list) {
	        	String updateKey = prop.getQname();
	            if (!updateKey.contains(ConstantUtils.CALCULATED_PREFIX)
	                    && !updateKey.contains(ConstantUtils.CALCULATED_URI)) {
	                filterProperties.add(new Property(prop.getQname(), prop.getValue()));
	            }
	        }
        }
        return filterProperties;
    }

    /**
     * Comprueba si el la referencia del nodo es una version
     *
     * @param nodeRef
     * @throws GdibException si es una version
     */
    public void checkIsAVersion(NodeRef nodeRef) throws GdibException {
        if(versionService.isAVersion(nodeRef))
            throw exUtils.isAVersionException(nodeRef.getId());
    }

    @SuppressWarnings("deprecation")
    public List<NodeVersion> getVersionList(NodeRef node) {
        List<NodeVersion> versionList = new ArrayList<NodeVersion>();
        NodeVersion nodeVersion = null;

        VersionHistory history = versionService.getVersionHistory(node);
        if(history!=null){
        	for (Version version : history.getAllVersions()) {
	            nodeVersion = new NodeVersion();

	            Date createdDate = version.getCreatedDate();
	            String label = version.getVersionLabel();

	            //nodeVersion.setDate(new SimpleDateFormat("yyyy-MM-dd").format(createdDate));
	            nodeVersion.setDate(ISO8601DateFormat.format(createdDate));
	            nodeVersion.setId(label);
	            versionList.add(nodeVersion);
	        }
        }
        return versionList;
    }

    public NodeRef getVersion(NodeRef node, String version) throws GdibException {
        NodeRef ref = null;
        try{
            VersionHistory verHistory = versionService.getVersionHistory(node);
            if(verHistory == null){
            	throw exUtils.versionNotExitsException(version, node.getId());
            }
            Version ver = verHistory.getVersion(version);
            ref = ver.getFrozenStateNodeRef();
        }catch(VersionDoesNotExistException ex){
            throw exUtils.nodeVersionNotFoundException(node.getId(), version);
        }
        return ref;
    }

    /**
     * Obtiene el nombre (cm:name) del Noderef
     *
     * @param noderef, Noderef del cual se quiere obtener su nombre
     * @return el nombre del Noderef
     */
    public String getNameNode(NodeRef noderef) throws GdibException {
        String name = null;
        if(nodeService.exists(noderef)){
            name = (String) nodeService.getProperty(noderef, ConstantUtils.PROP_NAME);
        }else{
            throw exUtils.nodeNotFoundException("nodeId", noderef.getId());
        }

        return name;
    }

    /**
     * Devuelve true si el node esta bloqueado.
     *
     * @param node
     *            Nodo el cual se va a comprobar su estado.
     *
     * @return true en caso que el nodo este bloqueado.
     * @throws GdibException
     */
    public Boolean isNodeLocked(final NodeRef noderef) throws GdibException {
    	final String userName = AuthenticationUtil.getFullyAuthenticatedUser();

        String result = AuthenticationUtil.runAsSystem(new RunAsWork<String>() {
			// @Override
			public String doWork() throws Exception {
				Boolean locked = Boolean.FALSE;

				if (noderef !=null && nodeService.hasAspect(noderef, ContentModel.ASPECT_LOCKABLE)) {
		            LockStatus lockStatus = lockService.getLockStatus(noderef, userName);
		            if (lockStatus == LockStatus.LOCKED || lockStatus == LockStatus.LOCK_OWNER) {
		                locked = Boolean.TRUE;
		            }
		        }
				return locked.toString();
			}
    	});
        return Boolean.valueOf(result);
    }

    /**
     * Recibido un nodo, verifica si el nodo o cualquier hijo del nodo esta bloqueado
     *
     * @param node
     * @return
     * @throws GdibException
     */
    public boolean isSomeoneLockedDown(NodeRef node) throws GdibException {
		boolean lock = Boolean.FALSE;
		Queue<NodeRef> pilaNodos = new ArrayDeque<NodeRef>();
		pilaNodos.add(node);

		while (!pilaNodos.isEmpty() && Boolean.FALSE.equals(lock)) {
			NodeRef nodo = pilaNodos.poll();
			lock = isNodeLocked(nodo);
			if (fileFolderService.getFileInfo(nodo).isFolder()) {
				List<ChildAssociationRef> hijos = nodeService.getChildAssocs(nodo);
				for (ChildAssociationRef hijo : hijos) {
					// solo añado los hijos que tengan como primaryparent el mismo nodo
					if (nodeService.getPrimaryParent(hijo.getChildRef()).getParentRef().equals(nodo)) {
						pilaNodos.add(hijo.getChildRef());
					}
				}
			}
		}
		return lock;
    }

    /**
     * Obtengo el nodeRef del expediente al que pertenece el nodo pasado por parametro
     *
     * @param node nodo donde buscar
     * @return
     */
    public NodeRef getExpedientNodeRef(NodeRef node){
    	NodeRef result = null;
    	if(this.isType(nodeService.getType(node), ConstantUtils.TYPE_EXPEDIENTE_QNAME)){
    		return node;
    	}else{
    		ChildAssociationRef assoc = nodeService.getPrimaryParent(node);
        	if(assoc.getParentRef() != null){
        		result = getExpedientNodeRef(assoc.getParentRef());
        	}
    	}
    	return result;
    }

    @SuppressWarnings("deprecation")
	public void lockNode(NodeRef node) throws GdibException{
    	 try {
             lockService.lock(node, LockType.READ_ONLY_LOCK);
         } catch (UnableToAquireLockException ex) {
             throw new GdibException(ex.getMessage());
         }
    }

    public void unlockNode(NodeRef nodeRef) throws GdibException {
        try {
            lockService.unlock(nodeRef);
        } catch (UnableToReleaseLockException ex) {
            throw new GdibException(ex.getMessage());
        }
    }

    public void checkVersionParam(String version) throws GdibException {
        if(StringUtils.isEmpty(version))
            throw exUtils.checkParamsException("version", "null or empty");
        if(!ConstantUtils.VERSION_PATTERN.matcher(version).matches())
            throw exUtils.checkParamsException("version", version);
    }

    /**
     * Devuelvo el NodeRef donde se va a crear el nodo.
     * Las validaciones realizadas son:
     * * Exista el nodo
     * * Que sea un expediente o agrupacion
     * * Si la estructura de carpetas necesaria para que exista el nodo no existe, se crea
     * * Que se tenga los permisos necesarios
     * * Que el nodo padre este dentro del DM
     * * Que no este bloqueado
     *
     * @param node nodo a insertar
     * @param parentId uuid del padre donde se va a insertar el nodo
     * @return el NodRef del padre validado
     * @throws GdibException
     */
	public NodeRef checkParentId(Node node, String parentId) throws GdibException {
		NodeRef parentRef = null;

		// si esta vacio el parent id y es un expediente lo que estoy creando
		// tengo que consultar el cuadro de clasificacion para obtener la ruta donde crear el expediente
		if(this.repositoryDisableCheck.booleanValue() == false){
			// me salto este paso si esta desactivado los check principales del repositorio
			if (isType(node.getType(), ConstantUtils.TYPE_EXPEDIENTE_QNAME) && StringUtils.isEmpty(parentId)) {
				String classificationCode = this.getProperty(node.getProperties(), ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
				parentRef = getParentFromClassificationTable(node, classificationCode);
				// si al consultar el cuadro de clasificacion no tengo el nodeRef del padre es que ha habido un error
				// en el cuadro de clasificacion
				if (parentRef == null)
					throw exUtils.checkDocumentarySeriesInClassificationTableExcepcion(classificationCode);
				return parentRef;
			}else{
				if(StringUtils.isEmpty(parentId))
					throw exUtils.checkParamsException("parentId", "null or empty");
			}
		}
		// compruebo que el valor del parentId cumple algun patron, path relativo o uuid
		if(this.validParentId(parentId)){
			parentRef = this.idToNodeRef(parentId);
		}else{
			if(StringUtils.isEmpty(parentId))
				parentId = "null or empty";
			throw exUtils.checkParamsException("parentId", parentId);
		}

		// si al obtener el nodeRef del parentId es null, quiere decir apunta a una carpeta
		// que aun no existe en el repositorio
		if(parentRef == null){
			parentRef = this.idToNodeRefPathRelPattern(parentId, true);
		}else{
			parentRef = this.checkParentId(parentRef);
		}
		return parentRef;
	}

	private NodeRef checkParentId(NodeRef node) throws GdibException{
		NodeRef parentRef = node;
		if(nodeService.exists(parentRef)){
			QName type = nodeService.getType(parentRef);
			// para no dejar crear nodos hijos de un contenido
			if(!isType(type, ConstantUtils.TYPE_FOLDER)){
				throw exUtils.invalidTypeException(type.getLocalName());
			}

			 // comprobar permisos del usuario
		    this.hasPermission(parentRef, CaibServicePermissions.WRITE);

			// compruebo que el padre donde voy a crear el nodo este dentro del DM
		    if(this.repositoryDisableCheck.booleanValue() == false)
		    	// me salto este paso si esta desactivado los check principales del repositorio
		    	this.inDMPath(parentRef);

		    // compruebo si el nodo donde se va a crear esta bloqueado
		    if(parentRef != null && this.isNodeLocked(parentRef)){
		    	throw exUtils.lockedNode(parentRef.getId());
			}
		}else{
			throw exUtils.nodeNotFoundException("nodeId", parentRef.getId());
		}
		return parentRef;
	}

	/**
	 * Consulta el cuadro de clasificacion creado en alfresco. comprobando que la categoria recibida
	 * por parametro exista dentro. Y con este dato puedo crear la estructura de carpetas para obtener
	 * el NodeRef del padre donde se va a crear el nodo
	 *
	 * @param classificationCategory
	 * @return
	 * @throws GdibException
	 */
    public NodeRef getParentFromClassificationTable(Node node, String classificationCategory) throws GdibException{
    	NodeRef nodeParent = null;
    	nodeParent = getParentFromClassificationTable(classificationCategory);
    	if(nodeParent != null){
    		String expedientDate= this.getProperty(node.getProperties(), ConstantUtils.PROP_FECHA_INICIO_QNAME);
    		expedientDate = this.getISO860DateFormat(expedientDate);
       		node.getProperties().add(new Property(ConstantUtils.PROP_FECHA_INICIO_QNAME, expedientDate));
			String functionName = (String) nodeService.getProperty(nodeParent, ConstantUtils.PROP_NAME);
			String path = ConstantUtils.PATH_SEPARATOR + functionName
					+ ConstantUtils.PATH_SEPARATOR + classificationCategory + ConstantUtils.PATH_SEPARATOR + expedientDate.replace("-", ConstantUtils.PATH_SEPARATOR);
			nodeParent = this.generateExpedientTree(toNodeRef(rootDM), this.splitPath(path));
    	}
    	return nodeParent;
    }

    private String getISO860DateFormat(String date){
    	Date expedientDate = null;
    	String expedienteDateString = null;
    	if(StringUtils.isEmpty(date)){
			expedientDate = new Date();
    		expedienteDateString = new SimpleDateFormat("yyyy-MM-dd").format(expedientDate);
    	}else if( date.length() > 10){
			expedientDate = ISO8601DateFormat.parse(date);
			expedienteDateString = new SimpleDateFormat("yyyy-MM-dd").format(expedientDate);
		}else{
			expedienteDateString = date.replace(ConstantUtils.PATH_SEPARATOR, "-");
		}
    	return expedienteDateString;
    }

    private NodeRef getParentFromClassificationTable(String classificationCategory) throws GdibException{
    	if(this.repositoryClassificationTableMode)
    		return getParentFromRM(classificationCategory);
    	else
    		return getParentFromAlfresco(classificationCategory);
    }

    private NodeRef getParentFromAlfresco(String classificationCategory){
    	Collection<ChildAssociationRef> categories = categoryService.getChildren(this.toNodeRef(rootCT),
    			org.alfresco.service.cmr.search.CategoryService.Mode.SUB_CATEGORIES,
    			org.alfresco.service.cmr.search.CategoryService.Depth.ANY);
    	for (ChildAssociationRef child : categories) {
			String categoryName = (String) nodeService.getProperty(child.getChildRef(), ConstantUtils.PROP_NAME);
			if(categoryName.equals(classificationCategory)){
				return nodeService.getPrimaryParent(child.getChildRef()).getParentRef();
			}
    	}
    	return null;
    }

    private NodeRef getParentFromRM(String classificationCategory) throws GdibException{
    	return ccUtils.getFunctionFromDocumentarySeries(classificationCategory);
    }

    /**
     * Compruebo si el tipo es del tipo que se pasa como parametro para comparar
     *
     * @param type tipo a comparar
     * @param compareToType tipo contra el que se compara
     * @return
     */
    public boolean isType(QName type, QName compareToType){
    	return dictionaryService.getSubTypes(compareToType, true).contains(type);
    }

    /**
     * Compruebo si el tipo es del tipo que se pasa como parametro para comparar
     *
     * @param type tipo a comparar
     * @param compareToType tipo contra el que se compara
     * @return
     * @throws GdibException
     */
    public boolean isType(QName type, String compareToType) throws GdibException{
    	QName qnameCompareToType = createQName(compareToType);
    	return isType(type, qnameCompareToType);
    }

    /**
     * Compruebo si el tipo es del tipo que se pasa como parametro para comparar
     *
     * @param type tipo a comparar
     * @param compareToType tipo contra el que se compara
     * @return
     * @throws GdibException
     */
    public boolean isType(String type, String compareToType) throws GdibException{
    	this.checkNodeType(type);
    	QName qnameType = createQName(type);
    	QName qnameCompareToType = createQName(compareToType);
    	return isType(qnameType, qnameCompareToType);
    }

    /**
     * Compruebo si el tipo es del tipo que se pasa como parametro para comparar
     *
     * @param type tipo a comparar
     * @param compareToType tipo contra el que se compara
     * @return
     * @throws GdibException
     */
    public boolean isType(String type, QName compareToType) throws GdibException{
        QName qnameType = createQName(type);
        return isType(qnameType, compareToType);
    }

    public void checkMandatoryAspects(NodeRef updateNode, List<String> removeAspects) throws GdibException {
        QName type = nodeService.getType(updateNode);
        TypeDefinition def = dictionaryService.getAnonymousType(type);
        for (String removeAspect : removeAspects) {
            AspectDefinition aspectDef = dictionaryService.getAspect(createQName(removeAspect));
            if(def.getDefaultAspects().contains(aspectDef)){
                throw exUtils.checkMandatoryMetadataException(removeAspect);
            }
        }
    }

	/**
	 * Genero una estructura de carpetas recibida en un array de String,
	 * empezando en el nodo "root" recibido como parametro
	 *
	 * @param root
	 *            nodo donde empezar a crear la estructura
	 * @param path
	 *            ruta completa a crear
	 * @return
	 * @throws GdibException
	 */
    public NodeRef generateExpedientTree(NodeRef root, String[] path) throws GdibException {
        NodeRef last = root;
        for (String folder : path) {
            NodeRef son = fileFolderService.searchSimple(last, folder);
            if(son == null){
            	this.hasPermission(last, CaibServicePermissions.WRITE);
                FileInfo file = fileFolderService.create(last, folder, ConstantUtils.TYPE_AGREGACION_DOC_QNAME);
                last = file.getNodeRef();
            }else{
                last = son;
            }
        }
        return last;
    }

    public List<String> getRemoveAspects(List<String> aspects) {
        List<String> removeAspects = new ArrayList<String>();
        for (String key : aspects) {
            if (key.startsWith(ConstantUtils.REMOVE_PROPERTY_TOKEN)) {
                // remove
                removeAspects.add(key.substring(1));
            }
        }
        return removeAspects;
    }

    public List<String> getModifyAspects(List<String> updateAspects, List<String> originalAspects) throws GdibException {
        List<String> modifyAspects = new ArrayList<String>();
        List<QName> originalQnameAspects = this.transformListStringToQname(originalAspects);
        for (String key : updateAspects) {
        	QName qnameKey = createQName(key);
            if (!originalQnameAspects.contains(qnameKey)) {
                modifyAspects.add(key);
            }
        }
        return modifyAspects;
    }

    public void checkValidAspects(List<String> aspects) throws GdibException {
    	if(!CollectionUtils.isEmpty(aspects)){
		    for (String aspect : aspects) {
		        QName qname = createQName(aspect);
		        if (dictionaryService.getAspect(qname) == null)
		            throw exUtils.invalidAspectExcepcion(aspect);
		    }
    	}
    }

    public boolean removeAspects(NodeRef node, List<String> aspects) throws GdibException{
    	if(aspects.size() == 0 )
    		return Boolean.FALSE;
        for (String aspect : aspects) {
            nodeService.removeAspect(node, createQName(aspect));
        }
        return Boolean.TRUE;
    }

	private void hasPermission(NodeRef node, String permission) throws GdibException {
		AccessStatus acces = permissionService.hasPermission(node, permission);
		if (!AccessStatus.ALLOWED.equals(acces)) {
			throw exUtils.invalidPermissionException(permission);
		}
	}

	public void hasPermission(NodeRef node, CaibServicePermissions permissions) throws GdibException {
		
		for (String permission : permissions.getPermissions()) {
			if(!PermissionService.UNLOCK.equals(permission)){
				hasPermission(node, permission);
			}
			else if (this.isNodeLocked(node) && PermissionService.UNLOCK.equals(permission)) {
				hasPermission(node, permission);
			}
		}
	}

    /**
     * Verifico si el nodo es final o, en el caso de una carpeta, si tiene algun nodo hijo que es final
     *
     * @param node
     * @throws GdibException
     */
    public void checkFinalNode(NodeRef node) throws GdibException{
    	QName type = nodeService.getType(node);
        if(this.isType(type, ConstantUtils.TYPE_CONTENT) && !nodeService.hasAspect(node, ConstantUtils.ASPECT_BORRADOR_QNAME)){
            throw exUtils.finalDocumentException(node.getId());
        }

        List<ChildAssociationRef>  childList = nodeService.getChildAssocs(node);
        for (ChildAssociationRef child : childList) {
        	NodeRef childRef = child.getChildRef();
        	QName typeChild = nodeService.getType(childRef);
        	if(this.isType(typeChild, ConstantUtils.TYPE_FOLDER) || this.isType(typeChild, ConstantUtils.TYPE_DOC_BASE_QNAME)){
        		this.checkFinalNode(child.getChildRef());
        	}
        }
    }

    /**
     * Verifico si el nodo (Documento solamente) es final. No tiene el aspecto borrador
     *
     * @param node
     * @throws GdibException
     */
    public boolean isFinalNode(Node node) throws GdibException{
    	if(StringUtils.isEmpty(node.getId()))
    		return Boolean.TRUE;
    	return isFinalNode(this.toNodeRef(node.getId()));
    }

    /**
     * Verifico si el nodo (Documento solamente) es final. No tiene el aspecto borrador
     *
     * @param node
     * @throws GdibException
     */
    public boolean isFinalNode(NodeRef nodeRef) throws GdibException{
    	Boolean result = Boolean.FALSE;

    	if(fileFolderService.getFileInfo(nodeRef).isFolder()){
    		result = Boolean.TRUE;
    	}else if(!nodeService.hasAspect(nodeRef, ConstantUtils.ASPECT_BORRADOR_QNAME)){
    		result = Boolean.TRUE;
    	}
    	return result;
    }

    public String getPathFromUID(final NodeRef nodeRef){

    	String path  = "";
    	path = AuthenticationUtil.runAsSystem(new RunAsWork<String>() {
			// @Override
			public String doWork() throws Exception {
				String result  = "";
				Path path = nodeService.getPath(nodeRef);
				result = path.toDisplayPath(nodeService, permissionService);
				result += "/"+ nodeService.getProperty(nodeRef, ConstantUtils.PROP_NAME);
				return result;
			}
		});
    	return path;
    }

	public NodeRef getExpedientTemplate(NodeRef node) throws GdibException {
		NodeRef template = null;
		String documentalSeries = (String)nodeService.getProperty(node, ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
		if(!StringUtils.isEmpty(rootTemplate)){
			List<ChildAssociationRef> childs = nodeService.getChildAssocs(this.toNodeRef(rootTemplate));
			if(childs.size() > 0){
				for (ChildAssociationRef child : childs) {
					template = searchDocumentalSeries(child.getChildRef(), documentalSeries);
				}
			}
		}else{
			throw exUtils.configurationRootTemplateException();
		}
		return template;
	}

	public NodeRef searchDocumentalSeries(NodeRef root, String documentalSeries){
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(root);
		if(childs.size() > 0){
			for (ChildAssociationRef child : childs) {
				String name = (String) nodeService.getProperty(child.getChildRef(), ConstantUtils.PROP_NAME);
				if(name.equals(documentalSeries)){
					return child.getChildRef();
				}
			}
		}
		return null;
	}

	/**
	 * Obtengo una propiedad del expediente al que pertenece el nodo pasado por
	 * parametro
	 *
	 * @param nodeRef
	 *            hace referencia a un nodo dentro de un expediente
	 * @param qnameProperty
	 *            propiedad a obtener
	 * @return el valor de la propiedad
	 */
	public String getPropertyFromExpedient(final NodeRef nodeRef, final QName qnameProperty) {

		String result = AuthenticationUtil.runAsSystem(new RunAsWork<String>() {
			// @Override
			public String doWork() throws Exception {
				String prop = (String) nodeService.getProperty(nodeRef, qnameProperty);
				return prop;
			}
		});

		return result;
	}

	/**
	 * Aplico el metadato de tipo contenido en un nodo
	 *
	 * @param nodeId
	 *            nodo donde meter el datahandler
	 * @param prop
	 *            metadato donde meter el datahandler
	 * @param data
	 *            el datahandler a aplicar
	 * @param mimeType
	 *            tipo del datahandler
	 * @param encoding
	 *            the encoding to use
	 * @throws GdibException
	 * @throws ContentIOException
	 * @throws IOException
	 */
	public void setDataHandler(NodeRef node, QName prop, DataHandler data, String mimeType, String encoding) throws ContentIOException, IOException{
		ContentWriter cW = contentService.getWriter(node, prop, true);
		cW.setMimetype(mimeType);
		if(StringUtils.isEmpty(encoding))
			cW.putContent(data.getInputStream());
		else
			cW.putContent(IOUtils.toString(data.getInputStream(), encoding));
	}

	/**
	 * Aplico el metadato de tipo contenido en un nodo
	 *
	 * @param nodeId
	 *            nodo donde meter el datahandler
	 * @param prop
	 *            metadato donde meter el datahandler
	 * @param data
	 *            el datahandler a aplicar
	 * @param mimeType
	 *            tipo del datahandler
	 * @throws GdibException
	 * @throws ContentIOException
	 * @throws IOException
	 */
	public void setDataHandler(NodeRef node, QName prop, DataHandler data, String mimeType) throws ContentIOException, IOException{
		this.setDataHandler(node, prop, data, mimeType, null);
	}

	/**
	 * Aplico el metadato de tipo contenido en un nodo
	 *
	 * @param nodeId
	 *            nodo donde meter el datahandler
	 * @param prop
	 *            metadato donde meter el datahandler
	 * @param data
	 *            el datahandler a aplicar
	 * @param mimeType
	 *            tipo del datahandler
	 * @param encoding
	 *            the encoding to use
	 * @throws GdibException
	 * @throws ContentIOException
	 * @throws IOException
	 */
	public void setUnsecureDataHandler(NodeRef node, QName prop, DataHandler data, String mimeType, String encoding) throws ContentIOException, IOException{
		ContentWriter cW = contentService.getWriter(node, prop, true);
		LOGGER.debug("Obtengo el contentWriter");
		cW.setMimetype(mimeType);
		if(StringUtils.isEmpty(encoding)){
			LOGGER.debug("encoding vacio");
			cW.putContent(data.getInputStream());
		}
		else{
			LOGGER.debug("encoding: " + encoding);
			cW.putContent(IOUtils.toString(data.getInputStream(), encoding));
		}
	}

	/**
	 * Aplico el metadato de tipo contenido en un nodo
	 *
	 * @param nodeId
	 *            nodo donde meter el datahandler
	 * @param prop
	 *            metadato donde meter el datahandler
	 * @param data
	 *            el datahandler a aplicar
	 * @param mimeType
	 *            tipo del datahandler
	 * @throws GdibException
	 * @throws ContentIOException
	 * @throws IOException
	 */
	public void setUnsecureDataHandler(NodeRef node, QName prop, DataHandler data, String mimeType) throws ContentIOException, IOException{
		this.setUnsecureDataHandler(node, prop, data, mimeType, null);
	}

	public void checkAuthorityExists(String authority) throws GdibException {
    	if(!authorityService.authorityExists(authority)){
    		throw exUtils.authorityNotExitsException(authority);
    	}
	}

	public void setRepositoryDisableCheck(Boolean repositoryDisableCheck) {
		this.repositoryDisableCheck = repositoryDisableCheck;
	}

	public void setPathDM(String pathDM) {
		this._pathDM = pathDM;
	}

	public void setPathMigration(String pathMigration) {
		this._pathMigration = pathMigration;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

    public void setRootCT(String rootCT) {
		this.rootCT = rootCT;
	}

    public void setRootDM(String rootDM) {
        this.rootDM = rootDM;

    }

    public void setRootMigration(String rootMigration) {
        this.rootMigration = rootMigration;

    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setGdibAmpPropertiesAplicationFilter(FilterPlaceholderProperties gdibAmpPropertiesAplicationFilter) {
        this.gdibAmpPropertiesAplicationFilter = gdibAmpPropertiesAplicationFilter;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setCaibDocumentMetadata(String caibDocumentMetadata) {
        this.caibDocumentMetadata = caibDocumentMetadata;
    }

    public void setCaibExpedienteMetadata(String caibExpedienteMetadata) {
        this.caibExpedienteMetadata = caibExpedienteMetadata;
    }

    public void setExUtils(ExUtils exUtils) {
        GdibUtils.exUtils = exUtils;
    }

    public void setFileFolderService(FileFolderService fileFolderService){
        this.fileFolderService = fileFolderService;
    }

    public void setNamespaceService(NamespaceService namespaceService){
    	GdibUtils.namespaceService = namespaceService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setRootTemplate(String rootTemplate) {
		this.rootTemplate = rootTemplate;
	}

    public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

    public void setCcUtils(CuadroClasificacionUtils ccUtils) {
		this.ccUtils = ccUtils;
	}

    public void setRepositoryClassificationTableMode(Boolean repositoryClassificationTableMode) {
		this.repositoryClassificationTableMode = repositoryClassificationTableMode;
	}

    public void setRegistroCentralSeries(String registroCentralSeries) {
		this.registroCentralSeries = registroCentralSeries;
    }

    public void setSignatureService(SignatureService signatureService) {
		this.signatureService = signatureService;
	}

    public void setNoModifyCaibDocumentMetadata(String noModifyCaibDocumentMetadata) {
		this.noModifyCaibDocumentMetadata = noModifyCaibDocumentMetadata;
	}

    public void setNoModifyCaibExpedienteMetadata(String noModifyCaibExpedienteMetadata) {
		this.noModifyCaibExpedienteMetadata = noModifyCaibExpedienteMetadata;
	}

    public void setInDMPathCheckActive(Boolean inDMPathCheckActive) {
		this.inDMPathCheckActive = inDMPathCheckActive;
	}

    public void setGdibAmpPropertiesESBOperationFilter(
			FilterPlaceholderProperties gdibAmpPropertiesESBOperationFilter) {
		this.gdibAmpPropertiesESBOperationFilter = gdibAmpPropertiesESBOperationFilter;
	}

	public void checkRestriction(NodeRef nodeRef, GdibHeader gdibHeader) throws GdibException {
		if ( gdibHeader == null )
			return;
		GdibRestriction gdibRestriction = gdibHeader.getGdibRestriction();
		if ( gdibRestriction == null)
			return;
		List<String> types = gdibRestriction.getTypes();
		if ( types == null )
			return;
		if ( types.size() == 0 )
			return;
		if ( types.size() == 1 && types.get(0) == null)
			return;
		if ( types.size() == 1 && "".equals(types.get(0)))
			return;
		boolean valid = false;
		QName nodeType = nodeService.getType(nodeRef);
		LOGGER.debug("Checking Restriction for NodeRef="+nodeRef.getId() + "; Actual type ="+nodeType);
		for (String type:types){
			QName qtype = createQName(type);
			LOGGER.debug("Comparing " + qtype+ "  with our NodeType="+nodeType);
			if ( qtype.equals(nodeType) ){
				valid = true;
				break;
			}
		}
		if ( ! valid ){
			throw exUtils.checkRestrictionException();
		}
	}

	public void checkRestriction(List<NodeRef> nodeRefs, GdibHeader header) throws GdibException {
		for ( NodeRef nodeRef:nodeRefs ){
			checkRestriction(nodeRef,header);
		}

	}

	public void checkFinalMandatoryProperties(String type, Map<QName, Serializable> properties, String esbOperation) throws GdibException {
		if (isType(type, ConstantUtils.TYPE_DOCUMENTO_QNAME)) {
			checkFinalMandatoryProperties(properties, Arrays.asList(noModifyCaibDocumentMetadata.split(ConstantUtils.CSV_SEPARATOR)), esbOperation);
		}
		if (isType(type, ConstantUtils.TYPE_EXPEDIENTE_QNAME)) {
			checkFinalMandatoryProperties(properties, Arrays.asList(noModifyCaibExpedienteMetadata.split(ConstantUtils.CSV_SEPARATOR)), esbOperation);
		}
	}

	/**
	* Realiza la comprobacion de que las metadatos ha modificar no estan dentro
	* de una lista de metadatos no modificables
	*
	* @param properties
	*            metadatos que se modifican
	* @param noModifyMetadataList
	*            metadatos que no se pueden modificar
	* @param esbOperation
	*            operacion del ESB
	* @throws GdibException
	*/
	private void checkFinalMandatoryProperties(Map<QName, Serializable> properties, List<String> noModifyMetadataList, String esbOperation) throws GdibException{
	// se filtra la lista de metadatos no modificables, eliminando aquellas propiedades que se pueden modificar segun la operacion del ESB
	noModifyMetadataList = filterESBOperationProperties(noModifyMetadataList, esbOperation);
    	List<QName> noModifyMetadataListQname = this.transformListStringToQname(noModifyMetadataList);
    	for (QName qname : properties.keySet()) {
    		if(noModifyMetadataListQname.contains(qname)){
    			throw exUtils.checkNoModifyMetadataException(qname.toString());
    		}
		}
    }

    /**
    * Elimina de una lista de metadatos no modificables, las propiedades que se
    * pueden modificar segun el tipo de operacion en el ESB.
    *
    * La lista de metadatos segun la operacion del ESB se recupera de una
    * property "gdib.modifyNode.<ESBOperation>.metadataCollection"
    *
    * @param noModifyMetadataList
    *            lista de metadatos
    * @param esbOperation
    *            operacion del ESB
    * @return lista de metadatos filtrada
    */
    private List<String> filterESBOperationProperties(List<String> noModifyMetadataList, String esbOperation){
    	List<String> res = new ArrayList<String>();
    	res.addAll(noModifyMetadataList);
    	String esbOperationProperty = gdibAmpPropertiesESBOperationFilter.getProperty(esbOperation, ConstantUtils.GDIB_MODIFY_PROPERTIES_ESB_OPERATION);
    	if(esbOperationProperty != null){
    		List<String> esbOperationPropertyList = Arrays.asList(esbOperationProperty.split(ConstantUtils.CSV_SEPARATOR));
        	for (String prop : esbOperationPropertyList) {
        		res.remove(prop);
    		}
    	}
    	return res;
    }

    /**
	 * Comprobación de la firma de un documento.
	 *
	 * @param node Nodo que contiene la firma.
	 *
	 * */
    public SignatureValidationReport checkDocumentSignature(String nodeId, Content contentNode, DataHandler sign, String tipoFirma, String perfilFirma) throws GdibException{
    	byte[] content, signature;

    	LOGGER.debug("Se inicia la validación de la firma electrónica del documento " + nodeId);

    	LOGGER.debug("Tipo de firma ENI: " + tipoFirma);
    	if(tipoFirma == null){
    		throw new GdibException("La propiedad o metadato " + EniModelUtilsInterface.PROP_TIPO_FIRMA + " del documento " +
    				nodeId + " no ha sido establecida.");
    	}

    	EniSignatureType eniSignatureType = EniSignatureType.valueOf(tipoFirma);
    	if(eniSignatureType == null){
    		throw new GdibException("La propiedad o metadato " + EniModelUtilsInterface.PROP_TIPO_FIRMA + " del documento " +
    				nodeId + " tiene un valor no admitido: " + tipoFirma + ".");
    	}

        try{
        	// si es un documento, es version definitiva y si la firma es realizada mediante certificado electrónico
        	if(!EniSignatureType.TF01.equals(eniSignatureType)){
        		content = null;
        		signature = null;

        		if(!EniSignatureType.TF01.equals(eniSignatureType) &&
        				!EniSignatureType.TF04.equals(eniSignatureType)){
        			//Firma electrónica implicita (TF02, TF03, TF05 y TF06)
        			signature = getByteArrayFromHandler(contentNode.getData());
        		} else {
        			//Firma electrónica explicita
        			signature = getByteArrayFromHandler(sign);
        		}

        		if(!EniSignatureType.TF05.equals(eniSignatureType) &&
        				!EniSignatureType.TF06.equals(eniSignatureType)){
        			//Los tipos de firmas TF02,TF03 y TF04 requieren informar el contenido del documento para validar la firma electrónica
        			content = this.getByteArrayFromHandler(contentNode.getData());
        		}
        		LOGGER.debug("Preparando invocación a plataforma @firma (ValidarFirma)...");

        		// Se verifica la firma
        		SignatureValidationReport result = signatureService.verifySignature(content, signature);

        		LOGGER.debug("Parseando resultado de validación de la firma electrónica del documento " + nodeId + ".");
        		if(result.getValidationStatus() == ValidationStatus.CORRECTO){
        			return result;
        		} else if(result.getValidationStatus() == ValidationStatus.NO_CORRECTO){
        			LOGGER.debug("Resultado de validación de la firma electrónica del documento " + nodeId + " incorrecto.");
        			return result;
        		} else if(result.getValidationStatus() == ValidationStatus.NO_DETERMINADO){
        			LOGGER.debug("Resultado de validación de la firma electrónica del documento " + nodeId + " no determinado.");
        			return result;
        		}

        	}
    	}catch(GdibException e){
    		LOGGER.debug("Error: " + e.getMessage());
    	} finally {
    		LOGGER.debug("Finalizada la validación de la firma electrónica del documento " + nodeId);
    	}
		return new SignatureValidationReport();
    }

    /**
     * Recupera de manera recursiva la propiedad eni:cod_clasificacion
     *
     * @param nodeRef NodeRef del nodo
     *
     * */
	public String getCodClasificacion(NodeRef nodeRef) {
		LOGGER.debug("Get Cod Classification for nodeRef : "+nodeRef.getId());
		QName type = nodeService.getType(nodeRef);
		NodeRef actualNode = nodeRef;
		LOGGER.debug("Type of nodeRef "+nodeRef.getId()+ "  : " + type);
		LOGGER.debug("searching for type = "+ConstantUtils.TYPE_EXPEDIENTE_QNAME);
		LOGGER.debug("entering while" );
		while  ( ! type.equals(ConstantUtils.TYPE_EXPEDIENTE_QNAME) ){
			actualNode = nodeService.getPrimaryParent(actualNode).getParentRef();
			LOGGER.debug("actualNodeID = "+actualNode.getId());
			type = nodeService.getType(actualNode);
			LOGGER.debug("type of ActualNode =  = "+type);
		}
		return nodeService.getProperty(actualNode, ConstantUtils.PROP_COD_CLASIFICACION_QNAME).toString();
	}

	@SuppressWarnings("unchecked")
	public String calculateEniId(NodeRef node) throws GdibException{
		List<String> organo = (List<String>) nodeService.getProperty(node, ConstantUtils.PROP_ORGANO_QNAME);
		if(organo != null)
			return calculateEniId(node.getId(), nodeService.getType(node), organo);
		return null;
    }

	private String calculateEniId(String uuid, QName type, List<String> organo) throws GdibException{
    	String eniId = null;

       	// Tipo documento. Id eni: ES_<ORGANO>_<AAAA>_<ID_ESP>
    	// El campo <ID_ESP> no sera calculcado mediante el UUID de Alfresco, debido a que este tiene un
    	// tamaño superior a 30 caracteres (fijado por ENI), sino que se corresponderá con un valor aleatorio
    	// obtenido mediante SecureRandom sobre un BigInteger de 150 bits, y codificado en base 32.
       	String organoString = organo.get(0);

       	eniId = null;
       	if(isType(type, ConstantUtils.TYPE_EXPEDIENTE_QNAME))
       		eniId = ConstantUtils.ENI_ID + organoString + "_" + Calendar.getInstance().get(Calendar.YEAR) + "_EXP_";
       	else
       		eniId = ConstantUtils.ENI_ID + organoString + "_" + Calendar.getInstance().get(Calendar.YEAR) + "_";

       	String specificId = new BigInteger(150, secureRandom).toString(32);

       	eniId += specificId;

       	return eniId;
    }
	/*
	 * La siguiente función aplica tanto a documentos nuevos como a
	 * documentos que se quieren modifciar. Para documentos eni y migrados transformados.
	 */
	public DataHandler getNodeContent(Node node) throws GdibException{

		//Si se está creando el nodo, venimos de un create_node
		if ( StringUtils.isEmpty(node.getId())){
			//Cuando se crea un nodo puede ser o eni, o migrado/transformado.
			if( contains(node.getAspects(), ConstantUtils.ASPECT_TRANSFORMADO_QNAME) && node instanceof MigrationNode ){
				DataHandler dataHandler = ((MigrationNode)node).getZipContent();
				return dataHandler;
			}else{ //Si no es migrado transformado
				return node.getContent().getData();
			}
		}else{ //El id del nodo no es vacío y estamos modificándolo.
			NodeRef noderef= idToNodeRef(node.getId());
			if(nodeService.hasAspect(noderef, ConstantUtils.ASPECT_TRANSFORMADO_QNAME)){
				//Se está modificando un migradotransformado.
				DataHandler dataHandler = getDataHandler(noderef, ConstantUtils.PROP_ZIPMIGRACION_QNAME);
				return dataHandler;
			} else {
				//Primero controlar si están modificando el contenido, y sino recuprarlo del servicio
				if ( node.getContent() != null ){
					return node.getContent().getData();
				}else{
					return getDataHandler(noderef, ConstantUtils.PROP_CONTENT);
				}
			}
		}
	}
	/*
	 * La siguiente función aplica tanto a documentos nuevos como a
	 * documentos que se quieren modifciar. Para documentos eni y migrados transformados.
	 */
	public DataHandler getNodeSign(Node node) throws GdibException{
		//create_node
		if ( StringUtils.isEmpty(node.getId())){
			return node.getSign();
		}else{ //modify_node
			if ( node.getSign() != null ){
				return node.getSign();
			}else{
				NodeRef noderef = idToNodeRef(node.getId());
				return getDataHandler (noderef, ConstantUtils.PROP_FIRMA_QNAME);
			}
		}
	}

	public String getESBOp(GdibHeader header){
		String esbOp = "";
        if ( header!= null && header.getGdibAudit() != null ){
        	esbOp = header.getGdibAudit().getEsbOperation();
        	if ( esbOp == null)
        		esbOp = "";
        }
        return esbOp;
	}

	public XMLGregorianCalendar createXMLGregorianCalendar(Date date) throws GdibException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		XMLGregorianCalendar xmlDate = null;
		try {
			xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
			GdibException exc = exUtils.generateXMLGregorianCalendarErrorException();
			throw exc;
		}
		return xmlDate;
	}

	/**
	 * Compruebo si el nodo esta dentro del nodo carpeta. Nos sirve para
	 * verificar si es un expediente o documento enlazado
	 *
	 * @param folder
	 *            carpeta donde buscar el nodo
	 * @param node
	 *            nodo a buscar
	 * @return true si esta presente, false si no
	 */
	public boolean isInFolder(NodeRef folder, NodeRef node) {
		boolean result = false;
		NodeRef parent = nodeService.getPrimaryParent(node).getParentRef();
		if (parent != null && parent.equals(folder)) {
			return true;
		} else {
			if (parent.equals(getRootDM())) {
				return false;
			} else {
				result = isInFolder(folder, parent);
			}
		}
		return result;
	}

	/**
	 * Verifica que el tipo especificado como parámetro de entrada se corresponde con un tipo admitido como contenido de un expediente.
	 * @param nodeType Tipo a comprobar
	 * @return true, si el tipo es eni:documento, eni:expedeinte o eni:agregacionDoc. En caso contrario, false.
	 */
	public boolean isAllowedFileOrFolderContent(QName nodeType){
		Boolean res = Boolean.FALSE;
		if(nodeType != null){
			res = isType(nodeType, ConstantUtils.TYPE_DOCUMENTO_QNAME) ||
    			isType(nodeType, ConstantUtils.TYPE_EXPEDIENTE_QNAME) ||
    				isType(nodeType, ConstantUtils.TYPE_AGREGACION_DOC_QNAME);
		}
    	return res;
    }

	/**
	 * Actualizo la fecha de sellado del nodo con la fecha del sistema
	 *
	 * @param node
	 *            nodo a modificar
	 */
	public void updateResealDate(Node node) {
		Property property = new Property(ConstantUtils.PROP_FECHA_SELLADO_QNAME, ISO8601DateFormat.format(new Date(0)));
		node.getProperties().add(property);
	}

	public void updateResealDate(NodeRef node) {
		nodeService.setProperty(node, ConstantUtils.PROP_FECHA_SELLADO_QNAME, ISO8601DateFormat.format(new Date(0)));
	}

	/**
	 * M�todo que devuelve el identificador del certificado usado para generar el sello de tiempo tipo A de una firma XAdES
	 * @param signature XML que contiene la firma en un �ndice
	 * @return Serial number del certificado de la TSA
	 * @throws GdibException Excepcion propagada en caso de que no sea capaz decodificar el ASN1
	 * @throws IOException  Excepci�n lanzada si no consigue leer el XML
	 */
	public String parseTimeStampASN1(org.w3c.dom.Document signature) throws GdibException,IOException{

		org.w3c.dom.NodeList completeCertificateRefs = signature.getElementsByTagName("xadesv141:ArchiveTimeStamp");
        if(completeCertificateRefs.getLength() != 0)
        {
        	org.w3c.dom.Node certRefs = completeCertificateRefs.item(0);
        	if(certRefs != null)
        	{
        		if(certRefs.hasChildNodes())
        		{
        			Element archivtimeStamp = (Element) certRefs;
        			org.w3c.dom.Node timeStamp = archivtimeStamp.getElementsByTagName("xades:EncapsulatedTimeStamp").item(0);

        			if(timeStamp != null)
        			{
        				String toDecode = timeStamp.getTextContent();
        				if(toDecode == null)
        					return "";
        				
        				byte[] valueDecoded = Base64.decode(toDecode.getBytes());
        				ASN1InputStream bIn = new ASN1InputStream(valueDecoded);
        				
        				DERObject obj;
        				try {
        					while ((obj = bIn.readObject()) != null) {
        					    ASN1Sequence asn1 = ASN1Sequence.getInstance(obj);

        					    DEREncodable a = asn1.getObjectAt(1);
        					    DERTaggedObject derTaggedObject = (DERTaggedObject) a;
        					    
        					    ASN1Sequence asn2 = ASN1Sequence.getInstance(derTaggedObject.getObject());
        					    
        					    for(int i=0;i<asn2.size();i++) {
        					    	DEREncodable b = asn2.getObjectAt(i);
        					    	if (b instanceof DERTaggedObject) {
        					    		DERTaggedObject tag = ((DERTaggedObject) b);
        					    		ASN1Sequence asn3 = ASN1Sequence.getInstance(tag.getObject());
        					    		
        					    		for(int j=0;j<asn3.size();j++) {
        							    	DEREncodable c = asn3.getObjectAt(j);
        							    	if (c instanceof DERSequence) {
        							    		DERSequence seq = (DERSequence) c;
        							    		for(int k=0;k<seq.size();k++) {
        							    			DEREncodable d = seq.getObjectAt(k);
      									    	
        									    	if(d instanceof ASN1Integer) {
        									    		return d.toString();
        									    	}
        									    	
        							    		}
        							    		
        							    	}
        							    	
        							    }
        					    		
        					    	}
        					     }
        					   
        					}
        				} catch (IOException e) {
        					LOGGER.debug("Excepcion reading TSA Cert serial Number");
        					throw new GdibException("Excepcion reading TSA Cert serial Number");
        				}
        			}
        			
        		}
        	}
        }
		
		return "";

		
	}
	/**
	 * M�todo que devuelve el identificador del certificado usado para generar el sello de tiempo tipo A de una firma XAdES
	 * @param signature XML que contiene la firma en un �ndice
	 * @return Serial number del certificado de la TSA
	 * @throws GdibException Excepcion propagada en caso de que no sea capaz decodificar el ASN1
	 * @throws IOException  Excepci�n lanzada si no consigue leer el XML
	 */
	public Date parseTimeStampASN1CertCad(org.w3c.dom.Document signature) throws GdibException,IOException{

		org.w3c.dom.NodeList completeCertificateRefs = signature.getElementsByTagName("xadesv141:ArchiveTimeStamp");
        if(completeCertificateRefs.getLength() != 0)
        {
        	org.w3c.dom.Node certRefs = completeCertificateRefs.item(0);
        	if(certRefs != null)
        	{
        		if(certRefs.hasChildNodes())
        		{
        			Element archivtimeStamp = (Element) certRefs;
        			org.w3c.dom.Node timeStamp = archivtimeStamp.getElementsByTagName("xades:EncapsulatedTimeStamp").item(0);

        			if(timeStamp != null)
        			{
        				String toDecode = timeStamp.getTextContent();
        				if(toDecode == null)
        					return null;
        				
        				byte[] valueDecoded = Base64.decode(toDecode.getBytes());
        				ASN1InputStream bIn = new ASN1InputStream(valueDecoded);
        				
        				DERObject obj;
        				try {
        					while ((obj = bIn.readObject()) != null) {
        					    ASN1Sequence asn1 = ASN1Sequence.getInstance(obj);

        					    DEREncodable a = asn1.getObjectAt(1);
        					    DERTaggedObject derTaggedObject = (DERTaggedObject) a;
        					    
        					    ASN1Sequence asn2 = ASN1Sequence.getInstance(derTaggedObject.getObject());
        					    
        					    for(int i=0;i<asn2.size();i++) {
        					    	DEREncodable b = asn2.getObjectAt(i);
        					    	if (b instanceof DERTaggedObject) {
        					    		DERTaggedObject tag = ((DERTaggedObject) b);
        					    		ASN1Sequence asn3 = ASN1Sequence.getInstance(tag.getObject());
        					    		
        					    		for(int j=0;j<asn3.size();j++) {
        							    	DEREncodable c = asn3.getObjectAt(j);
        							    	if (c instanceof DERSequence) {
        							    		DERSequence seq = (DERSequence) c;
        							    		DEREncodable d = seq.getObjectAt(4);
        							    		DERSequence seq2 = (DERSequence) d;
        							    		for(int l=0;l<seq2.size();l++) {
        							    			DEREncodable e = seq2.getObjectAt(l);
        							    			
        							    			String input = e.toString();
        							    			DateFormat parser = new SimpleDateFormat("yyMMddHHmmss");
        							    			Date date;
        											try {
        												date = parser.parse(input);
        								    			
        								    			return date;
        											} catch (ParseException e1) {
        												LOGGER.debug("Ocurri� un error obteniendo caducidad del certificado.");
        					        					throw new GdibException("Excepcion reading TSA Cert Validity");
        											}
        							    			
        							    		}
        							    		
        							    	}
        							    	
        							    }
        					    		
        					    	}
        					     }
        					   
        					}
        				} catch (IOException e) {
        					LOGGER.debug("Excepcion reading TSA Cert Validity");
        					throw new GdibException("Excepcion reading TSA Cert Validity");
        				}
        			}
        			
        		}
        	}
        }
		
		return null;

		
	}
	//public void setUnsecureContentService(ContentService unsecureContentService) {
		//this.unsecureContentService = unsecureContentService;
	//}
}
