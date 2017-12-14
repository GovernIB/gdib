package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.activation.DataHandler;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
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
@ContextConfiguration("classpath:alfresco/context/repositoryService-context.xml")
public class RepositoryServiceExportNodeTest {

    static Logger log = Logger.getLogger(RepositoryServiceExportNodeTest.class);

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    @Qualifier("gdibUtils")
    private GdibUtils utils;

    @Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Autowired
    @Qualifier("PermissionService")
    private PermissionService permissionService;

    @Autowired
    @Qualifier("FileFolderService")
    private FileFolderService fileFolderService;

    @Autowired
    private Node nodeDocument;

    @Autowired
    private Node nodeExpedient;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void configureUp() throws NotSupportedException, SystemException{
    	// preparo entorno de pruebas
    	testUtils.configureUp();
    	utils.setRootDM(TestUtils.rootDM.getId());
    	utils.setRootCT(TestUtils.rootCT.getId());
    	utils.setRootTemplate(TestUtils.rootTemplate.getId());
    	((RepositoryServiceSoapPortImpl)repositoryServiceSoap).setTempFolder(TestUtils.rootTemp.getId());
    }

    @After
    public void configureDown() throws IllegalStateException, SecurityException, SystemException{
    	testUtils.configureDown();
    }

    @Test
    public void testExportNodeAdmin() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	NodeRef exportFolder = fileFolderService.create(utils.toNodeRef(expId), ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME, ConstantUtils.TYPE_FOLDER).getNodeRef();
		repositoryServiceSoap.createNode(nodeDocument, expId, null);

		assertEquals(2, nodeService.countChildAssocs(utils.toNodeRef(expId), true));
		assertNotNull(fileFolderService.searchSimple(utils.toNodeRef(expId), ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME));
		assertNotNull(fileFolderService.searchSimple(utils.toNodeRef(expId), nodeDocument.getName()));
		assertEquals(0, nodeService.countChildAssocs(exportFolder, true));

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.ADMIN_USER);

		DataHandler subExpedientId = repositoryServiceSoap.exportNode(expId, null);

		assertEquals(0, nodeService.countChildAssocs(TestUtils.rootTemp, true));
		assertEquals(1, nodeService.countChildAssocs(exportFolder, true));
		NodeRef subExpedientExport = nodeService.getChildAssocs(exportFolder).get(0).getChildRef();
		assertEquals(subExpedientId, subExpedientExport.getId());
		String subExpedientExportName = (String)nodeService.getProperty(subExpedientExport, ConstantUtils.PROP_NAME);
		assertTrue(subExpedientExportName.startsWith(nodeExpedient.getName()));
		assertNotNull(fileFolderService.searchSimple(subExpedientExport, nodeDocument.getName()));

		assertEquals(ConstantUtils.ESTADO_EXP_E03, (String)nodeService.getProperty(subExpedientExport, ConstantUtils.PROP_ESTADO_ELABORACION_QNAME));
    }

    @Test
    public void testExportNodeUser() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	NodeRef exportFolder = fileFolderService.create(utils.toNodeRef(expId), ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME, ConstantUtils.TYPE_FOLDER).getNodeRef();
		repositoryServiceSoap.createNode(nodeDocument, expId, null);

		assertEquals(2, nodeService.countChildAssocs(utils.toNodeRef(expId), true));
		assertNotNull(fileFolderService.searchSimple(utils.toNodeRef(expId), ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME));
		assertNotNull(fileFolderService.searchSimple(utils.toNodeRef(expId), nodeDocument.getName()));
		assertEquals(0, nodeService.countChildAssocs(exportFolder, true));

		repositoryServiceSoap.authorizeNode(
    			Arrays.asList(expId),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);
		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);

		DataHandler subExpedientId = repositoryServiceSoap.exportNode(expId, null);

		assertEquals(0, nodeService.countChildAssocs(TestUtils.rootTemp, true));
		assertEquals(1, nodeService.countChildAssocs(exportFolder, true));
		NodeRef subExpedientExport = nodeService.getChildAssocs(exportFolder).get(0).getChildRef();
		assertEquals(subExpedientId, subExpedientExport.getId());
		String subExpedientExportName = (String)nodeService.getProperty(subExpedientExport, ConstantUtils.PROP_NAME);
		assertTrue(subExpedientExportName.startsWith(nodeExpedient.getName()));
		assertNotNull(fileFolderService.searchSimple(subExpedientExport, nodeDocument.getName()));

		assertEquals(ConstantUtils.ESTADO_EXP_E03, (String)nodeService.getProperty(subExpedientExport, ConstantUtils.PROP_ESTADO_ELABORACION_QNAME));

		AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST2);

//		assertEquals(AccessStatus.ALLOWED, permissionService.hasPermission(utils.toNodeRef(subExpedientId), PermissionService.CONSUMER));
//		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(subExpedientId), PermissionService.EDITOR));
//		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(subExpedientId), PermissionService.CONTRIBUTOR));
//		assertEquals(AccessStatus.DENIED, permissionService.hasPermission(utils.toNodeRef(subExpedientId), PermissionService.COORDINATOR));
    }
}

