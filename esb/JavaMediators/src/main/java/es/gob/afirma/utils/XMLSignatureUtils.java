package es.gob.afirma.utils;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.dsig.XMLSignature;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParseException;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESTimeStampType;
import es.gob.afirma.signature.SigningException;
import es.gob.afirma.signature.xades.IXMLConstants;

/**
 * Method that manages operations over XML signatures.
 * 
 * @author RICOH
 *
 */
public final class XMLSignatureUtils {

	/**
	 * Constant attribute that represents the URI of the namespace associated to XAdES v1.3.2. 
	 */
	public static final String XMLNS_1_3_2 = "http://uri.etsi.org/01903/v1.3.2#";

	/**
	 * Constructor method for the class XMLSignatureUtils.java. 
	 */
	private XMLSignatureUtils() {
	}

	/**
	 * Method that obtains the signing certificate of a XAdES signature.
	 * @param signatureElement Parameter that represents <code>ds:Signature</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @return an object that represents the signing certificate.
	 * @throws UtilsException If the method fails.
	 */
	public static X509Certificate getSigningCertificate(Element signatureElement, String signatureId) throws UtilsException {
		// Comprobamos que se ha indicado el elemento ds:Signature
		if (signatureElement == null) {
			throw new IllegalArgumentException("No se ha indicado el elemento Signature.");
		}

		// Accedemos al elemento xades:QualifyingProperties
		Element qualifyingPropertiesElement = getElement(signatureElement, XMLNS_1_3_2, IXMLConstants.ELEMENT_QUALIFIYING_PROPERTIES, signatureId, true);

		// Accedemos al elemento xades:SignedProperties
		Element signedPropertiesElement = getElement(qualifyingPropertiesElement, XMLNS_1_3_2, IXMLConstants.ELEMENT_SIGNED_PROPERTIES, signatureId, true);

		// Accedemos al elemento xades:SignedSignatureProperties
		Element signedSignaturePropertiesElement = getElement(signedPropertiesElement, XMLNS_1_3_2, IXMLConstants.ELEMENT_SIGNED_SIGNATURE_PROPERTIES, signatureId, true);

		// Accedemos al elemento xades:SigningCertificate
		Element signingCertificateElement = getElement(signedSignaturePropertiesElement, XMLNS_1_3_2, IXMLConstants.ELEMENT_SIGNING_CERTIFICATE, signatureId, false);

		// Si hemos encontrado el elemento xades:SigningCertificate
		if (signingCertificateElement != null) {
			// Accedemos al elemento xades:Cert
			Element certElement = getElement(signingCertificateElement, XMLNS_1_3_2, IXMLConstants.ELEMENT_CERT, signatureId, true);

			// Accedemos al primer elemento xades:CertDigest
			Element certDigestElement = getElement(certElement, XMLNS_1_3_2, IXMLConstants.ELEMENT_CERT_DIGEST, signatureId, true);

			// Recuperamos del elemento xades:SigningCertificate el algoritmo de
			// hash
			String signingCertificateHashAlgorithm = getSigningCertificateHashAlgorithm(certDigestElement, signatureId);

			// Accedemos al elemento ds:DigestValue
			Element digestValueElement = getElement(certDigestElement, XMLSignature.XMLNS, IXMLConstants.ELEMENT_DIGEST_VALUE, signatureId, true);

			// Obtenemos el valor del resumen del certificado firmante
			String signingCertificateDigest = digestValueElement.getTextContent();

			// Accedemos al elemento ds:KeyInfo
			Element keyInfoElement = getElement(signatureElement, XMLSignature.XMLNS, IXMLConstants.ELEMENT_KEY_INFO, signatureId, true);

			// Accedemos al elemento ds:X509Data
			Element x509DataElement = getElement(keyInfoElement, XMLSignature.XMLNS, IXMLConstants.ELEMENT_X509_DATA, signatureId, true);

			// Accedemos a la lista de elementos ds:X509Certificate
			NodeList x509CertificateNodeList = x509DataElement.getElementsByTagNameNS(XMLSignature.XMLNS, IXMLConstants.ELEMENT_X509CERTIFICATE);

			// Comprobamos que existe al menos un certificado
			if (x509CertificateNodeList.getLength() == 0) {
				throw new UtilsException("La firma XML con Id " + signatureId + " no contiene ningún elemento ds:X509Certificate dentro de ds:KeyInfo/ds:X509Data.");
			}

			try {
				// Instanciamos un mapa donde ubicar los certificados incluidos
				// en
				// ds:KeyInfo/ds:X509Data. La clave será el resumen en Base64
				// del
				// certificado
				Map<String, X509Certificate> mapCertificatesIntoKeyInfo = new HashMap<String, X509Certificate>();

				// Recorremos la lista de certificados
				for (int i = 0; i < x509CertificateNodeList.getLength(); i++) {
					if (x509CertificateNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
						// Accedemos al elemento ds:X509Certificate
						Element x509CertificateElement = (Element) x509CertificateNodeList.item(i);

						// Obtenemos el certificado codificado en Base64
						String encodedCert = x509CertificateElement.getTextContent();

						// Obtenemos el certificado como tal
						X509Certificate cert = CertificateUtils.getCertfromByteArray(Base64.decode(encodedCert));

						// Añadimos al mapa una entrada
						mapCertificatesIntoKeyInfo.put(new String(Base64.encode(CryptoUtil.digest(signingCertificateHashAlgorithm, cert.getEncoded()))), cert);
					}
				}

				// Extraemos del mapa aquél certificado cuyo resumen coincide
				// con el
				// resumen del certificado firmante
				X509Certificate signingCertificate = mapCertificatesIntoKeyInfo.remove(signingCertificateDigest);

				// Comprobamos que hemos encontrado el certificado firmante
				if (signingCertificate == null) {
					throw new UtilsException("No se ha encontrado dentro de ds:KeyInfo/ds:X509Data el certificado firmante definido en xades:SigningCertificate para la firma XML con Id [" + signatureId + "].");
				}
				return signingCertificate;
			} catch (SigningException e) {
				throw new UtilsException("Se ha producido un error procesando los certificados incluidos en ds:KeyInfo/ds:X509Data para la firma XML con Id [" + signatureId + "].", e);
			} catch (CertificateException e) {
				throw new UtilsException("Se ha producido un error procesando los certificados incluidos en ds:KeyInfo/ds:X509Data para la firma XML con Id [" + signatureId + "].", e);
			}
		}
		// Si no hemos encontrado el elemento xades:SigningCertificate
		else {
			throw new UtilsException("La firma XML con Id [" + signatureId + "] no contiene el elemento xades:SigningCertificate.");
		}

	}

