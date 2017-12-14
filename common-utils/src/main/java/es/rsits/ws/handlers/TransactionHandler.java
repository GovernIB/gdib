package es.rsits.ws.handlers;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import es.rsits.ws.exception.WSException;
import es.rsits.ws.exception.WSProtocolRuntimeException;
import es.rsits.ws.transaction.WSTransaction;

public class TransactionHandler extends SpringBeanAutowiringSupport implements SOAPHandler<SOAPMessageContext> {

	private static final Logger LOGGER = Logger.getLogger(TransactionHandler.class);

	@Value("$base{gdib.exception.active}")
	private Boolean activeHandler;

	@Autowired
	private WSTransaction wsTransact;

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		// compruebo si el handler esta activa en la property del modulo ws-amp
		if(this.activeHandler.booleanValue()){
			Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			try {
				if (outboundProperty.booleanValue()) {
					LOGGER.debug("Enter - outboundProperty!!");
					wsTransact.commit();
				} else {
					LOGGER.debug("Enter - inboundProperty!!");
					wsTransact.createTransaction();
				}
			} catch (WSException e) {
				throw new WSProtocolRuntimeException(e.getMessage(), e);
			}
		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// compruebo si el handler esta activa en la property del modulo ws-amp
		if(this.activeHandler.booleanValue()){
			LOGGER.debug("Enter - handleFault!!");
			try {
				wsTransact.rollback();
			} catch (WSException e) {
				throw new WSProtocolRuntimeException(e.getMessage(), e);
			}
		}
		return true;
	}

	@Override
	public void close(MessageContext context) {
		LOGGER.debug("Enter - close!!");
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	public void setWsTransact(WSTransaction wsTransact) {
		this.wsTransact = wsTransact;
	}

	public void setActiveHandler(Boolean activeHandler) {
		this.activeHandler = activeHandler;
	}
}
