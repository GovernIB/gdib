package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
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
import es.caib.gdib.ws.common.types.header.GdibRestriction;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/repositoryService-searchNodeTest-context.xml")
public class RepositoryServiceSearchTest {

    static Logger log = Logger.getLogger(RepositoryServiceSearchTest.class);

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private GdibUtils utils;

    @Autowired
    @Qualifier("VersionService")
    private VersionService versionService;

    @Autowired
    @Qualifier("FileFolderService")
    private FileFolderService fileFolderService;

    @Autowired
    @Qualifier("nodeLocatorService")
    private NodeLocatorService nodeLocatorService;

    @Autowired
    private GdibHeader gdibHeader;

    @Autowired
    private Node nodeDocument;
    @Autowired
    private Node nodeDocument2;
    @Autowired
    private Node nodeExpedient;
    @Autowired
    private Node cmFolder;
    @Autowired
    private Node nodeDocumentSign;

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

    /**
     * Crear un nodo y devolver el nodo creado
     *
     * @throws GdibException
     */
    @Test
    public void testCreateAndGetNode() throws GdibException{
        Node node = repositoryServiceSoap.createAndGetNode(nodeDocument, TestUtils.rootDM.getId(), gdibHeader);
        Node nodeSearch = repositoryServiceSoap.getNode(node.getId(), false, false, gdibHeader);

        assertEquals(nodeSearch.getId(), node.getId());
        assertEquals(nodeSearch.getName(), node.getName());
        assertEquals(utils.formatQname(nodeSearch.getType()), node.getType());
    }

    /**
     * Crear un nodo y y obtenerlo con otro usuario que tiene permisos para obtenerlo
     *
     * @throws GdibException
     */
    @Test
    public void testGetNodeOtherUser() throws GdibException{
        String nodeId = repositoryServiceSoap.createNode(nodeDocument, TestUtils.rootDM.getId(), null);
        repositoryServiceSoap.authorizeNode(
    			Arrays.asList(nodeId),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_READ,
    			null);

    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
        Node nodeSearch = repositoryServiceSoap.getNode(nodeId, false, false, null);

        assertEquals(nodeId, nodeSearch.getId());
        assertEquals(nodeDocument.getName(), nodeSearch.getName());
        assertEquals(utils.formatQname(nodeDocument.getType()), nodeSearch.getType());
    }

    /**
     * Crear un nodo y y obtenerlo con otro usuario que NO tiene permisos para obtenerlo
     *
     * @throws GdibException
     */
    @Test(expected = GdibException.class)
    public void testGetNodeNoPermissino() throws GdibException{
        String nodeId = repositoryServiceSoap.createNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
        Node nodeSearch = repositoryServiceSoap.getNode(nodeId, false, false, null);

        assertEquals(nodeId, nodeSearch.getId());
        assertEquals(nodeDocument.getName(), nodeSearch.getName());
        assertEquals(utils.formatQname(nodeDocument.getType()), nodeSearch.getType());
    }

    /**
     * Obtengo un nodo sin contenido y sin firma
     *
     * @throws GdibException
     */
    @Test
    public void testGetNodeEmpty() throws GdibException{
        Node node = repositoryServiceSoap.createAndGetNode(nodeDocumentSign, TestUtils.rootDM.getId(), null);
        Node nodeSearch = repositoryServiceSoap.getNode(node.getId(), false, false, null);

        assertEquals(nodeDocument.getName(), nodeSearch.getName());
        assertEquals(node.getId(), nodeSearch.getId());
        assertEquals(node.getType(), nodeSearch.getType());
        assertNull(nodeSearch.getContent());
        assertNull(nodeSearch.getSign());
    }

    /**
     * Obtengo un nodo con contenido y sin firma
     *
     * @throws GdibException
     */
    @Test
    public void testGetNodeWithContent() throws GdibException{
        Node node = repositoryServiceSoap.createAndGetNode(nodeDocumentSign, TestUtils.rootDM.getId(), null);
        Node nodeSearch = repositoryServiceSoap.getNode(node.getId(), true, false, null);

        assertEquals(node.getName(), nodeSearch.getName());
        assertNotNull(nodeSearch.getContent());
        assertNull(nodeSearch.getSign());
    }

