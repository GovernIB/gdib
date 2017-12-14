package es.caib.archivodigital.esb.services.mediators.afirma.signature.xades;

import java.math.BigInteger;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.caib.archivodigital.esb.services.mediators.afirma.TimestampType;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureFormat;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParseException;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureUtils;
import es.gob.afirma.integraFacade.pojo.Result;
import es.gob.afirma.signature.xades.IXMLConstants;
import es.gob.afirma.utils.UtilsException;
import es.gob.afirma.utils.XMLSignatureUtils;

/**
 * Class that retrieves information from XAdES v1.3.2 and XAdES v1.4.2. signatures returned from <code>DSSAfirmaVerify</code> service of @firma platform v6.1.1.
 * This information is required by the documents validation service.
 * 
 * @author RICOH
 *
 */
public class XAdESSignatureParser extends SignatureParser {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 *  Attribute that represents the object that manages the log of the class.
	 */
	private static final Logger LOGGER = Logger.getLogger(XAdESSignatureParser.class);

	/**
	 * Attribute that represents a map with all <code>ds:Signature</code> elements of the signatures and counter-signatures contained inside of the signed XML document. The key
	 * is the element that represents the signature or counter-signature and the value is the signing certificate.
	 */
	private Map<Element, X509Certificate> mapSignatureElements = new HashMap<Element, X509Certificate>();

	/**
	 * Attribute that represents a map with the information about the time-stamp contained inside of <code>SignatureTimeStamp</code> elements of the signatures and counter-signatures.
	 * The key is the element that represents the signature or counter-signature and the value is the list of associated time-stamp information objects.
	 */
	private Map<Element, List<XAdESTimeStampType>> mapSignatureTimeStamps = new HashMap<Element, List<XAdESTimeStampType>>();

	/**
	 * Attribute that represents a map with the information about the time-stamp contained inside of <code>SigAndRefsTimeStamp</code> and <code>RefsOnlyTimeStamp</code> elements of
	 * the signatures and counter-signatures. The key is the element that represents the signature or counter-signature and the value is the list of associated time-stamp
	 * information objects.
	 */
	private Map<Element, List<XAdESTimeStampType>> mapReferencesTimeStamps = new HashMap<Element, List<XAdESTimeStampType>>();

	/**
	 * Attribute that represents a map with the information about the time-stamp contained inside of <code>ArchiveTimeStamp</code> elements of the signatures and counter-signatures.
	 * The key is the element that represents the signature or counter-signature and the value is the list of associated time-stamp information objects.
	 */
	private Map<Element, List<XAdESTimeStampType>> mapArchiveTimeStamps = new HashMap<Element, List<XAdESTimeStampType>>();

