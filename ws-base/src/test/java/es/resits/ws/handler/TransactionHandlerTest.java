package es.resits.ws.handler;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.rsits.ws.handlers.TransactionHandler;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/application-context.xml")
public class TransactionHandlerTest {

	private final static String ADMIN_USER_NAME = "admin";
	private final static String UNIT_TEST_FOLDER = "UnitTest";

	private static NodeRef rootTest = null;

	@Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Autowired
    @Qualifier("nodeLocatorService")
    private NodeLocatorService nodeLocatorService;

    @Autowired
    @Qualifier("FileFolderService")
    private FileFolderService fileFolderService;

	@Autowired
	private TransactionHandler transactionHandler;

	@Autowired
	@Qualifier(value="TransactionService")
	private TransactionService transactionService;

	@Before
    public void configureUp() throws FileNotFoundException{
		// me autentifico
		AuthenticationUtil.setFullyAuthenticatedUser(ADMIN_USER_NAME);
		// obtengo el nodo raiz y el numero de nodos que tiene
		NodeRef companyHome = nodeLocatorService.getNode("companyhome", null, null);
		rootTest = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, UNIT_TEST_FOLDER);
		if(rootTest == null){
			rootTest = fileFolderService.create(companyHome, UNIT_TEST_FOLDER, ContentModel.TYPE_FOLDER).getNodeRef();
		}

    }

    @After
    public void configureDown(){
    	if(rootTest != null){
    		fileFolderService.delete(rootTest);
    	}
    }

	@Test
	public void testTransactionCommit() {
		NodeRef nodeTest = null;

		// creo la transaccion
		SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
		when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);
		transactionHandler.handleMessage(soapMsgCtx);

		// creo el nodo
		nodeTest = createNode();
		assertTrue(exitsNode(nodeTest));

		// cierro la transaccion con un commit
		soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
		when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(true);
		transactionHandler.handleMessage(soapMsgCtx);

		// el nodo sigue existiendo
		assertTrue(exitsNode(nodeTest));
	}

	@Test
	public void testTransactionRollback() {
		NodeRef nodeTest = null;

		// creo la transaccion
		SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
		when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);
		transactionHandler.handleMessage(soapMsgCtx);

		// creo el nodo
		nodeTest = createNode();
		assertTrue(exitsNode(nodeTest));

		// cierro la transaccion con un roolback
		transactionHandler.handleFault(soapMsgCtx);

		// el nodo ya no existe
		assertTrue(!exitsNode(nodeTest));
	}

	private NodeRef createNode(){
		ChildAssociationRef createdChildRef = nodeService.createNode(rootTest,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName("nombre"),
				ContentModel.TYPE_CONTENT,
				null);
		return createdChildRef.getChildRef();
	}

	private boolean exitsNode(NodeRef node){
		return nodeService.exists(node);
	}

}
