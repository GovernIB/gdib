package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
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
@ContextConfiguration("classpath:alfresco/context/repositoryService-linkNode-context.xml")
public class RepositoryServiceLinkNodeTest {

	static Logger log = Logger.getLogger(RepositoryServiceLinkNodeTest.class);

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Autowired
    @Qualifier("PermissionService")
    private PermissionService permissionService;

    @Autowired
    @Qualifier("gdibUtils")
    private GdibUtils utils;

    @Autowired
    private Node nodeDocument;
    @Autowired
    private Node nodeExpedient;
    @Autowired
    private Node nodeExpedient2;

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
	public void testLinkNodeDocument() throws GdibException {
    	String expediente1 = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String expediente2 = repositoryServiceSoap.createNode(nodeExpedient2, TestUtils.rootDM.getId(), null);
    	String documento = repositoryServiceSoap.createNode(nodeDocument, expediente1, null);

    	assertEquals(0, nodeService.getChildAssocs(utils.toNodeRef(expediente2)).size());
		repositoryServiceSoap.linkNode(expediente2, documento, "reference", null);
		assertEquals(1, nodeService.getChildAssocs(utils.toNodeRef(expediente2)).size());
		ChildAssociationRef child = nodeService.getChildAssocs(utils.toNodeRef(expediente2)).get(0);
		assertEquals(documento, child.getChildRef().getId());
	}

    @Test
	public void testLinkNodeDocumentOtherUser() throws GdibException {
    	String expediente1 = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String expediente2 = repositoryServiceSoap.createNode(nodeExpedient2, TestUtils.rootDM.getId(), null);
    	String documento = repositoryServiceSoap.createNode(nodeDocument, expediente1, null);

    	// le doy los permisos de consumidor al origen y contribuidor al destino del enlazado
    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(documento),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_READ,
    			null);
    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(expediente2),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);

    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
    	assertEquals(0, nodeService.getChildAssocs(utils.toNodeRef(expediente2)).size());
		repositoryServiceSoap.linkNode(expediente2, documento, "reference", null);
		assertEquals(1, nodeService.getChildAssocs(utils.toNodeRef(expediente2)).size());
		ChildAssociationRef child = nodeService.getChildAssocs(utils.toNodeRef(expediente2)).get(0);
		assertEquals(documento, child.getChildRef().getId());
	}

    @Test(expected = GdibException.class)
	public void testLinkNodeDocumentNoPermissionSource() throws GdibException {
    	String expediente1 = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String expediente2 = repositoryServiceSoap.createNode(nodeExpedient2, TestUtils.rootDM.getId(), null);
    	String documento = repositoryServiceSoap.createNode(nodeDocument, expediente1, null);

    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(documento),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_READ,
    			null);

    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		repositoryServiceSoap.linkNode(expediente2, documento, "reference", null);
	}

    @Test(expected = GdibException.class)
	public void testLinkNodeDocumentNoPermissionTarget() throws GdibException {
    	String expediente1 = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String expediente2 = repositoryServiceSoap.createNode(nodeExpedient2, TestUtils.rootDM.getId(), null);
    	String documento = repositoryServiceSoap.createNode(nodeDocument, expediente1, null);

    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(expediente2),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);

    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		repositoryServiceSoap.linkNode(expediente2, documento, "reference", null);
	}

    @Test
	public void testCopyNodeDocument() throws GdibException {
    	String expediente1 = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String expediente2 = repositoryServiceSoap.createNode(nodeExpedient2, TestUtils.rootDM.getId(), null);
    	String documento = repositoryServiceSoap.createNode(nodeDocument, expediente1, null);


    	assertEquals(0, nodeService.getChildAssocs(utils.toNodeRef(expediente2)).size());
		repositoryServiceSoap.linkNode(expediente2, documento, "copy", null);
		assertEquals(1, nodeService.getChildAssocs(utils.toNodeRef(expediente2)).size());
		ChildAssociationRef child = nodeService.getChildAssocs(utils.toNodeRef(expediente2)).get(0);
		assertNotEquals(documento, child.getChildRef().getId());
	}

    @Test
	public void testCopyNodeDocumentOtherUser() throws GdibException {
    	String expediente1 = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String expediente2 = repositoryServiceSoap.createNode(nodeExpedient2, TestUtils.rootDM.getId(), null);
    	String documento = repositoryServiceSoap.createNode(nodeDocument, expediente1, null);

    	// le doy los permisos de consumidor al origen y contribuidor al destino del enlazado
    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(documento),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_READ,
    			null);
    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(expediente2),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);

    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
    	assertEquals(0, nodeService.getChildAssocs(utils.toNodeRef(expediente2)).size());
		repositoryServiceSoap.linkNode(expediente2, documento, "copy", null);
		assertEquals(1, nodeService.getChildAssocs(utils.toNodeRef(expediente2)).size());
		ChildAssociationRef child = nodeService.getChildAssocs(utils.toNodeRef(expediente2)).get(0);
		assertNotEquals(documento, child.getChildRef().getId());
	}

    @Test(expected = GdibException.class)
    @DirtiesContext
	public void testLinkNodeParentNotFolder() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String documento = repositoryServiceSoap.createNode(nodeDocument, exp, null);

    	String exp2 = repositoryServiceSoap.createNode(nodeExpedient2, TestUtils.rootDM.getId(), null);
    	nodeDocument.setName("document2.txt");
    	String documento2 = repositoryServiceSoap.createNode(nodeDocument, exp2, null);
		repositoryServiceSoap.linkNode(documento, documento2, "copy", null);
	}

    @Test(expected = GdibException.class)
    public void testCopyNodeNoPermissionSource() throws GdibException{
    	String expediente1 = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String expediente2 = repositoryServiceSoap.createNode(nodeExpedient2, TestUtils.rootDM.getId(), null);
    	String documento = repositoryServiceSoap.createNode(nodeDocument, expediente1, null);

    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(expediente2),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);

    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		repositoryServiceSoap.linkNode(expediente2, documento, "copy", null);
    }

    @Test(expected = GdibException.class)
    public void testCopyNodeNoPermissionTarget() throws GdibException{
    	String expediente1 = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String expediente2 = repositoryServiceSoap.createNode(nodeExpedient2, TestUtils.rootDM.getId(), null);
    	String documento = repositoryServiceSoap.createNode(nodeDocument, expediente1, null);

    	repositoryServiceSoap.authorizeNode(
    			Arrays.asList(documento),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_READ,
    			null);

    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		repositoryServiceSoap.linkNode(expediente2, documento, "copy", null);
    }

}