	/**
	 * Constructor method for the class XAdESSignatureParser.java.
	 * @param signatureFormat Parameter that represents the signature format.
	 * @param base64Signature Parameter that represents the signature encoded on Base64.
	 * @throws SignatureParseException If the constructor fails.
	 */
	public XAdESSignatureParser(SignatureFormat signatureFormat, String base64Signature) throws SignatureParseException {
		super(signatureFormat, base64Signature);

		// Decodificamos la firma codificada en Base64
		byte[ ] decodedSignature = null;
		try {
			decodedSignature = Base64.decode(base64Signature);
		} catch (DecoderException e) {
			String errorMsg = "La firma no se encuentra codificada en Base64.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg, e);
		}

		// Accedemos al documento XML
		Document doc = XMLSignatureUtils.getXMLDocument(decodedSignature);

		// Obtenemos la lista de elementos ds:Signature contenidos que no
		// pertenezcan a un sello de tiempo XML
		List<Element> listSignatureElements = XMLSignatureUtils.getListXMLSignatures(doc);

		// Recorremos la lista de firmas contenidas en el documento XML
		for (Element signatureElement: listSignatureElements) {

			// Obtenemos el identificador de la firma/contra-firma
			String signatureId = signatureElement.getAttribute(IXMLConstants.ATTRIBUTE_ID);
			signatureId = signatureId == null ? "" : signatureId;
			try {
				// Obtenemos el certificado firmante
				X509Certificate signingCertificate = XMLSignatureUtils.getSigningCertificate(signatureElement, signatureId);

				// Añadimos la entrada al mapa con las firmas y contra-firmas
				// contenidas en el documento XML
				mapSignatureElements.put(signatureElement, signingCertificate);

				// Accedemos al elemento xades:QualifyingProperties
				Element qualifyingPropertiesElement = XMLSignatureUtils.getElement(signatureElement, XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_QUALIFIYING_PROPERTIES, signatureId, true);

				// Accedemos al elemento xades:UnsignedProperties
				Element unsignedPropertiesElement = XMLSignatureUtils.getElement(qualifyingPropertiesElement, XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_UNSIGNED_PROPERTIES, signatureId, false);
				if (unsignedPropertiesElement != null) {
					// Accedemos al elemento UnsignedSignatureProperties
					Element unsignedSignaturePropertiesElement = XMLSignatureUtils.getElement(unsignedPropertiesElement, XMLSignatureUtils.XMLNS_1_3_2, IXMLConstants.ELEMENT_UNSIGNED_SIGNATURE_PROPERTIES, signatureId, false);
					if (unsignedSignaturePropertiesElement != null) {
						// Recuperamos la lista de sellos de tiempo contenidos
						// en
						// elementos SignatureTimeStamp
						List<XAdESTimeStampType> listSignatureTimeStamps = XMLSignatureUtils.getListSignatureTimeStamps(unsignedSignaturePropertiesElement, signatureId);

						// Añadimos la entrada al mapa con los sellos de tiempo
						// contenidos en elementos SignatureTimeStamp
						mapSignatureTimeStamps.put(signatureElement, listSignatureTimeStamps);

						// Recuperamos la lista de sellos de tiempo contenidos
						// en
						// elementos SigAndRefsTimeStamp y RefsOnlyTimeStamp
						List<XAdESTimeStampType> listReferencesTimeStamps = XMLSignatureUtils.getListReferencesTimeStamps(unsignedSignaturePropertiesElement, signatureId);

						// Añadimos la entrada al mapa con los sellos de tiempo
						// contenidos en elementos SigAndRefsTimeStamp y
						// RefsOnlyTimeStamp
						mapReferencesTimeStamps.put(signatureElement, listReferencesTimeStamps);

						// Recuperamos la lista de sellos de tiempo contenidos
						// en
						// elementos ArchiveTimeStamp
						List<XAdESTimeStampType> listArchiveTimeStamps = XMLSignatureUtils.getListArchiveTimeStamps(unsignedSignaturePropertiesElement, signatureId);

						// Añadimos la entrada al mapa con los sellos de tiempo
						// contenidos en elementos ArchiveTimeStamp
						mapArchiveTimeStamps.put(signatureElement, listArchiveTimeStamps);
					}
				}
			} catch (UtilsException e) {
				String errorMsg = "Se ha producido un error procesando la firma XML con Id [" + signatureId + "].";
				LOGGER.error(errorMsg);
				throw new SignatureParseException(errorMsg, e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getAllSignatures(java.lang.String, es.gob.afirma.integraFacade.pojo.Result, java.lang.String)
	 */
	@Override
	public final String getAllSignatures(String signatureFormat, Result dssResult, String extraInfo) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		// Recorremos la lista de firmas y contra-firmas contenidas en el
		// documento XML
		Iterator<Element> it = mapSignatureElements.keySet().iterator();
		while (it.hasNext()) {
			// Accedemos a la firma/contra-firma
			Element signatureElement = it.next();

			// Añadimos la información de la firma/contra-firma
			stringBuilder.append(getSignature(signatureFormat, dssResult, extraInfo, signatureElement));
		}

		return stringBuilder.toString();
	}

	/**
	 * Method that obtains information about a signer or counter-signer of a XML document on XML format.
	 * @param signatureFormat Parameter that represents the signature format associated to the signed XML document.
	 * @param dssResult Parameter that represents the information about the result of the process retrieved from @Firma.
	 * @param extraInfo Parameter that represents additional information.
	 * @param signatureElement Parameter that represents <code>ds:Signature</code> element.
	 * @return information about a signer or counter-signer of a XML document on XML format.
	 * @throws SignatureParseException If the method fails.
	 */
	private String getSignature(String signatureFormat, Result dssResult, String extraInfo, Element signatureElement) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<csgd:signaturesDetailedInfo>");

		// Resultado de validación
		stringBuilder.append(buildResultResponse(dssResult));

		// Formato de firma electrónica
		stringBuilder.append("<csgd:signatureFormat>");
		stringBuilder.append(signatureFormat);
		stringBuilder.append("</csgd:signatureFormat>");

		// Certificado firmante
		X509Certificate signingCertificate = mapSignatureElements.get(signatureElement);
		stringBuilder.append(buildValidateCertificateResults(signingCertificate));

		// Sellos de tiempo (-T)
		List<XAdESTimeStampType> listSignatureTimeStamps = mapSignatureTimeStamps.get(signatureElement);
		if (listSignatureTimeStamps != null) {
			for (XAdESTimeStampType signatureTimeStampType: listSignatureTimeStamps) {
				stringBuilder.append(buildTimeStampResult(signatureTimeStampType.getTimestampGenerationDate(), TimestampType.SIGNER, signatureTimeStampType.getTstCertificate()));
			}
		}

		// Sellos de tiempo (-X)
		List<XAdESTimeStampType> listReferencesTimeStamps = mapReferencesTimeStamps.get(signatureElement);
		if (listReferencesTimeStamps != null) {
			for (XAdESTimeStampType referenceTimeStampType: listReferencesTimeStamps) {
				stringBuilder.append(buildTimeStampResult(referenceTimeStampType.getTimestampGenerationDate(), TimestampType.CUSTODY, referenceTimeStampType.getTstCertificate()));
			}
		}

		// Sellos de tiempo (-A)
		List<XAdESTimeStampType> listArchiveTimeStamps = mapArchiveTimeStamps.get(signatureElement);
		if (listArchiveTimeStamps != null) {
			for (XAdESTimeStampType referenceTimeStampType: listArchiveTimeStamps) {
				stringBuilder.append(buildTimeStampResult(referenceTimeStampType.getTimestampGenerationDate(), TimestampType.ARCHIVE, referenceTimeStampType.getTstCertificate()));
			}
		}

		// Información adicional a incluir
		if (extraInfo != null && !extraInfo.isEmpty()) {
			stringBuilder.append(extraInfo);
		}

		stringBuilder.append("</csgd:signaturesDetailedInfo>");

		return stringBuilder.toString();
	}

	/**
	 * Method that obtains the <code>ds:Signature</code> element contained inside of the XML document for the data related to certain signer/counter-signer.
	 * @param signatureIdentifier Parameter that represents the identifier of the signer/counter-signer to find.
	 * @return an object that represents the <code>ds:Signature</code> element or null.
	 */
	private Element getSignatureElement(SignatureIdentifier signatureIdentifier) {
		// Obtenemos el número de serie del certificado firmante
		BigInteger targetCertificateSerialNumber = signatureIdentifier.getSerialNumber();

		// Obtenemos el nombre del emisor del certificado firmante
		String targetCertificateIssuerName = signatureIdentifier.getIssuerName();

		// Recorremos todos los certificados firmantes de las firmas y
		// contra-firmas hasta encontrar el certificado firmante objetivo
		Element targetSigner = null;
		Iterator<Element> it = mapSignatureElements.keySet().iterator();
		while (it.hasNext()) {
			// Accedemos a la firma/contra-firma
			Element signatureElement = it.next();

			// Accedemos al certificado firmante de la firma/contra-firma
			X509Certificate signingCertificate = mapSignatureElements.get(signatureElement);

			// Accedemos al número de serie
			BigInteger currentCertificateSerialNumber = signingCertificate.getSerialNumber();

			// Accedemos al nombre del emisor, canonicalizado
			String currentCertificateIssuerName = SignatureUtils.canonicalizeX500Principal(signingCertificate.getIssuerX500Principal().toString());

			// Comprobamos si este certificado coincide con el certificado
			// objetivo
			if (targetCertificateSerialNumber.equals(currentCertificateSerialNumber) && targetCertificateIssuerName.equals(currentCertificateIssuerName)) {

				// Si en la información del firmante objetivo se ha incluido
				// información del sello de tiempo más reciente contenido en un
				// elemento SignatureTimeStamp
				if (signatureIdentifier.getLastTimestampGenTime() != null) {
					// Comprobamos si el firmante encontrado también contiene
					// elementos SignatureTimeStamp y, en caso afirmativo, su
					// sello de tiempo más reciente coincide con el
					// indicado
					List<XAdESTimeStampType> listTimeStamps = mapSignatureTimeStamps.get(signatureElement);
					if (listTimeStamps != null) {
						Long lasTimeStampGenTime = listTimeStamps.get(listTimeStamps.size() - 1).getTimestampGenerationDate().getTime();
						if (lasTimeStampGenTime.equals(signatureIdentifier.getLastTimestampGenTime())) {
							targetSigner = signatureElement;
						}
					}
				} else {
					targetSigner = signatureElement;
				}
			}
		}
		return targetSigner;
	}

	/* (non-Javadoc)
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getSignature(java.lang.String, es.gob.afirma.integraFacade.pojo.Result, java.lang.String, es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier)
	 */
	@Override
	public final String getSignature(String signatureFormat, Result dssResult, String extraInfo, SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		// Comprobamos que se han indicado los datos del firmante que procesar
		if (signatureIdentifier == null) {
			String errorMsg = "Se produjo un error al construir el objeto XAdESSignatureParser: No fue informado el identificador del certificado firmante.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}

		// Recorremos todos los certificados firmantes de las firmas y
		// contra-firmas hasta encontrar el certificado firmante objetivo
		Element targetSigner = getSignatureElement(signatureIdentifier);

		// Si no hemos encontrado el firmante/contra-firmante solicitado
		// lanzamos una excepción
		if (targetSigner == null) {
			String errorMsg = "Se produjo un error al construir el objeto XAdESSignatureParser: El identificador del certificado firmante informado no se encuentra en la firma electrónica.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}
		return getSignature(signatureFormat, dssResult, extraInfo, targetSigner);
	}

	/* (non-Javadoc)
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getSignerCerts()
	 */
	@Override
	public final String getSignerCerts() throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		// Recorremos los certificados firmantes contenidos en el documento XML
		Iterator<X509Certificate> it = mapSignatureElements.values().iterator();
		while (it.hasNext()) {
			stringBuilder.append(buildValidateCertificateResults(it.next()));
		}

		return stringBuilder.toString();
	}

	/* (non-Javadoc)
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getSignerCerts(es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier)
	 */
	@Override
	public final String getSignerCerts(SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		// Comprobamos que se han indicado los datos del firmante que procesar
		if (signatureIdentifier == null) {
			String errorMsg = "Se produjo un error al construir el objeto XAdESSignatureParser: No fue informado el identificador del certificado firmante.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}

		// Recorremos todos los certificados firmantes de las firmas y
		// contra-firmas hasta encontrar el certificado firmante objetivo
		Element targetSigner = getSignatureElement(signatureIdentifier);

		// Si no hemos encontrado el firmante/contra-firmante solicitado
		// lanzamos una excepción
		if (targetSigner == null) {
			String errorMsg = "Se produjo un error al construir el objeto XAdESSignatureParser: El identificador del certificado firmante informado no se encuentra en la firma electrónica.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}

		// Accedemos al certificado firmante
		X509Certificate targetCertificate = mapSignatureElements.get(targetSigner);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(buildValidateCertificateResults(targetCertificate));
		return stringBuilder.toString();
	}

	/* (non-Javadoc)
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getTimestamps()
	 */
	@Override
	public final String getTimestamps() throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		// Sellos de tiempo (-T)
		stringBuilder.append(getTimestampsByType(TimestampType.SIGNER));

		// Sellos de tiempo (-X)
		stringBuilder.append(getTimestampsByType(TimestampType.CUSTODY));

		// Sellos de tiempo (-A)
		stringBuilder.append(getTimestampsByType(TimestampType.ARCHIVE));

		return stringBuilder.toString();
	}

	/* (non-Javadoc)
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getTimestamps(es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier)
	 */
	@Override
	public final String getTimestamps(SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		// Comprobamos que se han indicado los datos del firmante que procesar
		if (signatureIdentifier == null) {
			String errorMsg = "Se produjo un error al construir el objeto XAdESSignatureParser: No fue informado el identificador del certificado firmante.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}

		// Recorremos todos los certificados firmantes de las firmas y
		// contra-firmas hasta encontrar el certificado firmante objetivo
		Element targetSigner = getSignatureElement(signatureIdentifier);

		// Si no hemos encontrado el firmante/contra-firmante solicitado
		// lanzamos una excepción
		if (targetSigner == null) {
			String errorMsg = "Se produjo un error al construir el objeto XAdESSignatureParser: El identificador del certificado firmante informado no se encuentra en la firma electrónica.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}
		StringBuilder stringBuilder = new StringBuilder();

		// Buscamos en el mapa con los sellos de tiempo contenidos en elementos
		// SignatureTimeStamp si hay sellos de tiempo para la firma/contra-firma
		// objetivo
		stringBuilder.append(getTimestampsByTypeAndSignature(TimestampType.SIGNER, targetSigner));

		// Buscamos en el mapa con los sellos de tiempo contenidos en elementos
		// SigAndRefsTimeStamp y RefsOnlyTimeStamp si hay sellos de tiempo para
		// la firma/contra-firma objetivo
		stringBuilder.append(getTimestampsByTypeAndSignature(TimestampType.CUSTODY, targetSigner));

		// Buscamos en el mapa con los sellos de tiempo contenidos en elementos
		// ArchiveTimeStamp si hay sellos de tiempo para la firma/contra-firma
		// objetivo
		stringBuilder.append(getTimestampsByTypeAndSignature(TimestampType.ARCHIVE, targetSigner));
		return stringBuilder.toString();
	}

	/**
	 * Method that obtains a representation on XML format with the information about the time-stamps of certain signer/counter-signer by time-stamp type.
	 * @param timestampType Parameter that represents the time-stamp type.
	 * @param targetSigner Parameter that represents the <code></code> element to process.
	 * @return the representation of the found time-stamps on XML format.
	 */
	private String getTimestampsByTypeAndSignature(TimestampType timestampType, Element targetSigner) {
		StringBuilder stringBuilder = new StringBuilder();

		if (timestampType.equals(TimestampType.SIGNER)) {
			List<XAdESTimeStampType> listSignatureTimeStamps = mapSignatureTimeStamps.get(targetSigner);
			if (listSignatureTimeStamps != null) {
				for (XAdESTimeStampType signatureTimeStampType: listSignatureTimeStamps) {
					stringBuilder.append(buildTimeStampResult(signatureTimeStampType.getTimestampGenerationDate(), TimestampType.SIGNER, signatureTimeStampType.getTstCertificate()));
				}
			}
		} else if (timestampType.equals(TimestampType.CUSTODY)) {
			List<XAdESTimeStampType> listReferencesTimeStamps = mapReferencesTimeStamps.get(targetSigner);
			if (listReferencesTimeStamps != null) {
				for (XAdESTimeStampType referenceTimeStampType: listReferencesTimeStamps) {
					stringBuilder.append(buildTimeStampResult(referenceTimeStampType.getTimestampGenerationDate(), TimestampType.CUSTODY, referenceTimeStampType.getTstCertificate()));
				}
			}
		} else if (timestampType.equals(TimestampType.ARCHIVE)) {
			List<XAdESTimeStampType> listArchiveTimeStamps = mapArchiveTimeStamps.get(targetSigner);
			if (listArchiveTimeStamps != null) {
				for (XAdESTimeStampType archiveTimeStampType: listArchiveTimeStamps) {
					stringBuilder.append(buildTimeStampResult(archiveTimeStampType.getTimestampGenerationDate(), TimestampType.ARCHIVE, archiveTimeStampType.getTstCertificate()));
				}
			}
		}

		return stringBuilder.toString();
	}

	/**
	 * Method that obtains a representation on XML format with the information about each time-stamp contained inside of the signed XML document by time-stamp type.
	 * @param timestampType Parameter that represents the time-stamp type.
	 * @return the representation of the found time-stamps on XML format.
	 */
	private String getTimestampsByType(TimestampType timestampType) {
		StringBuilder stringBuilder = new StringBuilder();

		if (timestampType.equals(TimestampType.SIGNER)) {
			Iterator<List<XAdESTimeStampType>> itSignatureTimeStamps = mapSignatureTimeStamps.values().iterator();
			while (itSignatureTimeStamps.hasNext()) {
				List<XAdESTimeStampType> listSignatureTimeStamps = itSignatureTimeStamps.next();
				for (XAdESTimeStampType signatureTimeStampType: listSignatureTimeStamps) {
					stringBuilder.append(buildTimeStampResult(signatureTimeStampType.getTimestampGenerationDate(), TimestampType.SIGNER, signatureTimeStampType.getTstCertificate()));
				}
			}
		} else if (timestampType.equals(TimestampType.CUSTODY)) {
			Iterator<List<XAdESTimeStampType>> itReferencesTimeStamps = mapReferencesTimeStamps.values().iterator();
			while (itReferencesTimeStamps.hasNext()) {
				List<XAdESTimeStampType> listReferencesTimeStamps = itReferencesTimeStamps.next();
				for (XAdESTimeStampType referenceTimeStampType: listReferencesTimeStamps) {
					stringBuilder.append(buildTimeStampResult(referenceTimeStampType.getTimestampGenerationDate(), TimestampType.CUSTODY, referenceTimeStampType.getTstCertificate()));
				}
			}
		} else if (timestampType.equals(TimestampType.ARCHIVE)) {
			Iterator<List<XAdESTimeStampType>> itArchiveTimeStamps = mapArchiveTimeStamps.values().iterator();
			while (itArchiveTimeStamps.hasNext()) {
				List<XAdESTimeStampType> listArchiveTimeStamps = itArchiveTimeStamps.next();
				for (XAdESTimeStampType archiveTimeStampType: listArchiveTimeStamps) {
					stringBuilder.append(buildTimeStampResult(archiveTimeStampType.getTimestampGenerationDate(), TimestampType.ARCHIVE, archiveTimeStampType.getTstCertificate()));
				}
			}
		}

		return stringBuilder.toString();
	}

	/* (non-Javadoc)
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getTimestamps(java.util.List)
	 */
	@Override
	public final String getTimestamps(List<TimestampType> timestampTypes) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		// Si se han indicado los tipos de sello de tiempo que procesar
		if (timestampTypes != null) {
			// Recorremos la lista de tipos de sello de tiempo y obtenemos la
			// información de los mismos por tipo
			for (TimestampType timestampType: timestampTypes) {
				stringBuilder.append(getTimestampsByType(timestampType));
			}
		}
		return stringBuilder.toString();
	}

	/* (non-Javadoc)
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getTimestamps(java.util.List, es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier)
	 */
	@Override
	public final String getTimestamps(List<TimestampType> timestampTypes, SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		// Recuperamos el elemento ds:Signature asociado a los datos del
		// identificador facilitado
		Element targetSigner = getSignatureElement(signatureIdentifier);

		// Si se han indicado los tipos de sello de tiempo que procesar y hemos
		// encontrado el elemento ds:Signature indicado
		if (timestampTypes != null && targetSigner != null) {
			// Recorremos la lista de tipos de sello de tiempo y obtenemos la
			// información de los mismos por tipo
			for (TimestampType timestampType: timestampTypes) {
				stringBuilder.append(getTimestampsByTypeAndSignature(timestampType, targetSigner));
			}
		}
		return stringBuilder.toString();
	}

}
