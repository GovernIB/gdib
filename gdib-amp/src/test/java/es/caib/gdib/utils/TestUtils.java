package es.caib.gdib.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import es.caib.gdib.webscript.cuadroclasif.CuadroClasificacionSerieDocumental;
import es.caib.gdib.ws.common.types.Content;
import es.caib.gdib.ws.common.types.MigrationID;

public class TestUtils {
	private static final Logger LOGGER = Logger.getLogger(TestUtils.class);

	public final static String USER_TEST = "testUser";
	public final static String USER_TEST2 = "testUser2";
	public final static String DM_SITE = "DM_test";
	public final static String MIGRATION_SITE = ConstantUtils.REPO_MIGR;
	public final static String ADMIN_USER = "admin";
	public final static String TEMP_FOLDER = "temp";

	public static NodeRef rootDM;
	public static NodeRef rootMigration;
	public static NodeRef rootCT;
	public static NodeRef rootTemplate;
	public static NodeRef rootTemp;

	private UserTransaction userTrx;

	@Autowired
    private GdibUtils utils;

	@Autowired
	@Qualifier("NodeService")
	private NodeService nodeService;

	@Autowired
	@Qualifier("PersonService")
	private PersonService personService;

	@Autowired
	@Qualifier("TransactionService")
	private TransactionService transactionService;

	@Autowired
    @Qualifier("SiteService")
    private SiteService siteService;

	@Autowired
    @Qualifier("PermissionService")
    private PermissionService permissionService;

    @Autowired
    @Qualifier("FileFolderService")
    private FileFolderService fileFolderService;

    @Autowired
    @Qualifier("CategoryService")
    private CategoryService categoryService;

    @Autowired
    @Qualifier("repositoryHelper")
    private Repository repository;

    public void createUser(String user) {
		if (!personService.personExists(user)) {
			PropertyMap ppOne = new PropertyMap(4);
			ppOne.put(ContentModel.PROP_USERNAME, user);
			ppOne.put(ContentModel.PROP_FIRSTNAME, user);
			ppOne.put(ContentModel.PROP_LASTNAME, user);
			ppOne.put(ContentModel.PROP_EMAIL, user + "@example.com");
			ppOne.put(ContentModel.PROP_JOBTITLE, user);

			personService.createPerson(ppOne);
		}
	}

	private void createTransaction() throws NotSupportedException, SystemException {
		userTrx = transactionService.getUserTransaction();
    	userTrx.begin();
	}

	private NodeRef createSite(String name) {
		SiteInfo siteInfo = siteService.createSite("site-dashboard", name, name, name, SiteVisibility.PRIVATE);
		NodeRef nodeSite = siteInfo.getNodeRef();
		NodeRef rootDoc = fileFolderService.create(nodeSite, "documentLibrary", ContentModel.TYPE_FOLDER).getNodeRef();

		return rootDoc;
	}

	private NodeRef createClassificationTable(){
		QName generalclassifiable = QName.createQName("{http://www.alfresco.org/model/content/1.0}generalclassifiable");
		NodeRef rootClassificationTable = categoryService.createRootCategory(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
				generalclassifiable, "Classification Table Test");

		NodeRef rootSanidad = categoryService.createCategory(rootClassificationTable, "Sanidad");
		categoryService.createCategory(rootSanidad, "EXP001234");
		categoryService.createCategory(rootSanidad, "EXP001235");
		NodeRef rootEducacion = categoryService.createCategory(rootClassificationTable, "Educacion");
		categoryService.createCategory(rootEducacion, "EXP004321");
		categoryService.createCategory(rootEducacion, "EXP004322");

		// replico el cuadro de clasificacion en el DM
		NodeRef dmSanidad = fileFolderService.create(rootDM, "Sanidad", ConstantUtils.TYPE_FOLDER).getNodeRef();
		NodeRef dmEXP001234 = fileFolderService.create(dmSanidad, "EXP001234", ConstantUtils.TYPE_FOLDER).getNodeRef();
		NodeRef dmEXP001235 = fileFolderService.create(dmSanidad, "EXP001235", ConstantUtils.TYPE_FOLDER).getNodeRef();
		NodeRef dmEducacion = fileFolderService.create(rootDM, "Educacion", ConstantUtils.TYPE_FOLDER).getNodeRef();
		NodeRef dmEXP004321 = fileFolderService.create(dmEducacion, "EXP004321", ConstantUtils.TYPE_FOLDER).getNodeRef();
		NodeRef dmEXP004322 = fileFolderService.create(dmEducacion, "EXP004322", ConstantUtils.TYPE_FOLDER).getNodeRef();

		// doy permisos para los usuario de test en las series documentales del DM
		permissionService.setPermission(rootDM, USER_TEST, PermissionService.CONSUMER, true);
		permissionService.setPermission(dmEXP001234, USER_TEST, PermissionService.FULL_CONTROL, true);
		permissionService.setPermission(dmEXP001235, USER_TEST, PermissionService.FULL_CONTROL, true);
		permissionService.setPermission(rootDM, USER_TEST2, PermissionService.CONSUMER, true);
		permissionService.setPermission(dmEXP004321, USER_TEST2, PermissionService.FULL_CONTROL, true);
		permissionService.setPermission(dmEXP004322, USER_TEST2, PermissionService.FULL_CONTROL, true);

		return rootClassificationTable;
	}

