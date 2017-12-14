package es.rsits.ws.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

public class WSUtils {

	private static final Logger LOGGER = Logger.getLogger(WSUtils.class);

	private static final String WSSE_NS_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	private static final String WSSE_SECURITY = "Security";
	private static final QName QNAME_WSSE_USERNAMETOKEN = new QName(WSSE_NS_URI, "UsernameToken");
	public static final QName QNAME_WSSE_USERNAME = new QName(WSSE_NS_URI, "Username");
	public static final QName QNAME_WSSE_PASSWORD = new QName(WSSE_NS_URI, "Password");

	public static final String LECTURA = "lectura";
	public static final String ESCRITURA = "modificacion";
	public static final String BORRADO = "borrado";
	public static final String ACTUALIZACION = "actualizacion";
	public static final String PERMISOS = "permisos";
	public static final String USERNAME = "Usuario";
	public static final String ANONYMOUS = "Anonimo";
	public static final String GUEST = "guest";

	public static final String WSSE_PREFIX = "wsse"; // ws service security
	public static final String TICKET_TOKEN = "_AuthenticationTicket";

	/**
	 * Creates the WS-Security SOAP header for UsernameToken as SOAPElement.
	 *
	 * @return the WS-Security SOAP header for UsernameToken
	 * @throws SOAPException
	 * @throws Exception
	 *             as appropriate
	 */
	public static SOAPElement createUsernameTokenSecurityHeader(String username, String password) throws SOAPException {
		SOAPFactory factory = SOAPFactory.newInstance();

		// create a UsernameToken element
		SOAPElement usernameToken = factory.createElement(QNAME_WSSE_USERNAMETOKEN.getLocalPart(), WSSE_PREFIX,
				WSSE_NS_URI);

		// add the username element
		SOAPElement usernameElement = factory.createElement(QNAME_WSSE_USERNAME.getLocalPart(), WSSE_PREFIX,
				WSSE_NS_URI);
		usernameElement.addTextNode(username);
		usernameToken.addChildElement(usernameElement);

		// add the password element
		SOAPElement passwordElement = factory.createElement(QNAME_WSSE_PASSWORD.getLocalPart(), WSSE_PREFIX,
				WSSE_NS_URI);
		passwordElement.addTextNode(password);
		usernameToken.addChildElement(passwordElement);

		// create the Security Header
		SOAPElement securityHeader = factory.createElement(WSSE_SECURITY, WSSE_PREFIX, WSSE_NS_URI);
		securityHeader.addChildElement(usernameToken);

		return securityHeader;
	}

	/**
	 * Mapa estatico no modificable, donde se identifican las operaciones dentro
	 * del repositoryService
	 */
	public static final Map<String, String> AUDIT_OPERATION_TYPE;
	static {
		Hashtable<String, String> tmp = new Hashtable<String, String>();
		tmp.put("getMigrationNode", LECTURA);
		tmp.put("transformNode", ESCRITURA);
		tmp.put("createNode", ESCRITURA);
		tmp.put("createAndGetNode", ESCRITURA);
		tmp.put("modifyNode", ACTUALIZACION);
		tmp.put("getNode", LECTURA);
		tmp.put("moveNode", ESCRITURA);
		tmp.put("searchNode", LECTURA);
		tmp.put("removeNode", BORRADO);
		tmp.put("linkNode", ESCRITURA);
		tmp.put("foliateNode", ACTUALIZACION);
		tmp.put("exportNode", ACTUALIZACION);
		tmp.put("getNodeVersionList", LECTURA);
		tmp.put("authorizeNode", PERMISOS);
		tmp.put("removeAuthority", PERMISOS);
		tmp.put("lockNode", ACTUALIZACION);
		tmp.put("unlockNode", ACTUALIZACION);
		tmp.put("openFile", ACTUALIZACION);
		tmp.put("closeFile", ACTUALIZACION);
		AUDIT_OPERATION_TYPE = Collections.unmodifiableMap(tmp);
	}

