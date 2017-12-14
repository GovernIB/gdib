package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.version.VersionService;
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
import es.caib.gdib.ws.common.types.NodeVersion;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;


@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/repositoryService-version-context.xml")
public class RepositoryServiceVersionNodeTest {

    static Logger log = Logger.getLogger(RepositoryServiceVersionNodeTest.class);

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    @Qualifier("VersionService")
    private VersionService versionService;

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
    private TestUtils testUtils;

    @Before
    public void configureUp() throws NotSupportedException, SystemException{
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
	public void testGetVersionList() throws GdibException {
    	Node create = null;
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		create = repositoryServiceSoap.getNode(nodeId, false, false, null);
		create.getProperties().add(new Property(ConstantUtils.PROP_FECHA_INICIO_QNAME, "2015-02-20"));
		repositoryServiceSoap.modifyNode(create, null);

		create = repositoryServiceSoap.getNode(nodeId, false, false, null);
		create.getProperties().add(new Property("cm:title", "Titulo Version 1"));
		repositoryServiceSoap.modifyNode(create, null);;

		create = repositoryServiceSoap.getNode(nodeId, false, false, null);
		create.getProperties().add(new Property("cm:title", "Titulo Version 2"));
		repositoryServiceSoap.modifyNode(create, null);

		List<NodeVersion> versionList = repositoryServiceSoap.getNodeVersionList(nodeId, null);
		assertEquals(4, versionList.size());
		assertEquals("1.3", versionList.get(0).getId());
		assertEquals("1.2", versionList.get(1).getId());
		assertEquals("1.1", versionList.get(2).getId());
		assertEquals("1.0", versionList.get(3).getId());
	}

    @Test
	public void testGetVersion() throws GdibException {
    	Property title = new Property("cm:title");
    	Node create = null;
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);

		create = repositoryServiceSoap.getNode(nodeId, false, false, null);
		create.getProperties().add(new Property("cm:title", "Titulo Version 1"));
		repositoryServiceSoap.modifyNode(create, null);

		create = repositoryServiceSoap.getNode(nodeId, false, false, null);
		create.getProperties().add(new Property("cm:title", "Titulo Version 2"));
		repositoryServiceSoap.modifyNode(create, null);

		create = repositoryServiceSoap.getNode(nodeId, false, false, null);
		create.getProperties().add(new Property("cm:title", "Titulo Version 3"));
		repositoryServiceSoap.modifyNode(create, null);

		Node version = null;
		version = repositoryServiceSoap.getNode("1.1@"+nodeId,false, false, null);
		assertTrue(version.getProperties().get(version.getProperties().indexOf(title)).getValue().equals("Titulo Version 1"));

		version = repositoryServiceSoap.getNode("1.2@"+nodeId,false, false, null);
		assertTrue(version.getProperties().get(version.getProperties().indexOf(title)).getValue().equals("Titulo Version 2"));

		version = repositoryServiceSoap.getNode("1.3@"+nodeId,false, false, null);
		assertTrue(version.getProperties().get(version.getProperties().indexOf(title)).getValue().equals("Titulo Version 3"));
	}

    @Test(expected = GdibException.class)
	public void testGetVersionNotExit() throws GdibException {
    	Node created = repositoryServiceSoap.createAndGetNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	NodeRef ref = utils.toNodeRef(created.getId());

    	versionService.ensureVersioningEnabled(ref, null);

    	assertNotNull(repositoryServiceSoap.getNode("1.0@"+ref.getId(), false, false, null));
    	repositoryServiceSoap.getNode("1.1@"+ref.getId(), false, false, null);
	}

    @Test(expected = GdibException.class)
	public void testGetVersionInvalidID() throws GdibException {
    	Node created = repositoryServiceSoap.createAndGetNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	NodeRef ref = utils.toNodeRef(created.getId());

    	versionService.ensureVersioningEnabled(ref, null);

    	repositoryServiceSoap.getNode("1.0@"+ref.getId()+"a", false, false, null);
	}

    @Test(expected = GdibException.class)
	public void testGetVersionEmptyID() throws GdibException {
    	Node created = repositoryServiceSoap.createAndGetNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	NodeRef ref = utils.toNodeRef(created.getId());

    	versionService.ensureVersioningEnabled(ref, null);

    	repositoryServiceSoap.getNode("", false, false, null);
	}

    @Test(expected = GdibException.class)
	public void testGetVersionNullID() throws GdibException {
    	Node created = repositoryServiceSoap.createAndGetNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	NodeRef ref = utils.toNodeRef(created.getId());

    	versionService.ensureVersioningEnabled(ref, null);

    	repositoryServiceSoap.getNode(null, false, false, null);
	}



    @Test(expected = GdibException.class)
	public void testGetVersionEmptyVersion() throws GdibException {
    	Node created = repositoryServiceSoap.createAndGetNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	NodeRef ref = utils.toNodeRef(created.getId());

    	versionService.ensureVersioningEnabled(ref, null);

    	repositoryServiceSoap.getNode("@"+ref.getId(), false, false, null);
	}

    @Test(expected = GdibException.class)
	public void testGetVersionInvalidVersion() throws GdibException {
    	Node created = repositoryServiceSoap.createAndGetNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	NodeRef ref = utils.toNodeRef(created.getId());

    	versionService.ensureVersioningEnabled(ref, null);

    	repositoryServiceSoap.getNode("12,2@"+ref.getId(), false, false, null);
	}

    @Test(expected = GdibException.class)
	public void testGetVersionNoPermission() throws GdibException {
    	Node created = repositoryServiceSoap.createAndGetNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	NodeRef ref = utils.toNodeRef(created.getId());

    	// version 1.0
    	versionService.ensureVersioningEnabled(ref, null);

    	List<Property> nodeProp = utils.filterCalculatedProperties(created.getProperties());
    	nodeProp.add(new Property(ConstantUtils.PROP_RESOLUCION_QNAME, "resolucion"));
    	utils.addProperties(ref, nodeProp);

    	// version 1.1
    	versionService.createVersion(ref, null);

    	List<NodeVersion> list = repositoryServiceSoap.getNodeVersionList(ref.getId(), null);
    	assertEquals(2, list.size());

    	AuthenticationUtil.setRunAsUser(TestUtils.USER_TEST);
    	repositoryServiceSoap.getNode("1.1@"+ref.getId(), false, false, null);
	}

}