	public NodeRef createExpedienteTemplate(String function, String documentarySeries, Map<String, String> folderTree){
//		NodeRef dataDictionary = fileFolderService.searchSimple(repository.getCompanyHome(), "Diccionario de datos");
//		NodeRef spaceTemplates = fileFolderService.searchSimple(dataDictionary, "Plantillas de espacio");
		NodeRef dataDictionary = fileFolderService.searchSimple(repository.getCompanyHome(), "Data Dictionary");
		NodeRef spaceTemplates = fileFolderService.searchSimple(dataDictionary, "Space Templates");
		NodeRef gdib = fileFolderService.create(spaceTemplates, "gdibTest", ConstantUtils.TYPE_FOLDER).getNodeRef();

		NodeRef functionNode = fileFolderService.create(gdib, function, ConstantUtils.TYPE_FOLDER).getNodeRef();
		NodeRef documentarySeriesNode = fileFolderService.create(functionNode, documentarySeries, ConstantUtils.TYPE_FOLDER).getNodeRef();
		for (String key : folderTree.keySet()) {
			String path[] = key.split(ConstantUtils.PATH_SEPARATOR);
			NodeRef root = documentarySeriesNode;
			if(path.length != 0){
				for (int i = 1; i < path.length; i++) {
					if(fileFolderService.searchSimple(root, path[i]) == null){
						root = fileFolderService.create(root, path[i], ConstantUtils.TYPE_FOLDER).getNodeRef();
					}else{
						root = fileFolderService.searchSimple(root, path[i]);
					}
				}
			}
			String[] folders = folderTree.get(key).split(",");
			for (int j = 0; j < folders.length; j++) {
				if(fileFolderService.searchSimple(root, folders[j]) == null){
					fileFolderService.create(root, folders[j], ConstantUtils.TYPE_FOLDER).getNodeRef();
				}
			}
		}
		return gdib;
	}

	private void rollbackTransaction() throws IllegalStateException, SecurityException, SystemException {
		userTrx.rollback();
	}

	public void configureUp() throws NotSupportedException, SystemException {
		AuthenticationUtil.setFullyAuthenticatedUser(ADMIN_USER);

		// creo una transaccion
		createTransaction();

		// creo un usuario de prueba
		createUser(USER_TEST);
		createUser(USER_TEST2);

		TestUtils.rootTemp = fileFolderService.create(repository.getCompanyHome(), TestUtils.TEMP_FOLDER, ContentModel.TYPE_FOLDER).getNodeRef();
		permissionService.setPermission(TestUtils.rootTemp, PermissionService.ALL_AUTHORITIES, PermissionService.COORDINATOR, true);

		// creo un site de prueba
		TestUtils.rootDM = createSite(DM_SITE);
		TestUtils.rootMigration = createSite(MIGRATION_SITE);

		TestUtils.rootCT = createClassificationTable();

		Map<String, String> expedientTree = new HashMap<String, String>();
		expedientTree.put("/3 Carpeta3/1.3 Carpeta13", "1.3.1 Carpeta131");
		expedientTree.put("/3 Carpeta3", "1.3 Carpeta13");
		expedientTree.put("/1 Carpeta1", "1.1 Carpeta11,1.1 Carpeta12");
		expedientTree.put("/", "1 Carpeta1,2 Carpeta2,3 Carpeta3");
		TestUtils.rootTemplate = createExpedienteTemplate("Educacion", "EXP004321", expedientTree);
	}

