package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
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

/**
 * Test para {@link es.caib.gdib.ws.impl.RepositoryServiceSoapPortImpl#authorizeNode(List, List, String, es.caib.gdib.ws.common.types.GdibHeader)}
 * y para {@link es.caib.gdib.ws.impl.RepositoryServiceSoapPortImpl#removeAuthority(List, List, es.caib.gdib.ws.common.types.GdibHeader)}
 *
 * - autorizar nodo para leerlo, miro que no tengo acceso en un nodo, y luego se lo doy y ya puede leerlo
 * - autorizar nodo, con otro usuario no dueño del nodo
 * - autorizar nodo con un permiso que no existe - excepcion
 * - autorizar nodo bloqueado - excepcion
 * - autorizar nodo que esta dentro de un expediente bloqueado - excepcion
 * - autorizar nodo a una autoridad que no existe - excepcion
 * - autorizar nodos que no existen. O con uuid nulos, blancos o no validos - excepcion
 * @author RICOH
 *
 */
@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/repositoryService-context.xml")
public class RepositoryServiceAuthorizeNodeTest {

    static Logger log = Logger.getLogger(RepositoryServiceAuthorizeNodeTest.class);

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    @Qualifier("gdibUtils")
    private GdibUtils utils;

    @Autowired
    @Qualifier("PermissionService")
    private PermissionService permissionService;

    @Autowired
    @Qualifier("AuthorityService")
    private AuthorityService authorityService;

    @Autowired
    private Node nodeDocument;

    @Autowired
    private Node nodeExpedient;

    @Autowired
    private TestUtils testUtils;

    private List<String> nodeIds;
    private List<String> authorities;

    @Before
    public void configureUp() throws NotSupportedException, SystemException{
    	// preparo entorno de pruebas
    	testUtils.configureUp();
    	utils.setRootDM(TestUtils.rootDM.getId());
    	utils.setRootCT(TestUtils.rootCT.getId());
    	utils.setRootTemplate(TestUtils.rootTemplate.getId());
    	nodeIds = new ArrayList<String>();
        authorities = new ArrayList<String>();
    }

    @After
    public void configureDown() throws IllegalStateException, SecurityException, SystemException{
    	testUtils.configureDown();
    }