	/**
	 * Method that obtains the hash algorithm used to calculate the digest of the signing certificate of a XAdES signature.
	 * @param certDigestElement Parameter that represents <code>CertDigest</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @return the hash algorithm used to calculate the digest of the signing certificate.
	 * @throws UtilsException If the method fails.
	 */
	private static String getSigningCertificateHashAlgorithm(Element certDigestElement, String signatureId) throws UtilsException {
		// Accedemos al elemento ds:DigestMethod
		Element digestMethodElement = getElement(certDigestElement, XMLSignature.XMLNS, IXMLConstants.ELEMENT_DIGEST_METHOD, signatureId, true);

		// Accedemos al atributo Algorithm del elemento ds:DigestMethod
		String xmlHashAlg = digestMethodElement.getAttribute(IXMLConstants.ATTRIBUTE_ALGORITHM);
		if (xmlHashAlg == null) {
			throw new UtilsException("El elemento xades:SigningCertificate de la firma XML con Id [" + signatureId + "] está mal formado: El primer elemento xades:Cert contiene un elemento xades:CertDigest con un elemento ds:DigestMethod sin el atributo Algorithm.");
		}
		return CryptoUtil.translateXmlDigestAlgorithm(xmlHashAlg);
	}

	/**
	 * Method that obtains a element from a XAdES signature.
	 * @param parentElement Parameter that represents the parent element.
	 * @param namespaceURI Parameter that represents the namespace URI of the elements to match on. The special value "*" matches all namespaces.
	 * @param elementName Parameter that represents the name of the element to retrieve.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of the signature.
	 * @param isRequired Parameter that indicates whether the elements is required (true) or not (false).
	 * @return an object that represents the element, or <code>null</code> if the element isn't a child of the parent element.
	 * @throws UtilsException If the parent element hasn't the serached element and it's required.
	 */
	public static Element getElement(Element parentElement, String namespaceURI, String elementName, String signatureId, boolean isRequired) throws UtilsException {
		Element element = null;

		// Accedemos al elemento solicitado
		NodeList listElements = parentElement.getElementsByTagNameNS(namespaceURI, elementName);

		// Si no hemos encontrado el elemento
		if (listElements == null || listElements.getLength() == 0) {
			// Si el elemento es requerido, lanzamos una excepción
			if (isRequired) {
				throw new UtilsException("Firma XML con Id [" + signatureId + "] mal formada: Se ha encontrado un elemento [" + parentElement.getLocalName() + "] que no contiene al elemento [" + elementName + "].");
			}
		} else {
			element = (Element) listElements.item(0);
		}
		return element;
	}

