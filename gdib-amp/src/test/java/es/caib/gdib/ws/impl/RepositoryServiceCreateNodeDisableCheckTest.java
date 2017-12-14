package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
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
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;


@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/repositoryService-createNodeTest-context.xml")
public class RepositoryServiceCreateNodeDisableCheckTest {

    static Logger log = Logger.getLogger(RepositoryServiceCreateNodeDisableCheckTest.class);

    @Autowired
    private RepositoryServiceSoapPortImpl repositoryServiceSoap;

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
    private Repository repositoryHelper;

    @Autowired
    private Node nodeDocument;
    @Autowired
    private Node nodeExpedient;
    @Autowired
    private Node emptyNodeDocument;
    @Autowired
    private Node alfrescoFolder;

    @Before
    public void configureUp() throws FileNotFoundException, NotSupportedException, SystemException, GdibException{
    	// preparo entorno de pruebas
    	testUtils.configureUp();
    	utils.setRootDM(TestUtils.rootDM.getId());
    	utils.setRootCT(TestUtils.rootCT.getId());
    	utils.setRootTemplate(TestUtils.rootTemplate.getId());
    	repositoryServiceSoap.setRepositoryDisableCheck(Boolean.TRUE);
    	utils.setRepositoryDisableCheck(Boolean.TRUE);
    }

    @After
    public void configureDown() throws IllegalStateException, SecurityException, SystemException{
    	testUtils.configureDown();
    }

    @Test
	public void testCreateNodeDocumentInExpedient() throws GdibException {
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, null);
		assertTrue(!StringUtils.isEmpty(nodeId));

		NodeRef createNode = utils.toNodeRef(nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		String type = nodeService.getType(createNode).getPrefixString();

		assertEquals(nodeDocument.getName(), name);
		assertEquals(nodeDocument.getType(), type);
		assertTrue(utils.getPathFromUID(createNode).contains("expedient/document.txt"));

		assertTrue(repositoryServiceSoap.getNodeVersionList(nodeId, null).get(0).getId().equals("1.0"));
	}

