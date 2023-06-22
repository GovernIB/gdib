package es.caib.gdib.ws.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.w3c.dom.Document;

import es.caib.gdib.rm.utils.ExportUtils;
import es.caib.gdib.rm.utils.ImportUtils;
import es.caib.gdib.utils.AdministrativeProcessingIndexSignerFactory;
import es.caib.gdib.utils.CaibServicePermissions;
import es.caib.gdib.utils.Certificate;
import es.caib.gdib.utils.CertificateUtils;
import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.ExUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.InputStreamDataSource;
import es.caib.gdib.utils.SignatureUtils;
import es.caib.gdib.utils.SubTypeDocInfo;
import es.caib.gdib.utils.SubTypeDocUtil;
import es.caib.gdib.utils.XmlUtils;
import es.caib.gdib.utils.iface.CaibConstraintsUtilsInterface;
import es.caib.gdib.utils.iface.EniModelUtilsInterface;
import es.caib.gdib.ws.common.types.Content;
import es.caib.gdib.ws.common.types.EemgdeSignatureProfile;
import es.caib.gdib.ws.common.types.EniSignatureType;
import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.IndiceElectronicoManager;
import es.caib.gdib.ws.common.types.MigrationInfo;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.NodeChild;
import es.caib.gdib.ws.common.types.NodeVersion;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.common.types.SearchResults;
import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.common.types.SignatureValidationReport;
import es.caib.gdib.ws.common.types.ValidationStatus;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;
import es.caib.gdib.ws.iface.SignatureService;

@Component
@Scope("request")
@WebService(serviceName = "RepositoryService", portName = "GdibRepositoryServiceSoapPort", targetNamespace = "http://www.caib.es/gdib/repository/ws", endpointInterface = "es.caib.gdib.ws.iface.RepositoryServiceSoapPort")
public class RepositoryServiceSoapPortImpl extends SpringBeanAutowiringSupport implements RepositoryServiceSoapPort {

	private static final Logger LOGGER = Logger.getLogger(RepositoryServiceSoapPortImpl.class);

	private static final String DEFAULT_CHARSET_ENCODING = "UTF-8";

	@Autowired
	private TransactionService transactionService;

	private UserTransaction usrTrx;

	/**
	 * Modos de enlazar documentos: - Copy : Se copia de un nodo a otro nodo -
	 * Reference : Se aÃ±ade un enlace padre-hijo
	 */
	public static final String LINK_COPY = "copy";
	public static final String LINK_REF = "reference";

	private static final String EXCHANGE = "Exchange";

	private static final String INTERNAL = "Internal";

	/**
	 * Localización de ficheros temporales
	 */
	@Value("$gdib{gdib.repository.temp.folder.uuid}")
	private String tempFolder;
	/**
	 * Flag que activa/desactiva diversas comprobaciones en el repositorio.
	 */
	@Value("$gdib{gdib.repository.disable.check}")
	private Boolean repositoryDisableCheck;

	@Value("$gdib{gdib.repository.custody.doc.minSignatureFormats}")
	private String minCustodyAdvancedSignatureFormats;

	@Value("$gdib{gdib.createNode.dispatchDocument.eni_id.noReplace}")
	private Boolean eniIdNoReplace;

	private Boolean addEniExchangeFiles;

	@Value("$gdib{gdib.repository.custody.exp.eniIndexV10.addExchangeFiles}")
	private String addEniExchangeFilesPropValue;

	private Boolean addIntExchangeFiles;

	@Value("$gdib{gdib.repository.custody.exp.caibIndexV10.addExchangeFiles}")
	private String addIntExchangeFilesPropValue;

	@Value("$gdib{gdib.rm.preregistro.active}")
	private boolean preingreso;

	/**
	 * L�mite de resultados obtenidos en la bÃºsqueda
	 */
	@Value("$gdib{gdib.repository.search.limit}")
	private String searchLimit;

	/**
	 * Servicios repositorio de Alfresco
	 */
	@Autowired
	private NodeService nodeService;
	@Autowired
	private SearchService searchService;
	@Autowired
	private AuthorityService authorityService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private FileFolderService fileFolderService;
	@Autowired
	private VersionService versionService;
	@Autowired
	private ContentService contentService;
	@Autowired
	private SignatureService signatureService;
	@Autowired
	private CopyService copyService;

	/**
	 * Clases de utilidades
	 */
	@Autowired
	private GdibUtils utils;
	@Autowired
	private ExUtils exUtils;
	@Autowired
	private ExportUtils exportUtils;
	@Autowired
	private ImportUtils importUtils;
	@Autowired
	private CertificateUtils certUtils;

	@Autowired
	private IndiceElectronicoManager indiceElectronicoManager;
	@Autowired
	private SubTypeDocUtil subTypeDocUtil;

	//private static Object createMutex;
	//private static Object closeMutex;
	
	public RepositoryServiceSoapPortImpl() {
//	    if (createMutex == null)
//	    	createMutex = new Object();
	    
//	    if (closeMutex == null)
//	    	closeMutex = new Object();
	    
		try {
			this.addEniExchangeFiles = Boolean.valueOf(addEniExchangeFilesPropValue);
		} catch (Exception e) {
			this.addEniExchangeFiles = ConstantUtils.DEFAULT_ADD_EXCHANGE_FILES_VALUE;
		}

		try {
			this.addIntExchangeFiles = Boolean.valueOf(addIntExchangeFilesPropValue);
		} catch (Exception e) {
			this.addIntExchangeFiles = ConstantUtils.DEFAULT_ADD_EXCHANGE_FILES_VALUE;
		}
	}