	public void configureDown() throws IllegalStateException, SecurityException, SystemException {
		rollbackTransaction();
	}

	public NodeRef createMigrationNode(String nodeName, String appId, String externalId, Content content,
			DataHandler contentDataHandlerFirma, DataHandler contentDataHandlerFirmaMigracion, DataHandler contentDataHandlerZipMigracion){

		NodeRef node = fileFolderService.create(TestUtils.rootMigration, nodeName, ConstantUtils.TYPE_DOCUMENTO_MIGRADO_QNAME).getNodeRef();
    	Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

    	properties.put(ContentModel.PROP_TITLE, nodeName);
    	properties.put(ConstantUtils.PROP_ID_TRAMITE_QNAME, appId);
    	properties.put(ConstantUtils.PROP_CODIGO_EXTERNO_QNAME, externalId);

//    	tipo documento migrado
    	properties.put(ConstantUtils.PROP_FECHA_MIGRACION_QNAME, "2016-02-15");
    	properties.put(ConstantUtils.PROP_CODIGO_QNAME, "123456789");
    	properties.put(ConstantUtils.PROP_FECHA_CUSTODIA_QNAME, "2016-02-15");
    	properties.put(ConstantUtils.PROP_VIGENTE_QNAME, true);
    	properties.put(ConstantUtils.PROP_FECHA_FIN_VIGENCIA_QNAME, "2016-02-15");
    	properties.put(ConstantUtils.PROP_TIPO_DOCUMENTAL_QNAME, "tipo_documental");
    	properties.put(ConstantUtils.PROP_FECHA_CREACION_QNAME,  "2016-02-15");
    	properties.put(ConstantUtils.PROP_FECHA_ELIMINACION_QNAME,  "2016-02-15");
    	properties.put(ConstantUtils.PROP_FECHA_PURGADO_QNAME,  "2016-02-15");
    	properties.put(ConstantUtils.PROP_CLASE_QNAME, "PDF_FIRMADO");
    	properties.put(ConstantUtils.PROP_HASH_QNAME, "hash");

//    	aspecto transferible
		properties.put(ConstantUtils.PROP_CATEGORIA_QNAME, "Serie");

//    	aspecto registrable
		properties.put(ConstantUtils.PROP_TIPO_ASIENTO_REGISTRAL_QNAME, "0");

    	nodeService.addProperties(node, properties );

    	try {
    		if(content != null){
				utils.setDataHandler(node, ConstantUtils.PROP_CONTENT, content.getData(), content.getMimetype());
    		}
			if(contentDataHandlerFirma != null){
	    		NodeRef firma = fileFolderService.create(TestUtils.rootMigration, nodeName+".firma", ConstantUtils.TYPE_FIRMA_VALCERT_QNAME).getNodeRef();
	    		utils.setDataHandler(firma, ConstantUtils.PROP_CONTENT, contentDataHandlerFirma, content.getMimetype());
			}
			if(contentDataHandlerFirmaMigracion != null){
	    		NodeRef firmaMigracion = fileFolderService.create(TestUtils.rootMigration, nodeName+".firmaMigracion", ConstantUtils.TYPE_FIRMA_MIGRACION_QNAME).getNodeRef();
	    		utils.setDataHandler(firmaMigracion, ConstantUtils.PROP_CONTENT, contentDataHandlerFirmaMigracion, content.getMimetype());
			}
			if(contentDataHandlerZipMigracion != null){
	    		NodeRef zipMigracion = fileFolderService.create(TestUtils.rootMigration, nodeName+".zipMigracion.zip", ConstantUtils.TYPE_ZIP_MIGRACION_QNAME).getNodeRef();
	    		utils.setDataHandler(zipMigracion, ConstantUtils.PROP_CONTENT, contentDataHandlerZipMigracion, content.getMimetype());
			}
    	} catch (ContentIOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		}

		return node;
    }

	public NodeRef createMigrationNode(String nodeName, String appId, String externalId){
		return createMigrationNode(nodeName, appId, externalId, null, null, null, null);
	}

	public MigrationID createMigrationID(String appId, String externalId){
    	MigrationID migration = new MigrationID();
    	migration.setAppId(appId);
        migration.setExternalId(externalId);
    	return migration;
    }
}

