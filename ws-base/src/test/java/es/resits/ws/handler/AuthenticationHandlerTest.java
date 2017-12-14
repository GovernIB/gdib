package es.resits.ws.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.rsits.ws.exception.WSProtocolRuntimeException;
import es.rsits.ws.handlers.AlfrescoAuthHandler;
import es.rsits.ws.utils.WSUtils;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/application-context.xml")
public class AuthenticationHandlerTest {

	private static final Logger LOGGER = Logger.getLogger(AuthenticationHandlerTest.class);

	@Autowired
	@Qualifier(value = "AuthenticationService")
	private AuthenticationService authenticationService;

	@Autowired
	private AlfrescoAuthHandler alfrescoAuthHandler;

	@Test
	public void testAuthOutbound() {
		SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
		when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(true);

		boolean result = alfrescoAuthHandler.handleMessage(soapMsgCtx);
		assertTrue(result);
	}

	@Test
	public void testAuthInboundLoginSuccess() throws IOException {
		MessageFactory factory;
		try {
			factory = MessageFactory.newInstance();

			SOAPMessage soapMsg = factory.createMessage();
			SOAPPart part = soapMsg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();
			SOAPHeader header = envelope.getHeader();

			header.addChildElement(WSUtils.createUsernameTokenSecurityHeader("admin", "admin"));

			SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
			when(soapMsgCtx.getMessage()).thenReturn(soapMsg);
			when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

			boolean result = alfrescoAuthHandler.handleMessage(soapMsgCtx);

			assertTrue(result);
		} catch (SOAPException e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Test(expected = WSProtocolRuntimeException.class)
	public void testAuthInboundLoginFail() {
		MessageFactory factory;
		try {
			factory = MessageFactory.newInstance();

			SOAPMessage soapMsg = factory.createMessage();
			SOAPPart part = soapMsg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();
			SOAPHeader header = envelope.getHeader();

			header.addChildElement(WSUtils.createUsernameTokenSecurityHeader("admin", "a"));

			SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
			when(soapMsgCtx.getMessage()).thenReturn(soapMsg);
			when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

			alfrescoAuthHandler.handleMessage(soapMsgCtx);
		} catch (SOAPException e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Test
	public void testAuthInboundLoginTicket() {
		MessageFactory factory;
		try {
			AuthenticationUtil.setFullyAuthenticatedUser("admin");
			String ticket = authenticationService.getNewTicket();

			factory = MessageFactory.newInstance();

			SOAPMessage soapMsg = factory.createMessage();
			SOAPPart part = soapMsg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();
			SOAPHeader header = envelope.getHeader();

			header.addChildElement(WSUtils.createUsernameTokenSecurityHeader("", ticket));

			SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
			when(soapMsgCtx.getMessage()).thenReturn(soapMsg);
			when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

			alfrescoAuthHandler.handleMessage(soapMsgCtx);

			String loginTicket = authenticationService.getCurrentTicket();
			assertTrue(loginTicket.equals(ticket));
		} catch (SOAPException e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Test(expected = WSProtocolRuntimeException.class)
	public void testAuthInboundLoginTicketFailEmpty() {
		MessageFactory factory;
		try {
			factory = MessageFactory.newInstance();

			SOAPMessage soapMsg = factory.createMessage();
			SOAPPart part = soapMsg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();
			SOAPHeader header = envelope.getHeader();

			header.addChildElement(WSUtils.createUsernameTokenSecurityHeader("", ""));

			SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
			when(soapMsgCtx.getMessage()).thenReturn(soapMsg);
			when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

			alfrescoAuthHandler.handleMessage(soapMsgCtx);

		} catch (SOAPException e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Test(expected = WSProtocolRuntimeException.class)
	public void testAuthInboundLoginTicketFail() {
		MessageFactory factory;
		try {
			factory = MessageFactory.newInstance();

			SOAPMessage soapMsg = factory.createMessage();
			SOAPPart part = soapMsg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();
			SOAPHeader header = envelope.getHeader();

			header.addChildElement(WSUtils.createUsernameTokenSecurityHeader("", "ticket"));

			SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
			when(soapMsgCtx.getMessage()).thenReturn(soapMsg);
			when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

			alfrescoAuthHandler.handleMessage(soapMsgCtx);

		} catch (SOAPException e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Test(expected = WSProtocolRuntimeException.class)
	public void testAuthInboundLoginFailEmptyPassword() {
		MessageFactory factory;
		try {
			factory = MessageFactory.newInstance();

			SOAPMessage soapMsg = factory.createMessage();
			SOAPPart part = soapMsg.getSOAPPart();

			SOAPEnvelope envelope = part.getEnvelope();
			SOAPHeader header = envelope.getHeader();

			header.addChildElement(WSUtils.createUsernameTokenSecurityHeader("admin", ""));

			SOAPMessageContext soapMsgCtx = Mockito.mock(SOAPMessageContext.class);
			when(soapMsgCtx.getMessage()).thenReturn(soapMsg);
			when(soapMsgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(false);

			alfrescoAuthHandler.handleMessage(soapMsgCtx);
		} catch (SOAPException e) {
			LOGGER.error(e.getMessage());
		}
	}

}