	public void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
	}

	/**
	 * @param minCustodyAdvancedSignatureForm the minCustodyAdvancedSignatureForm to
	 *                                        set
	 */
	public void setMinCustodyAdvancedSignatureFormats(String minCustodyAdvancedSignatureFormats) {
		this.minCustodyAdvancedSignatureFormats = minCustodyAdvancedSignatureFormats;
	}

	/**
	 * Genera una versi�n en el nodo.
	 * 
	 * @param nodeRef NodeRef del nodo que se quiere avanzar una versi�n
	 *
	 */
	private void createVersion(NodeRef nodeRef) {
		versionService.ensureVersioningEnabled(nodeRef, null);
		nodeService.setProperty(nodeRef, ContentModel.PROP_AUTO_VERSION_PROPS, false);
	}

	/**
	 * El metadato eni:id de un nodo siempre se va a calcular salvo si la operacion
	 * del ESB es "dispatchDocument" y si una la property
	 * "gdib.createNode.dispatchDocument.eni_id.noReplace" es true.
	 *
	 * En ese caso, el eni:id va a venir informado porque es un documento que viene
	 * del registro y entonces no se va a calcular
	 *
	 *
	 * @param node NodeRef del nodo a actualizar.
	 * @throws GdibException Si la propiedad no se asigna correctamente.
	 *
	 */
	private void checkCalculateEniId(NodeRef node, String esbOperation) throws GdibException {
		QName type = nodeService.getType(node);
		if (utils.isType(type, ConstantUtils.TYPE_EXPEDIENTE_QNAME)
				|| utils.isType(type, ConstantUtils.TYPE_DOCUMENTO_QNAME)) {
			String eniId = (String) nodeService.getProperty(node, ConstantUtils.PROP_ID_QNAME);
			if (eniIdNoReplace) {
				// debo reemplazar el eni:id si la operacion NO es de registro
				if (!ConstantUtils.ESB_OPERATION_REGISTRY.equals(esbOperation)) {
					eniId = utils.calculateEniId(node);

					// si falta el dato de eniId lanzo una expcecion pues es dato obligatorio
				} else if (StringUtils.isEmpty(eniId)) {
					throw exUtils.checkMandatoryMetadataException(ConstantUtils.PROP_ID_QNAME.toString());
				}
			} else {
				// siempre calculo la propiedad eni si el flag es FALSE
				eniId = utils.calculateEniId(node);
			}
			nodeService.setProperty(node, ConstantUtils.PROP_ID_QNAME, eniId);
		}
	}

	/**
	 * Dado un nodo se recoge a travÃ©s de su serie documental una plantilla y se
	 * copia dentro del nodo.
	 * 
	 * @param node Nodo que se acaba de crear y que habr�a que aplicar dicha
	 *             plantilla.
	 * @throws GdibException Si no se tienen permisos o se produce algÃºn error.
	 */
	private void getTemplate(NodeRef node) throws GdibException {
		// busco en las plantillas de alfresco si la serie documental de este expediente
		// tiene una plantilla de expediente
		NodeRef expedientTemplate = utils.getExpedientTemplate(node);
		// obtengo todos los hijos de dicho plantilla y los copio dentro del nodo creado
		if (expedientTemplate != null) {
			List<ChildAssociationRef> list = nodeService.getChildAssocs(expedientTemplate);
			for (ChildAssociationRef child : list) {
				this._internal_copy(node, child.getChildRef());
			}
		}
	}

	public NodeRef _internal_createNode(NodeRef parentRef, QName name, QName type, Map<QName, Serializable> props,
			Content content, DataHandler sign) throws GdibException {
		NodeRef nodeRef = _internal_createNode(parentRef, name, type, props, content);
		if (sign != null) {
			try {
				utils.setDataHandler(nodeRef, ConstantUtils.PROP_FIRMA_QNAME, sign, MimetypeMap.MIMETYPE_BINARY);
			} catch (IOException exception) {
				throw exUtils.setSignException(nodeRef.getId(), exception);
			}
		}
		return nodeRef;
	}

	/**
	 * Desacoplamiento de createNode. Realiza la operaci�n de createNode sin
	 * comprobaciones previas y con todos los elementos: - Firma - Contenido -
	 * Metadatos
	 *
	 * @param parentRef NodeRef del nodo padre donde se va a generar el nuevo nodo.
	 * @param name      Nombre del nuevo nodo.
	 * @param type      QName del tipo del nuevo nodo
	 * @param props     Mapa<QName,Serializable> de propiedades del nuevo nodo.
	 * @param aspects   Lista<QName> de aspectos que tendr� el nuevo nodo.
	 * @param content   Contenido del nuevo nodo a crear.
	 * @param sign      Contenido de la firma del nuevo nodo a crear.
	 * @return NodeRef del nuevo nodo creado.
	 * @throws GdibException Si no se tienen permisos para crear el nodo.
	 * @throws GdibException Si el nombre es incorrecto.
	 * @throws GdibException Si el tipo es incorrecto.
	 * @throws GdibException Si los aspectos o propiedades son incorrectas
	 */
	private NodeRef _internal_createNode(NodeRef parentRef, QName name, QName type, Map<QName, Serializable> props,
			List<QName> aspects, Content content, DataHandler sign, String esbOperation) throws GdibException {
		NodeRef nodeRef = _internal_createNode(parentRef, name, type, props, content, sign);
		// incluyo los aspectos
		if (!CollectionUtils.isEmpty(aspects)) {
			utils.addAspects(nodeRef, aspects);
		}

		// actualizo la propiedad ENI - ID para incluirl el uid
		this.checkCalculateEniId(nodeRef, esbOperation);

		// obtengo la plantilla del expediente
		if (!repositoryDisableCheck.booleanValue()) {
			// me salto este paso si esta desactivado los check principales del repositorio
			this.getTemplate(nodeRef);
		}

		// creo la version 1.0 del nodo, y desactivo el autoversionado del nodo
		this.createVersion(nodeRef);

		return nodeRef;
	}

	/**
	 * Desacoplamiento de createNode. Realiza la operaci�n de createNode sin
	 * comprobaciones previas con los elementos: - Contenido - Metadatos
	 *
	 * @param parentRef NodeRef del nodo padre donde se va a generar el nuevo nodo.
	 * @param name      Nombre del nuevo nodo.
	 * @param type      QName del tipo del nuevo nodo
	 * @param props     Mapa<QName,Serializable> de propiedades del nuevo nodo.
	 * @param aspects   Lista<QName> de aspectos que tendr� el nuevo nodo.
	 * @param content   Contenido del nuevo nodo a crear.
	 * @return NodeRef del nuevo nodo creado.
	 * @throws GdibException Si no se tienen permisos para crear el nodo.
	 * @throws GdibException Si el nombre es incorrecto.
	 * @throws GdibException Si el tipo es incorrecto.
	 * @throws GdibException Si los aspectos o propiedades son incorrectas
	 */
	private NodeRef _internal_createNode(NodeRef parentRef, QName name, QName type, Map<QName, Serializable> props,
			Content content) throws GdibException {
		NodeRef nodeRef = _internal_createNode(parentRef, name, type, props);
		if (content != null && content.getMimetype() != null && content.getData() != null) {
			try {
				long startCreate = System.currentTimeMillis();
				utils.setDataHandler(nodeRef, ContentModel.PROP_CONTENT, content.getData(), content.getMimetype());
				long endCreate = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
				Date d1 = new Date(startCreate);
				Date d2 = new Date(endCreate);
				LOGGER.info(nodeRef.getId() + ": LLamada a servicio copiar contenido al sistema de ficheros  "
						+ (endCreate - startCreate) + "ms entre " + sdf.format(d1) + " y " + sdf.format(d2) + ".");
			} catch (IOException exception) {
				throw exUtils.setContentException(nodeRef.getId(), exception);
			}
		}
		return nodeRef;
	}

	/**
	 * Desacoplamiento de createNode. Realiza la operaci�n de createNode sin
	 * comprobaciones previas con sus metadatos
	 *
	 * @param parentRef NodeRef del nodo padre donde se va a generar el nuevo nodo.
	 * @param name      Nombre del nuevo nodo.
	 * @param type      QName del tipo del nuevo nodo
	 * @param props     Mapa<QName,Serializable> de propiedades del nuevo nodo.
	 * @param aspects   Lista<QName> de aspectos que tendr� el nuevo nodo.
	 * @return NodeRef del nuevo nodo creado.
	 * @throws GdibException Si no se tienen permisos para crear el nodo.
	 * @throws GdibException Si el nombre es incorrecto.
	 * @throws GdibException Si el tipo es incorrecto.
	 * @throws GdibException Si los aspectos o propiedades son incorrectas
	 */
	private NodeRef _internal_createNode(NodeRef parentRef, QName name, QName type, Map<QName, Serializable> props)
			throws GdibException {
		if (props.get(ConstantUtils.PROP_NAME) == null)
			props.put(ConstantUtils.PROP_NAME, name.getLocalName());
		long startCreate = System.currentTimeMillis();

		ChildAssociationRef createdChildRef = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, name, type,
				props);
		long endCreate = System.currentTimeMillis();
		NodeRef ret = createdChildRef != null ? createdChildRef.getChildRef() : null;
		if (ret != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
			Date d1 = new Date(startCreate);
			Date d2 = new Date(endCreate);
			LOGGER.info(ret + ": LLamada a servicio crear nodo de Alfresco de " + (endCreate - startCreate)
					+ "ms entre " + sdf.format(d1) + " y " + sdf.format(d2) + ".");
		}
		return ret;
	}

	/**
	 * Desacoplamiento de getNode. Realiza la operaci�n de getNode sin
	 * comprobaciones previas.
	 *
	 * @param nodeRef     NodeRef del nodo a recuperar.
	 * @param withContent Flag que indica si se quiere recuperar o no el contenido
	 *                    del nodo.
	 * @param withSign    Flag que indica si se quiere recuperar o no la firma del
	 *                    nodo.
	 * @return Node Objeto Nodo con la informaci�n que alfresco tiene en el
	 *         repositorio.
	 * @throws GdibException Si no se tienen permisos para recuperar el nodo.
	 */
	public Node _internal_getNode(NodeRef nodeRef, boolean withContent, boolean withSign) throws GdibException {
		Node node = new Node();

		node.setId(nodeRef.getId());
		node.setType(utils.getNodeType(nodeRef));
		node.setName(utils.getNameNode(nodeRef));
		node.setAspects(utils.getAspects(nodeRef));

		List<Property> allProperties = utils.getProperties(nodeRef);
		allProperties.addAll(utils.getPropertiesCalculated(nodeRef));
		node.setProperties(allProperties);
		// obtener las propiedades de eni:transferible que estan en la base de datos del
		// cuadro de clasificacion
		subTypeDocUtil.fillSubTypeDocInfo(node);

		if (withContent) {
			node.setContent(utils.getContent(nodeRef));
		}

		if (withSign) {
			node.setSign(utils.getDataHandler(nodeRef, ConstantUtils.PROP_FIRMA_QNAME));
		}

		List<ChildAssociationRef> hijosRef = nodeService.getChildAssocs(nodeRef);
		List<NodeChild> hijos = new ArrayList<NodeChild>();
		for (ChildAssociationRef hijoRef : hijosRef) {
			NodeRef childNode = hijoRef.getChildRef();
			QName childNodeType = nodeService.getType(childNode);
			if (utils.isAllowedFileOrFolderContent(childNodeType)) {
				String name = (String) nodeService.getProperty(childNode, ContentModel.PROP_NAME);
				String typeString = utils.formatQname(childNodeType);
				hijos.add(new NodeChild(childNode.getId(), name, typeString));
			}
		}
		node.setChilds(hijos);

		return node;
	}

	/**
	 * Desacoplamiento de linkNode. Realiza la operaci�n de copia de un nodo sin
	 * comprobaciones previas.
	 *
	 * @param target NodeRef del nodo padre donde se va a copiar el nodo.
	 * @param source NodeRef del nodo que se quiere copiar.
	 * @return NodeRef del nuevo nodo copiado.
	 * @throws GdibException Si no se tienen permisos para crear el nodo en la nueva
	 *                       localizaci�n.
	 * @throws GdibException Si no se tienen permisos para leer el nodo source.
	 * @throws GdibException Si el nodo copiado ya exist�a previamente
	 * @throws GdibException Si no se encuentra el nodo origen.
	 */
	private NodeRef _internal_copy(NodeRef target, NodeRef source) throws GdibException {
		NodeRef copy = null;
		try {
			String expedienteName = (String) nodeService.getProperty(source, ConstantUtils.PROP_NAME);
			copy = fileFolderService
					.copy(source, target,
							expedienteName + "_" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()))
					.getNodeRef();
		} catch (FileExistsException ex) {
			throw exUtils.nodeAlreadyExists(source.getId());
		} catch (FileNotFoundException ex) {
			throw exUtils.nodeNotFoundException("nodeId", target.getId());
		}
		return copy;
	}

	/**
	 * Desacoplamiento de linkNode. Realiza la operaci�n de enlace de un nodo sin
	 * comprobaciones previas. La operaci�n de enlazar dispone un enlace padre-hijo
	 * entre nodos.
	 *
	 * @param target NodeRef del nodo padre donde se va a enlazar el nodo.
	 * @param source NodeRef del nodo que se quiere enlazar.
	 * @return el uuid del nodo padre donde se enlazo el documento
	 * @throws GdibException Si no se tienen permisos para enlazar el nodo en la
	 *                       nueva localizaci�n.
	 *
	 */
	private void _internal_ref(NodeRef target, NodeRef source) throws GdibException {
		LOGGER.debug("INTERNAL REF :: TARGET :::" + target.getStoreRef() + "  " + target);
		LOGGER.debug("INTERNAL REF :: SOURCE :::" + source.getStoreRef() + "  " + source);
		nodeService.addChild(target, source, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CHILDREN);
	}

	/**
	 * Desacoplamiento de moveNode. Realiza la operaci�n de movimiento de un nodo
	 * sin comprobaciones previas.
	 *
	 * @param nodeRef          del nodo que se pretende mover
	 * @param newParentNodeRef NodeRef del nodo padre donde se va a mover el nodo.
	 * @throws GdibException Si no se tienen permisos para mover el nodo en la nueva
	 *                       localizaci�n.
	 * @throws GdibException Si el nodo que se quiere mover est� bloqueado por otro
	 *                       usuario.
	 */
	private void _internal_moveNode(NodeRef nodeRef, NodeRef newParentNodeRef) throws GdibException {
		if (!utils.isNodeLocked(nodeRef)) {
			nodeService.moveNode(nodeRef, newParentNodeRef, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CHILDREN);
		} else {
			throw exUtils.lockedNode(nodeRef.getId());
		}
	}

	/**
	 * Desacoplamiento de authorizeNode Realiza la asignaci�n/desasignaci�n de
	 * permisos a unas autoridades en concreto para unos nodos de alfresco sin
	 * comprobaciones previas.
	 *
	 * @param nodeRefs    Lista de nodos que se pretenden modificar sus ACLs
	 * @param authorities Lista de autoridades que van a modificar los permisos de
	 *                    los nodos.
	 * @param permission  Permiso que se va a modificar
	 */
	private void _internal_authorizeNode(List<NodeRef> nodeRefs, List<String> authorities, String permission)
			throws GdibException {
		for (NodeRef nodeRef : nodeRefs) {
			// comprobar si se tienen los permisos para realizar la operacion
			utils.hasPermission(nodeRef, CaibServicePermissions.WRITE);
			utils.inDMPath(nodeRef);
			// comprobar si el nodo o alguno de los nodos que agrupa esta bloqueado
			if (utils.isSomeoneLockedDown(nodeRef)) {
				throw exUtils.lockedNode(nodeRef.getId());
			}
			for (String authority : authorities) {
				_internal_authorizeNode(nodeRef, authority, permission);
			}
		}
	}

	/**
	 * Desacoplamiento de authorizeNode Realiza la asignaci�n/desasignaci�n de
	 * permisos a una sola autoridad en concreto para un nodo de alfresco sin
	 * comprobaciones previas.
	 *
	 * @param nodeRef    Nodo que se pretende modificar sus ACLs
	 * @param authority  Autoridad que va a modificar los permisos del nodo.
	 * @param permission Permiso que se va a modificar
	 */
	private void _internal_authorizeNode(NodeRef nodeRef, String authority, String permission) throws GdibException {
		// comprobar si la autoridad existe
		if (authorityService.authorityExists(authority)) {

			switch (permission) {
			case ConstantUtils.PERMISSION_READ:
				this._internal_authorizeNode(nodeRef, authority, CaibServicePermissions.READ.getPermissions());
				break;
			case ConstantUtils.PERMISSION_WRITE:
				this._internal_authorizeNode(nodeRef, authority, CaibServicePermissions.WRITE.getPermissions());
				break;
			default:
				break;
			}
		} else {
			throw exUtils.authorityNotExitsException(authority);
		}
	}

	/**
	 * Desacoplamiento de authorizeNode Realiza la asignaci�n/desasignaci�n de una
	 * lista de permisos a una autoridad en concreto para un nodo de alfresco sin
	 * comprobaciones previas.
	 *
	 * @param nodeRef     Nodo que se pretende modificar sus ACLs
	 * @param authority   Autoridad que va a modificar los permisos del nodo.
	 * @param permissions Lista<String> de permisos que se va a modificar
	 */
	private void _internal_authorizeNode(NodeRef nodeRef, String authority, List<String> permissions) {
		for (String permission : permissions) {
			permissionService.setPermission(nodeRef, authority, permission, true);
		}
	}

	/**
	 * Genera y firma el indice electr�nico del expediente.
	 *
	 * AdministrativeProcessingIndexSignerFactory.CAIB_INDEX_V10 => Indice
	 * electronico interno de CAIB. Se usa en los servicios de cerrar expediente y
	 * foliado de expediente
	 * AdministrativeProcessingIndexSignerFactory.ENI_INDEX_V10 => Indice
	 * electronico ENI. Se usa en el servicio de export expediente.
	 *
	 * @param nodeRef   Expediente que se va a foliar
	 * @param indexType Tipo de indice que se quiere generar
	 * @return DataHandler con la info del indice generado
	 * @throws GdibException
	 */
	private DataHandler _internal_foliate(NodeRef nodeRef, String indexType) throws GdibException {
		DataHandler dh = null;
		Object ie;

		try {
			// 1 Generar indice XML
			// 2 Firma del indice electr�nico
			byte[] signedXmlIndex = null;
			switch (indexType) {
			case AdministrativeProcessingIndexSignerFactory.CAIB_INDEX_V10:
				LOGGER.debug("Genero el indice CAIB_INDEX_V10");
				ie = indiceElectronicoManager.getIndiceElectronico(nodeRef);
				LOGGER.debug("Firmo el indice CAIB_INDEX_V10");
				signedXmlIndex = indiceElectronicoManager.signXmlIndex(ie,
						AdministrativeProcessingIndexSignerFactory.CAIB_INDEX_V10, null);
				LOGGER.debug("Indice CAIB_INDEX_V10 completado");
				break;
			case AdministrativeProcessingIndexSignerFactory.ENI_INDEX_V10:
				// Firma del del expediente ENI
				LOGGER.debug("Genero el indice ENI_INDEX_V10");
				ie = indiceElectronicoManager.getExpedienteElectronicoENI(nodeRef);
				// Se especifica como referencia de firma el elemento ra�z expediente
				Map<String, Object> optionalParams = new HashMap<String, Object>();
				optionalParams.put(ConstantUtils.INDEX_ID_ATT_KEY,
						((es.caib.gdib.ws.xsd.expediente.eni.TipoExpediente) ie).getId());
				LOGGER.debug("Firmo el indice ENI_INDEX_V10");
				signedXmlIndex = indiceElectronicoManager.signXmlIndex(ie,
						AdministrativeProcessingIndexSignerFactory.ENI_INDEX_V10, optionalParams);

				LOGGER.debug("Indice ENI_INDEX_V10 completado");
				break;
			default:
				throw new GdibException("El tipo de �ndice " + indexType + " no es soportado.");
			}
			LOGGER.debug("Construir DataHandler a partir del XML firmado.");
			// 3 Return DataHandler del XML firmado.
			dh = indiceElectronicoManager.generateXML(signedXmlIndex);
			LOGGER.debug("DataHandler correctamente generado.");
		} catch (ContentIOException e) {
			throw new GdibException(e.getMessage());
		}

		return dh;
	}

	/**
	 * Comprobaci�n de la firma de un documento.
	 *
	 * @param node Nodo que contiene la firma.
	 *
	 */
	private void checkDocumentSignature(Node node) throws GdibException {
		Boolean implicitSignature = Boolean.FALSE;
		byte[] content, signature;
		EemgdeSignatureProfile eniSignatureProfile;
		SignatureFormat minCustodySignatureFormat, signatureFormat, currentSignatureFormat;
		String signatureTypeProp, nodeIdValue, signatureProfileNodeProp;
		String[] custodyAdvancedSignatureFormats;

		nodeIdValue = (node.getId() == null ? "nuevo documento" : node.getId());

		LOGGER.debug("Se inicia la validacion de la firma electr�nica del documento " + nodeIdValue);
		signatureTypeProp = utils.getProperty(node.getProperties(),
				EniModelUtilsInterface.ENI_MODEL_PREFIX + EniModelUtilsInterface.PROP_TIPO_FIRMA);
		LOGGER.debug("Tipo de firma ENI: " + signatureTypeProp);
		if (signatureTypeProp == null) {
			throw new GdibException("La propiedad o metadato " + EniModelUtilsInterface.PROP_TIPO_FIRMA
					+ " del documento " + nodeIdValue + " no ha sido establecida.");
		}

		EniSignatureType eniSignatureType = EniSignatureType.valueOf(signatureTypeProp);
		if (eniSignatureType == null) {
			throw new GdibException("La propiedad o metadato " + EniModelUtilsInterface.PROP_TIPO_FIRMA
					+ " del documento " + nodeIdValue + " tiene un valor no admitido: " + signatureTypeProp + ".");
		}

		try {
			// si es un documento, es version definitiva y si la firma es realizada mediante
			// certificado electr�nico
			if (utils.isType(node.getType(), ConstantUtils.TYPE_DOCUMENTO_QNAME)
					&& !EniSignatureType.TF01.equals(eniSignatureType)) {
				content = null;
				signature = null;

				if (!EniSignatureType.TF01.equals(eniSignatureType)
						&& !EniSignatureType.TF04.equals(eniSignatureType)) {
					// Firma electr�nica implicita (TF02, TF03, TF05 y TF06)
					signature = utils.getByteArrayFromHandler(utils.getNodeContent(node));
					implicitSignature = Boolean.TRUE;
				} else {
					// Firma electr�nica explicita
					signature = utils.getByteArrayFromHandler(utils.getNodeSign(node));
				}

				if (signature == null) {
					throw exUtils.checkMetadataValueException(ConstantUtils.PROP_FIRMA, "null");
				}

				if (!EniSignatureType.TF05.equals(eniSignatureType)
						&& !EniSignatureType.TF06.equals(eniSignatureType)) {
					// Los tipos de firmas TF02,TF03 y TF04 requieren informar el contenido del
					// documento para validar la firma electr�nica
					// se comprueba que es un documento migrado, mirando si viene informado el uuid
					// o si es un nodo de migracion y con aspecto transformado
					content = utils.getByteArrayFromHandler(utils.getNodeContent(node));
				}
				LOGGER.debug("Preparando invocaci�n a plataforma @firma (ValidarFirma)...");
				// Se obtiene el actual perfil de firma establecido para el documento
				signatureProfileNodeProp = utils.getProperty(node.getProperties(),
						EniModelUtilsInterface.ENI_MODEL_PREFIX + EniModelUtilsInterface.PROP_PERFIL_FIRMA);
				LOGGER.debug("Valor informado para el metadato " + EniModelUtilsInterface.ENI_MODEL_PREFIX
						+ EniModelUtilsInterface.PROP_PERFIL_FIRMA + ": " + signatureProfileNodeProp + ".");
				currentSignatureFormat = SignatureUtils.eniSigntureFormatToInernalSignatureFormat(
						eniSignatureType.getName(), signatureProfileNodeProp);
				// Se verifica la firma
				SignatureValidationReport result = signatureService.verifySignature(content, signature);
				LOGGER.debug("Parseando resultado de validaci�n de la firma electr�nica del documento " + node.getId()
						+ ".");
				if (result.getValidationStatus() == ValidationStatus.CORRECTO) {
					LOGGER.debug("Resultado de validaci�n de la firma electr�nica del documento "
							+ (node.getId() == null ? "nuevo documento" : node.getId()) + " correcto.");
					// Si la firma es correcta, se verifica que el formato avanzado es igual o
					// superior al m�nimo exigido
					signatureFormat = SignatureUtils.dssSigntureFormatToInernalSignatureFormat(
							result.getSignatureType(), result.getSignatureForm());
					LOGGER.debug(
							"Se verifica que la firma y el metadato de firma son coherentes (familia o tipo de firma).");
					if (!currentSignatureFormat.getType().equalsIgnoreCase(signatureFormat.getType())) {
						// Se verifica que el formato de firma establecido para el nodo y el retornado
						// por @firma
						// pertenecen a la misma familia de formatos (CAdES, XAdES o PAdES)
						throw new GdibException("Metadato " + EniModelUtilsInterface.PROP_TIPO_FIRMA
								+ " con valor incorrecto para el documento " + nodeIdValue + ". Formato DSS esperado "
								+ result.getSignatureType() + ".");
					}
					LOGGER.debug(
							"Se verifica que la firma presenta un formato avanzado igual o superior al m�nimo exigido...");
					custodyAdvancedSignatureFormats = minCustodyAdvancedSignatureFormats
							.split(ConstantUtils.COMMA_SEPARATOR);
					minCustodySignatureFormat = SignatureFormat.UNRECOGNIZED;
					eniSignatureProfile = null;
					Boolean found = Boolean.FALSE;

					for (int i = 0; !found && i < custodyAdvancedSignatureFormats.length; i++) {
						if (custodyAdvancedSignatureFormats[i].toUpperCase()
								.startsWith(eniSignatureType.getName().toUpperCase())) {
							minCustodySignatureFormat = SignatureUtils
									.eniSigntureFormatToInernalSignatureFormat(custodyAdvancedSignatureFormats[i]);
							eniSignatureProfile = SignatureUtils
									.getEniSignatureProfile(custodyAdvancedSignatureFormats[i]);
							LOGGER.debug("Formato m�nimo exigido configurado para el tipo de firma "
									+ eniSignatureType.getName() + ": " + custodyAdvancedSignatureFormats[i] + ".");
							found = Boolean.TRUE;
						}
					}

					if (!found || SignatureFormat.UNRECOGNIZED.equals(minCustodySignatureFormat)
							|| eniSignatureProfile == null) {
						throw new GdibException(
								"Sistema mal cofigurado, debe establecerse un formato de firma electr�nica avanzado m�nimo para el tipo de firma "
										+ eniSignatureType.getName() + ".");
					}

					// Si el formato de firma es inferior, se evoluciona al m�nimo exigido
					if (minCustodySignatureFormat.isMoreAdvancedSignatureFormat(signatureFormat)) {
						LOGGER.debug(
								"Formato de firma inferior al m�nimo exigido para custodia, se procede a evolucionar la firma al formato: "
										+ minCustodySignatureFormat.getName() + " (Perfil de firma: "
										+ eniSignatureProfile.getName() + ").");
						LOGGER.debug("Preparando invocaci�n a plataforma @firma (UpgradeFirma)...");
						signature = signatureService.upgradeSignature(signature, minCustodySignatureFormat);
						LOGGER.debug("Modificando firma electr�nica del documento " + nodeIdValue + "....");
						DataHandler signatureDataHandler = new DataHandler(
								new InputStreamDataSource(new ByteArrayInputStream(signature)));
						// Se actualiza la informaci�n del nodo y la firma electr�nica
						if (implicitSignature) {
							if (node.getContent() != null) {
								node.getContent().setData(signatureDataHandler);
							} else { // si el contenido no esta en el nodo es porque no se pasa como parametro pero
										// existe anteriormente
								Content contenido = utils.getContent(utils.idToNodeRef(node.getId()));
								contenido.setData(signatureDataHandler);
								node.setContent(contenido);
							}
							node.setSign(null);
						} else {
							node.setSign(signatureDataHandler);
						}
						
						LOGGER.debug("Se procede a poner fecha de sellado del documento " + nodeIdValue + "....");
						utils.updateResealDate(node);
					}

					// Se verifica que el perfil de firma informado es el mismo que el retornado por
					// @firma
					// Si no es as�, se modifica el metadato
					if (!signatureProfileNodeProp.equals(eniSignatureProfile.getName())) {
						LOGGER.debug("Modificando el metadato " + EniModelUtilsInterface.PROP_PERFIL_FIRMA
								+ " del documento " + nodeIdValue + ". Nuevo valor: " + eniSignatureProfile.getName()
								+ ".");
						Boolean propFound = Boolean.FALSE;

						for (int i = 0; !propFound && i < node.getProperties().size(); i++) {
							if (node.getProperties().get(i).getQname()
									.endsWith(EniModelUtilsInterface.PROP_PERFIL_FIRMA)) {
								node.getProperties().get(i).setValue(eniSignatureProfile.getName());
								LOGGER.debug("Metadato " + EniModelUtilsInterface.PROP_PERFIL_FIRMA + " del documento "
										+ nodeIdValue + " encontrado y modificado. Nuevo valor: "
										+ eniSignatureProfile.getName() + ".");
								propFound = Boolean.TRUE;
							}
						}
						if (!propFound) {
							nodeService.setProperty(utils.idToNodeRef(node.getId()),
									EniModelUtilsInterface.PROP_PERFIL_FIRMA_QNAME, eniSignatureProfile.getName());
						}
					}

					LOGGER.debug("Validaci�n firma electr�nica del documento " + nodeIdValue + " finalizada.");
				} else if (result.getValidationStatus() == ValidationStatus.NO_CORRECTO) {
					LOGGER.debug("Resultado de validaci�n de la firma electr�nica del documento " + node.getId()
							+ " incorrecto.");
					throw new GdibException("La firma del documento " + node.getId() + " no es valida: "
							+ result.getValidationMessage() + "(" + result.getDetailedValidationStatus() + ").");
				} else if (result.getValidationStatus() == ValidationStatus.NO_DETERMINADO) {
					LOGGER.debug("Resultado de validaci�n de la firma electr�nica del documento " + node.getId()
							+ " no determinado.");
					throw new GdibException(
							"No ha sido posible determinar la validez de la firma del documento " + node.getId() + ".");
				}
			}
		} catch (GdibException e) {
			throw e;
		} finally {
			LOGGER.debug("Finalizada la validaci�n de la firma electr�nica del documento " + node.getId());
		}
	}

	/**
	 * Checks para modificar nodo
	 * 
	 * @param nodeRef NodeRef del nodo que se va a modificar.
	 *
	 */
	private void modifyNodeCheck(NodeRef nodeRef) throws GdibException {

		// comprobar si tiene permisos
		utils.hasPermission(nodeRef, CaibServicePermissions.WRITE);

		// compruebo que el padre donde voy a crear el nodo este dentro del DM
		utils.inDMPath(nodeRef);

		// compruebo si el nodo es una version
		utils.checkIsAVersion(nodeRef);

		// compruebo si el nodo esta bloqueado
		if (utils.isNodeLocked(nodeRef)) {
			throw exUtils.lockedNode(nodeRef.getId());
		}

		NodeRef parentExpedient = utils.getExpedientNodeRef(nodeRef);
		if (utils.isType(nodeService.getType(nodeRef), ConstantUtils.TYPE_DOCUMENTO_QNAME)) {
			String expState = utils.getPropertyFromExpedient(parentExpedient, ConstantUtils.PROP_ESTADO_EXP_QNAME);
			if (!ConstantUtils.ESTADO_EXP_E01.equals(expState)) {
				// el expediente no esta abierto
				throw exUtils.notOpenExpedientException(parentExpedient.getId());
			}
		}
	}

	/**
	 * Compara y aÃ±ade/borra/modifica las propiedades de un nodo para la operacion
	 * de modifyNode
	 *
	 * @param type               Tipo del nodo
	 * @param updateNode         Nodo actualizado
	 * @param updateProperties   Nuevas propiedades
	 * @param originalProperties Propiedades anteriores del nodo
	 *
	 */
	private void modifyNodeProperties(Node node, List<Property> originalProperties, String esbOperation,
			boolean isFinalNode, boolean isSign) throws GdibException {

		// Si el nombre cambia Y no hay definida en node ninguna propiedad de nombre
		if (node.getProperties() == null)
			node.setProperties(new ArrayList<Property>());
		// nombre nodo original, de las props del nodo original
		String nameOriginal = originalProperties.get(originalProperties.indexOf(new Property(ContentModel.PROP_NAME)))
				.getValue();
		// nombre del nodo actualizado, del dato name de la clase Node
		String newName = node.getName();
		// nombre del nodo actualizado, de las propiedades del nodo.
		int newNameIndex = node.getProperties().indexOf(new Property(ContentModel.PROP_NAME));
		String newNameProp = "";
		if (newNameIndex != -1)
			newNameProp = node.getProperties().get(newNameIndex).getValue();
		// Si el nodo actualizado en las propiedades no tiene name (caso facil)
		if ("".equals(newNameProp)) {
			if (newName != null && !newName.equals(nameOriginal)) { // si tiene nuevo nombre se lo pongo.
				node.getProperties().add(new Property(ContentModel.PROP_NAME, newName));
			}
		} else { // el nodo actualizado tiene name en sus props.
			if (newNameProp.equals(nameOriginal)) { // El nombre en las props es el original
				if (newName != null && !newName.equals(nameOriginal)) { // si tiene nuevo nombre se lo pongo.
					node.getProperties().get(newNameIndex).setValue(newName);
				}
			} else { // El nombre en las props NO es el original.
				/**
				 * El nombre original es diferente a newname y a newnameprop, es decir, el
				 * nombre en las propiedades y el nombre en el mÃ©todo getName son diferentes
				 * del orignial. En este punto solo caben dos opciones, ambos son iguales (no
				 * habr�a que hacer nada) o son diferentes, con lo que habr�a que lanzar una
				 * excepci�n porque se est� intentando modificar dos veces el nombre
				 */
				if (newName != null && !newName.equals(newNameProp)) {
					exUtils.checkMetadataException(ContentModel.PROP_NAME.getLocalName());
				}
			}
		}

		// elimino las propiedades calculadas
		utils.fillNodeMetadata(node);
		List<Property> updateProperties = utils.filterCalculatedProperties(node.getProperties());
		originalProperties = utils.filterCalculatedProperties(originalProperties);

		// valida las propiedades que se van a modificar
		utils.checkValidProperties(updateProperties);
		// obtengo las propiedades que han cambiado de valor con respecto al original
		Map<QName, Serializable> properties = utils.getModifyProperties(updateProperties, originalProperties);

		// Si el nodo es final, comprobamos que no se cambia un conjunto de propiedades
		// que no se pueden modificar
		if (isFinalNode)
			utils.checkFinalMandatoryProperties(node.getType(), properties, esbOperation);

		// (31/05/2023) Se le modifica siempre el perfil de firma a uno BES, para que no venga dado errónamente 
		// por aplicaciones clientes y así se calcule siempre automáticamente.
		if (isSign) {
			String signTypeProp = utils.getProperty(node.getProperties(),
					EniModelUtilsInterface.ENI_MODEL_PREFIX + EniModelUtilsInterface.PROP_TIPO_FIRMA);
			LOGGER.debug("Tipo de firma ENI para inicializar: " + signTypeProp);
			EniSignatureType eniSignatureType = EniSignatureType.valueOf(signTypeProp);
			
			if (!EniSignatureType.TF01.equals(eniSignatureType)
					&& !EniSignatureType.TF04.equals(eniSignatureType)) {
				LOGGER.info("Se modifica el perfil de firma:"+ properties.get(ConstantUtils.PROP_PERFIL_FIRMA_QNAME) + " a: " +  CaibConstraintsUtilsInterface.PERFIL_FIRMA_BES);
				if (properties.get(ConstantUtils.PROP_PERFIL_FIRMA_QNAME)==null) {
					properties.put(ConstantUtils.PROP_PERFIL_FIRMA_QNAME, CaibConstraintsUtilsInterface.PERFIL_FIRMA_BES);
				}else {
					properties.replace(ConstantUtils.PROP_PERFIL_FIRMA_QNAME, CaibConstraintsUtilsInterface.PERFIL_FIRMA_BES);	
				}
			}
		}
		
		nodeService.addProperties(utils.idToNodeRef(node.getId()), properties);
	}

	/**
	 * Compara y aÃ±ade/borra/modifica los aspectos de un nodo para la operacion de
	 * modifyNode
	 *
	 * @param updateNode      El NodeRef del nodo modificado
	 * @param updateAspects   Los nuevos aspectos
	 * @param originalAspects Los aspectos anteriores del nodo.
	 */
	private void modifyNodeAspects(NodeRef updateNode, List<String> updateAspects, List<String> originalAspects)
			throws GdibException {
		List<String> modifyAspects = null;
		List<String> removeAspects = null;

		// si el nodo no es borrador, no se puede aÃ±adir el aspecto de borrador
		if (!utils.contains(originalAspects, ConstantUtils.ASPECT_BORRADOR_QNAME)
				&& utils.contains(updateAspects, ConstantUtils.ASPECT_BORRADOR_QNAME)) {
			throw exUtils.invalidAddDraftAspectExcepcion();
		}

		removeAspects = utils.getRemoveAspects(updateAspects);
		utils.checkValidAspects(removeAspects);
		utils.checkMandatoryAspects(updateNode, removeAspects);
		utils.removeAspects(updateNode, removeAspects);

		modifyAspects = utils.getModifyAspects(utils.filterRemoveAspects(updateAspects), originalAspects);
		utils.checkValidAspects(modifyAspects);
		utils.addAspects(updateNode, utils.transformListStringToQname(modifyAspects));
	}

	/**
	 * Modifica el contenido de un nodo para la operaci�n modifyNode.
	 *
	 * @param node            NodeRef del nodo a modificar su contenido.
	 * @param updateContent   Nuevo contenido del nodo
	 * @param originalContent Contenido original del nodo.
	 *
	 */
	private void modifyContentNode(NodeRef node, Content updateContent) throws GdibException {
		if (updateContent == null)
			return;
		try {
			utils.setDataHandler(node, ContentModel.PROP_CONTENT, updateContent.getData(), updateContent.getMimetype());
			/*
			 * if(!utils.compareDataHandlers(updateContent.getData(),originalContent.getData
			 * ()) && !isFinalNode){ // validacion de no realizar esta operacion con
			 * documentos finales y si cambia el contenido throw
			 * exUtils.setContentException(node.getId(), new
			 * IOException("No se puede modificar el contenido de un documento definitivo.")
			 * ); }else{ utils.setDataHandler(node, ContentModel.PROP_CONTENT,
			 * updateContent.getData(), updateContent.getMimetype()); }
			 */
		} catch (IOException exception) {
			throw exUtils.setContentException(node.getId(), exception);
		}

	}

	/***
	 * Modifica la firma de un nodo para la operaci�n de modifyNode
	 *
	 * @param node         NodeRef del nodo
	 * @param updateSign   Nueva firma
	 * @param originalSign Firma anterior.
	 *
	 */
	private void modifySignNode(NodeRef node, DataHandler updateSign) throws GdibException {
		if (updateSign == null)
			return;

		try {
			utils.setDataHandler(node, ConstantUtils.PROP_FIRMA_QNAME, updateSign, MimetypeMap.MIMETYPE_BINARY);
			/*
			 * if(!utils.compareDataHandlers(updateSign,originalSign) && isFinalNode){ //
			 * validacion de no realizar esta operacion con documentos finales y si cambia
			 * la firma throw exUtils.setContentException(node.getId(), new
			 * IOException("No se puede modificar la firma en documentos finales")); }else{
			 * utils.setDataHandler(node, ConstantUtils.PROP_FIRMA_QNAME, updateSign,
			 * MimetypeMap.MIMETYPE_BINARY); }
			 */
		} catch (IOException exception) {
			throw exUtils.setContentException(node.getId(), exception);
		}

	}

	/**
	 * Generaci�n del export de un expediente sin comprobaciones previas.
	 *
	 * @param nodeRef NodeRef del expediente a exportar.
	 * @return Identificador del subexpediente generado.
	 *
	 */
	private DataHandler _internal_export(final NodeRef nodeRef) throws GdibException {
		/*
		 * String result = AuthenticationUtil.runAsSystem(new RunAsWork<String>() {
		 * // @Override public String doWork() throws Exception {
		 */
		// copio el expediente a una carpeta temporal - CompanyHome/temp
		NodeRef tempNode = utils.idToNodeRef(tempFolder);
//		    	NodeRef exportExpedient = _internal_copy(tempNode, nodeRef);
		// tengo que copiar el expediente cambiando el nombre y todos los documentos
		// aÃ±adiendo al nombre la _fecha
		String dateName = "_" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
		NodeRef exportExpedient = copyExpedientExport(tempNode, nodeRef, dateName, true);

		// borro dentro de ese expediente copiado la carpeta con las exportaciones
//		    	NodeRef exportFolder = fileFolderService.searchSimple(exportExpedient, ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME);
		// si no existe la carpeta es porque es la primera exportacion y no tenemos que
		// borrarla
//		    	if(exportFolder!=null)	fileFolderService.delete(exportFolder);
		NodeRef ie_node = null;
		try {
			// Folio el expediente generando el XML de indice electr�nico ENI firmado
			DataHandler dh = _internal_foliate(nodeRef, AdministrativeProcessingIndexSignerFactory.ENI_INDEX_V10);

			String eniId = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_ID_QNAME);
			String dateString = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
			String exchangeIndexNodeName = ConstantUtils.EXCHANGE_INDEX_NAME_PREFIX + eniId + "-" + dateString + ".xml";
			Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
			props.put(ContentModel.PROP_NAME, exchangeIndexNodeName);
			props.put(ConstantUtils.PROP_INDEX_TYPE_QNAME, ConstantUtils.EXCHANGE_ENI_V10_INDEX_TYPE);

			// Lo aÃ±ado a la raiz del expediente de exportacion.
			ie_node = _internal_createNode(exportExpedient, utils.createNameQName(exchangeIndexNodeName),
					ConstantUtils.TYPE_FILE_INDEX_QNAME, props);

			// Escribo el contenido del Datahandler en el content del Nodo
			ContentWriter writer = contentService.getWriter(ie_node, ContentModel.PROP_CONTENT, true);
			writer.setMimetype(MimetypeMap.MIMETYPE_XML);
			writer.setEncoding(DEFAULT_CHARSET_ENCODING);
			writer.putContent(IOUtils.toString(dh.getInputStream(), DEFAULT_CHARSET_ENCODING));
		} catch (ContentIOException | IOException e) {
			throw new GdibException(e.getMessage());
		}

		// muevo el expediente de exportacion a la carpeta "export" dentro del
		// expediente
		NodeRef exportFolder = fileFolderService.searchSimple(nodeRef, ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME);
		// si es la primera vez que se exporta esta carpeta no existira dentro del
		// expediente, y tenemos que crearla
		if (exportFolder == null)
			exportFolder = fileFolderService
					.create(nodeRef, ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME, ConstantUtils.TYPE_FOLDER)
					.getNodeRef();
		_internal_moveNode(exportExpedient, exportFolder);

		// hay que incluir tambien un aspecto de que es transferible
		List<QName> aspects = new ArrayList<QName>();
		aspects.add(ConstantUtils.ASPECT_TRANSFERIBLE_QNAME);
		utils.addAspects(exportExpedient, aspects);
		// cambiar propiedades para indicar que es un expediente de exportacion
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property(ConstantUtils.PROP_ESTADO_EXP_QNAME.toString(), ConstantUtils.ESTADO_EXP_E03));
		utils.addProperties(exportExpedient, properties);
		// cambiar los permisos para que solo sea de lectura el subexpediente creado.

		for (AccessPermission accessPermission : permissionService.getAllSetPermissions(exportExpedient)) {
			for (String permission : CaibServicePermissions.WRITE.getPermissions()) {
				permissionService.setPermission(exportExpedient, accessPermission.getAuthority(), permission, false);
			}
			for (String permission : CaibServicePermissions.READ.getPermissions()) {
				permissionService.setPermission(exportExpedient, accessPermission.getAuthority(), permission, true);
			}
		}
		// devuelvo el indice
		if (ie_node != null) {
			return utils.getContent(ie_node).getData();
		}
		return null;
		// devuelvo el uuid del expediente de exportacion
		// return exportExpedient.getId();
		/*
		 * } }); return result;
		 */
	}

	private NodeRef copyExpedientExport(NodeRef temp, NodeRef exp, String dateName, Boolean isFileRoot)
			throws GdibException {
		// genero el nuevo expediente
		NodeRef newExp = null;
		String name = (String) nodeService.getProperty(exp, ConstantUtils.PROP_NAME);
		newExp = copyService.copyAndRename(exp, temp, ContentModel.ASSOC_CONTAINS,
				utils.createNameQName(name + dateName), false);
		nodeService.setProperty(newExp, ConstantUtils.PROP_NAME, name + dateName);

		List<ChildAssociationRef> childs = nodeService.getChildAssocs(exp);
		for (ChildAssociationRef child : childs) {
			NodeRef son = child.getChildRef();
			if (fileFolderService.getFileInfo(son).isFolder()) {
				String childName = (String) nodeService.getProperty(son, ConstantUtils.PROP_NAME);
				if (nodeService.getPrimaryParent(son).getParentRef().equals(exp)) {
					if (ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME.equals(childName) && isFileRoot) {
						if (addEniExchangeFiles) {
							copyExpedientExport(newExp, son, dateName, false);
						}
					} else {
						copyExpedientExport(newExp, son, dateName, false);
					}
				} else {
					// es un expediente enlazado
					_internal_ref(newExp, son);
				}
			} else {
				String nameSon = (String) nodeService.getProperty(son, ConstantUtils.PROP_NAME);
				NodeRef newSon = copyService.copyAndRename(son, newExp, ContentModel.ASSOC_CONTAINS,
						utils.createNameQName(nameSon + dateName), false);
				nodeService.setProperty(newSon, ConstantUtils.PROP_NAME, nameSon + dateName);
			}
		}
		return newExp;
	}

	/**
	 * Funci�n que bloquea un nodo sin comprobaciones previas
	 *
	 * @param node NodeRef del nodo a bloquear.
	 *
	 */
	private void _internal_lockNode(NodeRef node) throws GdibException {
		// compruebo si el nodo no esta bloqueado, si ya esta bloqueado no se hace nada
		if (!utils.isNodeLocked(node)) {
			utils.lockNode(node);
		}
	}

	private void _internal_unlockNode(NodeRef nodeRef) throws GdibException {
		if (utils.isNodeLocked(nodeRef)) {
			utils.unlockNode(nodeRef);
		}

	}
	
	public String _createNode(Node node, String parentId, GdibHeader gdibHeader) throws GdibException {
		NodeRef nodeRef;
		// comprobar parametros de entrada
		long initMill = System.currentTimeMillis();		
		LOGGER.debug("Comienza _createNode. Se comprueba que node sea correcto.");
		
//		String prop = "";
//		for (Property p: node.getProperties()) prop += prop + p.getQname() + ":" + p.getValue() + "#";
//		LOGGER.debug(String.format("nodeId: %s, name: %s, type: %s, aspects: %s, properties: %s", node.getId(), node.getName(), node.getType(), node.getAspects(), prop));
		
		utils.checkNode(node);
		LOGGER.debug("Comprobacion uid padre.");
		NodeRef parentRef = utils.checkParentId(node, parentId);
		long checkMill = System.currentTimeMillis();
		LOGGER.debug("Relleno metadatos automaticos");
		// relleno los metadatos automaticos del nodo
		utils.fillNodeMetadata(node);
		LOGGER.debug("Ultimas comprobaciones.");
		long signMill = 0;
		if (!repositoryDisableCheck.booleanValue()) {
			if (!utils.contains(node.getAspects(), ConstantUtils.ASPECT_BORRADOR_QNAME)) {
				if (utils.isType(node.getType(), ConstantUtils.TYPE_DOCUMENTO_QNAME)) {
					LOGGER.debug("Documento: NO es un borrador. Se pasa a chequear la firma");
					long beginMill = System.currentTimeMillis();
					// Se comprueba para todos menos los migrados transformados.
					if (!utils.contains(node.getAspects(), ConstantUtils.ASPECT_TRANSFORMADO_QNAME)) {
						checkDocumentSignature(node);
					}
					// incluir a lista de propiedades la fecha de sellado pues la firma es valida
					utils.updateResealDate(node);
					signMill = System.currentTimeMillis() - beginMill;
					LOGGER.debug("Firma checkeada. Se pasa a comprobar el cod Classif.");
					checkDocClassification(node, parentRef);
					//checkDocCSV(node, parentRef);
				} else if (utils.isType(node.getType(), ConstantUtils.TYPE_EXPEDIENTE_QNAME)) {
					if (parentId != null && !parentId.isEmpty()) {
						// Subexpediente
						LOGGER.debug("Expediente: NO es un borrador. Se pasa a chequear el cod clasif.");
						checkDocClassification(node, parentRef);
					}
				}
				LOGGER.debug("Última comprobación integridad del nodo.");
				utils.checkNodeIntegrity(node);
				verifySubtypeDoc(node);
			} else {
				if (utils.isType(node.getType(), ConstantUtils.TYPE_DOCUMENTO_QNAME)) {
					LOGGER.debug("Documento: SI es un borrador. Se pasa a chequear la firma si está firmado");
					// (06/06/2023) Si no tiene el documento firma o no es válida y no es estado final del
					// documento, se deja las propiedades que viene de las aplicaciones cliente y no se firma el documento.
					String signTypeProp, nodeIdValue;
					boolean isSign = true;
					nodeIdValue = (node.getId() == null ? "nuevo documento" : node.getId());
					LOGGER.debug("Se inicia la validacion de la firma electr�nica del documento " + nodeIdValue);
					signTypeProp = utils.getProperty(node.getProperties(),
							EniModelUtilsInterface.ENI_MODEL_PREFIX + EniModelUtilsInterface.PROP_TIPO_FIRMA);
					LOGGER.debug("Tipo de firma ENI: " + signTypeProp);
					if (signTypeProp == null) {
						isSign = false;
					}
					LOGGER.debug("Está firmado?: " + isSign);
					
					if (isSign) {
						EniSignatureType eniSignatureType = EniSignatureType.valueOf(signTypeProp);
						if (!EniSignatureType.TF01.equals(eniSignatureType)
							&& !EniSignatureType.TF04.equals(eniSignatureType)) {
								long beginMill = System.currentTimeMillis();
								// Se comprueba para todos menos los migrados transformados.
								if (!utils.contains(node.getAspects(), ConstantUtils.ASPECT_TRANSFORMADO_QNAME)) {
									checkDocumentSignature(node);
								}
								// incluir a lista de propiedades la fecha de sellado pues la firma es valida
								utils.updateResealDate(node);
								signMill = System.currentTimeMillis() - beginMill;
								LOGGER.debug("Firma checkeada. Se pasa a comprobar el cod Classif.");
								checkDocClassification(node, parentRef);
						}
					}
				}
			}
		}
		LOGGER.debug("Preparación para la llamada al servicio");
		// preparar datos
		QName name = utils.createNameQName(node.getName());
		QName type = GdibUtils.createQName(node.getType());
		Map<QName, Serializable> props = utils.transformMapStringToQname(node.getProperties());
		List<QName> aspects = utils.transformListStringToQname(node.getAspects());
		long prepareProps = System.currentTimeMillis();
		LOGGER.debug("Se llama al servicio de creación de nodos");
		nodeRef = _internal_createNode(parentRef, name, type, props, aspects, node.getContent(), node.getSign(),
				utils.getESBOp(gdibHeader));		
		long endCreate = System.currentTimeMillis();
		LOGGER.info(nodeRef.getId() + " creado en " + (endCreate - initMill) + "ms (Checks: " + (checkMill - initMill)
				+ "ms Props: " + (prepareProps - checkMill - signMill) + "ms Firma: " + signMill + "ms Servicio: "
				+ (endCreate - prepareProps) + "ms).");
		return nodeRef.getId();
	}

	/**
	 * Servicio que crea un nodo en el Repositorio DM de Alfresco. Se realizar�n las
	 * comprobaciones necesarias: Permisos, formato de metadatos, tipo, etc...
	 * Adem�s se realizan dos comprobaciones adicionales dependiendo del flag
	 * repositoryDisableCheck est� activo o no: 1Âº Que el nodo creado estÃ© dentro
	 * del DM. 2Âº Que tenga una serie documental v�lida. El formato de QName
	 * siempre que venga especificado como un String podr� ser tanto formato
	 * extendido {uri}prop o reducido prefix:prop .
	 *
	 * Adem�s los Ids de nodos podr�n venir especificados tanto en nodeRef como en
	 * paths relativos tomando siempre como primer nodo un Id de NodeRef. por
	 * ejemplo: 1111-11111-11111-11111/archivos/pruebas/test.pdf
	 *
	 * @param node       Nodo a crear
	 * @param parentId   Id del padre
	 * @param gdibHeader cabecera de auditor�a y configuraci�n.
	 * @return Id del nodo a devolver
	 * @throws GdibException Si se produce cualquier error en el proceso (Falta de
	 *                       permisos, errores de formato, de tipos, QNames...).
	 *
	 */
	@Override
	public String createNode(Node node, String parentId, GdibHeader gdibHeader) throws GdibException {
		String ret = _createNode(node, parentId, gdibHeader);
		return ret;
	}

	/**
	 * Comprueba que la serie documental de un nodo, subexpediente o documento, sea
	 * la misma que la de su expediente padre
	 * 
	 * @param node      Nodo a verificar
	 * @param parentRef Nodo padre inmediato
	 * @throws GdibException si el nodo y el expediente padre no pertenecen a la
	 *                       misma serie documental.
	 */
	private void checkDocClassification(Node node, NodeRef parentRef) throws GdibException {

		// Check para eni:documento y eni:expediente (subexpediente), de la serie
		// documental. Nodo y padre expediente deben tener el mismo codigo de
		// clasificacion

		if (utils.isType(node.getType(), ConstantUtils.TYPE_DOCUMENTO_QNAME)
				|| utils.isType(node.getType(), ConstantUtils.TYPE_EXPEDIENTE_QNAME)) {
			String nodeCodClasif = utils.getProperty(node.getProperties(), ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
			String parentCodCasif = utils.getCodClasificacion(parentRef);
			if (!StringUtils.isEmpty(nodeCodClasif) && !nodeCodClasif.equals(parentCodCasif)) {
				throw exUtils.checkMetadataValueException(ConstantUtils.PROP_COD_CLASIFICACION_QNAME.toString(),
						nodeCodClasif);
			}
		}
	}
	
	private void checkDocCSV(Node node, NodeRef parentRef) throws GdibException {
		if (utils.isType(node.getType(), ConstantUtils.TYPE_DOCUMENTO_QNAME)) {
			String csv = utils.getProperty(node.getProperties(), ConstantUtils.PROP_CSV);			
			if (csv == null || StringUtils.isEmpty(csv)) {
				throw exUtils.checkMetadataValueException(ConstantUtils.PROP_CSV.toString(), "null");//			nodeCodClasif);
			}
		}
	}

	/**
	 * Servicio que crea un nodo y lo devuelve.
	 * 
	 * @see createNode
	 * @param node       Nodo a crear
	 * @param parentId   Id del nodo padre donde se va a crear el nuevo nodo.
	 * @param GdibHeader Cabecera de auditor�a y configuraci�n.
	 * @return Objeto Node con los metadatos y aspectos pero sin firma ni contenido.
	 * @throws GdibException Si se produjera cualquier error en el proceso (Falta de
	 *                       permisos, errores de formato, de tipos, QNames...).
	 *
	 */
	@Override
	public Node createAndGetNode(Node node, String parentId, GdibHeader gdibHeader) throws GdibException {
		String id;
		Node ret;
		long flag = System.currentTimeMillis();
//		synchronized(createMutex) {
			id = _createNode(node, parentId, gdibHeader);
			ret = _internal_getNode(utils.toNodeRef(id), true, true);
//		}
		LOGGER.info(ret.getId() + " nodo construido y devuelto en " + (System.currentTimeMillis() - flag) + "ms.");
		return ret;
	}

	/**
	 * Servicio que modifica la informaci�n de un nodo de alfresco. Se aplican las
	 * mismas restricciones que al crear nodo. Tiene la particularidad del
	 * tratamiento de un documento al pasar de estado borrador a estado final.
	 * Impide la modificaci�n de la firma o contenido de un documento en estado
	 * final. La forma en que se modifica un nodo es la siguiente: 1Âº Se especifica
	 * al servicio el nodo con los metadatos o propiedades que se quieran. Estos
	 * podr�n ser iguales o diferentes de los originales. Los metadatos/aspectos que
	 * sean iguales no se hace nada con ellos. Aquellos que sean diferentes se
	 * cambiar�n. Los que no aparezcan tampoco se modificar�n. Y para eliminar
	 * propiedades o metadatos ser� necesario especificarlo mediante el car�cter "-"
	 * en su QName. Por ejemplo: -eni:id eliminar�a la propiedad ID. El sistema
	 * comprueba que no se puedan eliminar metadatos/aspectos obligatorios, si el
	 * documento esta en estado final
	 *
	 * @param node       Nodo que se quiere modificar.
	 * @param gdibHeader cabecera de auditor�a y configuraci�n
	 * @throws GdibException Si se produjera cualquier error en el proceso (Falta de
	 *                       permisos, errores de formato, de tipos, QNames...).
	 */
	@Override
	public void modifyNode(Node node, GdibHeader gdibHeader) throws GdibException {

		long initMod = System.currentTimeMillis();
		NodeRef nodeRef = utils.checkNodeId(node.getId());
		utils.checkRestriction(nodeRef, gdibHeader);
		this.modifyNodeCheck(nodeRef);
		Node original = _internal_getNode(nodeRef, true, true);
		// compruebo que el tipo de nodo es el mismo
		if (node.getType() == null)
			node.setType(nodeService.getType(nodeRef).toString());
		if (!utils.isType(node.getType(), original.getType())) {
			throw exUtils.changeTypeException(node.getType(), original.getType());
		}

		boolean isFinalNode = false;
		if (!utils.contains(original.getAspects(), ConstantUtils.ASPECT_BORRADOR_QNAME)) {
			isFinalNode = true;
		}
		

		// (06/06/2023) Si no tiene el documento firma o no es válida y no es estado final del
		// documento, se deja las propiedades que viene de las aplicaciones cliente y no se firma el documento.
		String signTypeProp, nodeIdValue;
		boolean isSign = true;
		nodeIdValue = (node.getId() == null ? "nuevo documento" : node.getId());
		LOGGER.debug("Se inicia la validacion de la firma electr�nica del documento " + nodeIdValue);
		signTypeProp = utils.getProperty(node.getProperties(),
				EniModelUtilsInterface.ENI_MODEL_PREFIX + EniModelUtilsInterface.PROP_TIPO_FIRMA);
		LOGGER.debug("Tipo de firma ENI: " + signTypeProp);
		if (signTypeProp == null) {
			if (utils.contains(original.getAspects(), ConstantUtils.ASPECT_BORRADOR_QNAME)
					&& !nodeService.hasAspect(nodeRef, ConstantUtils.ASPECT_BORRADOR_QNAME)) {
				throw new GdibException("La propiedad o metadato " + EniModelUtilsInterface.PROP_TIPO_FIRMA
						+ " del documento " + nodeIdValue + " no ha sido establecida.");
			} else {
				isSign = false;
				nodeService.removeProperty(utils.idToNodeRef(original.getId()), EniModelUtilsInterface.PROP_TIPO_FIRMA_QNAME);
				nodeService.removeProperty(utils.idToNodeRef(original.getId()), EniModelUtilsInterface.PROP_PERFIL_FIRMA_QNAME);
				nodeService.removeProperty(utils.idToNodeRef(original.getId()), EniModelUtilsInterface.PROP_FECHA_SELLADO_QNAME);
				nodeService.removeProperty(utils.idToNodeRef(original.getId()), EniModelUtilsInterface.PROP_CSV_QNAME);
				nodeService.removeAspect(utils.idToNodeRef(original.getId()), EniModelUtilsInterface.ASPECT_FIRMADO_BASE_QNAME);
			}
		} else {
			EniSignatureType eniSignatureType = EniSignatureType.valueOf(signTypeProp);
			if (!EniSignatureType.TF01.equals(eniSignatureType)
					&& !EniSignatureType.TF04.equals(eniSignatureType)) {
				nodeService.setProperty(nodeRef, EniModelUtilsInterface.PROP_PERFIL_FIRMA_QNAME, nodeIdValue);
			}
		}
		LOGGER.debug("Está firmado?: " + isSign);
		modifyNodeAspects(nodeRef, node.getAspects(), original.getAspects());
		modifyNodeProperties(node, original.getProperties(), utils.getESBOp(gdibHeader), isFinalNode, isSign);
		LOGGER.info("ModifyNode - Nombre: " + node.getName());
		LOGGER.info("ModifyNode - Aspectos: " + node.getAspects());
		LOGGER.info("ModifyNode - Es nodo final? " + isFinalNode);
		
		if (!isFinalNode) {
			LOGGER.info("ModifyNode - Se modifica el contenido del documento");
			modifyContentNode(nodeRef, node.getContent());
			LOGGER.info("ModifyNode - Se firma el documento");
			modifySignNode(nodeRef, node.getSign());
		}
		// Esto NO se puede hacer -> this.getNode(nodeRef.getId(), false, false,
		// gdibHeader);
		Node newNode = _internal_getNode(nodeRef, false, false);
		this.checkCalculateEniId(nodeRef, null);
		
		long signMill = 0;
		// (31/05/2023) Se comprueba el resellar sólo si no es definitivo.
		if (utils.contains(original.getAspects(), ConstantUtils.ASPECT_BORRADOR_QNAME) && isSign) {
			// Documentos
			NodeRef parentNodeRef = nodeService.getPrimaryParent(nodeRef).getParentRef();
			checkDocClassification(node, parentNodeRef);
			if (utils.contains(original.getAspects(), ConstantUtils.ASPECT_BORRADOR_QNAME)
					&& !nodeService.hasAspect(nodeRef, ConstantUtils.ASPECT_BORRADOR_QNAME)) {
				utils.checkNodeIntegrity(newNode);
			}
			
			verifySubtypeDoc(newNode);

			long initSign = System.currentTimeMillis();

			String perfil = utils.getProperty(newNode.getProperties(),
					EniModelUtilsInterface.ENI_MODEL_PREFIX + EniModelUtilsInterface.PROP_PERFIL_FIRMA);
			LOGGER.info("Perfil de firma antes de comprobar firma: " + perfil);
			checkDocumentSignature(newNode);
			String newperfil = utils.getProperty(newNode.getProperties(),
					EniModelUtilsInterface.ENI_MODEL_PREFIX + EniModelUtilsInterface.PROP_PERFIL_FIRMA);
			LOGGER.info("Perfil de firma despues de comprobar firma: " + newperfil);

			if (!perfil.equals(newperfil)) {
				// El perfil de firma ha cambiado.
				LOGGER.info("El Perfil de firma ha cambiado de: " + perfil + " a: " + newperfil);
				String signatureTypeProp = utils.getProperty(newNode.getProperties(),
						EniModelUtilsInterface.ENI_MODEL_PREFIX + EniModelUtilsInterface.PROP_TIPO_FIRMA);
				EniSignatureType eniSignatureType = EniSignatureType.valueOf(signatureTypeProp);
				if (!EniSignatureType.TF01.equals(eniSignatureType)
						&& !EniSignatureType.TF04.equals(eniSignatureType)) {
					// actualizar contenido
					modifyContentNode(nodeRef, newNode.getContent());
				} else {
					// actualizar firma
					modifySignNode(nodeRef, newNode.getSign());
				}
				// modificamos el perfil de firma.
				nodeService.setProperty(nodeRef, EniModelUtilsInterface.PROP_PERFIL_FIRMA_QNAME, newperfil);
			}
			// se incluye la fecha sellado pues el documento a sido firmado correctamente
			// utils.updateResealDate(nodeRef);
			signMill = System.currentTimeMillis() - initSign;
		}

		Map<String, Serializable> props = new HashMap<String, Serializable>();
		// para crear versiones menores, se puede pasar "VersionType.MAJOR" para una
		// version mayor
		props.put("VersionType", VersionType.MINOR);
		versionService.createVersion(nodeRef, props);
		this.createVersion(nodeRef);
		long endMill = System.currentTimeMillis();
		LOGGER.info(node.getId() + "modificado en " + (endMill - initMod) + "ms (Firma: " + signMill + "ms).");
	}

	/**
	 *
	 * Recupera un nodo del repositorio.
	 *
	 * @param nodeId      Identificado del nodo a recuperar
	 * @param withContent Flag que indica si se requiere la recuperaci�n del
	 *                    contenido
	 * @param withSigh    Flag que indica si se requiere la recuperaci�n de la
	 *                    firma.
	 * @param gdibHeader  Cabecera de auditor�a y configuraci�n.
	 * @return Objeto con la informaci�n del nodo.
	 * @throws GdibException si el identificador de nodo no es correcto.
	 * @throws GdibException si el nodo no pertenece al repositorio.
	 *
	 */
	@Override
	public Node getNode(String nodeId, boolean withContent, boolean withSign, GdibHeader gdibHeader)
			throws GdibException {
		long initMill = System.currentTimeMillis();
		// compruebo parametros de entrada
		NodeRef nodeRef = utils.checkNodeId(nodeId);
		utils.checkRestriction(nodeRef, gdibHeader);

		// compruebo que tenga permisos
		if (versionService.isAVersion(nodeRef)) {
			utils.hasPermission(utils.toNodeRef(nodeId.substring(nodeId.lastIndexOf("@") + 1)),
					CaibServicePermissions.READ);
		} else {
			utils.hasPermission(nodeRef, CaibServicePermissions.READ);
		}
		LOGGER.debug("permisos checkeados");

		//Este if lo comentamos, no hace nada...
		/*
		if (!repositoryDisableCheck.booleanValue()) {
			// me salto este paso si esta desactivado los check principales del repositorio
			if (versionService.isAVersion(nodeRef)) {
				utils.inDMPath(utils.toNodeRef(nodeId.substring(nodeId.lastIndexOf("@") + 1)));
			} else {
				// compruebo que el nodo este dentro del path del DM
				utils.inDMPath(nodeRef);
			}
		}
		*/
		
		Node ret;
		NodeRef nodeSinVersion = utils.toNodeRef(nodeId.substring(nodeId.lastIndexOf("@") + 1));
		//Comprobamos si está en el RM para pasarle la versión o el nodo workspace
		if(utils.isInRM(nodeSinVersion)) {
			LOGGER.debug("El documento está en el RM: obtenemos el objeto del workspace");
			ret = _internal_getNode(nodeSinVersion, withContent, withSign);
		} else {
			LOGGER.debug("El documento NO está en el RM: obtenemos el nodo especificado en la llamada");
			ret = _internal_getNode(nodeRef, withContent, withSign);
		}

		LOGGER.info(nodeId + " recuperado en " + (System.currentTimeMillis() - initMill) + "ms");
		return ret;
	}

	/**
	 * Servicio que mueve la localizaci�n de un nodo padre a otro diferente.
	 * 
	 * @param nodeId     Identificador de nodo a mover
	 * @param newParent  Identificador del nuevo padre
	 * @param gdibHeader Cabecera de auditor�a y configuraci�n
	 * @throws GdibException Si no se tienen permisos para realizar la operaci�n
	 *                       (permisos de escritura en ambos nodos).
	 *
	 *
	 */
	@Override
	public void moveNode(String nodeId, String newParent, GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
		NodeRef nodeRef = utils.checkNodeId(nodeId);
		utils.inDMPath(nodeRef);
		utils.checkRestriction(nodeRef, gdibHeader);

		NodeRef newParentRef = utils.checkNodeId(newParent);
		utils.inDMPath(newParentRef);

		// Se verifica que la nueva localizaci�n pertence a la misma serie documental

		String serieDocNodo = null;
		boolean hasDraftAspect = nodeService.hasAspect(nodeRef, ConstantUtils.ASPECT_BORRADOR_QNAME);
		if (!hasDraftAspect) {
			if (fileFolderService.getFileInfo(nodeRef).isFolder()) {
				serieDocNodo = utils.getCodClasificacion(nodeRef);
			} else {
				serieDocNodo = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
			}

			String parentCodCasif = utils.getCodClasificacion(newParentRef);
			if (!StringUtils.isEmpty(parentCodCasif) && !parentCodCasif.equals(serieDocNodo)) {
				throw exUtils.illegalLinkException();
			}
		}

		_internal_moveNode(nodeRef, newParentRef);

		LOGGER.info(nodeId + " movido en " + (System.currentTimeMillis() - initMill) + "ms.");
	}

	/**
	 * Servicio de bÃºsqueda de nodos en los �ndices. El nÃºmero de resultados est�
	 * condicionado por la variable gdib.repository.search.limit La funci�n recibe
	 * la p�gina de la bÃºsqueda que funciona de la siguiente manera: * La variable
	 * limite establece el numero de registros por p�ginas de la busqueda. Si la
	 * variable pagina es 0 y el limite son 50 registros, devuelve los 50 primeros
	 * registros. Si la variable pagina es 1 y el limte son 50 registros, devuelve
	 * desde el resultado 51 hasta el 100, es decir, Desde pagina*limite+1 hasta
	 * pagina*limite+limite.
	 *
	 * El numero total de paginas y nodos encontrados va en la petici�n, si por la
	 * raz�n que sea se solicita un numero mayor que lo que hay realmente no
	 * devolver� resultados, por ejemplo: * Una busqueda devuelve 1000 resultados
	 * con una configuracion de limite de 100. * El usuario pide la pagina 10 (
	 * 10*100 + 1 = 1001, devolver�a los resultados entre 1001 y 1100) la bÃºsqueda
	 * no devolver�a ningÃºn resultado.
	 *
	 * @param luceneSearch Cadena de lucene para realizar la bÃºsqueda
	 * @param pagina       Numero de pagina en la se recogen los resultados
	 * @param gdibHeader   Cabecera de auditor�a y configuraci�n
	 * @return SearchResults Lista de nodos con el resultado de la bÃºsqueda, numero
	 *         de resultados y numero de paginas
	 *
	 *
	 */
	@Override
	public SearchResults searchNode(String luceneSearch, int pagina, GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
		SearchResults res = new SearchResults();
		res = _internal_searchNode(luceneSearch, pagina);
		long endMill = System.currentTimeMillis();
		return res;
	}

	/**
	 * Servicio que elimina un nodo del repositorio.
	 *
	 * @param nodeId     identificador del nodo a eliminar
	 * @param gdibHeader cabecera de auditor�a y configuraci�n.
	 * @throws GdibException Si no se puede eliminar el nodo (Bloqueo, falta de
	 *                       permisos...).
	 *
	 */

	@Override
	public void removeNode(String nodeId, GdibHeader gdibHeader) throws GdibException {

		long initMill = System.currentTimeMillis();
		// comprobar parametros de entrada
		NodeRef ref = utils.checkNodeId(nodeId);
		utils.checkRestriction(ref, gdibHeader);

		// comprobar permisos de escritura sobre el nodo
		utils.hasPermission(ref, CaibServicePermissions.WRITE);

		// compruebo que el nodo este dentro del path del DM
		utils.inDMPath(ref);

		// compruebo si el nodo esta bloqueado, o si es un expediente, si tiene algun
		// documento dentro bloqueado
		if (utils.isSomeoneLockedDown(ref)) {
			throw exUtils.lockedNode(nodeId);
		}

		// Chequear si es un nodo definitivo => lanzar excepcion
		// Si es una carpeta que contiene documentos definitivos => lanzar excepcion
		utils.checkFinalNode(ref);

		nodeService.deleteNode(ref);
		LOGGER.info(nodeId + " borrado en " + (System.currentTimeMillis() - initMill) + "ms.");
	}

	/**
	 * Servicio que enlaza un documento o carpeta con otra carpeta. Hay dos tipos de
	 * enlaces: LINK y COPY
	 *
	 * Enlace LINK: Genera un enlace padre hijo nuevo en el nodo. El nodo
	 * conservar�a los enlaces anteriores padre-hijo. Esto significa que dos
	 * carpetas podr�an ver exactamente el mismo nodo, con su contenido y
	 * propiedades calcadas, y no ocuapar�a espacio extra en repositorio. Enlace
	 * COPY: Genera un NUEVO NODO en el repositorio hijo del nodo padre
	 * suministrado. Esto implica que los dos nodos mantendr�an diferentes metadatos
	 * y contenidos, pero al ser imposible la modificaci�n del contenido de un nodo
	 * si es definitivo, en la pr�ctica van a compartir contenido y diferenciarse en
	 * metadatos. Al copiar el nodo de alfresco tampoco duplicamos el espacio
	 * ocupado por ese contenido, Ãºnicamente se genera nuevo espacio en base de
	 * datos para mantener las propiedades.
	 *
	 * Resumen: * Enlace Link comparten metadatos y contenido * Enlace Copy
	 * comparten contenido y diferentes metadatos.
	 *
	 * @param parentId   Id del nuevo padre del nodo
	 * @param nodeId     Id del nodo que se quiere enlazar
	 * @param linkMode   Tipo de enlace
	 * @param gdibHeader cabecera de auditor�a y configuraci�n
	 * @throws GdibException Si no se tienen permisos para realizar la operaci�n.
	 *
	 */
	@Override
	public String linkNode(String parentId, String nodeId, String linkMode, GdibHeader gdibHeader)
			throws GdibException {
		long initMill = System.currentTimeMillis();
		// comprobar los parametros de entrada
		NodeRef parentRef = utils.checkNodeId(parentId);
		NodeRef nodeRef = utils.checkNodeId(nodeId);
		utils.inDMPath(parentRef);
		utils.inDMPath(nodeRef);
		utils.checkRestriction(nodeRef, gdibHeader);
		if (!LINK_REF.equals(linkMode) && !LINK_COPY.equals(linkMode)) {
			throw exUtils.checkParamsException("linkMode", linkMode);
		}
		// comprobar si el padre es una carpeta
		if (!utils.isType(nodeService.getType(parentRef), ConstantUtils.TYPE_FOLDER)) {
			throw exUtils.invalidTypeException(ContentModel.TYPE_FOLDER.getLocalName());
		}
		// comprobar si el padre esta bloqueado
		if (utils.isNodeLocked(parentRef))
			throw exUtils.lockedNode(parentId);

		// comprobar los permisos - iguales para los dos casos
		utils.hasPermission(nodeRef, CaibServicePermissions.READ);
		utils.hasPermission(parentRef, CaibServicePermissions.WRITE);
		LOGGER.debug("hasPermission passed");
		String res = _internal_linkNode(parentRef, nodeRef, linkMode);

		LOGGER.info(nodeId + " enlazado a " + parentId + " en " + (System.currentTimeMillis() - initMill) + "ms.");
		return res;
	}

	private String _internal_linkNode(NodeRef parentRef, NodeRef nodeRef, String linkMode) throws GdibException {
		String res = null;
		if (linkMode.equals(LINK_COPY)) {
			// mirar padres expedientes.
			String serieDocNodo = "";
			boolean hasDraftAspect = nodeService.hasAspect(nodeRef, ConstantUtils.ASPECT_BORRADOR_QNAME);
			if (!hasDraftAspect) {
				if (fileFolderService.getFileInfo(nodeRef).isFolder()) {
					serieDocNodo = utils.getCodClasificacion(nodeRef);
				} else {
					serieDocNodo = (String) nodeService.getProperty(nodeRef,
							ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
				}

				String parentCodCasif = utils.getCodClasificacion(parentRef);

				if (!StringUtils.isEmpty(parentCodCasif) && !parentCodCasif.equals(serieDocNodo)) {
					throw exUtils.checkMetadataValueException(ConstantUtils.PROP_COD_CLASIFICACION_QNAME.toString(),
							serieDocNodo);
				}
			}
			res = _internal_copy(parentRef, nodeRef).getId();
		} else if (linkMode.equals(LINK_REF)) {
			_internal_ref(parentRef, nodeRef);
		}
		return res;
	}

	/**
	 * Servicio que genera un �ndice electr�nico.
	 * 
	 * @param nodeId     Identificador del expediente a foliar.
	 * @param GdibHeader cabecera de auditor�a y configuraci�n.
	 * @throws DataHandler   con el contenido del �ndice.
	 * @throws GdibException si ocurre algÃºn error en la generaci�n del foliado o
	 *                       no se tienen los permisos adecuados para realizarla.
	 *
	 */
	@Override
	public DataHandler foliateNode(String nodeId, GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
		DataHandler res = null;
		LOGGER.info("Se inicia  la generaci�n del �ndice interno del expediente " + nodeId);
		NodeRef nodeRef = utils.checkNodeId(nodeId);
		QName nodeType = nodeService.getType(nodeRef);

		// tiene que ser un expediente
		if (!utils.isType(nodeType, ConstantUtils.TYPE_EXPEDIENTE_QNAME)
				&& !utils.isType(nodeType, RecordsManagementModel.TYPE_RECORD_FOLDER)) {
			throw exUtils.invalidTypeException(nodeType.toString());
		}

		utils.hasPermission(nodeRef, CaibServicePermissions.READ);

		String fileStatus = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_ESTADO_EXP_QNAME);

		// Verificamos que no se trata de un subexpediente de intercambio
		if (ConstantUtils.ESTADO_EXP_E03.equals(fileStatus)) {
			throw exUtils.isExchangeExpedientException(nodeId);
		}

		// miro si el expediente esta en estado abierto
		if (ConstantUtils.ESTADO_EXP_E01.equals(fileStatus)) {
			// El expediente se encuentra en DM, se genera el �ndice interno
			res = _internal_foliate(nodeRef, AdministrativeProcessingIndexSignerFactory.CAIB_INDEX_V10);
		} else if (ConstantUtils.ESTADO_EXP_E02.equals(fileStatus)) {
			// El expediente se encuentra en RM, se debe buscar el indice interno
			// El nombre del nodo que representa el indice de intercambio generado es:
			// ConstantUtils.INTERNAL_INDEX_NAME_PREFIX + eniId + "-" + dateString + ".xml";
			res = getInternalIndexFromRM(nodeRef);
		}

		LOGGER.info("Indice interno del expediente " + nodeId + " generado en "
				+ (System.currentTimeMillis() - initMill) + "ms.");
		return res;
	}

	/**
	 * Servicio que prepara un expediente para ser exportado del sistema. El
	 * servicio debe generar un subexpediente del mismo con la informaci�n actual
	 * del expediente y su �ndice obviando el resto de subexpedientes de
	 * exportaci�n.
	 *
	 * @param nodeId     Identificador del nodo a exportar.
	 * @param gdibHeader Cabecera de seguridad y configuraci�n.
	 * @return Identificador del subexpediente generado.
	 * @throws GdibException si ocurre algÃºn error al generar el subexpediente o no
	 *                       se tienen los permisos adecuados.
	 *
	 */
	@Override
	public DataHandler exportNode(String nodeId, GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
		DataHandler res = null;
		// comprobar parametros de entrada, verifica que el nodo exista
		NodeRef node = utils.checkNodeId(nodeId);
		QName nodeType = nodeService.getType(node);

		// tiene que ser un expediente
		if (!utils.isType(nodeType, ConstantUtils.TYPE_EXPEDIENTE_QNAME)
				&& !utils.isType(nodeType, RecordsManagementModel.TYPE_RECORD_FOLDER)) {
			throw exUtils.invalidTypeException(nodeType.toString());
		}

		// Si es un expediente de RM, se comprueba que no este expurgado
		if (utils.isType(nodeType, RecordsManagementModel.TYPE_RECORD_FOLDER))
			checkExpurgateExpedientInRM(node);
		else {
			// se comprueba escritura en expedientes de DM, no en RM.
			utils.hasPermission(node, CaibServicePermissions.WRITE);
		}

		String fileStatus = (String) nodeService.getProperty(node, ConstantUtils.PROP_ESTADO_EXP_QNAME);

		// Verificamos que no se trata de un subexpediente de intercambio
		if (ConstantUtils.ESTADO_EXP_E03.equals(fileStatus)) {
			throw exUtils.isExchangeExpedientException(nodeId);
		}

		// no esta bloqueado
		utils.isNodeLocked(node);

		// miro si el expediente esta en estado abierto
		if (ConstantUtils.ESTADO_EXP_E01.equals(fileStatus)) {
			// El expediente se encuentra en DM, se genera el expediente de intercambio
			res = _internal_export(node);
		} else if (ConstantUtils.ESTADO_EXP_E02.equals(fileStatus)) {
			// El expediente se encuentra en RM, se debe buscar el indice de intercambio
			// El nombre del nodo que representa el indice de intercambio generado es:
			// ConstantUtils.EXCHANGE_INDEX_NAME_PREFIX + eniId + "-" + dateString + ".xml";
			// Recupero los hijos y si es de t�po �ndice de intercambio lo devuelvo.
			res = getExchangeIndexFromRM(node);
		}

		LOGGER.info(nodeId + " exportado en " + (System.currentTimeMillis() - initMill) + "ms.");
		return res;
	}

	/**
	 * Servicio que recupera una lista de versiones del nodo.
	 *
	 * @param nodeId     Identificador del nodo que se recuperan sus versiones
	 * @param gdibHeader Cabecera de auditor�a y configuraci�n.
	 * @return List<NodeVersion> Lista de informaci�n de versiones.
	 * @throws GdibException si no se tienen permisos para consultar la lista de
	 *                       versiones o el identificador es incorrecto.
	 *
	 */
	@Override
	public List<NodeVersion> getNodeVersionList(String nodeId, GdibHeader gdibHeader) throws GdibException {

		long initMill = System.currentTimeMillis();
		// comprobar los parametros de entrada
		NodeRef node = utils.checkNodeId(nodeId);
		//Si es version revisamos permisos sobre el nodo del workspace
		if(versionService.isAVersion(node) && !utils.isInRM(node)) {
			utils.hasPermission(utils.toNodeRef(nodeId.substring(nodeId.lastIndexOf("@") + 1)),
					CaibServicePermissions.READ);
		} else {
			utils.hasPermission(node, CaibServicePermissions.READ);
		}
		
		List<NodeVersion> list;
		//Si es una versión habrá que buscar las versiones sobre el nodo del workspace
		if(versionService.isAVersion(node) && !utils.isInRM(node)) {
			list = utils.getVersionList(utils.toNodeRef(nodeId.substring(nodeId.lastIndexOf("@") + 1)));
		} else {
			list = utils.getVersionList(node);
		}
		
		LOGGER.info("Lista de versiones de " + nodeId + " recuperada en " + (System.currentTimeMillis() - initMill) + "ms.");
		return list;
	}

	/**
	 * Servicio que asigna un permiso a una lista de nodos para unas autoridades
	 * dadas.
	 *
	 * @param nodeIds     Lista de identificadores de nodo
	 * @param authorities Lista de autoridades
	 * @param permission  Permiso a otorgar.
	 * @param gdibHeader  Cabecera de seguridad y configuraci�n.
	 *
	 */
	@Override
	public void authorizeNode(List<String> nodeIds, List<String> authorities, String permission, GdibHeader gdibHeader)
			throws GdibException {

		long initMill = System.currentTimeMillis();
		permission = permission.toLowerCase();
		List<NodeRef> nodeRefs = utils.checkNodeIds(nodeIds);
		utils.checkRestriction(nodeRefs, gdibHeader);

		if (ConstantUtils.PERMISSIONS.contains(permission.toLowerCase())) {
			_internal_authorizeNode(nodeRefs, authorities, permission);
		} else {
			throw exUtils.invalidPermission(permission, StringUtils.join(nodeIds, ","));
		}
		if (nodeIds.size() > 1) {
			LOGGER.info("Permisos [" + nodeIds.get(0) + ".." + nodeIds.get(nodeIds.size() - 1) + "] cambiados en "
					+ (System.currentTimeMillis() - initMill) + "ms.");
		} else if (nodeIds.size() == 1) {
			LOGGER.info("Permisos de " + nodeIds.get(0) + " cambiados en " + (System.currentTimeMillis() - initMill)
					+ "ms.");
		} else {
			LOGGER.info("Sin permisos que cambiar en " + (System.currentTimeMillis() - initMill) + "ms.");
		}

	}

	/**
	 * Servicio que elimina todos los permisos que tuvieran las autoridades en una
	 * lista de nodos.
	 *
	 * QuÃ© pasa si invoco esto con permisos inexistentes o autoridades
	 * inexistentes? - Si la autoridad no tiene permisos asignados => Nada - Si la
	 * autoridad no existe => Excepci�n. - Si el nodo esta bloqueado => Excepci�n.
	 * 
	 * @param nodeIds     Lista de identificadores de nodo
	 * @param authorities Lista de autoridades
	 * @param gdibHeader  Cabecera de seguridad y configuraci�n.
	 *
	 */
	@Override
	public void removeAuthority(List<String> nodeIds, List<String> authorities, GdibHeader gdibHeader)
			throws GdibException {

		long initMill = System.currentTimeMillis();
		List<NodeRef> nodeRefs = utils.checkNodeIds(nodeIds);
		utils.checkRestriction(nodeRefs, gdibHeader);
		for (NodeRef node : nodeRefs) {
			// compruebo si tengo permisos sobre el nodo
			utils.hasPermission(node, CaibServicePermissions.WRITE);
			utils.inDMPath(node);
			// compruebo si el nodo esta bloqueado
			if (utils.isSomeoneLockedDown(node))
				throw exUtils.lockedNode(node.getId());

			for (String authority : authorities) {
				this.utils.checkAuthorityExists(authority);
				for (String permission : CaibServicePermissions.WRITE.getPermissions()) {
					permissionService.setPermission(node, authority, permission, false);
				}
			}
		}
		if (nodeIds.size() > 1) {
			LOGGER.info("Permisos [" + nodeIds.get(0) + ".." + nodeIds.get(nodeIds.size() - 1) + "] eliminados en "
					+ (System.currentTimeMillis() - initMill) + "ms.");
		} else if (nodeIds.size() == 1) {
			LOGGER.info("Permisos de " + nodeIds.get(0) + " eliminados en " + (System.currentTimeMillis() - initMill)
					+ "ms.");
		} else {
			LOGGER.info("Sin permisos que eliminar en " + (System.currentTimeMillis() - initMill) + "ms.");
		}
	}

	/***
	 * Servicio que bloquea la escritura de un nodo.
	 *
	 * @param nodeId     Identificador de nodo a bloquear
	 * @param gdibHeader Cabecera de auditor�a y configuraci�n
	 *
	 */
	@Override
	public void lockNode(String nodeId, GdibHeader gdibHeader) throws GdibException {

		long initMill = System.currentTimeMillis();
		// comprobar los parametros de entrada
		NodeRef nodeRef = utils.checkNodeId(nodeId);
		utils.checkRestriction(nodeRef, gdibHeader);
		utils.inDMPath(nodeRef);

		// si tiene permisos de escritura para bloquear el nodo
		utils.hasPermission(nodeRef, CaibServicePermissions.WRITE);

		Queue<NodeRef> pilaNodos = new ArrayDeque<NodeRef>();
		pilaNodos.add(nodeRef);

		while (!pilaNodos.isEmpty()) {
			NodeRef nodo = pilaNodos.poll();
			_internal_lockNode(nodo);
			if (fileFolderService.getFileInfo(nodo).isFolder()) {
				List<ChildAssociationRef> hijos = nodeService.getChildAssocs(nodo);
				for (ChildAssociationRef hijo : hijos) {
					// solo aÃ±ado los hijos que tengan como primaryparent el mismo nodo
					if (nodeService.getPrimaryParent(hijo.getChildRef()).getParentRef().equals(nodo)) {
						pilaNodos.add(hijo.getChildRef());
					}
				}
			}
		}
		LOGGER.info("Nodo " + nodeId + " bloqueado en " + (System.currentTimeMillis() - initMill) + "ms.");

	}

	/**
	 * Servicio que desbloquea un nodo.
	 *
	 * @param nodeId     Identificador del nodo a desbloquear
	 * @param gdibHeader Cabecera de auditor�a y configuraci�n.
	 *
	 */

	@Override
	public void unlockNode(String nodeId, GdibHeader gdibHeader) throws GdibException {

		long initMill = System.currentTimeMillis();
		// comprobar los parametros de entrada
		NodeRef nodeRef = utils.checkNodeId(nodeId);
		utils.checkRestriction(nodeRef, gdibHeader);
		utils.hasPermission(nodeRef, CaibServicePermissions.WRITE);
		utils.inDMPath(nodeRef);

		Queue<NodeRef> pilaNodos = new ArrayDeque<NodeRef>();
		pilaNodos.add(nodeRef);

		while (!pilaNodos.isEmpty()) {
			NodeRef nodo = pilaNodos.poll();
			_internal_unlockNode(nodo);
			if (fileFolderService.getFileInfo(nodo).isFolder()) {
				List<ChildAssociationRef> hijos = nodeService.getChildAssocs(nodo);
				for (ChildAssociationRef hijo : hijos) {
					// solo aÃ±ado los hijos que tengan como primaryparent el mismo nodo
					if (nodeService.getPrimaryParent(hijo.getChildRef()).getParentRef().equals(nodo)) {
						pilaNodos.add(hijo.getChildRef());
					}
				}
			}
		}
		LOGGER.info("Nodo " + nodeId + " desbloqueado en " + (System.currentTimeMillis() - initMill) + "ms.");
	}

	/**
	 * Recupera un ticket de alfresco.
	 *
	 * @param gdibHeader Cabecera de auditor�a y configuraci�n
	 *
	 */
	@Override
	public String getTicket(GdibHeader gdibHeader) throws GdibException {
		long initMill = System.currentTimeMillis();
		String ticket = this.authenticationService.getCurrentTicket();
		LOGGER.info("Ticket " + ticket + " recuperado en " + (System.currentTimeMillis() - initMill) + "ms.");
		return ticket;
	}

	/**
	 * Servicio que recupera la informaci�n de migraci�n de un nodo migrado y
	 * transformado.
	 *
	 * @param nodeId     Identificador del nodo
	 * @param gdibHeader Cabecera de auditor�a y configuraci�n.
	 * @return MigrationInfo con la firma Valcert y el ZIP con el contenido.
	 *
	 */
	@Override
	public MigrationInfo getMigrationInfo(String nodeId, GdibHeader gdibHeader) throws GdibException {

		long initMill = System.currentTimeMillis();
		MigrationInfo ret = new MigrationInfo();
		NodeRef nodeRef = utils.checkNodeId(nodeId);
		utils.checkRestriction(nodeRef, gdibHeader);

		ret.setValcertSign(utils.getContent(nodeRef, ConstantUtils.PROP_FIRMAVALCERT_QNAME).getData());
		ret.setZipContent(utils.getContent(nodeRef, ConstantUtils.PROP_ZIPMIGRACION_QNAME).getData());
		LOGGER.info(
				"Info de migracion de " + nodeId + " recuperada en " + (System.currentTimeMillis() - initMill) + "ms.");
		return ret;
	}

	/**
	 * Servicio que genera un CSV Aleatorio
	 *
	 * @param gdibHeader cabecera de auditor�a y configuraci�n.
	 *
	 */
	@Override
	public String getCSV(GdibHeader gdibHeader) throws GdibException {

		try {
			// createTransaction(gdibHeader);
			long initMill = System.currentTimeMillis();
			UUID idOne = UUID.randomUUID();
			System.out.println("CSV: " + idOne.toString());
			MessageDigest md;
			byte[] digest = null;
			try {
				md = MessageDigest.getInstance("SHA-256");
				md.update(idOne.toString().getBytes(DEFAULT_CHARSET_ENCODING)); // Change this to "UTF-16" if needed
				digest = md.digest();
				String ret = String.format("%064x", new java.math.BigInteger(1, digest));
				LOGGER.info("CSV " + ret + " recuperado en " + (System.currentTimeMillis() - initMill) + "ms.");
				// commit();
				return ret;

			} catch (NoSuchAlgorithmException e) {
				throw exUtils.csvError(e);
			} catch (UnsupportedEncodingException e) {
				throw exUtils.csvError(e);
				/*
				 * } catch (GdibTransactionException e) { throw new GdibException(e);
				 */
			}
		} catch (GdibException e) {
			// rollback();
			throw e;
		}
	}

	/**
	 * Apertura de un expediente tras haberse cerrado. El sistema genera un nuevo
	 * expediente con la informaci�n del anterior.
	 *
	 * @param nodeId     Identificador de Nodo de expediente.
	 * @param gdibHeader Cabecera de auditor�a y configuraci�n.
	 * @return Identificador del Expediente generado.
	 *
	 */
	@Override
	public String openFile(String nodeId, GdibHeader gdibHeader) throws GdibException {

		NodeRef expedientRef = utils.checkNodeId(nodeId);

		// compruebo permisos de lectura sobre el expediente a abrir
		utils.hasPermission(expedientRef, CaibServicePermissions.READ);

		// incluir check para mirar si el expediente esta expurgado
		// comprobar el estado de archivo distinto de expurgado parcialmente
		// y si tiene el fichero descriptor XML
		checkExpurgateExpedientInRM(expedientRef);

		// realizo la apertura de expediente
		NodeRef newExpedientRef = importUtils.importExpedient(expedientRef);

		// Se procede a modificar las propiedades de archivo de los nodos hijos
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		// Se modifica el estado de tramitaci�n del expediente, asign�ndole el valor
		// â€œCerradoâ€�.
		properties.put(ConstantUtils.PROP_ESTADO_EXP_QNAME, ConstantUtils.ESTADO_EXP_E01);
		// Se modifica el estado de archivo del expediente, asign�ndole el valor
		// â€œpreingresoâ€�.
		properties.put(ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME, null);
		// Se modifica la fase de archivo del expediente, asign�ndole el valor "Archivo
		// historico".
		properties.put(ConstantUtils.PROP_FASE_ARCHIVO_QNAME, ConstantUtils.FASE_ARCHIVO_ACTIVO);
		// Se modifica la fecha fin de expediente, asignadole la fecha actual
		properties.put(ConstantUtils.PROP_FECHA_FIN_EXP_QNAME, null);
		LOGGER.info(
				"Se procede a establecer propiedades de archivado (interoperables) a los documentos del expediente. Propiedades: "
						+ properties);
		setFileContentArchivedMetadataCollection(newExpedientRef, properties, true);

		// restauro los expedientes enlazados de todo el expediente
		// LOGGER.debug("beforeRestoreLInkedExpedient");
		this.restoreLinkedExpedient(newExpedientRef);

		// enlazo el expediente con el expediente en el RM
		// _internal_ref(expedientRef, newExpedientRef);

		// remove el nodo del indice electronico, recorro los hijos y busco el nodo cuyo
		// nombre contiene la constante
		// ConstantUtils.INTERNAL_INDEX_NAME_PREFIX = indice-
		String eniId = (String) nodeService.getProperty(newExpedientRef, ConstantUtils.PROP_ID_QNAME);
		for (ChildAssociationRef childRef : nodeService.getChildAssocs(newExpedientRef)) {
			NodeRef child = childRef.getChildRef();
			String name = (String) nodeService.getProperty(child, ConstantUtils.PROP_NAME);
			if (name.contains(ConstantUtils.INTERNAL_INDEX_NAME_PREFIX)) {
				nodeService.deleteNode(child);
				continue;
			}
		}

		// LOGGER.debug("retunrning newExpedient ref" + newExpedientRef.getId());
		// devuelvo el nuevo id del expediente en el DM
		return newExpedientRef.getId();
	}

	/**
	 * Compruebo si el nodo a sido expurgado, es decir, el estado es "Eliminado"
	 * 
	 * @param node
	 * @throws GdibException
	 */
	private void checkExpurgateExpedientInRM(NodeRef node) throws GdibException {
		String estadoArchivo = (String) nodeService.getProperty(node, ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME);

		if (ConstantUtils.ESTADO_ARCHIVO_ELIMINADO.equals(estadoArchivo)) {
			throw exUtils.reopenExpurgateExpedientError(node.getId());
		}

	}

	/**
	 * Restaura los enlazes a expedientes relacionados, sus uuid estan guardados en
	 * la propiedad "gdib:exp_enlazado" si hay subexpedientes dentro del expediente,
	 * tengo que restaurar sus expedientes enlazados tambien
	 *
	 * @param expedientRef
	 * @throws GdibException
	 */
	@SuppressWarnings("unchecked")
	private void restoreLinkedExpedient(NodeRef expedientRef) throws GdibException {

		List<String> uuids = (List<String>) nodeService.getProperty(expedientRef,
				ConstantUtils.PROP_EXP_ENLAZADO_QNAME);
		// hago los enlazes del expediente si los tiene
		if (!CollectionUtils.isEmpty(uuids)) {
			for (String uuid : uuids) {
				this._internal_ref(expedientRef, utils.toNodeRef(uuid));
			}
		}

		List<ChildAssociationRef> childs = nodeService.getChildAssocs(expedientRef);
		for (ChildAssociationRef child : childs) {
			NodeRef son = child.getChildRef();
			// si encuentro una carpeta, recursivamente recorro los hijos
			if (utils.isType(nodeService.getType(son), ConstantUtils.TYPE_FOLDER)
					&& nodeService.getPrimaryParent(son).getParentRef().equals(expedientRef)) {
				restoreLinkedExpedient(son);
			}
		}
	}

	/**
	 * Verificar subtipo documental
	 */
	public void verifySubtypeDoc(Node node) throws GdibException {

		String subtipoDoc = utils.getProperty(node.getProperties(), EniModelUtilsInterface.PROP_SUBTIPO_DOC_QNAME);
		if (subtipoDoc != null && !"".equals(subtipoDoc)) {
			List<SubTypeDocInfo> subtipos = subTypeDocUtil.getAllSubtypedoc();
			boolean encontrado = false;
			Iterator<SubTypeDocInfo> it = subtipos.iterator();
			while (it.hasNext() && !encontrado) {
				SubTypeDocInfo info = it.next();
				if (info.getSubtypeDoc().equals(subtipoDoc))
					encontrado = true;
			}
			if (!encontrado)
				exUtils.checkMetadataException(EniModelUtilsInterface.PROP_SUBTIPO_DOC);
		}
	}

	/**
	 * Cierra un expediente que se encuentre en el DM para enviarlo al RM.
	 *
	 * @param nodeId     Identificador del expediente a cerrar.
	 * @param gdibHeader Cabecera de configuraci�n y auditor�a.
	 *
	 */
	@Override
	public void closeFile(String nodeId, GdibHeader gdibHeader) throws GdibException {

		LOGGER.info("Se solicita el cierre del expediente " + nodeId);
		LOGGER.debug("Obteniendo la referencia al nodo en el SGD...");
		// compruebo el nodeId
		NodeRef expedientRef = utils.checkNodeId(nodeId);

		LOGGER.debug("Obteniendo la la clasificaci�n documental del expediente " + nodeId);
		// compruebo que la seriedocumental este valorada para poder cerrar el
		// expediente
		String codClasificacion = (String) nodeService.getProperty(expedientRef,
				ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
		String subTypeDoc = (String) nodeService.getProperty(expedientRef, ConstantUtils.PROP_SUBTIPO_DOC_QNAME);

		SubTypeDocInfo subTypeDocInfo = subTypeDocUtil.getSubTypeDocInfo(codClasificacion, subTypeDoc);

		if (subTypeDocInfo == null) {
			if (codClasificacion != null) {
				throw exUtils.documentarySeriesNoDocumentedException(codClasificacion);
			} else {
				throw exUtils.documentarySeriesNoDocumentedException("");
			}
		}
		LOGGER.debug("Clasificaci�n documental del expediente " + nodeId + ": " + subTypeDocInfo.getDocumentarySeries()
				+ "/" + subTypeDocInfo.getSubtypeDoc());
		LOGGER.debug("Comprobando permisos sobre el expediente y su contenido...");

		// compruebo permisos de escritura sobre el nodo
		utils.hasPermission(expedientRef, CaibServicePermissions.WRITE);
		LOGGER.debug("Comprobando bloqueos sobre el expediente y su contenido...");
		// compruebo que no este ningun nodo bloqueado dentro del expediente
		utils.isSomeoneLockedDown(expedientRef);

		try {
			LOGGER.info("Se procede a realizar el cierre del expediente " + nodeId);
			// realizo el cierre del expediente. Pasandolo del DM al RM
			Date closeDate = new Date();
			__internal_closeFile(expedientRef, closeDate);

			LOGGER.debug("Se procede a establecer metadatos del RM sobre el expediente cerrado, " + nodeId
					+ ", y su contenido.");
			// Declaramos los documentos del expediente como "documento de archivo completo"
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();

			props.put(RecordsManagementModel.PROP_DECLARED_AT, closeDate);
			props.put(RecordsManagementModel.PROP_DECLARED_BY, AuthenticationUtil.getFullyAuthenticatedUser());
			for (ChildAssociationRef child : nodeService.getChildAssocs(expedientRef)) {
				NodeRef childRef = child.getChildRef();
				nodeService.addProperties(childRef, props);
			}
		} catch (ContentIOException | IOException e) {
			throw new GdibException(e.getMessage());
		}

		LOGGER.info("Se finaliza el cierre del expediente " + nodeId);
	}

	private void __internal_closeFile(final NodeRef expedientRef, final Date closeDate)
			throws GdibException, ContentIOException, IOException {
		RunAsWork<?> raw = new RunAsWork<Object>() {
			public Object doWork() throws Exception {
				// Set permission to this folder for the logged in user
				_internal_closeFile(expedientRef, closeDate);
				return null;
			}
		};
		// Run as admin
		AuthenticationUtil.runAs(raw, "admin");
	}
	
	@Value("$gdib{verify.request.enable}")
    private String enableVerify;

	private NodeRef _internal_closeFile(NodeRef expedientRef, Date closeDate)
			throws GdibException, ContentIOException, IOException {
		Map<QName, Serializable> indexsProps;

		LOGGER.info("Se procede a eliminar documentos en estado borrador.");
		// se procede a eliminar aquellos documentos que no se encuentren en un estado
		// no definitivo o custodiado (borrador).
		this.deleteDraftDocuments(expedientRef);

		LOGGER.info("Se procede a procesar expedientes enlazados.");
		// salvo en el expediente y los subexpedientes la informacion referida a los
		// enlazados
		this.safeLinkedExpedient(expedientRef);

		// Se procede a modificar las propiedades de archivo de los nodos hijos
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		// Se modifica el estado de tramitaci�n del expediente, asign�ndole el valor
		// â€œCerradoâ€�.
		properties.put(ConstantUtils.PROP_ESTADO_EXP_QNAME, ConstantUtils.ESTADO_EXP_E02);
		// Se modifica el estado de archivo del expediente, asign�ndole el valor
		// â€œpreingresoâ€�.

		if (preingreso) {
			properties.put(ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME, ConstantUtils.ESTADO_ARCHIVO_PREINGRESO);
		} else {
			properties.put(ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME, ConstantUtils.ESTADO_ARCHIVO_INGRESADO);
		}
		// Se modifica la fase de archivo del expediente, asign�ndole el valor "Archivo
		// historico".
		properties.put(ConstantUtils.PROP_FASE_ARCHIVO_QNAME, ConstantUtils.FASE_ARCHIVO_HISTORICO);
		// Se modifica la fecha fin de expediente, asignadole la fecha actual
		properties.put(ConstantUtils.PROP_FECHA_FIN_EXP_QNAME, closeDate);
		LOGGER.info("Se procede a establcer propiedades de archivado (interoperables) al expediente. Propiedades: "
				+ properties);

		setFileContentArchivedMetadataCollection(expedientRef, properties, true);
		LOGGER.info("Se procede a generar los �ndices del expediente, interno y de intercambio.");
		// Se crean los indices interno y de intercambio del expediente
		String eniId = (String) nodeService.getProperty(expedientRef, ConstantUtils.PROP_ID_QNAME);
		String dateString = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
		String internalIndexNodeName = ConstantUtils.INTERNAL_INDEX_NAME_PREFIX + eniId + "-" + dateString + ".xml";
		indexsProps = new HashMap<QName, Serializable>(1);
		indexsProps.put(ContentModel.PROP_NAME, internalIndexNodeName);
		indexsProps.put(ConstantUtils.PROP_INDEX_TYPE_QNAME, ConstantUtils.INTERNAL_V10_INDEX_TYPE);
		DataHandler internalIndexHandler = _internal_foliate(expedientRef,
				AdministrativeProcessingIndexSignerFactory.CAIB_INDEX_V10);
		
		NodeRef rmExpedient;
//		synchronized(closeMutex) {
		
		NodeRef internalIndexNodeRef = _internal_createNode(expedientRef, utils.createNameQName(internalIndexNodeName),
				ConstantUtils.TYPE_FILE_INDEX_QNAME, indexsProps);
		utils.setDataHandler(internalIndexNodeRef, ContentModel.PROP_CONTENT, internalIndexHandler,
				MimetypeMap.MIMETYPE_XML, DEFAULT_CHARSET_ENCODING);
		
		LOGGER.info("Iniciant proces de validacio index intern: " + enableVerify);
		if("true".equalsIgnoreCase(enableVerify)) {
			try {
				byte[] indexBArrayInternal = utils
						.getByteArrayFromHandler(utils.getDataHandler(internalIndexNodeRef, ContentModel.PROP_CONTENT));
				Document dIndexInternal = XmlUtils.byteArrayToXmlDocument(indexBArrayInternal);
				dIndexInternal.getDocumentElement().normalize();
	
				LOGGER.debug("Before parsing index");
	
				String cert = utils.makeHttpValidSignatureRequest(indexBArrayInternal);
	
				Certificate certObj = utils.parseX509Cert(cert);
				Certificate existingCert = certUtils.searchCertBySerialNumber(certObj.getSerialNumber());
				boolean certExists = existingCert != null;
				certUtils.updateCertificatesInfo(certExists ? existingCert : certObj,
						certExists ? existingCert.getNumIndices() + 1 : 0);
	
				// utils.parseX509Index(dIndexEchange);
				LOGGER.debug("After parsing index");
				// Make HTTP petition
				// String serialCertIdentr= utils.parseTimeStampASN1(dIndexEchange);
				// Date certValidity = utils.parseTimeStampASN1CertCad(dIndexEchange);
	
				nodeService.setProperty(internalIndexNodeRef, ConstantUtils.PROP_INDEX_CERT_QNAME,
						certObj.getSerialNumber());
				nodeService.setProperty(internalIndexNodeRef, ConstantUtils.PROP_INDEX_VALID_QNAME, "SI");
				nodeService.setProperty(internalIndexNodeRef, ConstantUtils.PROP_INDEX_CERT_DATE_QNAME,
						ISO8601DateFormat.format(certObj.getNotAfter()));
	
			} catch (Exception e) {
				LOGGER.debug("Couldnt read TS token " + e.getMessage());
				LOGGER.debug("Couldnt read TS token " + e.getLocalizedMessage());
			}
		}

			LOGGER.info("Indice interno del expediente generado (" + internalIndexNodeRef.getId() + ").");
	
			String exchangeIndexNodeName = ConstantUtils.EXCHANGE_INDEX_NAME_PREFIX + eniId + "-" + dateString + ".xml";
			indexsProps = new HashMap<QName, Serializable>(1);
			indexsProps.put(ContentModel.PROP_NAME, exchangeIndexNodeName);
			indexsProps.put(ConstantUtils.PROP_INDEX_TYPE_QNAME, ConstantUtils.EXCHANGE_ENI_V10_INDEX_TYPE);
			DataHandler exchangeIndexHandler = _internal_foliate(expedientRef,
					AdministrativeProcessingIndexSignerFactory.ENI_INDEX_V10);
			
			
			NodeRef exchangeIndexNodeRef = this._internal_createNode(expedientRef,
					utils.createNameQName(exchangeIndexNodeName), ConstantUtils.TYPE_FILE_INDEX_QNAME, indexsProps);
			utils.setDataHandler(exchangeIndexNodeRef, ContentModel.PROP_CONTENT, exchangeIndexHandler,
					MimetypeMap.MIMETYPE_XML, DEFAULT_CHARSET_ENCODING);
			LOGGER.info("Indice de intercambio del expediente generado (" + exchangeIndexNodeRef.getId() + ").");
	
		LOGGER.info("Iniciant proces de validacio index interoperable: " + enableVerify);
		if("true".equalsIgnoreCase(enableVerify)) {
			try {
				byte[] indexBArrayExchange = utils
						.getByteArrayFromHandler(utils.getDataHandler(exchangeIndexNodeRef, ContentModel.PROP_CONTENT));
				Document dIndexEchange = XmlUtils.byteArrayToXmlDocument(indexBArrayExchange);
				dIndexEchange.getDocumentElement().normalize();
				LOGGER.debug("Before parsing index");
	
				String cert = utils.makeHttpValidSignatureRequest(indexBArrayExchange);
	
				Certificate certObj = utils.parseX509Cert(cert);
				// utils.parseX509Index(dIndexEchange);
				LOGGER.debug("After parsing index");
				// Make HTTP petition
				// String serialCertIdentr= utils.parseTimeStampASN1(dIndexEchange);
				// Date certValidity = utils.parseTimeStampASN1CertCad(dIndexEchange);
	
				Certificate existingCert = certUtils.searchCertBySerialNumber(certObj.getSerialNumber());
				boolean certExists = existingCert != null;
				certUtils.updateCertificatesInfo(certExists ? existingCert : certObj,
						certExists ? existingCert.getNumIndices() + 1 : 0);
	
				nodeService.setProperty(exchangeIndexNodeRef, ConstantUtils.PROP_INDEX_CERT_QNAME,
						certObj.getSerialNumber());
				nodeService.setProperty(exchangeIndexNodeRef, ConstantUtils.PROP_INDEX_VALID_QNAME, "SI");
				nodeService.setProperty(exchangeIndexNodeRef, ConstantUtils.PROP_INDEX_CERT_DATE_QNAME,
						ISO8601DateFormat.format(certObj.getNotAfter()));
	
			} catch (Exception e) {
				LOGGER.debug("Couldnt read TS token " + e.getMessage());
				LOGGER.debug("Couldnt read TS token " + e.getLocalizedMessage());
			}

			List<Certificate> certs = certUtils.getCertificatesInfo();
			for (Certificate c : certs)
				LOGGER.debug("FROM DB " + c.toString());
		}
	
			LOGGER.info("Se procede a realizar la transferencia a RM del expediente....");
			// Se efect�a la transferencia a la fase semi- activa del expediente.
			rmExpedient = exportUtils.exportExpediente(expedientRef);		
			LOGGER.info("Transferencia a RM del expedeinte " + expedientRef.getId() + " realizada (" + rmExpedient.getId() + ").");
//		}
		return rmExpedient;
	}

	/**
	 * Establece las propiedades de archivado a un expediente y su contenido cuando
	 * este es cerrado.
	 * 
	 * @param expedientRef Nodo que representa el expediente
	 * @param properties   Propiedades de archivado
	 */
	private void setFileContentArchivedMetadataCollection(NodeRef nodeRef, Map<QName, Serializable> properties,
			boolean isFileRoot) throws GdibException {
		List<ChildAssociationRef> childNodes;
		try {
			LOGGER.debug("Procesando nodo ({" + nodeRef.getId() + "})");
			if (utils.isType(nodeService.getType(nodeRef), ConstantUtils.TYPE_EXPEDIENTE_QNAME)) {
				LOGGER.debug("Nodo tipo expediente");
				nodeService.addProperties(nodeRef, properties);
				childNodes = nodeService.getChildAssocs(nodeRef);
				if (!CollectionUtils.isEmpty(childNodes)) {
					for (Iterator<ChildAssociationRef> it = childNodes.iterator(); it.hasNext();) {
						ChildAssociationRef childAssociationRef = it.next();
						NodeRef childNodeRef = childAssociationRef.getChildRef();
						// ADD LFP
						if (nodeRef.equals(nodeService.getPrimaryParent(childNodeRef).getParentRef()))
							setFileContentArchivedMetadataCollection(childNodeRef, properties, false);
					}
				}
			} else if (utils.isType(nodeService.getType(nodeRef), ConstantUtils.TYPE_AGREGACION_DOC_QNAME)) {
				LOGGER.debug("Nodo tipo agregacionDoc");
				childNodes = nodeService.getChildAssocs(nodeRef);
				if (!CollectionUtils.isEmpty(childNodes)) {
					for (Iterator<ChildAssociationRef> it = childNodes.iterator(); it.hasNext();) {
						ChildAssociationRef childAssociationRef = it.next();
						NodeRef childNodeRef = childAssociationRef.getChildRef();

						setFileContentArchivedMetadataCollection(childNodeRef, properties, false);
					}
				}
			} else if (utils.isType(nodeService.getType(nodeRef), ConstantUtils.TYPE_DOCUMENTO_QNAME)) {
				LOGGER.debug("Nodo tipo documento ENI");
				Map<QName, Serializable> docProp = new HashMap<QName, Serializable>();

				// Se modifica la fase de archivo del documento
				docProp.put(ConstantUtils.PROP_FASE_ARCHIVO_QNAME,
						properties.get(ConstantUtils.PROP_FASE_ARCHIVO_QNAME));
				// Se modifica la fecha fin de expediente para el documento
				docProp.put(ConstantUtils.PROP_FECHA_FIN_EXP_QNAME,
						properties.get(ConstantUtils.PROP_FECHA_FIN_EXP_QNAME));
				nodeService.addProperties(nodeRef, docProp);
			} else if (utils.isType(nodeService.getType(nodeRef), ConstantUtils.TYPE_FOLDER)) {
				LOGGER.debug("Nodo tipo carpeta");
				// Carpeta con expedeitnes de intercambio
				// son modificados, si se ha configurado para qe sean transferidos al RM
				String nodeName = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_NAME);
				if (ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME.equals(nodeName) && isFileRoot) {
					LOGGER.debug("Nodo tipo carpeta de exportacion");
					if (addIntExchangeFiles) {
						childNodes = nodeService.getChildAssocs(nodeRef);
						if (!CollectionUtils.isEmpty(childNodes)) {
							Map<QName, Serializable> exchangeFilesproperties = new HashMap<QName, Serializable>(
									properties);
							exchangeFilesproperties.remove(ConstantUtils.PROP_ESTADO_EXP_QNAME);
							// TODO (PAOT-16/12): Tengo dudas de si tambiÃ©n elimar el metadato fin
							// expediente, pero es requerido para el proceso de expurgo
							for (Iterator<ChildAssociationRef> it = childNodes.iterator(); it.hasNext();) {
								ChildAssociationRef childAssociationRef = it.next();
								NodeRef childNodeRef = childAssociationRef.getChildRef();

								setFileContentArchivedMetadataCollection(childNodeRef, exchangeFilesproperties, false);
							}
						}
					}
				}
			}
		} catch (InvalidNodeRefException e) {
			throw new GdibException("Se produjo un error al establecer los metadatos de archivado del nodo "
					+ nodeRef.getId() + ". Error: " + e.getMessage(), e);
		} catch (Exception e) {
			throw new GdibException("Se produjo un error al establecer los metadatos de archivado del nodo "
					+ nodeRef.getId() + ". Error: " + e.getMessage(), e);
		}
	}

	/**
	 * Voy recorriendo todos los nodos del expediente, y cuando encuentro un nodo
	 * que es expediente enlazado, guardo en la propiedad "gdib:exp_enlazado" el
	 * uuid de dicho expediente, para luego poder restaurar el enlaze de dicho
	 * expediente
	 *
	 * @param expedientRef
	 * @throws InvalidNodeRefException
	 * @throws GdibException
	 */
	private void safeLinkedExpedient(NodeRef expedientRef) throws InvalidNodeRefException, GdibException {
		List<String> uuidEnlazado = new ArrayList<String>();
		Queue<ChildAssociationRef> pilaNodos = new ArrayDeque<ChildAssociationRef>();
		pilaNodos.addAll(nodeService.getChildAssocs(expedientRef));

		while (!pilaNodos.isEmpty()) {
			ChildAssociationRef nodeAssoc = pilaNodos.poll();
			NodeRef node = nodeAssoc.getChildRef();
			// compruebo si es un expediente o agregacion, si es un documento no se hace
			// nada y se pasa al siguiente nodo
			if (utils.isType(nodeService.getType(node), ConstantUtils.TYPE_EXPEDIENTE_QNAME)) {
				if (!nodeService.getPrimaryParent(node).getParentRef().equals(expedientRef)) {
					// es un expediente enlazado porque su primary parent no es el expediente que
					// estoy tratando
					uuidEnlazado.add(node.getId());
				} else {
					// es un subexpediente, por lo que tengo que hacer la misma operacion, por eso
					// la
					// llamada recursiva
					safeLinkedExpedient(node);
				}
			} else if (utils.isType(nodeService.getType(node), ConstantUtils.TYPE_AGREGACION_DOC_QNAME)) {
				// si el nodo es una carpeta de agregacion, obtengo todos los hijos de dicha
				// carpeta y los apilo
				pilaNodos.addAll(nodeService.getChildAssocs(node));
			}
		}
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		if (uuidEnlazado.size() > 0) {
			properties.put(ConstantUtils.PROP_EXP_ENLAZADO_QNAME, (Serializable) uuidEnlazado);
			nodeService.addProperties(expedientRef, properties);
		}
	}

	private void deleteDraftDocuments(NodeRef expedientRef) throws GdibException {
		Queue<NodeRef> pilaNodos = new ArrayDeque<NodeRef>();
		pilaNodos.add(expedientRef);

		while (!pilaNodos.isEmpty()) {
			NodeRef nodo = pilaNodos.poll();

			if (fileFolderService.getFileInfo(nodo).isFolder()) {
				List<ChildAssociationRef> hijos = nodeService.getChildAssocs(nodo);
				for (ChildAssociationRef hijo : hijos) {
					// solo aÃ±ado los hijos que tengan como primaryparent el mismo nodo
					if (nodeService.getPrimaryParent(hijo.getChildRef()).getParentRef().equals(nodo)) {
						pilaNodos.add(hijo.getChildRef());
					}
				}
			}

			if (!utils.isFinalNode(nodo)) {
				nodeService.deleteNode(nodo);
			}
		}
	}

	public SearchResults _internal_searchNode(String luceneQuery, int pagina) throws GdibException {
		long initMill = System.currentTimeMillis();
		SearchResults res = new SearchResults();
		SearchParameters searchParameters = new SearchParameters();
		searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		searchParameters.setQuery(luceneQuery);
		ResultSet nodes;
		try {
			nodes = searchService.query(searchParameters);
		} catch (org.alfresco.repo.search.impl.lucene.LuceneQueryParserException e) {
			LOGGER.error("Se ha producido un error de sintaxis en la construcción de la consulta lucene [" + luceneQuery
					+ "]. Error: \n", e);
//			Se devuelve -1 a modo de codigo, en el bus se cambiara por el mensaje adecuado
			res.setNumResultados(-1);
			res.setNumPaginas(-1);
			res.setResultados(new ArrayList<>());
			return res;
//			throw exUtils.luceneQueryParserException(luceneQuery);
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error de tipo [" + e.getClass() + "] no controlado durante la "
					+ "ejecución de la consulta lucene: ", e);
			throw exUtils.genericException("while the lucene query was running");
		}

		// limite de resultados por bÃºsqueda, paginas y resultados encontrados
		int limit = Integer.parseInt(searchLimit);
		long numResultados = nodes.length();
		if (numResultados == -1)
			numResultados = nodes.getNumberFound();

		int numPaginas = new Double(numResultados / limit).intValue();
		if (numResultados % limit != 0)
			numPaginas++;
		long busMill = System.currentTimeMillis();
		ArrayList<Node> resultado = new ArrayList<Node>();
		// Recupero los nodos a devolver
		// OJO! Con el inDMPath es posible que se devuelvan menos resultados de los que
		// se esperan, porque
		// esos resultados ser�an incorrectos... y no aparecer�an en la bÃºsqueda, pero
		// no se puede capar por permisos
		for (int i = pagina * limit; i < pagina * limit + limit; i++) {
			if (i < numResultados) {
				try {
					NodeRef nodeRef = nodes.getNodeRef(i);
					if (utils.inDMPath(nodeRef)) {
						resultado.add(_internal_getNode(nodeRef, false, false));
					}
				} catch (Exception excpt) {
					// do nothing. Bypass IndexOutOfBounds or other exceptions
					LOGGER.error(excpt);
				}
			}
		}
		// Componer la respuesta
		res.setNumPaginas(numPaginas);
		res.setNumResultados(numResultados);
		res.setResultados(resultado);
		long endMill = System.currentTimeMillis();
		LOGGER.info("searchNode (Busqueda: " + (busMill - initMill) + "ms, Composicion nodos: " + (endMill - busMill)
				+ "ms)");
		return res;
	}

	/**
	 * Recupera el �ndice de intercambio de un nodo en el RM
	 * 
	 * @throws GdibException
	 *
	 */
	private DataHandler _getIndexFromRM(NodeRef node, String indexType) throws GdibException {
		String prefix = "";
		if (indexType.equals(EXCHANGE)) {
			prefix = ConstantUtils.EXCHANGE_INDEX_NAME_PREFIX;
		} else if (indexType.equals(INTERNAL)) {
			prefix = ConstantUtils.INTERNAL_INDEX_NAME_PREFIX;
		}
		List<ChildAssociationRef> hijos = nodeService.getChildAssocs(node);
		for (ChildAssociationRef childref : hijos) {
			NodeRef hijo = childref.getChildRef();
			if ("NO".equals(nodeService.getProperty(hijo, ConstantUtils.PROP_INDEX_VALID_QNAME)))
				continue;

			if (nodeService.getType(hijo).equals(ConstantUtils.TYPE_FILE_INDEX_QNAME)) {
				String name = nodeService.getProperty(hijo, ContentModel.PROP_NAME).toString();
				String eniId = nodeService.getProperty(node, ConstantUtils.PROP_ID_QNAME).toString();
				if (name.startsWith(prefix + eniId + "-")) {
					return utils.getContent(hijo).getData();
				}
			}
		}
		return null;
	}

	private DataHandler getInternalIndexFromRM(NodeRef node) throws GdibException {
		return _getIndexFromRM(node, INTERNAL);
	}

	private DataHandler getExchangeIndexFromRM(NodeRef node) throws GdibException {
		return _getIndexFromRM(node, EXCHANGE);
	}

	/*
	 * Setters.
	 *
	 */
	public void setSignatureService(SignatureService signatureService) {
		this.signatureService = signatureService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setExUtils(ExUtils exUtils) {
		this.exUtils = exUtils;
	}

	public void setUtils(GdibUtils utils) {
		this.utils = utils;
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}

	public void setRepositoryDisableCheck(Boolean repositoryDisableCheck) {
		this.repositoryDisableCheck = repositoryDisableCheck;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setIndiceElectronicoManager(IndiceElectronicoManager indiceElectronicoManager) {
		this.indiceElectronicoManager = indiceElectronicoManager;
	}

	public void setSearchLimit(String searchLimit) {
		this.searchLimit = searchLimit;
	}

	public ExportUtils getExportUtils() {
		return exportUtils;
	}

	public void setExportUtils(ExportUtils exportUtils) {
		this.exportUtils = exportUtils;
	}

	public void setSubTypeDocUtil(SubTypeDocUtil subTypeDocUtil) {
		this.subTypeDocUtil = subTypeDocUtil;
	}

	public void setImportUtils(ImportUtils importUtils) {
		this.importUtils = importUtils;
	}

	public void setEniIdNoReplace(Boolean eniIdNoReplace) {
		this.eniIdNoReplace = eniIdNoReplace;
	}

	public void setCopyService(CopyService copyService) {
		this.copyService = copyService;
	}

	/**
	 * @param addEniExchangeFilesPropValue the addEniExchangeFilesPropValue to set
	 */
	public void setAddEniExchangeFilesPropValue(String addEniExchangeFilesPropValue) {
		this.addEniExchangeFilesPropValue = addEniExchangeFilesPropValue;
	}

	/**
	 * @param addIntExchangeFilesPropValue the addIntExchangeFilesPropValue to set
	 */
	public void setAddIntExchangeFilesPropValue(String addIntExchangeFilesPropValue) {
		this.addIntExchangeFilesPropValue = addIntExchangeFilesPropValue;
	}

	public CertificateUtils getCertUtils() {
		return certUtils;
	}

	public void setCertUtils(CertificateUtils certUtils) {
		this.certUtils = certUtils;
	}

	/*
	 * private void createTransaction(GdibHeader gdibHeader) throws GdibException {
	 * LOGGER.debug("Begin Alfresco Transaction"); usrTrx =
	 * transactionService.getUserTransaction(); try {
	 * LOGGER.info("Transacci�n recuperada. Estado: "+
	 * String.valueOf(usrTrx.getStatus())); usrTrx.begin();
	 * LOGGER.info("Transacci�n iniciada.  Estado: "+
	 * String.valueOf(usrTrx.getStatus())); } catch (NotSupportedException e) {
	 * LOGGER.error(e.getMessage(), e); throw new GdibException(e.getMessage(), e);
	 * } catch (SystemException e) { throw new GdibException(e.getMessage(), e); } }
	 * 
	 * private void commit() throws GdibTransactionException {
	 * LOGGER.debug("Commit Alfresco Transaction"); try { usrTrx.commit(); } catch
	 * (SecurityException e) { LOGGER.error(e.getMessage(), e); throw
	 * exUtils.transactionException(e); } catch (IllegalStateException e) {
	 * LOGGER.error(e.getMessage(), e); throw exUtils.transactionException(e); }
	 * catch (RollbackException e) { LOGGER.error(e.getMessage(), e); throw
	 * exUtils.transactionException(e); } catch (HeuristicMixedException e) {
	 * LOGGER.error(e.getMessage(), e); throw exUtils.transactionException(e); }
	 * catch (HeuristicRollbackException e) { LOGGER.error(e.getMessage(), e); throw
	 * exUtils.transactionException(e); } catch (SystemException e) {
	 * LOGGER.error(e.getMessage(), e); throw exUtils.transactionException(e); } }
	 * 
	 * private void rollback() throws GdibException {
	 * LOGGER.debug("Rollback on Alfresco Transaction"); try { if (
	 * usrTrx.getStatus() == Status.STATUS_ACTIVE){ usrTrx.rollback(); } } catch
	 * (IllegalStateException e) { LOGGER.error(e.getMessage(), e); //throw new
	 * GdibException(e.getMessage(), e); } catch (SecurityException e) {
	 * LOGGER.error(e.getMessage(), e); throw new GdibException(e.getMessage(), e);
	 * } catch (SystemException e) { LOGGER.error(e.getMessage(), e); throw new
	 * GdibException(e.getMessage(), e); } }
	 * 
	 * private void doAuthentication(GdibHeader gdibHeader) throws GdibException{
	 * GdibSecurity security = gdibHeader.getGdibSecurity();
	 * doAuthentication(security.getUser(),security.getPassword()); }
	 * 
	 * private void doAuthentication(String username, String password) throws
	 * GdibException { try { // si el usuario viene vacio, se tiene que validar el
	 * ticket de autenticacion de alfresco if(StringUtils.isEmpty(username)){
	 * if(password==null) throw new
	 * GdibException("You need authentication to perfom this operation");
	 * authenticationService.validate(password); }else{ // y sino realizar la
	 * autenticacion normal de usuario y password if(StringUtils.isEmpty(password)){
	 * throw new GdibException("Username and Password are mandatory"); } // login
	 * con usuario y password authenticationService.authenticate(username,
	 * password.toCharArray()); } }catch (AuthenticationException ae){ throw new
	 * GdibException(ae.getMessage()); } }
	 */
}
