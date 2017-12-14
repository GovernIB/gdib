package es.caib.gdib.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/gidbUtils-context.xml")
public class GdibUtilsTest {

    static Logger LOGGER = Logger.getLogger(GdibUtilsTest.class);

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private GdibUtils utils;

    @Autowired
    @Qualifier("NamespaceService")
    private NamespaceService namespaceService;

    @Autowired
    @Qualifier("FileFolderService")
    private FileFolderService fileFolderService;

    @Autowired
    @Qualifier("VersionService")
    private VersionService versionService;

    @Autowired
    @Qualifier("repositoryHelper")
    private Repository repository;

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    private Node nodeDocument;
    @Autowired
    private Node nodeExpedient;

    @Before
    public void configureUp() throws FileNotFoundException, NotSupportedException, SystemException, GdibException{
    	// preparo entorno de pruebas
    	testUtils.configureUp();
    	utils.setRootDM(TestUtils.rootDM.getId());
    	utils.setRootCT(TestUtils.rootCT.getId());
    	utils.setRootTemplate(TestUtils.rootTemplate.getId());
    }

    @After
    public void configureDown() throws IllegalStateException, SecurityException, SystemException{
    	testUtils.configureDown();
    }

    @Test
    public void testIsValidUuid() throws GdibException{

    	assertFalse(utils.validNodeId(""));
    	assertFalse(utils.validNodeId(null));

    	// path relativo
    	assertTrue(utils.validNodeId("b24eeb92-aed8-439c-af4d-db25785b2fc4/ruta/al/nodo"));
    	assertTrue(utils.validNodeId("b24eeb92-aed8-439c-af4d-db25785b2fc4/ruta/al/nodo/"));
    	assertFalse(utils.validNodeId("b24eeb92-aed8-439c-af4d-db25785b2fc4/"));

    	// version
    	assertTrue(utils.validNodeId("0.1@b24eeb92-aed8-439c-af4d-db25785b2fc4"));
    	assertTrue(utils.validNodeId("1.0@b24eeb92-aed8-439c-af4d-db25785b2fc4"));
    	assertTrue(utils.validNodeId("1.11@b24eeb92-aed8-439c-af4d-db25785b2fc4"));
    	assertTrue(utils.validNodeId("11.11@b24eeb92-aed8-439c-af4d-db25785b2fc4"));
    	assertFalse(utils.validNodeId("1@b24eeb92-aed8-439c-af4d-db25785b2fc4"));
    	assertFalse(utils.validNodeId("@b24eeb92-aed8-439c-af4d-db25785b2fc4"));

    	// Valid Uid
    	assertTrue(utils.validNodeId("461cd886-35d9-45cb-8fe7-06e3bcc3c1c8"));
    	// First group lenght
    	assertFalse(utils.validNodeId("461cd88-35d9-45cb-8fe7-06e3bcc3c1c8"));
    	// First group invalid
    	assertFalse(utils.validNodeId("461cd88@-35d9-45cb-8fe7-06e3bcc3c1c8"));
    	// Second group lenght
    	assertFalse(utils.validNodeId("461cd886-35d-45cb-8fe7-06e3bcc3c1c8"));
    	// Second group invalid
    	assertFalse(utils.validNodeId("461cd886-35@9-45cb-8fe7-06e3bcc3c1c8"));
    	// Third group lenght
    	assertFalse(utils.validNodeId("461cd886-35d9-45b-8ye7-06e3bcc3c1c8"));
    	// Third group invalid
    	assertFalse(utils.validNodeId("461cd886-35d9-45c@-8fe7-06e3bcc3c1c8"));
    	// Fourth group lenght
    	assertFalse(utils.validNodeId("461cd886-35d9-45cb-8e7-06e3bcc3c1c8"));
    	// Fourth group invalid
    	assertFalse(utils.validNodeId("461cd886-35d9-45cb-8f@7-06e3bcc3c1c8"));
    	// Fifth group lenght
    	assertFalse(utils.validNodeId("461cd886-35d9-45cb-8fe7-06e3bcc3cc8"));
    	// Fifth group invalid
    	assertFalse(utils.validNodeId("461cd88e-35d9-45cb-8se7-06@3bcc3c1c8"));
    }