    @Test
    @DirtiesContext
	public void testAuthorizeNodeRead() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		nodeDocument.setName("document2.txt");
		String nodeId2 = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		nodeDocument.setName("document3.txt");
		String nodeId3 = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.READ));
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId2), PermissionService.READ));
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId3), PermissionService.READ));

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.ADMIN_USER);

		nodeIds.add(nodeId);
		nodeIds.add(nodeId2);
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, ConstantUtils.PERMISSION_READ, null);

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.READ));
		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId2), PermissionService.READ));
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId3), PermissionService.READ));
	}

    @Test(expected = GdibException.class)
	public void testAuthorizeNodePermissionNoExits() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(nodeId);
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, ConstantUtils.PERMISSION_READ+"aaaaaaaa", null);
	}

    @Test
	public void testAuthorizeNodeOtherUser() throws GdibException {
    	String user = TestUtils.USER_TEST + "2";
    	testUtils.createUser(user);

    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		repositoryServiceSoap.authorizeNode(
    			Arrays.asList(nodeId),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);

		nodeIds.add(nodeId);
		authorities.add(user);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, ConstantUtils.PERMISSION_READ, null);

		AuthenticationUtil.setFullyAuthenticatedUser(user);
		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.READ));
	}

    @Test(expected = GdibException.class)
	public void testAuthorizeNodeWihtoutPermission() throws GdibException {
    	String user = TestUtils.USER_TEST + "2";
    	testUtils.createUser(user);

    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);

		nodeIds.add(nodeId);
		authorities.add(user);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, PermissionService.READ, null);
	}

	@Test(expected = GdibException.class)
	public void testAuthorizeNodeLockedDocument() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		repositoryServiceSoap.lockNode(nodeId, null);

		nodeIds.add(nodeId);
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, PermissionService.READ, null);
	}

	@Test(expected = GdibException.class)
	public void testAuthorizeNodeExpedientWithDocumentLocked() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		repositoryServiceSoap.lockNode(nodeId, null);

		nodeIds.add(exp);
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, PermissionService.READ, null);
	}

    @Test(expected = GdibException.class)
	public void testAuthorizeNodeAuthorityNotExits() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		String user = TestUtils.USER_TEST + "aaaaaa";
		assertFalse(authorityService.authorityExists(user));
		nodeIds.add(nodeId);
		authorities.add(user);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, PermissionService.READ, null);
	}

    @Test(expected = GdibException.class)
	public void testAuthorizeNodeNodeIdsNotExits() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(nodeId);
		nodeIds.add("12345678-1234-1234-1234-123456789012");
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, PermissionService.READ, null);
	}

    @Test(expected = GdibException.class)
	public void testAuthorizeNodeNodeIdsUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(nodeId);
		nodeIds.add("inventado");
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, PermissionService.READ, null);
	}

    @Test(expected = GdibException.class)
	public void testAuthorizeNodeNodeIdsNull() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(nodeId);
		nodeIds.add(null);
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, PermissionService.READ, null);
	}

    @Test(expected = GdibException.class)
	public void testAuthorizeNodeNodeIdsEmpty() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(nodeId);
		nodeIds.add("");
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, PermissionService.READ, null);
	}

    @Test
    public void testAutorizeNodeWrite() throws GdibException{
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		nodeDocument.setName("document2.txt");
		String nodeId2 = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(exp);
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, ConstantUtils.PERMISSION_READ, null);

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.READ));
		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId2), PermissionService.READ));
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.WRITE));
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId2), PermissionService.WRITE));

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.ADMIN_USER);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, ConstantUtils.PERMISSION_WRITE, null);

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.WRITE));
		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId2), PermissionService.WRITE));
    }

    @Test
    public void testRemoveAuthorityNode() throws GdibException{
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(exp);
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.authorizeNode(nodeIds, authorities, ConstantUtils.PERMISSION_WRITE, null);

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.READ));
		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.WRITE));

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.ADMIN_USER);
		repositoryServiceSoap.removeAuthority(nodeIds, authorities, null);

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.WRITE));
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.WRITE));
    }

    @Test
	public void testRemoveAuthorityNodeOtherUser() throws GdibException {
    	String user = TestUtils.USER_TEST + "2";
    	testUtils.createUser(user);

    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		repositoryServiceSoap.authorizeNode(
    			Arrays.asList(nodeId),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);

		repositoryServiceSoap.authorizeNode(
    			Arrays.asList(nodeId),
    			Arrays.asList(user),
    			ConstantUtils.PERMISSION_WRITE,
    			null);

		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.READ));
		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.WRITE));

		nodeIds.add(nodeId);
		authorities.add(user);
		repositoryServiceSoap.removeAuthority(nodeIds, authorities, null);

		AuthenticationUtil.setFullyAuthenticatedUser(user);
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.READ));
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.WRITE));
	}

    @Test(expected = GdibException.class)
	public void testRemoveAuthorityNodeWihtoutPermission() throws GdibException {
    	String user = TestUtils.USER_TEST + "2";
    	testUtils.createUser(user);

    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);

		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.READ));
		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(nodeId), PermissionService.WRITE));
		nodeIds.add(nodeId);
		authorities.add(user);
		repositoryServiceSoap.removeAuthority(nodeIds, authorities, null);
	}

    @Test(expected = GdibException.class)
	public void testRemoveAuthorityNodeLockedDocument() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		repositoryServiceSoap.lockNode(nodeId, null);

		nodeIds.add(nodeId);
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.removeAuthority(nodeIds, authorities, null);
	}

	@Test(expected = GdibException.class)
	public void testRemoveAuthorityNodeExpedientWithDocumentLocked() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		repositoryServiceSoap.lockNode(nodeId, null);

		nodeIds.add(exp);
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.removeAuthority(nodeIds, authorities, null);
	}

    @Test(expected = GdibException.class)
	public void testRemoveAuthorityNodeAuthorityNotExits() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		String user = TestUtils.USER_TEST + "aaaaaa";
		assertFalse(authorityService.authorityExists(user));
		nodeIds.add(nodeId);
		authorities.add(user);
		repositoryServiceSoap.removeAuthority(nodeIds, authorities, null);
	}

    @Test(expected = GdibException.class)
	public void testRemoveAuthorityNodeNodeIdsNotExits() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(nodeId);
		nodeIds.add("12345678-1234-1234-1234-123456789012");
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.removeAuthority(nodeIds, authorities, null);
	}

    @Test(expected = GdibException.class)
	public void testRemoveAuthorityNodeNodeIdsUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(nodeId);
		nodeIds.add("inventado");
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.removeAuthority(nodeIds, authorities, null);
	}

    @Test(expected = GdibException.class)
	public void testRemoveAuthorityNodeNodeIdsNull() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(nodeId);
		nodeIds.add(null);
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.removeAuthority(nodeIds, authorities, null);
	}

    @Test(expected = GdibException.class)
	public void testRemoveAuthorityNodeNodeIdsEmpty() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, null, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		nodeIds.add(nodeId);
		nodeIds.add("");
		authorities.add(TestUtils.USER_TEST);
		repositoryServiceSoap.removeAuthority(nodeIds, authorities, null);
	}
}

