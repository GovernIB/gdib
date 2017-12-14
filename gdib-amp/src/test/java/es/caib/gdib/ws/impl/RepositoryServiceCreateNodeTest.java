package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.domain.node.ContentDataWithId;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.StringUtils;
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

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.TestUtils;
import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;

/**
 * Test para {@link es.caib.gdib.ws.impl.RepositoryServiceSoapPortImpl#createNode(Node, String, es.caib.gdib.ws.common.types.GdibHeader)}
 * y {@link es.caib.gdib.ws.impl.RepositoryServiceSoapPortImpl#createAndGetNode(Node, String, es.caib.gdib.ws.common.types.GdibHeader))}
 *
 * - crear nodo rellenando correctamente el metadato eni:id
 * - crear nodo rellenando correctamente el metadato eni:id, otro usuario no propietario del nodo padre
 * - crear nodo sin permisos - excepcion
 * - crear nodo expediente
 * - crear nodo expediente, otro usuario no propietario del nodo padre
 * - crear nodo expediente sin permisos - excepcion
 * - crear documento con padre no existe - excepcion
 * - crear documento con padre no dentro del DM - excepcion
 * - crear expediente con padre no existe - excepcion
 * - crear documento null - excepcion
 * - crear documento con padre en blanco, padre nulo, padre es una version, padre no es una carpeta - excepcion
 * - crear expediente con padre no es una carpeta - excepcion
 * - crear nodo con nombre vacio, nulo - excepcion
 * - crear nodo con nombre con caracteres extraños
 * - crear nodo sin tipo, tipo vacio, o tipo desconocido - excepcion
 * - crear contenido de alfresco
 * - crear carpeta de alfresco
 * - crear carpeta de alfresco sin padre, con padre vacio o padre no valido - excepcion
 * - crear documento con aspectos
 * - crear documento con aspectos para eliminar o aspectos inventados y marcados para eliminar - se ignoran
 * - crear documento con aspecto desconocidos - excepcion
 * - crear documento con contenido
 * - crear un documento con firma
 * - crear expediente con contenido o firma - excepcion
 * - crear documento faltando alguna propiedad obligatoria - excepcion
 * - crear documento con metadatos con un valor no valido - excepcion
 * - crear documento con namespace desconocido en los metadatos - excepcion
 * - crear nodo con metadatos inventados - excepcion
 * - crear documento con metadatos a eliminar - se ignoran
 * - crear documento con propiedades calculadas - se ignoran
 * - crear nodo con padre bloqueado - excepcion
 * - crear expediente informado el padre con path relativo
 * - crear documento con path relativo y que el uuid no sea un expediente - excepcion
 * - crear subexpediente con path relativo y que el uuid no sea un expediente - excepcion
 * - crear expediente utilizando el cuadro de clasificacion
 * - crear expediente utilizando el cuadro de clasificion, pero la serie documental no existe - excepcion
 * - crear documento con plantilla y sin plantilla
 * - crear expediente fisico con y sin los datos de localizacion
 * - crear documento fisico con y sin los datos de localizacion
 *
 * @author Ricoh
 *
 */
@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/repositoryService-createNodeTest-context.xml")
public class RepositoryServiceCreateNodeTest {

    static Logger log = Logger.getLogger(RepositoryServiceCreateNodeTest.class);

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private GdibUtils utils;

    @Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Autowired
    @Qualifier("FileFolderService")
    private FileFolderService fileFolderService;

    @Autowired
    private GdibHeader gdibHeader;

