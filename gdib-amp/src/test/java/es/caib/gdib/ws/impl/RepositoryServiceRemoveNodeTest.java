package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeService;
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
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/repositoryService-removeNodeTest-context.xml")
public class RepositoryServiceRemoveNodeTest {

    static Logger log = Logger.getLogger(RepositoryServiceRemoveNodeTest.class);

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    @Qualifier("gdibUtils")
    private GdibUtils utils;

    @Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Autowired
    private Node nodeDocument;
    @Autowired
    private Node nodeDraftDocument;
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

    /**
     * Eliminar un nodo
     *
     * @throws GdibException
     */
    @Test
    public void testRemoveDocument() throws GdibException {
        String nodeId = repositoryServiceSoap.createNode(nodeDraftDocument, TestUtils.rootDM.getId(), null);

        repositoryServiceSoap.removeNode(nodeId, null);
        assertFalse(nodeService.exists(utils.toNodeRef(nodeId)));
    }

    @Test
    public void testRemoveNodeOtherUser() throws GdibException {
        String nodeId = repositoryServiceSoap.createNode(nodeDraftDocument, TestUtils.rootDM.getId(), null);

        repositoryServiceSoap.authorizeNode(
    			Arrays.asList(nodeId),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);

        AuthenticationUtil.setRunAsUser(TestUtils.USER_TEST);
        repositoryServiceSoap.removeNode(nodeId, null);
        assertFalse(nodeService.exists(utils.toNodeRef(nodeId)));
    }

    /**
     * Eliminar un nodo, pasando un Id desconocido
     *
     * @throws GdibException
     */
    @Test(expected = GdibException.class)
    public void testRemoveNodeNullId() throws GdibException {
    	repositoryServiceSoap.removeNode(null, null);
    }

    /**
     * Eliminar un nodo, pasando un Id desconocido
     *
     * @throws GdibException
     */
    @Test(expected = GdibException.class)
    public void testRemoveNodeEmptyId() throws GdibException {
    	repositoryServiceSoap.removeNode("", null);
    }

    /**
     * Eliminar un nodo, pasando un Id desconocido
     *
     * @throws GdibException
     */
    @Test(expected = GdibException.class)
    public void testRemoveNodeUnkwonId() throws GdibException {
    	repositoryServiceSoap.removeNode("aaaaaaaaaaaaaa", null);
    }

    /**
     * Eliminar un expediente, con un documento
     *
     * @throws GdibException
     */
    @Test
    public void testRemoveExpedient() throws GdibException {
        String expedientId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
        String documentId = repositoryServiceSoap.createNode(nodeDraftDocument, expedientId, null);

        repositoryServiceSoap.removeNode(expedientId, null);

        assertFalse(nodeService.exists(utils.toNodeRef(expedientId)));
        assertFalse(nodeService.exists(utils.toNodeRef(documentId)));
    }

	@Test
    public void testRemoveDocumentLock() throws GdibException{
    	String documentId = repositoryServiceSoap.createNode(nodeDraftDocument, TestUtils.rootDM.getId(), null);
    	System.out.println("ID: " + documentId);
    	try{
    		repositoryServiceSoap.lockNode(documentId, null);

    		repositoryServiceSoap.removeNode(documentId, null);
    	}catch(GdibException ex){
    		assertTrue(nodeService.exists(utils.checkNodeId(documentId)));
    	}finally{
    		repositoryServiceSoap.unlockNode(documentId, null);
    	}
    }

	@Test(expected = GdibException.class)
    public void testRemoveExpedientLock() throws GdibException{
    	String expedientId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	repositoryServiceSoap.lockNode(expedientId, null);

    	repositoryServiceSoap.removeNode(expedientId, null);
    }

	@Test
    public void testRemoveExpedientWithChildrenLock() throws GdibException{
    	String expedientId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String documentId = repositoryServiceSoap.createNode(nodeDraftDocument, expedientId, null);
    	repositoryServiceSoap.lockNode(documentId, null);
    	try{
    		repositoryServiceSoap.removeNode(expedientId, null);
    	}catch(GdibException ex){
    		assertTrue(nodeService.exists(utils.checkNodeId(expedientId)));
    		assertTrue(nodeService.exists(utils.checkNodeId(documentId)));
    	}
    	repositoryServiceSoap.unlockNode(documentId, null);
    }

    @Test(expected = GdibException.class)
    public void testRemoveNodeNoPermission() throws GdibException{
    	String nodeId = repositoryServiceSoap.createNode(nodeDraftDocument, TestUtils.rootDM.getId(), null);

        AuthenticationUtil.setRunAsUser(TestUtils.USER_TEST);
        repositoryServiceSoap.removeNode(nodeId, null);
    }

    @Test(expected = GdibException.class)
    public void testRemoveExpedientNoPermission() throws GdibException {
        String expedientId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
        repositoryServiceSoap.createNode(nodeDocument, expedientId, null);

        AuthenticationUtil.setRunAsUser(TestUtils.USER_TEST);
        repositoryServiceSoap.removeNode(expedientId, null);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testRemoveFinallyDocument() throws GdibException {
    	nodeDocument.getAspects().remove("eni:borrador");
        String documentId = repositoryServiceSoap.createNode(nodeDocument, TestUtils.rootDM.getId(), null);

        repositoryServiceSoap.removeNode(documentId, null);
    }

}