    @Test
	public void testCreateNodeDocumentInExpedientInAlfrescoFolder() throws GdibException {
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String alfrescoFolderId = repositoryServiceSoap.createNode(alfrescoFolder, expId, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument,  alfrescoFolderId, null);
		assertTrue(!StringUtils.isEmpty(nodeId));

		NodeRef createNode = utils.toNodeRef(nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		String type = nodeService.getType(createNode).getPrefixString();

		assertEquals(nodeDocument.getName(), name);
		assertEquals(nodeDocument.getType(), type);
		assertTrue(utils.getPathFromUID(createNode).contains("expedient/folder/document.txt"));

		assertTrue(repositoryServiceSoap.getNodeVersionList(nodeId, null).get(0).getId().equals("1.0"));
	}

    @Test
	public void testCreateNodeDocumentOutDM() throws GdibException {
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, repositoryHelper.getCompanyHome().getId(), null);
		assertTrue(!StringUtils.isEmpty(nodeId));

		NodeRef createNode = utils.toNodeRef(nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		String type = nodeService.getType(createNode).getPrefixString();

		assertEquals(nodeDocument.getName(), name);
		assertEquals(nodeDocument.getType(), type);
		assertTrue(utils.getPathFromUID(createNode).contains("Company Home/document.txt"));

		assertTrue(repositoryServiceSoap.getNodeVersionList(nodeId, null).get(0).getId().equals("1.0"));
	}

    @Test
	public void testCreateNodeDocumentInExpedientOutDM() throws GdibException {
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, repositoryHelper.getCompanyHome().getId(), null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, null);
		assertTrue(!StringUtils.isEmpty(nodeId));

		NodeRef createNode = utils.toNodeRef(nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		String type = nodeService.getType(createNode).getPrefixString();

		assertEquals(nodeDocument.getName(), name);
		assertEquals(nodeDocument.getType(), type);
		assertTrue(utils.getPathFromUID(createNode).contains("expedient/document.txt"));

		assertTrue(repositoryServiceSoap.getNodeVersionList(nodeId, null).get(0).getId().equals("1.0"));
	}

    @Test
	public void testCreateNodeDocumentInExpedientInAlfrescoFolderOutDM() throws GdibException {
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, repositoryHelper.getCompanyHome().getId(), null);
		String alfrescoFolderId = repositoryServiceSoap.createNode(alfrescoFolder, expId, null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument,  alfrescoFolderId, null);
		assertTrue(!StringUtils.isEmpty(nodeId));

		NodeRef createNode = utils.toNodeRef(nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		String type = nodeService.getType(createNode).getPrefixString();

		assertEquals(nodeDocument.getName(), name);
		assertEquals(nodeDocument.getType(), type);
		assertTrue(utils.getPathFromUID(createNode).contains("expedient/folder/document.txt"));

		assertTrue(repositoryServiceSoap.getNodeVersionList(nodeId, null).get(0).getId().equals("1.0"));
	}

    @Test
    public void testCreateNodeDocumentRelativePath() throws GdibException{
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, repositoryHelper.getCompanyHome().getId() + "/folderTest/folder", null);
    	assertTrue(!StringUtils.isEmpty(nodeId));

		NodeRef createNode = utils.toNodeRef(nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		String type = nodeService.getType(createNode).getPrefixString();

		assertEquals(nodeDocument.getName(), name);
		assertEquals(nodeDocument.getType(), type);
		assertTrue(utils.getPathFromUID(createNode).contains("Company Home/folderTest/folder/document.txt"));
    }

    @Test
	public void testCreateEmptyDocument() throws GdibException {
    	assertTrue(emptyNodeDocument.getProperties().isEmpty());
    	assertTrue(emptyNodeDocument.getAspects().isEmpty());
		String nodeId = repositoryServiceSoap.createNode(emptyNodeDocument, repositoryHelper.getCompanyHome().getId(), null);
		assertTrue(!StringUtils.isEmpty(nodeId));

		NodeRef createNode = utils.toNodeRef(nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		String type = nodeService.getType(createNode).getPrefixString();

		assertEquals(nodeDocument.getName(), name);
		assertEquals(nodeDocument.getType(), type);
		assertTrue(utils.getPathFromUID(createNode).contains("Company Home/document.txt"));
	}

    @Test
	public void testCreateAndGetNodeDocumentInExpedient() throws GdibException {
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		Node createNode = repositoryServiceSoap.createAndGetNode(nodeDocument, expId, null);

		assertEquals(nodeDocument.getName(), createNode.getName());
		assertEquals(nodeDocument.getType(), createNode.getType());
		assertTrue(utils.getPathFromUID(utils.toNodeRef(createNode.getId())).contains("expedient/document.txt"));
	}

    @Test
	public void testCreateAndGetEmptyDocument() throws GdibException {
    	assertTrue(emptyNodeDocument.getProperties().isEmpty());
    	assertTrue(emptyNodeDocument.getAspects().isEmpty());
		Node node = repositoryServiceSoap.createAndGetNode(emptyNodeDocument, repositoryHelper.getCompanyHome().getId(), null);

		assertEquals(nodeDocument.getName(), node.getName());
		assertEquals(nodeDocument.getType(), node.getType());
		assertTrue(utils.getPathFromUID(utils.toNodeRef(node.getId())).contains("Company Home/document.txt"));
	}

    @Test
    public void testCreateExpedientWithoutTemplate() throws GdibException{
    	Property property = new Property(ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
    	nodeExpedient.getProperties().get(nodeExpedient.getProperties().indexOf(property)).setValue("EXP004321");

    	String nodeId = repositoryServiceSoap.createNode(nodeExpedient, repositoryHelper.getCompanyHome().getId(), null);

    	NodeRef exp = utils.toNodeRef(nodeId);
    	assertEquals(0, nodeService.countChildAssocs(exp, true));
    }

    @Test(expected = GdibException.class)
	public void testCreateNodeNull() throws GdibException {
		repositoryServiceSoap.createNode(null, repositoryHelper.getCompanyHome().getId(), null);
	}

    @Test(expected = GdibException.class)
    public void testCreateExpedientParentNotExits() throws GdibException{
    	repositoryServiceSoap.createNode(nodeExpedient, "12345678-1234-1234-1234-123456789012", null);
    }

    @Test(expected = GdibException.class)
    public void testCreateDocumentEmptyParent() throws GdibException{
    	repositoryServiceSoap.createNode(nodeDocument, "", null);
    }

    @Test(expected = GdibException.class)
    public void testCreateDocumentNullParent() throws GdibException{
    	repositoryServiceSoap.createNode(nodeDocument, null, null);
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeUnknowmParent() throws GdibException{
    	repositoryServiceSoap.createNode(nodeDocument, "aaaaaaaaaaaaaaaaaaaa", null);
    }

    @Test(expected = GdibException.class)
    public void testCreateNodeDocumentWithVersionParent() throws GdibException{
    	repositoryServiceSoap.createNode(nodeDocument, "1.1@" + TestUtils.rootDM.getId(), null);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeDocumentWithParentIsNotFolder() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, null);
    	nodeDocument.setName("document2.txt");
    	repositoryServiceSoap.createNode(nodeDocument, nodeId, null);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeExpedientWithParentIsNotFolder() throws GdibException{
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, null);
    	nodeExpedient.setName("expedient2");
    	repositoryServiceSoap.createNode(nodeExpedient, nodeId, null);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeEmptyName() throws GdibException{
    	nodeDocument.setName("");
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	repositoryServiceSoap.createNode(nodeDocument, expId, null);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeWithoutName() throws GdibException{
    	nodeDocument.setName(null);
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	repositoryServiceSoap.createNode(nodeDocument, expId, null);
    }

    @Test
	@DirtiesContext
	public void testCreateNodeRareName() throws GdibException {
		nodeDocument.setName("nam@.txt");
		String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, expId, null);

		NodeRef createNode = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);
		String name = (String) nodeService.getProperty(createNode, ContentModel.PROP_NAME);
		assertEquals(name, nodeDocument.getName());
	}

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeWithoutType() throws GdibException{
    	nodeDocument.setType(null);
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	repositoryServiceSoap.createNode(nodeDocument, expId, null);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeEmptyType() throws GdibException{
    	nodeDocument.setType("");
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	repositoryServiceSoap.createNode(nodeDocument, expId, null);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testCreateNodeUnknowmType() throws GdibException{
    	nodeDocument.setType("unknown");
    	String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	repositoryServiceSoap.createNode(nodeDocument, expId, null);
    }

}