	/**
	 * Method that indicates if the signature is an XMLTimeStamp element by the OASIS-DSS specifications (true) or not (false).
	 * @param eSignature Parameter that represents the XML signature.
	 * @return a boolean that indicates if the signature is an XMLTimeStamp element by the OASIS-DSS specifications (true) or not (false).
	 */
	public static boolean isDSSTimestamp(Node eSignature) {
		Node parent = eSignature.getParentNode();
		if (parent != null) {
			return parent.getNodeName().substring(parent.getNodeName().indexOf(":") + 1).equals(IXMLConstants.ELEMENT_TIMESTAMP) || parent.getNodeName().substring(parent.getNodeName().indexOf(":") + 1).equals(IXMLConstants.ELEMENT_TIMESTAMP);
		}
		return false;
	}

	/**
	 * Method that obtains a sorted list with the information about the time-stamps contained inside of <code>SignatureTimeStamp</code> elements of a signature/counter-signature.
	 * @param unsignedSignaturePropertiesElement Parameter that represents <code>UnsignedSignatureProperties</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @return the list sorted by generation date ascendant.
	 * @throws UtilsException If the method fails.
	 */
	public static List<XAdESTimeStampType> getListSignatureTimeStamps(Element unsignedSignaturePropertiesElement, String signatureId) throws UtilsException {
		// Instanciamos la lista que devolver
		List<XAdESTimeStampType> resultList = new ArrayList<XAdESTimeStampType>();

		// Obtenemos la lista de hijos del elemento
		// UnsignedSignatureProperties
		NodeList childNodes = unsignedSignaturePropertiesElement.getChildNodes();

		// Recorremos la lista de hijos del elemento
		// xades:UnsignedSignatureProperties
		for (int i = 0; i < childNodes.getLength(); i++) {
			// Accedemos al elemento hijo
			Node childElement = childNodes.item(i);

			// Si el elemento es SignatureTimeStamp
			if (childElement.getNodeType() == Node.ELEMENT_NODE && childElement.getNamespaceURI().equals(XMLSignatureUtils.XMLNS_1_3_2) && childElement.getLocalName().equals(IXMLConstants.ELEMENT_SIGNATURE_TIMESTAMP)) {
				// Accedemos al elemento SignatureTimeStamp
				Element signatureTimeStampElement = (Element) childElement;

				// Accedemos al atributo Id del elemento SignatureTimeStamp
				String signatureTimeStampId = signatureTimeStampElement.getAttribute(IXMLConstants.ATTRIBUTE_ID);

				// Comprobamos que el elemento xades:SignatureTimeStamp sólo
				// incluye
				// un sello de tiempo
				checkTimestampsNumber(signatureTimeStampElement, signatureTimeStampId, signatureId);

				// Comprobamos de qué tipo es el sello de tiempo contenido
				if (signatureTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_ENCAPSULATED_TIMESTAMP).item(0) != null) {
					// Sello de tiempo ASN.1
					String encodedTST = signatureTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_ENCAPSULATED_TIMESTAMP).item(0).getTextContent();

					// Obtenemos el sello de tiempo
					TimeStampToken timestamp = null;
					try {
						timestamp = new TimeStampToken(new CMSSignedData(Base64.decode(encodedTST.getBytes())));
					} catch (Exception e) {
						// Sello de tiempo ASN.1 incorrecto
						throw new UtilsException("El elemento con Id [" + signatureTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] posee un sello de tiempo ASN.1 mal formado.", e);
					}
					// Obtenemos la fecha de generación del sello de tiempo
					Date generationDate = timestamp.getTimeStampInfo().getGenTime();

					// Obtenemos el certificado firmante del sello de tiempo
					X509Certificate signingCertificate = CertificateUtils.getSigningCertificate(timestamp);

					// Instanciamos el objeto con información del sello de
					// tiempo y lo añadimos a la lista que devolver
					resultList.add(new XAdESTimeStampType(signatureTimeStampId, signingCertificate, generationDate));
				} else if (signatureTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_XML_TIMESTAMP).item(0) != null) {
					// Sello de tiempo XML
					Element xmlTimeStampElement = (Element) signatureTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_XML_TIMESTAMP).item(0);