    /**
     * Obtengo un nodo sin contenido y con firma
     *
     * @throws GdibException
     */
    @Test
    public void testGetNodeWithSign() throws GdibException{
        Node node = repositoryServiceSoap.createAndGetNode(nodeDocumentSign, TestUtils.rootDM.getId(), null);
        Node nodeSearch = repositoryServiceSoap.getNode(node.getId(), false, true, null);

        assertEquals(node.getName(), nodeSearch.getName());
        assertNull(nodeSearch.getContent());
        assertNotNull(nodeSearch.getSign());
    }

    /**
     * Obtengo un nodo con contenido y firma
     *
     * @throws GdibException
     */
    public void testGetNodeWithContentAndSign() throws GdibException{
        Node node = repositoryServiceSoap.createAndGetNode(nodeDocumentSign, TestUtils.rootDM.getId(), null);
        Node nodeSearch = repositoryServiceSoap.getNode(node.getId(), true, true, null);

        assertEquals(node.getName(), nodeSearch.getName());
        assertNotNull(nodeSearch.getContent());
        assertNotNull(nodeSearch.getSign());
    }

    /**
     * Crear un nodo y devolver el nodo creado del tipo expediente
     *
     * @throws GdibException
     */
    @Test
    public void testCreateAndGetNodeExpedient() throws GdibException{
        Node node = repositoryServiceSoap.createAndGetNode(nodeExpedient, TestUtils.rootDM.getId(), null);

        assertEquals(nodeExpedient.getName(), node.getName());
        assertEquals(utils.formatQname(nodeExpedient.getType()), node.getType());
    }

    /**
     * Crear y obtener un nodo expediente, intento recuperar contenido y firma y no llegan por no tenerlo
     *
     * @throws GdibException
     */
    @Test
    public void testGetNodeExpedientWithContent() throws GdibException{
        String nodeId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
        Node nodeSearch = repositoryServiceSoap.getNode(nodeId, true, true, null);

        assertEquals(nodeExpedient.getName(), nodeSearch.getName());
        assertEquals(utils.formatQname(nodeExpedient.getType()), nodeSearch.getType());
        assertEquals(null, nodeSearch.getContent());
        assertEquals(null, nodeSearch.getSign());
    }

    /**
     * Busco un nodo con un id no valido
     *
     * @throws GdibException
     */
    @Test(expected = GdibException.class)
    public void testGetNodeUnknownId() throws GdibException{
        repositoryServiceSoap.getNode("aaaaaaaaaa", false, false, null);
    }

    /**
     * Busco un nodo con un id vacio
     *
     * @throws GdibException
     */
    @Test(expected = GdibException.class)
    public void testGetNodeEmptyId() throws GdibException{
        repositoryServiceSoap.getNode("", false, false, null);
    }

    /**
     * Busco un nodo con un id nulo
     *
     * @throws GdibException
     */
    @Test(expected = GdibException.class)
    public void testGetNodeNullId() throws GdibException{
        repositoryServiceSoap.getNode(null, false, false, null);
    }

    /**
     * Busco un nodo con un id valido, pero el nodo no existe
     *
     * @throws GdibException
     */
    @Test(expected = GdibException.class)
    public void testGetNodeNotExits() throws GdibException{
        repositoryServiceSoap.getNode("12345678-1234-1234-1234-123456789012", false, false, null);
    }