    @Test
    @DirtiesContext
    public void testIdToNodeRef() throws GdibException{
    	nodeExpedient.getProperties().add(new Property(ConstantUtils.PROP_COD_CLASIFICACION_QNAME.toString(), "EXP004321"));
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		Node doc = repositoryServiceSoap.createAndGetNode(nodeDocument, exp, null);
		versionService.ensureVersioningEnabled(utils.toNodeRef(doc.getId()), null);

		nodeDocument.setName("document2.txt");
		Node doc2 = repositoryServiceSoap.createAndGetNode(nodeDocument, exp + "/1 Carpeta1", null);
		// busqueda por UID
    	NodeRef docRef = utils.idToNodeRef(doc.getId());
    	assertNotNull(docRef);
    	assertEquals(doc.getId(), docRef.getId());
    	assertNull(utils.idToNodeRef(docRef.getId()+"a"));
    	assertNull(utils.idToNodeRef(docRef.getId().substring(2)));

    	// busqueda por PATH relativo
    	NodeRef docRef2 = utils.idToNodeRef(exp+ "/1 Carpeta1/document2.txt");
    	assertNotNull(docRef2);
    	assertEquals(doc2.getId(), docRef2.getId());
    	assertNull(utils.idToNodeRef(exp+ "/1 Carpeta1/document2"));
    }

    @Test(expected = GdibException.class)
    public void testIdToNodeRefVersionException() throws GdibException{
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		Node doc = repositoryServiceSoap.createAndGetNode(nodeDocument, exp, null);
		versionService.ensureVersioningEnabled(utils.toNodeRef(doc.getId()), null);

		utils.idToNodeRef("1.5@" + doc.getId());
    }

    @Test
    public void testTransformListToMap() throws GdibException{
    	List<Property> prop = new ArrayList<Property>();
    	prop.add(new Property("cm:title", "expediente"));
    	Map<QName, Serializable> map;

    	map = utils.transformMapStringToQname(prop);

    	QName title = QName.createQName("cm:title", namespaceService);
    	assertTrue(map.containsKey(title));
		assertEquals("expediente", map.get(title));
    }

    @Test
    public void testConstainsProperties() throws GdibException{
    	HashMap<String, String> prop = new HashMap<>();
    	prop.put("{http://www.alfresco.org/model/content/1.0}name", "nombre");
    	prop.put("cm:title", "nombre completo");

    	assertTrue(utils.contains(prop, "cm:title"));
    	assertTrue(utils.contains(prop, "{http://www.alfresco.org/model/content/1.0}name"));
    	assertFalse(utils.contains(prop, "origen"));
    	assertFalse(utils.contains(prop, "{}name"));
    }

    @Test
    public void testConstainsPropertiesQname() throws GdibException{
    	List<String> list = new ArrayList<String>();
    	list.add("{http://www.alfresco.org/model/content/1.0}name");
    	list.add("cm:title");

    	QName title = QName.createQName("cm:title", namespaceService);
    	QName name = QName.createQName("{http://www.alfresco.org/model/content/1.0}name");

    	assertTrue(utils.contains(list, title));
    	assertTrue(utils.contains(list, name));
    	assertTrue(utils.contains(list, "cm:name"));
    	assertTrue(utils.contains(list, "{http://www.alfresco.org/model/content/1.0}title"));

    }

    @Test
    public void testProperty() throws GdibException{
    	HashMap<String, String> prop = new HashMap<>();
    	prop.put("cm:title", "titulo");
    	prop.put("{http://www.alfresco.org/model/content/1.0}name", "nombre");

    	assertEquals("nombre", utils.getProperty(prop, "cm:name"));
    	assertEquals("titulo", utils.getProperty(prop, "{http://www.alfresco.org/model/content/1.0}title"));
    }

    @Test
    public void testTypeToQName() throws GdibException{
    	assertEquals(ConstantUtils.TYPE_DOCUMENTO_QNAME, GdibUtils.createQName("eni:documento"));
    	assertEquals(ConstantUtils.TYPE_DOCUMENTO_QNAME, GdibUtils.createQName("{http://www.administracionelectronica.gob.es/model/eni/1.0}documento"));
    }

    @Test
    @DirtiesContext
    public void testGetContent() throws GdibException{
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String id = repositoryServiceSoap.createNode(nodeDocument, exp, null);
    	NodeRef node = utils.idToNodeRef(id);
    	assertNotNull(utils.getContent(node));
    	assertTrue(utils.compareDataHandlers(utils.getContent(node).getData(), nodeDocument.getContent().getData()));

    	nodeDocument.setContent(null);
    	nodeDocument.setName("document2.txt");
    	id = repositoryServiceSoap.createNode(nodeDocument, exp, null);
    	node = utils.idToNodeRef(id);
    	assertNull(utils.getContent(node));
    	assertNull(utils.getContent(null));
    }

    @Test
    public void testGetCalculatedProperties() throws GdibException{

    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	Node doc = repositoryServiceSoap.createAndGetNode(nodeDocument, exp, null);

    	List<Property> prop = utils.getPropertiesCalculated(utils.toNodeRef(doc.getId()));

    	Property calculatedSite = prop.get(prop.indexOf(new Property(ConstantUtils.CALCULATED_MODEL_PREFIX + ConstantUtils.CALCULATED_SITE)));
		assertEquals("unittest", calculatedSite.getValue());

    	Property path = prop.get(prop.indexOf(new Property(ConstantUtils.CALCULATED_MODEL_PREFIX + ConstantUtils.CALCULATED_PATH)));
		assertEquals("/Company Home/Sites/unittest/documentLibrary/expedient/document.txt", path.getValue());

    }

