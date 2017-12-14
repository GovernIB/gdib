package es.rsits.ws.handlers;

import java.util.HashSet;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import es.rsits.ws.auth.WSAuthentication;
import es.rsits.ws.exception.WSException;
import es.rsits.ws.exception.WSProtocolRuntimeException;
import es.rsits.ws.utils.WSUtils;

@Component
@Scope(value = "request")
public class AlfrescoAuthHandler extends SpringBeanAutowiringSupport  implements SOAPHandler<SOAPMessageContext>{

	private static final Logger LOGGER = Logger.getLogger(AlfrescoAuthHandler.class);

	@Value("$base{gdib.autentication.active}")
	private Boolean activeHandler;

	@Autowired
    private WSAuthentication wSAuth;

	private void login(SOAPMessageContext context){
		String wsseUsername = null;
        String wssePassword = null;
        try {
        	// recupero la informacion de usuario y password de la cabecera de seguridad de la request
        	wsseUsername = WSUtils.getInfoSecurityHeader(context, WSUtils.QNAME_WSSE_USERNAME);
        	wssePassword = WSUtils.getInfoSecurityHeader(context, WSUtils.QNAME_WSSE_PASSWORD);
        	if ( wsseUsername.equalsIgnoreCase(WSUtils.TICKET_TOKEN)){
        		wSAuth.doAuthentication(null, wssePassword);
        	}else{
        		wSAuth.doAuthentication(wsseUsername, wssePassword);
        	}
        } catch (SOAPException e) {
            throw new WSProtocolRuntimeException("Error reading SOAP message context: " + e.getMessage(), e);
        } catch (WSException e) {
        	throw new WSProtocolRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		// compruebo si el handler esta activa en la property del modulo ws-amp
		if(this.activeHandler.booleanValue()){
			Boolean outboundProperty = (Boolean)
			context.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				LOGGER.debug("Enter - outboundProperty!!");
			} else {
				LOGGER.debug("Enter - inboundProperty!!");
				// realizo el login contra alfresco
				login(context);
			}
		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		LOGGER.debug("Enter - handleFault!!");
		return true;
	}

	@Override
	public void close(MessageContext context) {
		LOGGER.debug("Enter - close!!");
	}

	@Override
	public Set<QName> getHeaders() {
		// incluido para que los servicios SOAP lanzados desde SOAPUI puedan leer la cabecera de seguridad
		// autogeneradas con SOAPUI
		final QName securityHeader = new QName(
	            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
	            "Security",
	            "wsse");

	        final HashSet headers = new HashSet();
	        headers.add(securityHeader);

	        return headers;
	}

	public void setwSAuth(WSAuthentication wSAuth) {
		this.wSAuth = wSAuth;
	}

	public void setActiveHandler(Boolean activeHandler) {
		this.activeHandler = activeHandler;
	}

}