    /**
     * Busquedas en el repositorio. Casos:
     * 		documento
     * 		expediente
     * 		documento por nombre
     *
     * @throws GdibException
     */
    @Test
    public void testSearchNode() throws GdibException{
    	int preOperationDoc = repositoryServiceSoap.searchNode("+TYPE:\"eni:documento\"", 0, null).getResultados().size();
    	int preOperationExp = repositoryServiceSoap.searchNode("+TYPE:\"eni:expediente\"", 0, null).getResultados().size();
    	int preOperationDocSearch = repositoryServiceSoap.searchNode("+TYPE:\"eni:documento\" +@cm\\:name:\"document.txt\"", 0,null).getResultados().size();
        repositoryServiceSoap.createNode(nodeDocument, TestUtils.rootDM.getId(), null);
        repositoryServiceSoap.createNode(nodeDocument2, TestUtils.rootDM.getId(), null);
        repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);

        // Sin paginacion
        List<Node> nodes = repositoryServiceSoap.searchNode("+TYPE:\"eni:documento\"", 0, null).getResultados();
        assertEquals(preOperationDoc+2, nodes.size());

        // Sin paginacion
        List<Node> nodesExpedient = repositoryServiceSoap.searchNode("+TYPE:\"eni:expediente\"", 0, null).getResultados();
        assertEquals(preOperationExp+1, nodesExpedient.size());

        // Sin paginacion
        List<Node> nodeName = repositoryServiceSoap.searchNode("+TYPE:\"eni:documento\" +@cm\\:name:\"document.txt\"", 0,null).getResultados();
        assertEquals(preOperationDocSearch+1, nodeName.size());
    }


    /**
     * Crear un documento, recuperar el documento por el path relativo
     *
     * @throws GdibException
     */
    @Test
    public void testGetNodeRelativePath() throws GdibException{
        String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
        String folder = repositoryServiceSoap.createNode(cmFolder, exp, null);
        String documentId = repositoryServiceSoap.createNode(nodeDocument, folder, null);

        Node nodeSearch = repositoryServiceSoap.getNode(exp + "/folder/" + nodeDocument.getName(), false, false, null);

        assertEquals(documentId, nodeSearch.getId());
        assertEquals(nodeDocument.getName(), nodeSearch.getName());
        assertEquals(utils.formatQname(nodeDocument.getType()), nodeSearch.getType());
        assertEquals(null, nodeSearch.getContent());
        assertEquals(null, nodeSearch.getSign());
    }

    @Test
    public void testGetNodeVersion() throws GdibException{
        String expedient = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
        String documentId = repositoryServiceSoap.createNode(nodeDocument, expedient, null);

        versionService.ensureVersioningEnabled(utils.toNodeRef(documentId), null);
        versionService.createVersion(utils.toNodeRef(documentId), null);
        Map<String, Serializable> verProp = new HashMap<String, Serializable>();
        verProp.put("versionType", VersionType.MAJOR);
        versionService.createVersion(utils.toNodeRef(documentId), verProp);

        assertNotNull(repositoryServiceSoap.getNode("1.1@"+documentId, false, false, null));
        assertNotNull(repositoryServiceSoap.getNode("2.0@"+documentId, false, false, null));
    }


    @Test(expected = GdibException.class)
    public void testGetNodeNotInDM() throws GdibException{
    	NodeRef root = fileFolderService.create(nodeLocatorService.getNode("companyhome", null, null), "test", ContentModel.TYPE_FOLDER).getNodeRef();
        repositoryServiceSoap.getNode(root.getId(), false, false, null);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeExpedientRestriction() throws GdibException{
    	GdibHeader header = new GdibHeader();
    	GdibRestriction restriction = new GdibRestriction();
    	List<String> types = new ArrayList<String>();
    	types.add("eni:documento");
		restriction.setTypes(types);
    	header.setGdibRestriction(restriction);
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, header);
		repositoryServiceSoap.getNode(expId, false, false, header);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeDocumentRestriction() throws GdibException{
    	GdibHeader header = new GdibHeader();
    	GdibRestriction restriction = new GdibRestriction();
    	List<String> types = new ArrayList<String>();
    	types.add("eni:expediente");
		restriction.setTypes(types);
    	header.setGdibRestriction(restriction);
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, null, header);
    	String docId = repositoryServiceSoap.createNode(nodeDocument, expId, header);
		repositoryServiceSoap.getNode(docId, false, false, header);
    }

}