    @Autowired
    private Node nodeDocument;
    @Autowired
    private Node nodeExpedient;
    @Autowired
    private Node subNodeExpedient;
    @Autowired
    private Node nodeWithAspects;
    @Autowired
    private Node nodeWithAspectsUnknown;
    @Autowired
    private Node nodeDocumentSign;
    @Autowired
    private Node nodeExpedientContent;
    @Autowired
    private Node nodeExpedientSign;
    @Autowired
    private Node alfrescoFolder;
    @Autowired
    private Node nodePhysicalDocumento;
    @Autowired
    private Node alfrescoDocument;
    @Autowired
    private Node nodePhysicalExpedient;

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
    public void testCreateNodeExpedientAndDocument() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);

		NodeRef createExpedient = utils.toNodeRef(expId);
		NodeRef createDocument = utils.toNodeRef(nodeId);

		assertNotNull(createExpedient);
		assertNotNull(createDocument);

		assertEquals(nodeExpedient.getName(), nodeService.getProperty(createExpedient, ContentModel.PROP_NAME));
		assertEquals(nodeDocument.getName(), nodeService.getProperty(createDocument, ContentModel.PROP_NAME));

		assertEquals(GdibUtils.createQName(nodeExpedient.getType()), nodeService.getType(createExpedient));
		assertEquals(GdibUtils.createQName(nodeDocument.getType()), nodeService.getType(createDocument));

		assertTrue(!StringUtils.isEmpty(expId));
		assertTrue(!StringUtils.isEmpty(nodeId));
    }

    @Test
    public void testCreateNodeExpedientAndDocumentOtherUser() throws GdibException{
    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);

		NodeRef createExpedient = utils.toNodeRef(expId);
		NodeRef createDocument = utils.toNodeRef(nodeId);

		assertNotNull(createExpedient);
		assertNotNull(createDocument);

		assertEquals(nodeExpedient.getName(), nodeService.getProperty(createExpedient, ContentModel.PROP_NAME));
		assertEquals(nodeDocument.getName(), nodeService.getProperty(createDocument, ContentModel.PROP_NAME));

		assertEquals(GdibUtils.createQName(nodeExpedient.getType()), nodeService.getType(createExpedient));
		assertEquals(GdibUtils.createQName(nodeDocument.getType()), nodeService.getType(createDocument));

		assertTrue(!StringUtils.isEmpty(expId));
		assertTrue(!StringUtils.isEmpty(nodeId));
    }

    /**
     * Creo un expediente y un documento.
     * - Compruebo que se ha creado, tiene bien el nombre, el tipo ....
     * - Compruebo que tiene los datos automaticos rellenados: eni:id, eni:v_nti, eni:categoria, soporte
     * @throws GdibException
     */
    @Test
	public void testCreateNodeCheckFilledAutomaticMetadata() throws GdibException {
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
		assertTrue(!StringUtils.isEmpty(nodeId));

		NodeRef createDocument = utils.toNodeRef(nodeId);
		NodeRef createExpedient = utils.toNodeRef(expId);

		assertEquals(nodeExpedient.getName(), nodeService.getProperty(createExpedient, ContentModel.PROP_NAME));
		assertEquals(nodeDocument.getName(), nodeService.getProperty(createDocument, ContentModel.PROP_NAME));

		assertEquals(GdibUtils.createQName(nodeExpedient.getType()), nodeService.getType(createExpedient));
		assertEquals(GdibUtils.createQName(nodeDocument.getType()), nodeService.getType(createDocument));

		assertNotNull(nodeService.getProperty(createExpedient, ConstantUtils.PROP_ID_QNAME));
		assertNotNull(nodeService.getProperty(createDocument, ConstantUtils.PROP_ID_QNAME));

		assertEquals(AuthenticationUtil.getFullyAuthenticatedUser(), nodeService.getProperty(createExpedient, ConstantUtils.PROP_APP_TRAMITE_EXP_QNAME));
		assertEquals(AuthenticationUtil.getFullyAuthenticatedUser(), nodeService.getProperty(createDocument, ConstantUtils.PROP_APP_TRAMITE_DOC_QNAME));

		assertEquals(ConstantUtils.V_NTI_EXP, nodeService.getProperty(createExpedient, ConstantUtils.PROP_V_NTI_QNAME));
		assertEquals(ConstantUtils.V_NTI_DOC, nodeService.getProperty(createDocument, ConstantUtils.PROP_V_NTI_QNAME));

		assertEquals(ConstantUtils.CATEGORIA_EXPEDIENTE, nodeService.getProperty(createExpedient, ConstantUtils.PROP_CATEGORIA_QNAME));
		assertEquals(ConstantUtils.CATEGORIA_DOC_SIMPLE, nodeService.getProperty(createDocument, ConstantUtils.PROP_CATEGORIA_QNAME));

		assertEquals(ConstantUtils.SOPORTE_DIGITAL, nodeService.getProperty(createExpedient, ConstantUtils.PROP_SOPORTE_QNAME));
		assertEquals(ConstantUtils.SOPORTE_DIGITAL, nodeService.getProperty(createDocument, ConstantUtils.PROP_SOPORTE_QNAME));

		assertTrue(repositoryServiceSoap.getNodeVersionList(nodeId, null).get(0).getId().equals("1.0"));
	}

    @Test(expected = GdibException.class)
    @DirtiesContext
	public void testCreateNodeDocumentNoPermission() throws GdibException {
    	Property cod_clasification = new Property(ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
    	nodeExpedient.getProperties().get(nodeExpedient.getProperties().indexOf(cod_clasification)).setValue("EXP005678");;
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
	}

    @Test
	public void testCreateNodeExpedient() throws GdibException {
		String nodeId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
		assertTrue(!StringUtils.isEmpty(nodeId));

		NodeRef createNode = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		String type = nodeService.getType(createNode).getPrefixString();

		assertEquals(name, nodeExpedient.getName());
		assertEquals(type, nodeExpedient.getType());
	}

    @Test
	public void testCreateNodeExpedientOtherUser() throws GdibException {
    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		String nodeId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
		assertTrue(!StringUtils.isEmpty(nodeId));

		NodeRef createNode = utils.toNodeRef(nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		String type = nodeService.getType(createNode).getPrefixString();

		assertEquals(name, nodeExpedient.getName());
		assertEquals(type, nodeExpedient.getType());
	}

    @Test(expected = GdibException.class)
   	public void testCreateNodeExpedientNoPermission() throws GdibException {
       	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST2);
   		repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
   	}

    @Test(expected = GdibException.class)
    public void testCreateDocumentParentNotExits() throws GdibException{
    	repositoryServiceSoap.createNode(nodeDocument, "12345678-1234-1234-1234-123456789012", gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testCreateDocumentParentNotInDM() throws GdibException{
    	NodeRef exp = fileFolderService.create(TestUtils.rootMigration, nodeExpedient.getName(), GdibUtils.createQName(nodeExpedient.getType())).getNodeRef();
		repositoryServiceSoap.createNode(nodeDocument, exp.getId(), null);
    }

    @Test(expected = GdibException.class)
    public void testCreateExpedientParentNotExits() throws GdibException{
    	repositoryServiceSoap.createNode(nodeExpedient, "12345678-1234-1234-1234-123456789012", gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testCreateDocumentNull() throws GdibException{
    	repositoryServiceSoap.createNode(null, null, gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testCreateDocumentEmptyParent() throws GdibException{
    	repositoryServiceSoap.createNode(nodeDocument, "", gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testCreateDocumentNullParent() throws GdibException{
    	repositoryServiceSoap.createNode(nodeDocument, null, gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeUnknowmParent() throws GdibException{
    	repositoryServiceSoap.createNode(nodeDocument, "aaaaaaaaaaaaaaaaaaaa", gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeDocumentWithVersionParent() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, "1.1@" + expId, null);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeDocumentWithParentIsNotFolder() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    	nodeDocument.setName("document2.txt");
    	repositoryServiceSoap.createNode(nodeDocument, nodeId, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeExpedientWithParentIsNotFolder() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    	nodeExpedient.setName("expedient2");
    	repositoryServiceSoap.createNode(nodeExpedient, nodeId, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeEmptyName() throws GdibException{
    	nodeDocument.setName("");
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeWithoutName() throws GdibException{
    	nodeDocument.setName(null);
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    }

    @Test
	@DirtiesContext
	public void testCreateNodeRareName() throws GdibException {
		nodeDocument.setName("nam@.txt");
		String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);

		NodeRef createNode = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		assertEquals(name, nodeDocument.getName());
	}

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeWithoutType() throws GdibException{
    	nodeDocument.setType(null);
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeEmptyType() throws GdibException{
    	nodeDocument.setType("");
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeUnknowmType() throws GdibException{
    	nodeDocument.setType("unknown");
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    }

    @Test
    @DirtiesContext
    public void testCreateNodeAlfrescoContent() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	Node node = repositoryServiceSoap.createAndGetNode(alfrescoDocument, expId, gdibHeader);
    	assertEquals(alfrescoDocument.getType(), node.getType());
    }

    @Test
    @DirtiesContext
    public void testCreateNodeAlfrescoFolder() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	Node nodeCreated = repositoryServiceSoap.createAndGetNode(alfrescoFolder, expId, gdibHeader);
    	assertEquals(alfrescoFolder.getType(), nodeCreated.getType());
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeAlfrescoFolderWithoutParent() throws GdibException{
    	repositoryServiceSoap.createAndGetNode(alfrescoFolder, null, gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeAlfrescoFolderWithParentEmpty() throws GdibException{
    	repositoryServiceSoap.createAndGetNode(alfrescoFolder, "", gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeAlfrescoFolderWithParentUnknown() throws GdibException{
    	repositoryServiceSoap.createAndGetNode(alfrescoFolder, "aaaaa", gdibHeader);
    }

	@Test
	public void testCreateNodeDocumentWithAspect() throws GdibException {
		String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
		String nodeId = repositoryServiceSoap.createNode(nodeWithAspects, expId, gdibHeader);
		NodeRef createNode = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);

		Set<QName> aspects = nodeService.getAspects(createNode);
		assertTrue(checkAspects(aspects, nodeWithAspects.getAspects()));
	}

	@Test
	@DirtiesContext
	public void testCreateNodeDocumentWithRemoveAspect() throws GdibException {
		// al crear un nodo con un aspecto a eliminar, el proceso lo ignora
		nodeWithAspects.getAspects().add("-cm:workingcopy");
		String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
		String nodeId = repositoryServiceSoap.createNode(nodeWithAspects, expId, gdibHeader);
		NodeRef createNode = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);

		Set<QName> aspects = nodeService.getAspects(createNode);
		assertFalse(foundAspect(aspects, "cm:workingcopy"));
	}

	@Test
	@DirtiesContext
	public void testCreateNodeDocumentWithInventedRemoveAspect() throws GdibException {
		// al crear un nodo con un aspecto a eliminar, el proceso lo ignora
		nodeWithAspects.getAspects().add("-cm:inventado");
		String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
		String nodeId = repositoryServiceSoap.createNode(nodeWithAspects, expId, gdibHeader);
		NodeRef createNode = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);

		Set<QName> aspects = nodeService.getAspects(createNode);
		assertFalse(foundAspect(aspects, "cm:inventado"));
	}

	@Test(expected = GdibException.class)
	public void testCreateNodeDocumentWithAspectUnknown() throws GdibException {
		String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
		repositoryServiceSoap.createNode(nodeWithAspectsUnknown, expId, gdibHeader);
	}

    @Test
    public void testCreateNodeDocumentWithContent() throws GdibException {
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocumentSign, expId, gdibHeader);

        NodeRef createNode = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);
        ContentDataWithId content = (ContentDataWithId) nodeService.getProperty(createNode, ContentModel.PROP_CONTENT);

        assertEquals(content.getMimetype(), nodeDocumentSign.getContent().getMimetype());
        assertEquals(content.getEncoding(), nodeDocumentSign.getContent().getEncoding());
    }

    @Test
    public void testCreateNodeDocumentWithSign() throws GdibException {
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocumentSign, expId, gdibHeader);

    	NodeRef createNode = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);
        Object sign = nodeService.getProperty(createNode, ConstantUtils.PROP_FIRMA_QNAME);
        assertNotNull(sign);
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeExpedientWithContent() throws GdibException {
    	repositoryServiceSoap.createNode(nodeExpedientContent, null, gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeExpedientWithSign() throws GdibException {
    	repositoryServiceSoap.createNode(nodeExpedientSign, null, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeDocumentCheckMandatoryProperties() throws GdibException{
    	nodeDocument.getProperties().remove("eni:organo");
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeDocumentPropertiesInvalidValue() throws GdibException{
    	nodeDocument.getProperties().add(new Property(ConstantUtils.PROP_ORIGEN_QNAME, "2"));
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeDocumentUnknownNamespaceProperties() throws GdibException{
    	nodeDocument.getProperties().add(new Property("pepe:pepe", "2"));
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeDocumentUnknownProperties() throws GdibException{
    	nodeDocument.getProperties().add(new Property("eni:pepe", "2"));
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    }

    @Test
    @DirtiesContext
    public void testCreateNodeDocumentWithRemoveProperties() throws GdibException{
    	// al crear un nodo, las propiedades de borrado si vienen, se ignoran
    	nodeDocument.getProperties().add(new Property(ConstantUtils.REMOVE_PROPERTY_TOKEN + ConstantUtils.PROP_RESOLUCION_QNAME.toString(), null));
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	String id = repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    	assertNull(nodeService.getProperty(utils.toNodeRef(id), ConstantUtils.PROP_RESOLUCION_QNAME));
    }

    @Test
    @DirtiesContext
    public void testCreateNodeDocumentWithCalculatedProperties() throws GdibException{
    	// al crear un nodo, las propiedades calculadas si vienen, se ignoran
    	nodeDocument.getProperties().add(new Property(ConstantUtils.CALCULATED_MODEL_PREFIX + ConstantUtils.CALCULATED_SITE, "SITE"));
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	String id = repositoryServiceSoap.createNode(nodeDocument, expId, gdibHeader);
    	QName site = QName.createQName(ConstantUtils.CALCULATED_URI, ConstantUtils.CALCULATED_SITE);
    	assertNull(nodeService.getProperty(utils.toNodeRef(id), site));
    }

	@Test(expected = GdibException.class)
    public void testCreateNodeWithParentLock() throws GdibException{
    	String expedient = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	repositoryServiceSoap.lockNode(expedient, gdibHeader);
    	repositoryServiceSoap.createNode(subNodeExpedient, expedient, gdibHeader);
    }

    @Test
    public void testCreateNodeExpedientRelativePath() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	String sub = repositoryServiceSoap.createNode(subNodeExpedient, expId + "/prueba/test", gdibHeader);
    	assertTrue(utils.getPathFromUID(utils.toNodeRef(sub)).contains("/prueba/test/subExpediente"));
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeDocumentRelativePathUuidNotExpedient() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId , gdibHeader);
    	repositoryServiceSoap.createNode(nodeDocument, nodeId + "/prueba/test" , gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeExpedientRelativePathUuidNotExpedient() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId , gdibHeader);
    	repositoryServiceSoap.createNode(subNodeExpedient, nodeId + "/prueba/test" , gdibHeader);
    }

    @Test
    @DirtiesContext
    public void testCreateNodeExpedientWithClassificationTable() throws GdibException{
    	nodeExpedient.getProperties().add(new Property(ConstantUtils.PROP_FECHA_INICIO_QNAME, "2016-02-15"));
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);

    	NodeRef test = fileFolderService.searchSimple(TestUtils.rootDM, "Sanidad");
    	test = fileFolderService.searchSimple(test, "EXP001234");
    	test = fileFolderService.searchSimple(test, "2016");
    	test = fileFolderService.searchSimple(test, "02");
    	test = fileFolderService.searchSimple(test, "15");
    	test = fileFolderService.searchSimple(test, nodeExpedient.getName());
    	assertNotNull(test);
    	assertEquals(expId, test.getId());
    	assertEquals(nodeExpedient.getName(), nodeService.getProperty(test, ConstantUtils.PROP_NAME));
    }

    @Test
    @DirtiesContext
    public void testCreateNodeExpedientWithClassificationTableISO8601LongFormat() throws GdibException{
    	nodeExpedient.getProperties().add(new Property(ConstantUtils.PROP_FECHA_INICIO_QNAME, "2016-11-27T12:00:00+01:00"));
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);

    	NodeRef test = fileFolderService.searchSimple(TestUtils.rootDM, "Sanidad");
    	test = fileFolderService.searchSimple(test, "EXP001234");
    	test = fileFolderService.searchSimple(test, "2016");
    	test = fileFolderService.searchSimple(test, "11");
    	test = fileFolderService.searchSimple(test, "27");
    	test = fileFolderService.searchSimple(test, nodeExpedient.getName());
    	assertNotNull(test);
    	assertEquals(expId, test.getId());
    	assertEquals(nodeExpedient.getName(), nodeService.getProperty(test, ConstantUtils.PROP_NAME));
    }

    @Test
    public void testCreateNodeExpedientWithClassificationTableSystemDate() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);

    	NodeRef test = fileFolderService.searchSimple(TestUtils.rootDM, "Sanidad");
    	test = fileFolderService.searchSimple(test, "EXP001234");
    	test = fileFolderService.searchSimple(test, String.format("%04d", Calendar.getInstance().get(Calendar.YEAR)));
    	test = fileFolderService.searchSimple(test, String.format("%02d", Calendar.getInstance().get(Calendar.MONTH)+1));
    	test = fileFolderService.searchSimple(test, String.format("%02d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
    	test = fileFolderService.searchSimple(test, nodeExpedient.getName());
    	assertNotNull(test);
    	assertEquals(expId, test.getId());
    	assertEquals(nodeExpedient.getName(), nodeService.getProperty(test, ConstantUtils.PROP_NAME));
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeExpedientWithClassificationTableUnknownSerieDocumental() throws GdibException{
    	nodeExpedient.getProperties().get(
    			nodeExpedient.getProperties().indexOf(
    					new Property(ConstantUtils.PROP_COD_CLASIFICACION_QNAME, null))).setValue("unknown");
    	repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    }

    @Test
    public void testCreateNodeExpedientWithTemplate() throws GdibException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException{
    	nodeExpedient.getProperties().get(
    			nodeExpedient.getProperties().indexOf(
    					new Property(ConstantUtils.PROP_COD_CLASIFICACION_QNAME, null))).setValue("EXP004321");
    	String nodeId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);

    	NodeRef exp = utils.toNodeRef(nodeId);
    	assertNotNull(fileFolderService.searchSimple(exp, "1 Carpeta1"));
    	assertNotNull(fileFolderService.searchSimple(exp, "2 Carpeta2"));
    	assertNotNull(fileFolderService.searchSimple(exp, "3 Carpeta3"));
    	NodeRef expFolder1 = fileFolderService.searchSimple(exp, "1 Carpeta1");
    	assertNotNull(fileFolderService.searchSimple(expFolder1, "1.1 Carpeta11"));
    	assertNotNull(fileFolderService.searchSimple(expFolder1, "1.1 Carpeta12"));
    	NodeRef expFolder3 = fileFolderService.searchSimple(exp, "3 Carpeta3");
    	assertNotNull(fileFolderService.searchSimple(expFolder3, "1.3 Carpeta13"));
    	NodeRef expFolder13 = fileFolderService.searchSimple(expFolder3, "1.3 Carpeta13");
    	assertNotNull(fileFolderService.searchSimple(expFolder13, "1.3.1 Carpeta131"));
    }

    @Test
    public void testCreateNodeExpedientWithoutTemplate() throws GdibException{
    	String nodeId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);

    	assertTrue(nodeService.getChildAssocs(utils.toNodeRef(nodeId)).size()==0);
    }

    @Test
    public void testCreateNodeExpedientPhysical() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodePhysicalExpedient, null, gdibHeader);
    	Node expNode = repositoryServiceSoap.getNode(expId, false, false, gdibHeader);
    	assertNotNull(expNode);

    	List<Property> prop = utils.filterCalculatedProperties(expNode.getProperties());
    	assertNotNull(utils.getProperty(prop, ConstantUtils.PROP_LOC_ARCHIVO_GENERAL_QNAME));
    	assertNotNull(utils.getProperty(prop, ConstantUtils.PROP_LOC_ARCHIVO_CENTRAL_QNAME));
    	assertEquals("archivo general", utils.getProperty(prop, ConstantUtils.PROP_LOC_ARCHIVO_GENERAL_QNAME));
    	assertEquals("archivo central", utils.getProperty(prop, ConstantUtils.PROP_LOC_ARCHIVO_CENTRAL_QNAME));
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeExpedientPhysicalWithoutLocationGeneral() throws GdibException{
    	nodePhysicalExpedient.getProperties().remove(new Property(ConstantUtils.PROP_LOC_ARCHIVO_GENERAL_QNAME));
    	repositoryServiceSoap.createNode(nodePhysicalExpedient, null, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeExpedientPhysicalWithoutLocationCentral() throws GdibException{
    	nodePhysicalExpedient.getProperties().remove(new Property(ConstantUtils.PROP_LOC_ARCHIVO_CENTRAL_QNAME));
    	repositoryServiceSoap.createNode(nodePhysicalExpedient, null, gdibHeader);
    }

    @Test
    public void testCreateNodeDocumentPhysical() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	Node node = repositoryServiceSoap.createAndGetNode(nodePhysicalDocumento, expId, gdibHeader);

    	assertNull(node.getContent());
    	assertNull(node.getSign());
    	List<Property> prop = utils.filterCalculatedProperties(node.getProperties());
    	assertNotNull(utils.getProperty(prop, ConstantUtils.PROP_LOC_ARCHIVO_GENERAL_QNAME));
    	assertNotNull(utils.getProperty(prop, ConstantUtils.PROP_LOC_ARCHIVO_CENTRAL_QNAME));
    	assertEquals("archivo general", utils.getProperty(prop, ConstantUtils.PROP_LOC_ARCHIVO_GENERAL_QNAME));
    	assertEquals("archivo central", utils.getProperty(prop, ConstantUtils.PROP_LOC_ARCHIVO_CENTRAL_QNAME));
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeDocumentPhysicalWithoutLocationGeneral() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	nodePhysicalDocumento.getProperties().remove(new Property(ConstantUtils.PROP_LOC_ARCHIVO_GENERAL_QNAME));
    	repositoryServiceSoap.createAndGetNode(nodePhysicalDocumento, expId, gdibHeader);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeDocumentPhysicalWithoutLocationCentral() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, gdibHeader);
    	nodePhysicalDocumento.getProperties().remove(new Property(ConstantUtils.PROP_LOC_ARCHIVO_CENTRAL_QNAME));
    	repositoryServiceSoap.createAndGetNode(nodePhysicalDocumento, expId, gdibHeader);
    }

    private boolean checkAspects(final Set<QName> aspects, final List<String> aspectList){
    	boolean found = false;
    	// recorrolo los valores de aspecto a buscar
    	for (String aspect : aspectList) {
    		found = foundAspect(aspects, aspect);
		}
    	return found;
    }

    private boolean foundAspect(final Set<QName> aspects, final String aspect){
    	Iterator<QName> it = aspects.iterator();
		// recorro la lista de aspectos del nodo creado en alfresco
    	while(it.hasNext()){
    		String ap = it.next().getLocalName();
    		if(aspect.contains(ap)){
    			return true;
    		}
    	}
    	return false;
    }

}

