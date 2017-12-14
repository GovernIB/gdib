package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@ContextConfiguration("classpath:alfresco/context/repositoryService-lockNode-context.xml")
public class RepositoryServiceLockNodeTest {

	static Logger log = Logger.getLogger(RepositoryServiceLockNodeTest.class);

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    @Qualifier("gdibUtils")
    private GdibUtils utils;

    @Autowired
    private Node nodeDocument;
    @Autowired
    private Node nodeExpedient;
    @Autowired
    private Node cmFolder;

    @Autowired
    private TestUtils testUtils;

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
	public void testLockUnlockNodeExpedient() throws GdibException {
    	Node expedient = repositoryServiceSoap.createAndGetNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	NodeRef exp = utils.toNodeRef(expedient.getId());
    	repositoryServiceSoap.lockNode(expedient.getId(), null);
    	assertTrue(utils.isNodeLocked(exp));
    	repositoryServiceSoap.unlockNode(expedient.getId(), null);
    	assertFalse(utils.isNodeLocked(exp));

    	// realizo la misma operacion con otros dos usuario (no creadores) pero con permisos
    	testUtils.createUser(TestUtils.USER_TEST+"2");
    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(TestUtils.rootDM.getId()),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);
    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(TestUtils.rootDM.getId()),
    			Arrays.asList(TestUtils.USER_TEST+"2"),
    			ConstantUtils.PERMISSION_WRITE,
    			null);
    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
    	repositoryServiceSoap.lockNode(expedient.getId(), null);
    	assertTrue(utils.isNodeLocked(exp));
    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST+"2");
    	repositoryServiceSoap.unlockNode(expedient.getId(), null);
    	assertFalse(utils.isNodeLocked(exp));
	}

   	@Test
   	public void testLockUnlockNodeDocument() throws GdibException {
   		Node expedient = repositoryServiceSoap.createAndGetNode(nodeExpedient, TestUtils.rootDM.getId(), null);
       	Node nodeDoc = repositoryServiceSoap.createAndGetNode(nodeDocument, expedient.getId(), null);
       	repositoryServiceSoap.lockNode(nodeDoc.getId(), null);
       	assertTrue(utils.isNodeLocked(utils.toNodeRef(nodeDoc.getId())));
       	repositoryServiceSoap.unlockNode(nodeDoc.getId(), null);
       	assertFalse(utils.isNodeLocked(utils.toNodeRef(nodeDoc.getId())));

       	// realizo la misma operacion con otros dos usuario (no creadores) pero con permisos
    	testUtils.createUser(TestUtils.USER_TEST+"2");
    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(TestUtils.rootDM.getId()),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);
    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(TestUtils.rootDM.getId()),
    			Arrays.asList(TestUtils.USER_TEST+"2"),
    			ConstantUtils.PERMISSION_WRITE,
    			null);
    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
    	repositoryServiceSoap.lockNode(nodeDoc.getId(), null);
       	assertTrue(utils.isNodeLocked(utils.toNodeRef(nodeDoc.getId())));
       	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST+"2");
       	repositoryServiceSoap.unlockNode(nodeDoc.getId(), null);
       	assertFalse(utils.isNodeLocked(utils.toNodeRef(nodeDoc.getId())));
   	}

   	@Test
   	public void testLockNodeSomeDocumentIsLockInExpedient() throws GdibException {
    	Node expedient = repositoryServiceSoap.createAndGetNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	Node folder = repositoryServiceSoap.createAndGetNode(cmFolder, expedient.getId(), null);
       	Node nodeDoc = repositoryServiceSoap.createAndGetNode(nodeDocument, folder.getId(), null);
       	repositoryServiceSoap.lockNode(nodeDoc.getId(), null);

       	assertTrue(utils.isSomeoneLockedDown(utils.toNodeRef(expedient.getId())));
   	}

   	@Test
   	public void testLockNodeLocked() throws GdibException {
   		Node expedient = repositoryServiceSoap.createAndGetNode(nodeExpedient, TestUtils.rootDM.getId(), null);
       	Node nodeDoc = repositoryServiceSoap.createAndGetNode(nodeDocument, expedient.getId(), null);
       	repositoryServiceSoap.lockNode(nodeDoc.getId(), null);
       	assertTrue(utils.isNodeLocked(utils.toNodeRef(nodeDoc.getId())));
       	repositoryServiceSoap.lockNode(nodeDoc.getId(), null);
       	assertTrue(utils.isNodeLocked(utils.toNodeRef(nodeDoc.getId())));
   	}

	@Test(expected = GdibException.class)
	public void testLockNodeNoPermission() throws GdibException {
		String expedient = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String node = repositoryServiceSoap.createNode(nodeExpedient, expedient, null);
    	assertFalse(utils.isNodeLocked(utils.toNodeRef(node)));

    	AuthenticationUtil.setRunAsUser(TestUtils.USER_TEST);
    	repositoryServiceSoap.lockNode(expedient, null);
	}

	@Test(expected = GdibException.class)
	public void testLockNodeNoExits() throws GdibException {
    	repositoryServiceSoap.lockNode("12345678-1234-1234-1234-123456789012", null);
	}

	@Test(expected = GdibException.class)
	public void testUnLockNodeNoPermission() throws GdibException {
    	String expedient = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String node = repositoryServiceSoap.createNode(nodeExpedient, expedient, null);
    	repositoryServiceSoap.lockNode(node, null);
    	assertTrue(utils.isNodeLocked(utils.toNodeRef(node)));

    	AuthenticationUtil.setRunAsUser(TestUtils.USER_TEST);
    	repositoryServiceSoap.unlockNode(node, null);
	}

   	@Test
   	public void testUnLockNodeUnLocked() throws GdibException {
   		String expedient = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
       	String nodeDoc = repositoryServiceSoap.createNode(nodeDocument, expedient, null);
       	repositoryServiceSoap.unlockNode(nodeDoc, null);
       	assertFalse(utils.isNodeLocked(utils.toNodeRef(nodeDoc)));
       	repositoryServiceSoap.unlockNode(nodeDoc, null);
       	assertFalse(utils.isNodeLocked(utils.toNodeRef(nodeDoc)));
   	}
}

