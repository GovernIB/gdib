package es.rsits.ws.handlers;

import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import es.rsits.ws.exception.WSProtocolRuntimeException;
import es.rsits.ws.utils.CaibExceptionsUtils;
@Component
@Scope(value = "request")
public class ExceptionHandler extends SpringBeanAutowiringSupport implements SOAPHandler<SOAPMessageContext>{

	private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class);

	@Value("$base{gdib.exception.active}")
	private Boolean activeHandler;

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// compruebo si el handler esta activa en la property del modulo ws-amp
		if(this.activeHandler.booleanValue()){
			// TODO sin terminar
			LOGGER.debug("Enter - handleFault!!");
			try {
				SOAPFactory factory = SOAPFactory.newInstance();
				SOAPMessage soapMsg = context.getMessage();
				SOAPBody soapBody = soapMsg.getSOAPBody();
					
// 				Extraigo la informacion del cuerpo del mensaje:
//						...				
//						<S:Body>
//							<ns2:Fault xmlns:ns2="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns3="http://www.w3.org/2003/05/soap-envelope">
//							<faultcode>ns2:Server</faultcode>
//							<faultstring>java.lang.NullPointerException</faultstring>
//							<detail>
//								<ns2:exception class="java.lang.NullPointerException" note="To disable this feature, set com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace system property to false" xmlns:ns2="http://jax-ws.dev.java.net/">
//									<ns2:stackTrace>
//										<ns2:frame class="es.caib.gdib.utils.GdibUtils" file="GdibUtils.java" line="422" method="transformMapStringToQname"/>
//										...
//							        </ns2:stackTrace>
//								</ns2:exception>
//							</detail>
//							</ns2:Fault>
//						</S:Body>
//						...
										
				// FAULT
				SOAPFault fault = soapBody.getFault();
				
				// CODE
				SOAPBodyElement code = (SOAPBodyElement) fault.getFirstChild();
				String codeString = code != null ? code.getTextContent() : "";
				
				// MESSAGE
				//SOAPBodyElement message = (SOAPBodyElement) code.getNextSibling();

				//EXCEPTION & STACKTRACE
				Detail d = fault.getDetail();
				SOAPElement exceptionElement = (SOAPElement) d.getFirstChild();
				String exceptionClass = exceptionElement != null ? exceptionElement.getAttribute("class") : "";
				//SOAPElement stackTraceElementt = (SOAPElement) exceptionElement.getFirstChild();
				
				if(! Objects.equals(exceptionClass, CaibExceptionsUtils.GDIBEXCEPTION_NAME)){
					LOGGER.info("ExceptionHandler ha detectado una excepcion cuya clase no es es.caib.gdib.ws.exception.GdibException");
					
					exceptionClass = CaibExceptionsUtils.GDIBEXCEPTION_NAME;
					LOGGER.debug("		exceptionClass value: " + exceptionClass);
					
					codeString = String.valueOf(CaibExceptionsUtils.CODE_GENERAL);
					LOGGER.debug("		codeString value: " + codeString);

					code.removeContents();
					code.addTextNode(codeString);
					d.addChildElement(code);

					Name classAttrName = factory.createName("class");
					exceptionElement.removeAttribute(classAttrName);
					exceptionElement.addAttribute(classAttrName, exceptionClass);

					soapMsg.saveChanges();
				}

			} catch (SOAPException e) {
				LOGGER.error(e.getMessage());
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

	public void setActiveHandler(Boolean activeHandler) {
		this.activeHandler = activeHandler;
	}

}