					// Accedemos al elemento dss:TimeStamp
					Element timeStampElement = (Element) xmlTimeStampElement.getElementsByTagNameNS(DSSConstants.OASIS_CORE_1_0_NS, IXMLConstants.ELEMENT_TIMESTAMP).item(0);
					if (timeStampElement == null) {
						// Sello de tiempo mal formado
						throw new UtilsException("El elemento con Id [" + signatureTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] está mal formado.");
					}
					// Obtenemos la fecha de generación del sello de tiempo
					Date generationDate = getGenTimeXMLTimestamp(xmlTimeStampElement, signatureTimeStampId, signatureId);

					// Obtenemos el certificado firmante del sello de tiempo
					X509Certificate signingCertificate = getCertificateFromXMLTimestamp(xmlTimeStampElement, signatureTimeStampId, signatureId);

					// Instanciamos el objeto con información del sello de
					// tiempo y lo añadimos a la lista que devolver
					resultList.add(new XAdESTimeStampType(signatureTimeStampId, signingCertificate, generationDate));
				} else {
					// Sello de tiempo mal formado
					throw new UtilsException("El elemento con Id [" + signatureTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] está mal formado.");
				}
			}
		}

		// Ordenamos la lista con información de todos los elementos
		// SignatureTimeStamp descendentemente por fecha de generación del
		// sello de tiempo contenido
		Collections.reverse(resultList);

		// Devolvemos la lista con los sellos de tiempo contenidos en elementos
		// SignatureTimeStamp
		return resultList;
	}

	/**
	 * Method that obtains the signer certificate from a XML timestamp.
	 * @param xmlTimestamp Parameter that represents the <code>XMLTimestamp</code> element.
	 * @param signatureTimeStampId Parameter that represents the value of the <code>Id</code> attribute of the <code>xades:SignatureTimeStamp</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @return an object that represents the signer certificate.
	 * @throws UtilsException If the method fails.
	 */
	private static X509Certificate getCertificateFromXMLTimestamp(Element xmlTimestamp, String signatureTimeStampId, String signatureId) throws UtilsException {
		// Recorremos el árbol de elementos hasta encontrar la firma
		X509Certificate cert = null;
		Node xmlSigNode = getXMLSignatureNode(xmlTimestamp);

		// Una vez hemos encontrado la firma
		if (xmlSigNode != null) {
			try {
				org.apache.xml.security.signature.XMLSignature sig = new org.apache.xml.security.signature.XMLSignature((Element) xmlSigNode, "");
				if (sig.getKeyInfo() != null && sig.getKeyInfo().getX509Certificate() != null) {
					// Accedemos al certificado
					cert = sig.getKeyInfo().getX509Certificate();
				}
			} catch (Exception e) {
				throw new UtilsException("Se ha producido un error intentando acceder al certificado del sello de tiempo XML contenido en el elemento con Id [" + signatureTimeStampId + "] para la firma XML con Id [" + signatureId + "].", e);
			}
		}
		if (cert == null) {
			throw new UtilsException("No ha sido posible acceder al certificado del sello de tiempo XML contenido en el elemento con Id [" + signatureTimeStampId + "] para la firma XML con Id [" + signatureId + "].");
		}
		return cert;
	}

	/**
	 * Method that obtains the ds:Signature element from a XML timestamp.
	 * @param xmlTimestamp Parameter that represents the XML timestamp.
	 * @return an object that represents the ds:Signature element.
	 */
	private static Node getXMLSignatureNode(Element xmlTimestamp) {
		Node xmlSigNode = null;
		if (xmlTimestamp.getElementsByTagNameNS(XMLSignature.XMLNS, IXMLConstants.ELEMENT_SIGNATURE).getLength() > 0) {
			xmlSigNode = xmlTimestamp.getElementsByTagNameNS(XMLSignature.XMLNS, IXMLConstants.ELEMENT_SIGNATURE).item(0);
		}
		return xmlSigNode;
	}

	/**
	 * Method that checks if a <code>xades:SignatureTimeStamp</code> element only contains a timestamp.
	 * @param signatureTimeStampElement Parameter that presents the <code>xades:SignatureTimeStamp</code> element.
	 * @param signatureTimeStampId Parameter that represents the value of the <code>Id</code> attribute of the <code>xades:SignatureTimeStamp</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @throws UtilsException If the method fails.
	 */
	private static void checkTimestampsNumber(Element signatureTimeStampElement, String signatureTimeStampId, String signatureId) throws UtilsException {
		if (signatureTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_ENCAPSULATED_TIMESTAMP).getLength() > 1 || signatureTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_XML_TIMESTAMP).getLength() > 1) {
			throw new UtilsException("El elemento con Id [" + signatureTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] posee más de un sello de tiempo.");
		}
	}

	/**
	 * Method that obtains the gentime from a XML timestamp.
	 * @param xmlTimestamp Parameter that represents the XML timestamp.
	 * @param signatureTimeStampId Parameter that represents the <code>Id</code> attribute of <code>SignatureTimeStamp</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @return an object that represents the gentime.
	 * @throws UtilsException If the method fails.
	 */
	private static Date getGenTimeXMLTimestamp(Element xmlTimestamp, String signatureTimeStampId, String signatureId) throws UtilsException {
		// Obtenemos la lista de References
		NodeList nl = xmlTimestamp.getChildNodes().item(0).getChildNodes();

		// Obtenemos el elemento SignedInfo
		Node si = getXMLSignedInfo(nl);

		// Obtenemos la URI del sello de tiempo
		String uriTST = getXMLURITimestamp(si);

		// Obtenemos la fecha de generación del sello de tiempo
		return searchXMLGenTimeTimestamp(nl, uriTST, signatureTimeStampId, signatureId);
	}

	/**
	 * Method that obtains the ds:SignedInfo element from a nodes list.
	 * @param nl Parameter that represents the nodes list.
	 * @return an object that represents the ds:SignedInfo element.
	 */
	private static Node getXMLSignedInfo(NodeList nl) {
		Node si = null;
		int i = 0;
		while (si == null && i < nl.getLength()) {
			if (nl.item(0).getLocalName().equals(IXMLConstants.ELEMENT_SIGNED_INFO)) {
				si = nl.item(i);
			}
			i++;
		}
		return si;
	}

	/**
	 * Method that obtains the URI of a XML timestamp.
	 * @param signedInfo Parameter that represents the ds:SignedInfo element of the XML timestamp.
	 * @return the URI.
	 */
	private static String getXMLURITimestamp(Node signedInfo) {
		String uriTST = null;
		if (signedInfo != null) {
			NodeList refs = signedInfo.getChildNodes();
			int i = 0;
			while (uriTST == null && i < refs.getLength()) {
				if (refs.item(i).getLocalName().equals(IXMLConstants.ELEMENT_REFERENCE)) {
					NamedNodeMap attsRef = refs.item(i).getAttributes();
					if (attsRef.getNamedItem(IXMLConstants.ATTRIBUTE_TYPE) != null && attsRef.getNamedItem(IXMLConstants.ATTRIBUTE_TYPE).getNodeValue().equals("urn:oasis:names:tc:dss:1.0:core:schema:XMLTimeStampToken") && attsRef.getNamedItem(IXMLConstants.ATTRIBUTE_URI) != null) {
						uriTST = attsRef.getNamedItem(IXMLConstants.ATTRIBUTE_URI).getNodeValue();
					}

				}
				i++;
			}
		}
		return uriTST;
	}

	/**
	 * Method that searchs the gentime of a XML timestamp from a nodes list.
	 * @param nl Parameter that represents the nodes list.
	 * @param uriTST Parameter that represents the URI of the timestamp.
	 * @param signatureTimeStampId Parameter that represents the <code>Id</code> attribute of <code>SignatureTimeStamp</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @return the generation time of the XML timestamp or <code>null</code> if it cannot be found.
	 * @throws UtilsException If the generation time cannot parse to UTC format.
	 */
	private static Date searchXMLGenTimeTimestamp(NodeList nl, String uriTST, String signatureTimeStampId, String signatureId) throws UtilsException {
		boolean enc = false;
		Date genTime = null;
		int i = 0;
		if (nl != null && uriTST != null) {
			while (i < nl.getLength() && !enc) {
				if (nl.item(i).getLocalName().equals(IXMLConstants.ELEMENT_OBJECT) && nl.item(i).getFirstChild().getLocalName().equals(IXMLConstants.ELEMENT_TST_INFO)) {
					NamedNodeMap attsObject = nl.item(i).getAttributes();
					String idValue = attsObject.getNamedItem(IXMLConstants.ATTRIBUTE_ID).getNodeValue();
					enc = uriTST.equals("#" + idValue);
					if (!enc) {
						NamedNodeMap attsTst = nl.item(i).getFirstChild().getAttributes();
						idValue = attsTst.getNamedItem(IXMLConstants.ATTRIBUTE_ID).getNodeValue();
						enc = uriTST.equals("#" + idValue);
					}
					if (enc) {
						genTime = searchXMLGenTimeTimestampAux(nl, i, signatureTimeStampId, signatureId);
					}
				}
				i++;
			}
		}
		return genTime;
	}

	/**
	 * Auxiliar method for {@link #searchXMLGenTimeTimestamp(NodeList, String)}.
	 * @param nl Parameter that represents the nodes list.
	 * @param i Parameter that represents the number of the node contains inside of the nodes list to process.
	 * @param signatureTimeStampId Parameter that represents the <code>Id</code> attribute of <code>SignatureTimeStamp</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @return the generation time of the XML timestamp.
	 * @throws UtilsException If the generation time cannot parse to UTC format.
	 */
	private static Date searchXMLGenTimeTimestampAux(NodeList nl, int i, String signatureTimeStampId, String signatureId) throws UtilsException {
		Date genTime = null;
		NodeList childs = nl.item(i).getFirstChild().getChildNodes();
		int j = 0;
		while (j < childs.getLength() && genTime == null) {
			if (childs.item(j).getLocalName().equals(IXMLConstants.ELEMENT_CREATION_TIME)) {
				genTime = parseDateToUTCDate(childs.item(j).getFirstChild().getNodeValue(), signatureTimeStampId, signatureId);
			}
			j++;
		}
		return genTime;
	}

	/**
	 * Method that obtains a date with UTC format.
	 * @param dateToParse Parameter that represents the date to parse.
	 * @param signatureTimeStampId Parameter that represents the <code>Id</code> attribute of <code>SignatureTimeStamp</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @return the date with UTC format
	 * @throws UtilsException If the date cannot parse to UTC format.
	 */
	private static Date parseDateToUTCDate(String dateToParse, String signatureTimeStampId, String signatureId) throws UtilsException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			return sdf.parse(dateToParse);
		} catch (ParseException e) {
			throw new UtilsException("El elemento con Id [" + signatureTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] posee un sello de tiempo XML con una fecha de generación con formato incorrecto.", e);
		}
	}

	/**
	 * Method that obtains a sorted list with the information about the time-stamps contained inside of <code>SigAndRefsTimeStamp</code> and <code>RefsOnlyTimeStamp</code> elements
	 * of a signature/counter-signature.
	 * @param unsignedSignaturePropertiesElement Parameter that represents <code>UnsignedSignatureProperties</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @return the list sorted by generation date ascendant.
	 * @throws UtilsException If the method fails.
	 */
	public static List<XAdESTimeStampType> getListReferencesTimeStamps(Element unsignedSignaturePropertiesElement, String signatureId) throws UtilsException {
		// Instanciamos la lista que devolver
		List<XAdESTimeStampType> resultList = new ArrayList<XAdESTimeStampType>();

		// Obtenemos la lista de hijos del elemento
		// UnsignedSignatureProperties
		NodeList childNodes = unsignedSignaturePropertiesElement.getChildNodes();

		// Recorremos la lista de hijos del elemento
		// xades:UnsignedSignatureProperties
		for (int i = 0; i < childNodes.getLength(); i++) {
			// Accedemos al elemento hijo
			Node childElement = childNodes.item(i);

			// Si el elemento es SigAndRefsTimeStamp o RefsOnlyTimeStamp
			if (childElement.getNodeType() == Node.ELEMENT_NODE && childElement.getNamespaceURI().equals(XMLSignatureUtils.XMLNS_1_3_2) && (childElement.getLocalName().equals(IXMLConstants.ELEMENT_SIG_AND_REFS_TIMESTAMP) || childElement.getLocalName().equals(IXMLConstants.ELEMENT_REFS_ONLY_TIMESTAMP))) {
				// Accedemos al elemento
				Element refsTimeStampElement = (Element) childElement;

				// Accedemos al atributo Id del elemento
				String refsTimeStampId = refsTimeStampElement.getAttribute(IXMLConstants.ATTRIBUTE_ID);

				// Comprobamos que el elemento sólo
				// incluye
				// un sello de tiempo
				checkTimestampsNumber(refsTimeStampElement, refsTimeStampId, signatureId);

				// Comprobamos de qué tipo es el sello de tiempo contenido
				if (refsTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_ENCAPSULATED_TIMESTAMP).item(0) != null) {
					// Sello de tiempo ASN.1
					String encodedTST = refsTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_ENCAPSULATED_TIMESTAMP).item(0).getTextContent();

					// Obtenemos el sello de tiempo
					TimeStampToken timestamp = null;
					try {
						timestamp = new TimeStampToken(new CMSSignedData(Base64.decode(encodedTST.getBytes())));
					} catch (Exception e) {
						// Sello de tiempo ASN.1 incorrecto
						throw new UtilsException("El elemento con Id [" + refsTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] posee un sello de tiempo ASN.1 mal formado.", e);
					}
					// Obtenemos la fecha de generación del sello de tiempo
					Date generationDate = timestamp.getTimeStampInfo().getGenTime();

					// Obtenemos el certificado firmante del sello de tiempo
					X509Certificate signingCertificate = CertificateUtils.getSigningCertificate(timestamp);

					// Instanciamos el objeto con información del sello de
					// tiempo y lo añadimos a la lista que devolver
					resultList.add(new XAdESTimeStampType(refsTimeStampId, signingCertificate, generationDate));
				} else if (refsTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_XML_TIMESTAMP).item(0) != null) {
					// Sello de tiempo XML
					Element xmlTimeStampElement = (Element) refsTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_XML_TIMESTAMP).item(0);

					// Accedemos al elemento dss:TimeStamp
					Element timeStampElement = (Element) xmlTimeStampElement.getElementsByTagNameNS(DSSConstants.OASIS_CORE_1_0_NS, IXMLConstants.ELEMENT_TIMESTAMP).item(0);
					if (timeStampElement == null) {
						// Sello de tiempo mal formado
						throw new UtilsException("El elemento con Id [" + refsTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] está mal formado.");
					}
					// Obtenemos la fecha de generación del sello de tiempo
					Date generationDate = getGenTimeXMLTimestamp(xmlTimeStampElement, refsTimeStampId, signatureId);

					// Obtenemos el certificado firmante del sello de tiempo
					X509Certificate signingCertificate = getCertificateFromXMLTimestamp(xmlTimeStampElement, refsTimeStampId, signatureId);

					// Instanciamos el objeto con información del sello de
					// tiempo y lo añadimos a la lista que devolver
					resultList.add(new XAdESTimeStampType(refsTimeStampId, signingCertificate, generationDate));
				} else {
					// Sello de tiempo mal formado
					throw new UtilsException("El elemento con Id [" + refsTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] está mal formado.");
				}
			}
		}

		// Ordenamos la lista con información de todos los elementos
		// SigAndRefsTimeStamp y RefsOnlyTimeStamp ascendentemente por fecha de
		// generación del
		// sello de tiempo contenido
		Collections.reverse(resultList);

		// Devolvemos la lista con los sellos de tiempo contenidos en elementos
		// SigAndRefsTimeStamp y RefsOnlyTimeStamp
		return resultList;
	}

	/**
	 * Method that obtains a sorted list with the information about the time-stamps contained inside of <code>ArchiveTimeStamp</code> elements of a signature/counter-signature.
	 * @param unsignedSignaturePropertiesElement Parameter that represents <code>UnsignedSignatureProperties</code> element.
	 * @param signatureId Parameter that represents the <code>Id</code> attribute of <code>ds:Signature</code> element.
	 * @return the list sorted by generation date ascendant.
	 * @throws UtilsException If the method fails.
	 */
	public static List<XAdESTimeStampType> getListArchiveTimeStamps(Element unsignedSignaturePropertiesElement, String signatureId) throws UtilsException {
		// Instanciamos la lista que devolver
		List<XAdESTimeStampType> resultList = new ArrayList<XAdESTimeStampType>();

		// Obtenemos la lista de hijos del elemento
		// UnsignedSignatureProperties
		NodeList childNodes = unsignedSignaturePropertiesElement.getChildNodes();

		// Recorremos la lista de hijos del elemento
		// xades:UnsignedSignatureProperties
		for (int i = 0; i < childNodes.getLength(); i++) {
			// Accedemos al elemento hijo
			Node childElement = childNodes.item(i);

			// Si el elemento es ArchiveTimeStamp
			if (childElement.getNodeType() == Node.ELEMENT_NODE && childElement.getLocalName().equals(IXMLConstants.ELEMENT_ARCHIVE_TIMESTAMP)) {
				// Accedemos al elemento ArchiveTimeStamp
				Element archiveTimeStampElement = (Element) childElement;

				// Accedemos al atributo Id del elemento ArchiveTimeStamp
				String archiveTimeStampId = archiveTimeStampElement.getAttribute(IXMLConstants.ATTRIBUTE_ID);

				// Comprobamos que el elemento ArchiveTimeStamp sólo
				// incluye
				// un sello de tiempo
				checkTimestampsNumber(archiveTimeStampElement, archiveTimeStampId, signatureId);

				// Comprobamos de qué tipo es el sello de tiempo contenido
				if (archiveTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_ENCAPSULATED_TIMESTAMP).item(0) != null) {
					// Sello de tiempo ASN.1
					String encodedTST = archiveTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_ENCAPSULATED_TIMESTAMP).item(0).getTextContent();

					// Obtenemos el sello de tiempo
					TimeStampToken timestamp = null;
					try {
						timestamp = new TimeStampToken(new CMSSignedData(Base64.decode(encodedTST.getBytes())));
					} catch (Exception e) {
						// Sello de tiempo ASN.1 incorrecto
						throw new UtilsException("El elemento con Id [" + archiveTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] posee un sello de tiempo ASN.1 mal formado.", e);
					}
					// Obtenemos la fecha de generación del sello de tiempo
					Date generationDate = timestamp.getTimeStampInfo().getGenTime();

					// Obtenemos el certificado firmante del sello de tiempo
					X509Certificate signingCertificate = CertificateUtils.getSigningCertificate(timestamp);

					// Instanciamos el objeto con información del sello de
					// tiempo y lo añadimos a la lista que devolver
					resultList.add(new XAdESTimeStampType(archiveTimeStampId, signingCertificate, generationDate));
				} else if (archiveTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_XML_TIMESTAMP).item(0) != null) {
					// Sello de tiempo XML
					Element xmlTimeStampElement = (Element) archiveTimeStampElement.getElementsByTagNameNS(XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_XML_TIMESTAMP).item(0);

					// Accedemos al elemento dss:TimeStamp
					Element timeStampElement = (Element) xmlTimeStampElement.getElementsByTagNameNS(DSSConstants.OASIS_CORE_1_0_NS, IXMLConstants.ELEMENT_TIMESTAMP).item(0);
					if (timeStampElement == null) {
						// Sello de tiempo mal formado
						throw new UtilsException("El elemento con Id [" + archiveTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] está mal formado.");
					}
					// Obtenemos la fecha de generación del sello de tiempo
					Date generationDate = getGenTimeXMLTimestamp(xmlTimeStampElement, archiveTimeStampId, signatureId);

					// Obtenemos el certificado firmante del sello de tiempo
					X509Certificate signingCertificate = getCertificateFromXMLTimestamp(xmlTimeStampElement, archiveTimeStampId, signatureId);

					// Instanciamos el objeto con información del sello de
					// tiempo y lo añadimos a la lista que devolver
					resultList.add(new XAdESTimeStampType(archiveTimeStampId, signingCertificate, generationDate));
				} else {
					// Sello de tiempo mal formado
					throw new UtilsException("El elemento con Id [" + archiveTimeStampId + "] contenido en la firma XML con Id [" + signatureId + "] está mal formado.");
				}
			}
		}

		// Ordenamos la lista con información de todos los elementos
		// ArchiveTimeStamp ascendentemente por fecha de generación del
		// sello de tiempo contenido
		Collections.reverse(resultList);

		// Devolvemos la lista con los sellos de tiempo contenidos en elementos
		// ArchiveTimeStamp
		return resultList;
	}

	/**
	 * Method that obtains the list of signatures contained inside of a XML document.
	 * @param document Parameter that represents the XML document.
	 * @return a list with the found signatures.
	 */
	public static List<Element> getListXMLSignatures(Document document) {
		// Definimos una lista donde ubicar los elementos ds:Signature, es
		// decir, aquellos que sean firmas o contra-firmas y no
		// formen parte de sellos de tiempo XML
		List<Element> listSignatures = new ArrayList<Element>();

		// Accedemos a la lista de elementos ds:Signature
		NodeList nl = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");

		// Recorremos la lista de elementos ds:Signature
		for (int i = 0; i < nl.getLength(); i++) {
			// Accedemos al elemento ds:Signature
			Element signatureElement = (Element) nl.item(i);

			// Comprobamos que no forme parte de ningún sello
			if (!isDSSTimestamp(signatureElement)) {
				// Añadimos el elemento ds:Signature a la lista de firmas
				// principales
				listSignatures.add(signatureElement);
			}
		}
		return listSignatures;
	}

	/**
	 * Method that obtains a {@link Document} from a XML document.
	 * @param xmlDocument Parameter that represents the XML document.
	 * @return an object that represents the XML document.
	 * @throws SignatureParseException If the method fails.
	 */
	public static Document getXMLDocument(byte[ ] xmlDocument) throws SignatureParseException {
		try {
			javax.xml.parsers.DocumentBuilder db = null;
			javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setIgnoringComments(true);
			dbf.setValidating(false);
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setExpandEntityReferences(false);
			db = dbf.newDocumentBuilder();
			db.setErrorHandler(null);
			return db.parse(new ByteArrayInputStream(xmlDocument));
		} catch (Exception e) {
			throw new SignatureParseException("La firma facilitada no tiene formato XML", e);
		}
	}
}