	/**
	 * Obtengo un valor de la cabeza de seguridad del mensaje
	 *
	 * @param context
	 *            contexto del mensaje
	 * @param search
	 *            qname a buscar
	 * @return el valor del campo
	 * @throws SOAPException
	 */
	public static String getInfoSecurityHeader(SOAPMessageContext context, QName search) throws SOAPException {
		SOAPHeader header = context.getMessage().getSOAPHeader();
		Iterator<?> headerElements = header.examineAllHeaderElements();
		while (headerElements.hasNext()) {
			SOAPHeaderElement headerElement = (SOAPHeaderElement) headerElements.next();
			if (headerElement.getElementName().getLocalName().equals(WSSE_SECURITY)) {
				SOAPHeaderElement securityElement = headerElement;
				Iterator<?> it2 = securityElement.getChildElements();
				while (it2.hasNext()) {
					Node soapNode = (Node) it2.next();
					if (soapNode instanceof SOAPElement) {
						SOAPElement element = (SOAPElement) soapNode;
						QName elementQname = element.getElementQName();
						if (QNAME_WSSE_USERNAMETOKEN.equals(elementQname)) {
							SOAPElement usernameTokenElement = element;
							return getFirstChildElementValue(usernameTokenElement, search);
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Obtengo el primero valor de un campo del primer elemento hijo de un
	 * elemento pasado por parametro
	 *
	 * @param soapElement
	 *            elemento donde buscar
	 * @param qNameToFind
	 *            qname a buscar
	 * @return valor del campo
	 */
	private static String getFirstChildElementValue(SOAPElement soapElement, QName qNameToFind) {
		String value = null;
		Iterator<?> it = soapElement.getChildElements(qNameToFind);
		while (it.hasNext()) {
			SOAPElement element = (SOAPElement) it.next(); // use first
			value = element.getValue();
		}
		return value;
	}

	/**
	 * Busco dentro de los hijos de un elemento del mensaje, el nodo que tiene
	 * el nombre pasado por parametro
	 *
	 * @param element
	 *            donde buscar
	 * @param name
	 *            nombre del elemento a buscar
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static SOAPElement getSOAPElement(SOAPElement element, String name) {
		SOAPElement ele = null;
		if(element!=null){
			Iterator it = element.getChildElements();
			while (it.hasNext()) {
				Object obj = it.next();
				if(obj instanceof SOAPElement){
					ele = (SOAPElement) obj;
					if (name == null || ele.getLocalName().contains(name)) {
						return ele;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Recupero el valor de un SOAPElement dentro de otro SOAPElement
	 *
	 * Por ejemplo en caso de abajo se recuperaria el valor "datoPrueba"
	 * pasandole el elemento "element" y el value "elementData"
	 *
	 * <element> <elementData>datoPrueba</elementData> </element>
	 *
	 * @param element
	 *            SOAPElement donde buscar el elemento que tiene el dato
	 * @param value
	 *            nombre del SOAPElement donde esta el dato a recuperar
	 * @return valor del SOAPElement
	 */
	public static String getSOAPElementValue(SOAPElement element, String value) {
		if(element!=null){
			SOAPElement elementSearch = getSOAPElement(element, value);
			if(elementSearch!=null)
				return elementSearch.getTextContent();
		}
		return null;
	}

	/**
	 * Devuelvo la direccion IP de una llamada a un webService
	 *
	 * @return string con la ip
	 */
	public static String getIPAddress() {
		LOGGER.debug("Extracting IP Address");
		InetAddress ip;
		try {
			// obtengo la ip
			ip = InetAddress.getLocalHost();
			return ip.getHostAddress();
		} catch (UnknownHostException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Devuelvo la direccion MAC de una llamada a un webservice
	 *
	 * @return string con la direccion mac
	 */
	public static String getMACAddress() {
		LOGGER.debug("Extracting MAC Address");
		InetAddress ip;
		StringBuilder sb = new StringBuilder();
		try {
			ip = InetAddress.getLocalHost();

			// obtengo la mac
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			return sb.toString();

		} catch (UnknownHostException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SocketException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Devuelvo el mensaje SOAP como un String, para mostrarlo por un log
	 *
	 * @param soapMessage
	 * @return string
	 */
	public static String getSOAPMessageAsString(SOAPMessage soapMessage) {
		try {
			TransformerFactory tff = TransformerFactory.newInstance();
			Transformer tf = tff.newTransformer();

			// Set formatting
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			Source sc = soapMessage.getSOAPPart().getContent();

			ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(streamOut);
			tf.transform(sc, result);

			String strMessage = streamOut.toString();
			return strMessage;
		} catch (Exception e) {
			LOGGER.error("Exception in getSOAPMessageAsString " + e.getMessage());
			return null;
		}
	}

	public static String getMethodFromExceptionTrace(SOAPElement faultTrace) {
		SOAPElement ele = null;
		NodeList nodeList = faultTrace.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			ele = (SOAPElement)nodeList.item(i);
			if(!ele.getAttribute("class").contains("es.caib.gdib")){
				ele = (SOAPElement)nodeList.item(i-1);
				return ele.getAttribute("method");
			}
		}
		return null;
	}

	public static String getMethodFromExceptionTraceRegex(SOAPMessage msg){
		String value = "";
		String msgString = getSOAPMessageAsString(msg);
		msgString = getMatch(msgString, "(<.*RepositoryServiceSoapPort|.*MigrationServiceSoapPort|.*CSVServiceSoapPort)(.*?)(/>)");
		value = getMatch(msgString, "(.*method=\")(.*?)(\")");
		return value;
	}

	private static String getMatch(String string, String patternString){
		String value = "";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			value = matcher.group(2);
		}
		return value;
	}

	/**
	 * Metodo utilizado en test para generar un SOAPMessage a trave de un
	 * fichero XML
	 *
	 * @param xml
	 * @return
	 * @throws SOAPException
	 * @throws IOException
	 */
	public static SOAPMessage getSoapMessageFromFile(File xml) throws SOAPException, IOException {
	    MessageFactory factory = MessageFactory.newInstance();
	    SOAPMessage message = factory.createMessage(new MimeHeaders(), new FileInputStream(xml));
	    return message;
	}

}
