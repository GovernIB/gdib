package es.resits.ws.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Map;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.rsits.ws.audit.dao.WSAuditDAO;
import es.rsits.ws.exception.DAOException;
import es.rsits.ws.handlers.AuditHandler;
import es.rsits.ws.utils.WSUtils;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/ws-base-test-context.xml")
public class AuditHandlerTest {

	@Autowired
	private WSAuditDAO wsAuditDAO;

	@Autowired
	private AuditHandler auditHandler;

	@Autowired
	private File repositoryServiceGetNodeFile;
	@Autowired
	private File repositoryServiceExceptionFile;

	@SuppressWarnings("rawtypes")
	@Test
	public void testAuditInBound() throws SOAPException, IOException, DAOException, InterruptedException{
		// recupero el numero de filas en la tabla de auditoria antes de realizar la operacion
		int preOperationRow = wsAuditDAO.getRowCountAudit();

		SOAPMessage soapMsg = WSUtils.getSoapMessageFromFile(repositoryServiceGetNodeFile);

		SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
		when(soapMsgCtx.getMessage()).thenReturn(soapMsg);
		when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

		// compruebo que en el ejecutor de thread no hay nada activo
		assertEquals(0, auditHandler.getWsAudit().getAuditTaskExecutor().getActiveCount());

		auditHandler.handleMessage(soapMsgCtx);
		when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(true);
		auditHandler.handleMessage(soapMsgCtx);

		// compruebo que en el ejecutor de thread hay uno activo despues de acabar en el handler
		assertEquals(1, auditHandler.getWsAudit().getAuditTaskExecutor().getActiveCount());
		// espero 2 segundos y compruebo que el hilo activo ya ha terminado
		Thread.sleep(2000);
		assertEquals(0, auditHandler.getWsAudit().getAuditTaskExecutor().getActiveCount());

		// recupero el numero de filas en la tabla de auditoria despues de realizar la operacion
		int postOperationRow = wsAuditDAO.getRowCountAudit();
		assertEquals(++preOperationRow, postOperationRow);

		// recupero el ultimo registro en la base de datos y compruebo los datos
		Map row = wsAuditDAO.getLastRowAudit();
		this.getInfoAudit(row);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testAuditFaultRepositoryService() throws DAOException, InterruptedException, SOAPException, IOException{
		// recupero el numero de filas en la tabla de auditoria antes de realizar la operacion
		int preOperationRow = wsAuditDAO.getRowCountError();

		SOAPMessage soapMsgRequest = WSUtils.getSoapMessageFromFile(repositoryServiceGetNodeFile);
		SOAPMessage soapMsgResponseFault = WSUtils.getSoapMessageFromFile(repositoryServiceExceptionFile);

		SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
		when(soapMsgCtx.getMessage()).thenReturn(soapMsgRequest);
		when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

		auditHandler.handleMessage(soapMsgCtx);

		when(soapMsgCtx.getMessage()).thenReturn(soapMsgResponseFault);
		auditHandler.handleFault(soapMsgCtx);

		// compruebo que en el ejecutor de thread hay uno activo despues de acabar en el handler
		assertEquals(1, auditHandler.getWsAudit().getAuditTaskExecutor().getActiveCount());
		// espero 2 segundos y compruebo que el hilo activo ya ha terminado
		Thread.sleep(2000);
		assertEquals(0, auditHandler.getWsAudit().getAuditTaskExecutor().getActiveCount());

		// recupero el numero de filas en la tabla de auditoria despues de realizar la operacion
		int postOperationRow = wsAuditDAO.getRowCountError();
		assertEquals(++preOperationRow, postOperationRow);

		// recupero el ultimo registro en la base de datos y compruebo los datos
		Map row = wsAuditDAO.getLastRowError();
		this.getInfoAudit(row);

		String code = (String) row.get("code");
		String message = (String) row.get("message");
		assertEquals("5000", code);
		assertEquals("nodeId is not valid (aaa)", message);
	}

	@SuppressWarnings("rawtypes")
	private void getInfoAudit( Map row){
		String username = (String) row.get("username");
		String application = (String) row.get("application");
		String operacion = (String) row.get("operation");
		String operacionType = (String) row.get("operationtype");
		String operationesb = (String) row.get("operationesb");
		Date executionDate = (Date) row.get("executionDate");
		String executiontime = (String) row.get("executiontime");
		String authenticationtype = (String) row.get("authenticationtype");
		String ip = (String) row.get("ip");
		String mac = (String) row.get("mac");

		String applicantname = (String) row.get("applicantname");
		String applicantdocument = (String) row.get("applicantdocument");
		String functionaryname = (String) row.get("functionaryname");
		String functionarydocument = (String) row.get("functionarydocument");
		String functionarytramitadoraunit = (String) row.get("functionarytramitadoraunit");
		String expedientnumber = (String) row.get("expedientnumber");
		String expedientprocedure = (String) row.get("expedientprocedure");
		String documentalseries = (String) row.get("documentalseries");

		assertEquals("admin", username);
		assertEquals("aplicacion", application);
		assertEquals("getNode", operacion);
		assertEquals(WSUtils.LECTURA, operacionType);
		assertEquals("operaionESB", operationesb);
		assertNotNull(executionDate);
		assertNotNull(executiontime);
		assertEquals(WSUtils.USERNAME, authenticationtype);
		assertEquals(WSUtils.getIPAddress(), ip);
		assertEquals(WSUtils.getMACAddress(), mac);

		assertEquals("solicitanteNombre", applicantname);
		assertEquals("solicitanteDocumento", applicantdocument);
		assertEquals("funcionarioNombre", functionaryname);
		assertEquals("funcionarioDocumento", functionarydocument);
		assertEquals("unidadTramitadora", functionarytramitadoraunit);
		assertEquals("numeroExpediente", expedientnumber);
		assertEquals("procedimientoExpediente", expedientprocedure);
		assertEquals("serieDocumental", documentalseries);
	}

}