    @Test
    public void testGetProperties() throws GdibException{
    	String id = repositoryServiceSoap.createNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	NodeRef node = utils.idToNodeRef(id);
    	List<Property> properties = utils.getProperties(node);
    	assertNotNull(utils.getProperty(properties, ConstantUtils.CAIB_MODEL_PREFIX + ConstantUtils.PROP_ID));
    	assertNotNull(utils.getProperty(properties, ContentModel.PROP_NAME));

    	assertNull(utils.getProperty(properties, "desconocido"));
    	assertNull(utils.getProperty(properties, QName.createQName("{desconocido}desconocido")));
    }

    @Test
    public void testGetAspects() throws GdibException{
    	String id = repositoryServiceSoap.createNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	NodeRef node = utils.idToNodeRef(id);
    	List<String> aspects = utils.getAspects(node);
    	assertNotNull(utils.getAspect(aspects, ConstantUtils.CAIB_MODEL_PREFIX + ConstantUtils.ASPECT_INTEROPERABLE));
    	assertNotNull(utils.getAspect(aspects, QName.createQName("{http://www.administracionelectronica.gob.es/model/eni/1.0}transferible")));

    	assertNull(utils.getAspect(aspects, "desconocido"));
    	assertNull(utils.getAspect(aspects, QName.createQName("{desconocido}desconocido")));
    }

    @Test
    public void testFormatQname(){
    	AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getGuestUserName());
    	assertEquals("{http://www.alfresco.org/model/content/1.0}name", utils.formatQname("cm:name"));
    	assertEquals("{http://www.alfresco.org/model/content/1.0}name", utils.formatQname(ContentModel.PROP_NAME));
    	AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
    	assertEquals("cm:name", utils.formatQname("cm:name"));
    	assertEquals("cm:name", utils.formatQname(ContentModel.PROP_NAME));
    }

    @Test
    public void testFormatQnameCalculated(){
    	AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getGuestUserName());
    	assertEquals("{"+ConstantUtils.CALCULATED_URI+"}"+ConstantUtils.CALCULATED_SITE, utils.formatQnameCalculated("site"));
    	AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
    	assertEquals("calc:site", utils.formatQnameCalculated("site"));
    }

    @Test
    public void testFilterRemoveProperties() throws GdibException{
    	List<Property> filterProperties = new ArrayList<Property>();
    	filterProperties.add(new Property("prop1", "value1"));
    	filterProperties.add(new Property("-prop2", "value2"));

    	filterProperties = utils.filterRemoveMetadata(filterProperties);

    	assertNotNull(utils.getProperty(filterProperties, "prop1"));
    	assertNull(utils.getProperty(filterProperties, "prop2"));
    }

    @Test
    public void testFilterCalculatedProperties() throws GdibException{
    	List<Property> filterProperties = new ArrayList<Property>();
    	filterProperties.add(new Property("prop1", "value1"));
    	filterProperties.add(new Property(ConstantUtils.CALCULATED_MODEL_PREFIX+ConstantUtils.CALCULATED_PATH, "value2"));

    	filterProperties = utils.filterCalculatedProperties(filterProperties);

    	assertNotNull(utils.getProperty(filterProperties, "prop1"));
    	assertNull(utils.getProperty(filterProperties, ConstantUtils.CALCULATED_MODEL_PREFIX+ConstantUtils.CALCULATED_PATH));
    }

    @Test
    public void testGenerateExpedientTree() throws GdibException{
    	utils.generateExpedientTree(TestUtils.rootDM, new String[]{"funcion","serieDocumental","anio", "mes", "dia"});
    	NodeRef funcion = fileFolderService.searchSimple(TestUtils.rootDM, "funcion");
    	assertNotNull(funcion);
    	NodeRef serieDocumental = fileFolderService.searchSimple(funcion, "serieDocumental");
    	assertNotNull(serieDocumental);
    	NodeRef anio = fileFolderService.searchSimple(serieDocumental, "anio");
    	assertNotNull(anio);
    	NodeRef mes = fileFolderService.searchSimple(anio, "mes");
    	assertNotNull(mes);
    	NodeRef dia = fileFolderService.searchSimple(mes, "dia");
    	assertNotNull(dia);
    }

    @Test
    public void testGetPathFromUID(){
    	assertEquals("/Company Home/Sites/unittest/documentLibrary", utils.getPathFromUID(TestUtils.rootDM));
    }
}
