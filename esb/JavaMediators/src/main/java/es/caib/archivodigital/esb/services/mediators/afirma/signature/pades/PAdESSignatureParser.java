package es.caib.archivodigital.esb.services.mediators.afirma.signature.pades;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;

import es.caib.archivodigital.esb.services.mediators.afirma.TimestampType;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureFormat;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParseException;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureUtils;
import es.gob.afirma.integraFacade.pojo.Result;
import es.gob.afirma.utils.ASN1Utils;
import es.gob.afirma.utils.CertificateUtils;
import es.gob.afirma.utils.PDFUtils;
import es.gob.afirma.utils.UtilsException;

/**
 * Class that retrieves information from PAdES signatures returned from <code>DSSAfirmaVerify</code> service of @firma platform v6.1.1.
 * This information is required by the documents validation service.
 * 
 * @author RICOH
 *
 */
public class PAdESSignatureParser extends SignatureParser {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 *  Attribute that represents the object that manages the log of the class.
	 */
	private static final Logger LOGGER = Logger.getLogger(PAdESSignatureParser.class);

	/**
	 * Attribute that represents the list of signature dictionaries contained inside of the PDF document.
	 */
	private List<SignatureDictionaryType> listSignatureDictionaries = new ArrayList<SignatureDictionaryType>();

	/**
	 * Constructor method for the class PAdESSignatureParser.java.
	 * @param signatureFormat Parameter that represents the signature format.
	 * @param base64Signature Parameter that represents the signature encoded on Base64.
	 * @throws SignatureParseException If the constructor fails.
	 */
	public PAdESSignatureParser(SignatureFormat signatureFormat, String base64Signature) throws SignatureParseException {
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

		// Accedemos al documento PDF
		PdfReader reader = PDFUtils.getReader(decodedSignature);

		// Creamos un objeto para consultar campos del PDF
		AcroFields af = reader.getAcroFields();

		// Obtenemos las firmas del documento, esto es, los objetos /Sig y
		// los objetos /DocTimeStamp
		List<String> names = af.getSignatureNames();

		// Obtenemos del documento PDF la información de los diccionarios de
		// firma y de sello de tiempo
		retrieveSignaturesFromPDFDocument(names, af);

	}

	/**
	 * Method that recovers from the PDF document the information related to the signature dictionaries and the Document Time-stamp dictionaries contained inside.
	 * @param names Parameter that represents the names of all the signature dictionaries and the Document Time-stamp dictionaries contained inside of the PDF document.
	 * @param af Parameter that allows to read from the PDF document.
	 * @throws SignatureParseException If the method fails.
	 */
	private void retrieveSignaturesFromPDFDocument(List<String> names, AcroFields af) throws SignatureParseException {
		// Definimos una lista donde ubicar la información asociada a los
		// diccionarios de sello de tiempo
		List<DocumentTimeStampDictionaryType> listDocumentTimeStampDictionaries = new ArrayList<DocumentTimeStampDictionaryType>();

		// Recorremos la lista de firmas
		for (int i = 0; i < names.size(); i++) {
			// Accedemos al nombre de la firma
			String signatureName = names.get(i);

			// Obtenemos el número de revisión
			Integer revisionNumber = af.getRevision(signatureName);

			// Obtenemos el diccionario de firma asociado
			PdfDictionary dictionary = af.getSignatureDictionary(signatureName);

			// Metemos en una variable el contenido de la clave /Contents
			byte[ ] contentsKey = dictionary.getAsString(PdfName.CONTENTS).getOriginalBytes();

			// Verificamos que el contenido de la clave /Contents no sea nulo
			if (contentsKey == null) {
				String errorMsg = "El contenido de la clave /Contents del diccionario de sello de tiempo [{0}] es nulo.";
				LOGGER.error(errorMsg);
				throw new SignatureParseException(errorMsg);
			}

			// Determinamos el tipo de diccionario obtenido
			String pdfType = null;
			if (dictionary.get(PdfName.TYPE) != null) {
				pdfType = dictionary.get(PdfName.TYPE).toString();
			}

			// Determinamos el contenido de la clave SubFilter
			String subFilter = dictionary.get(PdfName.SUBFILTER).toString();

			// Es diccionario de sello de tiempo
			if (subFilter.equals(new PdfName(PDFUtils.TST_SUBFILTER_VALUE).toString()) && (pdfType == null || pdfType != null && pdfType.equals(new PdfName(PDFUtils.DOC_TIME_STAMP_DICTIONARY_NAME).toString()))) {
				// Instanciamos un objeto donde almacenar toda la información
				// asociada al diccionario de sello de tiempo y lo añadimos a la
				// lista con los diccionarios de sello de tiempo
				DocumentTimeStampDictionaryType timestampDictionary = getTimeStampInfoFromDocumentTimeStampDictionary(contentsKey, signatureName, revisionNumber);
				listDocumentTimeStampDictionaries.add(timestampDictionary);
			}
			// Es diccionario de firma
			else if ((pdfType == null || pdfType != null && pdfType.equals(PdfName.SIG.toString())) && !subFilter.equals(new PdfName(PDFUtils.TST_SUBFILTER_VALUE).toString())) {
				// Si el diccionario de firma contiene una firma con formato
				// PKCS#7
				if (PDFUtils.containsPKCS7Signature(dictionary)) {
					// Instanciamos un objeto donde almacenar toda la
					// información
					// asociada al diccionario de sello de tiempo y lo añadimos
					// a la lista con los diccionarios de firma
					SignatureDictionaryType signatureDictionaryType = getSignatureInfoFromPDFSignatureDictionary(af, signatureName, revisionNumber);
					listSignatureDictionaries.add(signatureDictionaryType);
				}
				// Si el diccionario de firma contiene una firma con formato
				// CAdES (o CMS)
				else {
					// Instanciamos un objeto donde almacenar toda la
					// información
					// asociada al diccionario de sello de tiempo y lo añadimos
					// a la lista con los diccionarios de firma
					SignatureDictionaryType signatureDictionaryType = getSignatureInfoFromPAdESSignatureDictionary(af, signatureName, revisionNumber, dictionary);
					listSignatureDictionaries.add(signatureDictionaryType);
				}
			}
		}
		// Ordenamos la lista de diccionarios de firma ascendentemente por
		// número de revisión, o lo que es lo mismo, por orden de inserción
		Collections.sort(listSignatureDictionaries);

		// Ordenamos la lista de diccionarios de sello de tiempo ascendentemente
		// por número de revisión, o lo que es lo mismo, por orden de inserción
		Collections.sort(listDocumentTimeStampDictionaries);

		// Ahora, recorremos los diccionarios de firma de manera ascendente por
		// número de revisión
		for (SignatureDictionaryType signatureDictionaryType: listSignatureDictionaries) {
			// Accedemos al número de revisión del diccionario de firma
			Integer revisionNumber = signatureDictionaryType.getRevision();

			// Recorremos la lista de diccionarios de sello de tiempo de manera
			// que asociamos como sellos de tiempo de archivado para el
			// diccionario de firma aquellos sellos de tiempo
			// contenidos en diccionarios de sello de tiempo generados a
			// posteriori del diccionario de firma actual
			List<TimeStampType> listArchiveTimeStamps = new ArrayList<TimeStampType>();
			for (int i = 0; i < listDocumentTimeStampDictionaries.size(); i++) {
				DocumentTimeStampDictionaryType tstDictionaryType = listDocumentTimeStampDictionaries.get(i);
				if (tstDictionaryType.getRevision().compareTo(revisionNumber) >= 0) {
					listArchiveTimeStamps.add(new TimeStampType(tstDictionaryType.getTstCertificate(), tstDictionaryType.getTimestampGenerationDate()));
				}
			}
			signatureDictionaryType.setListArchiveTimeStamps(listArchiveTimeStamps);
		}
	}

	/**
	 * Method that obtains the information about the signature dictionary when the signature contained inside has CAdES form.
	 * @param af Parameter that allows to read from the PDF document.
	 * @param signatureName Parameter that represents the name of the signature dictionary.
	 * @param revisionNumber Parameter that represents the name of the revision when the signature dictionary was created into the PDD document.
	 * @param dictionary Parameter that represents the signature dictionary.
	 * @return an object that contains information about the signature dictionary.
	 * @throws SignatureParseException If the method fails.
	 */
	private SignatureDictionaryType getSignatureInfoFromPAdESSignatureDictionary(AcroFields af, String signatureName, Integer revisionNumber, PdfDictionary dictionary) throws SignatureParseException {
		try {
			CMSSignedData signedData = PDFUtils.getCMSSignature(dictionary, signatureName);

			// Accedemos al firmante
			SignerInformation signerInformation = ((List<SignerInformation>) signedData.getSignerInfos().getSigners()).iterator().next();

			// Obtenemos el certificado firmante
			X509Certificate signingCertificate = ASN1Utils.getSigningCertificate(signedData, signerInformation);

			// Obtenemos la lista de sellos de tiempo contenidos en los
			// atributos signature-time-stamp del firmante
			List<TimeStampToken> listTimeStamps = ASN1Utils.getListTimeStampTokensIntoSignatureTimeStampAtts(signerInformation);

			// Instanciamos una lista donde ubicar la información de cada sello
			// de tiempo
			List<TimeStampType> listTimeStampType = new ArrayList<TimeStampType>();

			// Recorremos los sellos de tiempo obtenidos
			for (TimeStampToken tst: listTimeStamps) {
				// Obtenemos el certificado firmante del sello de tiempo
				X509Certificate tstCertificate = CertificateUtils.getSigningCertificate(tst);

				// Instanciamos un objeto donde almacenar la información del
				// sello de tiempo y lo añadimos a la lista correspondiente
				listTimeStampType.add(new TimeStampType(tstCertificate, tst.getTimeStampInfo().getGenTime()));
			}
			// Ordenamos descendentemente la lista de sellos de tiempo de manera
			// que el primero en aparecer será el más reciente
			Collections.reverse(listTimeStampType);

			// Devolvemos un objeto con la información del diccionario de firma
			return new SignatureDictionaryType(signatureName, revisionNumber, signingCertificate, listTimeStampType, new ArrayList<TimeStampType>());
		} catch (Exception e) {
			String errorMsg = "Se ha producido un error procesando el diccionario de firma [" + signatureName + "].";
			LOGGER.error(errorMsg, e);
			throw new SignatureParseException(errorMsg, e);
		}
	}

	/**
	 * Method that obtains the information about the signature dictionary when the signature contained inside has PKCS#7 form.
	 * @param af Parameter that allows to read from the PDF document.
	 * @param signatureName Parameter that represents the name of the signature dictionary.
	 * @param revisionNumber Parameter that represents the name of the revision when the signature dictionary was created into the PDD document.
	 * @return an object that contains information about the signature dictionary.
	 * @throws SignatureParseException If the method fails.
	 */
	private SignatureDictionaryType getSignatureInfoFromPDFSignatureDictionary(AcroFields af, String signatureName, Integer revisionNumber) throws SignatureParseException {
		try {
			// Accedemos a la firma contenida en el diccionario de firma
			PdfPKCS7 pk = af.verifySignature(signatureName);

			// Obtenemos el certificado firmante
			X509Certificate signingCertificate = pk.getSigningCertificate();

			// Obtenemos el sello de tiempo contenido en un atributo
			// signature-time-stamp del primer firmante
			List<TimeStampType> listTimeStampType = new ArrayList<TimeStampType>();
			TimeStampToken tst = pk.getTimeStampToken();
			
			//Si se incluye sello de tiempo
			if(tst!=null) {
				// Obtenemos el certificado firmante del sello de tiempo
				X509Certificate tstCertificate = CertificateUtils.getSigningCertificate(tst);

				// Instanciamos un objeto donde almacenar la información del sello
				// de tiempo y lo añadimos a la lista correspondiente
				listTimeStampType.add(new TimeStampType(tstCertificate, tst.getTimeStampInfo().getGenTime()));
			}

			

			// Devolvemos un objeto con la información del diccionario de firma
			return new SignatureDictionaryType(signatureName, revisionNumber, signingCertificate, listTimeStampType, new ArrayList<TimeStampType>());
		} catch (UtilsException e) {
			String errorMsg = "No ha sido posible obtener el certificado firmante del sello de tiempo contenido en un atributo signature-time-stamp del firmante principal de la firma contenida en el diccionario de firma [" + signatureName + "].";
			LOGGER.error(errorMsg, e);
			throw new SignatureParseException(errorMsg, e);
		}
	}

	/**
	 * Method that obtains the information about a Document Time-stamp dictionary contained inside of the PDF document.
	 * @param contentsKey Parameter that represents the content of <code>/Contents</code> entry of the Document Time-stamp dictionary.
	 * @param signatureName Parameter that represents the name of the Document Time-stamp dictionary.
	 * @param revisionNumber Parameter that represents the name of the Document Time-stamp dictionary.
	 * @return an object that contains information about the Document Time-stamp dictionary.
	 * @throws SignatureParseException If the method fails.
	 */
	private DocumentTimeStampDictionaryType getTimeStampInfoFromDocumentTimeStampDictionary(byte[ ] contentsKey, String signatureName, Integer revisionNumber) throws SignatureParseException {
		try {
			TimeStampToken tst = new TimeStampToken(new CMSSignedData(contentsKey));
			X509Certificate tstCertificate = CertificateUtils.getSigningCertificate(tst);
			return new DocumentTimeStampDictionaryType(signatureName, revisionNumber, tstCertificate, tst.getTimeStampInfo().getGenTime());
		} catch (UtilsException e) {
			String errorMsg = "No ha sido posible obtener el certificado firmante del sello de tiempo contenido en el diccionario de sello de tiempo [" + signatureName + "].";
			LOGGER.error(errorMsg, e);
			throw new SignatureParseException(errorMsg, e);
		} catch (TSPException e) {
			String errorMsg = "No ha sido posible obtener el sello de tiempo contenido en el diccionario de sello de tiempo [" + signatureName + "].";
			LOGGER.error(errorMsg, e);
			throw new SignatureParseException(errorMsg, e);
		} catch (IOException e) {
			String errorMsg = "No ha sido posible obtener el sello de tiempo contenido en el diccionario de sello de tiempo [" + signatureName + "].";
			LOGGER.error(errorMsg, e);
			throw new SignatureParseException(errorMsg, e);
		} catch (CMSException e) {
			String errorMsg = "No ha sido posible obtener el sello de tiempo contenido en el diccionario de sello de tiempo [" + signatureName + "].";
			LOGGER.error(errorMsg, e);
			throw new SignatureParseException(errorMsg, e);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getAllSignatures(java.lang.String, es.gob.afirma.integraFacade.pojo.Result, java.lang.String)
	 */
	@Override
	public final String getAllSignatures(String signatureFormat, Result dssResult, String extraInfo) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		// Recorremos la lista de diccionarios de firma contenidos en el
		// documento PDF
		for (SignatureDictionaryType signatureDictionaryType: listSignatureDictionaries) {
			// Añadimos la información de la firma contenida en el diccionario
			// de firma
			stringBuilder.append(getSignature(signatureFormat, dssResult, extraInfo, signatureDictionaryType));
		}
		return stringBuilder.toString();
	}

	/**
	 * Method that obtains information about the first signer of the signature contained inside of a signature dictionary.
	 * @param signatureFormat Parameter that represents the signature format associated to the signed PDF document.
	 * @param dssResult Parameter that represents the information about the result of the process retrieved from @Firma.
	 * @param extraInfo  Parameter that represents additional information.
	 * @param signatureDictionaryType Parameter that contains information about the signature dictionary.
	 * @return information about the first signer of the signature contained inside of a signature dictionary on XML format.
	 * @throws SignatureParseException If the method fails.
	 */
	private String getSignature(String signatureFormat, Result dssResult, String extraInfo, SignatureDictionaryType signatureDictionaryType) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<csgd:signaturesDetailedInfo>");

		// Resultado de validación
		stringBuilder.append(buildResultResponse(dssResult));

		// Formato de firma electrónica
		stringBuilder.append("<csgd:signatureFormat>");
		stringBuilder.append(signatureFormat);
		stringBuilder.append("</csgd:signatureFormat>");

		// Certificado firmante
		stringBuilder.append(buildValidateCertificateResults(signatureDictionaryType.getSigningCertificate()));

		// Sellos de tiempo (-T)
		List<TimeStampType> listSignatureTimeStamps = signatureDictionaryType.getListSignatureTimeStamps();
		if (listSignatureTimeStamps != null) {
			for (TimeStampType signatureTimeStampType: listSignatureTimeStamps) {
				stringBuilder.append(buildTimeStampResult(signatureTimeStampType.getTimestampGenerationDate(), TimestampType.SIGNER, signatureTimeStampType.getTstCertificate()));
			}
		}

		// Sellos de tiempo (-A)
		List<TimeStampType> listArchiveTimeStamps = signatureDictionaryType.getListArchiveTimeStamps();
		if (listArchiveTimeStamps != null) {
			for (TimeStampType referenceTimeStampType: listArchiveTimeStamps) {
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
	 * {@inheritDoc}
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getSignature(java.lang.String, es.gob.afirma.integraFacade.pojo.Result, java.lang.String, es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier)
	 */
	@Override
	public final String getSignature(String signatureFormat, Result dssResult, String extraInfo, SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		// Comprobamos que se han indicado los datos del firmante que procesar
		if (signatureIdentifier == null) {
			String errorMsg = "Se produjo un error al construir el objeto PAdESSignatureParser: No fue informado el identificador del certificado firmante.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}

		// Recorremos todos los certificados firmantes de las firmas contenidas
		// en los diccionarios de firma hasta encontrar el certificado firmante
		// objetivo
		SignatureDictionaryType targetSignatureDictionary = getSignatureDictionaryType(signatureIdentifier);

		// Si no hemos encontrado el firmante solicitado
		// lanzamos una excepción
		if (targetSignatureDictionary == null) {
			String errorMsg = "Se produjo un error al construir el objeto PAdESSignatureParser: El identificador del certificado firmante informado no se encuentra en la firma electrónica.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}
		return getSignature(signatureFormat, dssResult, extraInfo, targetSignatureDictionary);
	}

	/**
	 * Method that obtains the information about a signature dicionary for the data related to certain signer.
	 * @param signatureIdentifier Parameter that represents the identifier of the signer to find.
	 * @return an object that contains the information about the signature dicionary  or null.
	 */
	private SignatureDictionaryType getSignatureDictionaryType(SignatureIdentifier signatureIdentifier) {
		// Obtenemos el número de serie del certificado firmante
		BigInteger targetCertificateSerialNumber = signatureIdentifier.getSerialNumber();

		// Obtenemos el nombre del emisor del certificado firmante
		String targetCertificateIssuerName = signatureIdentifier.getIssuerName();

		// Recorremos todos los certificados firmantes de las firmas contenidas
		// en los diccionarios de firma hasta encontrar el certificado firmante
		// objetivo
		SignatureDictionaryType targetSignatureDictionary = null;
		for (int i = 0; i < listSignatureDictionaries.size() && targetSignatureDictionary == null; i++) {
			// Accedemos al diccionario de firma
			SignatureDictionaryType currentSignatureDictionaryType = listSignatureDictionaries.get(i);

			// Accedemos al certificado firmante de la firma
			X509Certificate signingCertificate = currentSignatureDictionaryType.getSigningCertificate();

			// Accedemos al número de serie
			BigInteger currentCertificateSerialNumber = signingCertificate.getSerialNumber();

			// Accedemos al nombre del emisor, canonicalizado
			String currentCertificateIssuerName = SignatureUtils.canonicalizeX500Principal(signingCertificate.getIssuerX500Principal().toString());

			// Comprobamos si este certificado coincide con el certificado
			// objetivo
			if (targetCertificateSerialNumber.equals(currentCertificateSerialNumber) && targetCertificateIssuerName.equals(currentCertificateIssuerName)) {
				targetSignatureDictionary = currentSignatureDictionaryType;
			}
		}
		return targetSignatureDictionary;
	}

	/**
	 * {@inheritDoc}
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getSignerCerts()
	 */
	@Override
	public final String getSignerCerts() throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		// Recorremos los diccionarios de firma contenidos en el documento PDF
		for (SignatureDictionaryType signatureDictionaryType: listSignatureDictionaries) {
			// Accedemos al certificado firmante de la firma contenida en el
			// diccionario de firma
			stringBuilder.append(buildValidateCertificateResults(signatureDictionaryType.getSigningCertificate()));
		}
		return stringBuilder.toString();
	}

	/**
	 * {@inheritDoc}
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getSignerCerts(es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier)
	 */
	@Override
	public final String getSignerCerts(SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		// Comprobamos que se han indicado los datos del firmante que procesar
		if (signatureIdentifier == null) {
			String errorMsg = "Se produjo un error al construir el objeto PAdESSignatureParser: No fue informado el identificador del certificado firmante.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}

		// Recorremos todos los certificados firmantes de las firmas contenidas
		// en los diccionarios de firma hasta encontrar el certificado firmante
		// objetivo
		SignatureDictionaryType targetSignatureDictionaryType = getSignatureDictionaryType(signatureIdentifier);

		// Si no hemos encontrado el firmante solicitado
		// lanzamos una excepción
		if (targetSignatureDictionaryType == null) {
			String errorMsg = "Se produjo un error al construir el objeto PAdESSignatureParser: El identificador del certificado firmante informado no se encuentra en la firma electrónica.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}

		// Accedemos al certificado firmante
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(buildValidateCertificateResults(targetSignatureDictionaryType.getSigningCertificate()));
		return stringBuilder.toString();
	}

	/**
	 * Method that obtains a representation on XML format with the information about each time-stamp contained associated to certain signature dictionary.
	 * @param timestampType Parameter that represents the time-stamp type.
	 * @param signatureDictionaryType Parameter that contains information about the signature dictionary.
	 * @return the representation of the found time-stamps on XML format.
	 */
	private String getTimestampsByType(TimestampType timestampType, SignatureDictionaryType signatureDictionaryType) {
		StringBuilder stringBuilder = new StringBuilder();
		if (timestampType.equals(TimestampType.SIGNER)) {
			// Obtenemos los sellos de tiempo contenidos en los atributos
			// signature-time-stamp del primer firmante de la firma contenida en
			// el diccionario de firma
			List<TimeStampType> listSignatureTimeStamps = signatureDictionaryType.getListSignatureTimeStamps();
			if (listSignatureTimeStamps != null) {
				for (TimeStampType signatureTimeStampType: listSignatureTimeStamps) {
					stringBuilder.append(buildTimeStampResult(signatureTimeStampType.getTimestampGenerationDate(), TimestampType.SIGNER, signatureTimeStampType.getTstCertificate()));
				}
			}
		} else if (timestampType.equals(TimestampType.ARCHIVE)) {
			// Obtenemos los sellos de tiempo contenidos en diccionarios de
			// sello de tiempo posteriores al diccionario de firma
			List<TimeStampType> listArchiveTimeStamps = signatureDictionaryType.getListArchiveTimeStamps();
			if (listArchiveTimeStamps != null) {
				for (TimeStampType archiveTimeStampType: listArchiveTimeStamps) {
					stringBuilder.append(buildTimeStampResult(archiveTimeStampType.getTimestampGenerationDate(), TimestampType.ARCHIVE, archiveTimeStampType.getTstCertificate()));
				}
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * {@inheritDoc}
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getTimestamps()
	 */
	@Override
	public final String getTimestamps() throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		// Recorremos la lista de diccionarios de firma
		for (SignatureDictionaryType signatureDictionaryType: listSignatureDictionaries) {
			// Obtenemos los sellos de tiempo contenidos en los atributos
			// signature-time-stamp del primer firmante de la firma contenida en
			// el diccionario de firma
			stringBuilder.append(getTimestampsByType(TimestampType.SIGNER, signatureDictionaryType));

			// Obtenemos los sellos de tiempo contenidos en diccionarios de
			// sello de tiempo posteriores al diccionario de firma
			stringBuilder.append(getTimestampsByType(TimestampType.ARCHIVE, signatureDictionaryType));
		}
		return stringBuilder.toString();
	}

	/**
	 * {@inheritDoc}
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getTimestamps(es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier)
	 */
	@Override
	public final String getTimestamps(SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		// Comprobamos que se han indicado los datos del firmante que procesar
		if (signatureIdentifier == null) {
			String errorMsg = "Se produjo un error al construir el objeto PAdESSignatureParser: No fue informado el identificador del certificado firmante.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}

		// Recorremos todos los certificados firmantes de las firmas contenidas
		// en los diccionarios de firma hasta encontrar el certificado firmante
		// objetivo
		SignatureDictionaryType targetSignatureDictionaryType = getSignatureDictionaryType(signatureIdentifier);

		// Si no hemos encontrado el firmante solicitado
		// lanzamos una excepción
		if (targetSignatureDictionaryType == null) {
			String errorMsg = "Se produjo un error al construir el objeto PAdESSignatureParser: El identificador del certificado firmante informado no se encuentra en la firma electrónica.";
			LOGGER.error(errorMsg);
			throw new SignatureParseException(errorMsg);
		}
		StringBuilder stringBuilder = new StringBuilder();

		// Obtenemos los sellos de tiempo contenidos en los atributos
		// signature-time-stamp del primer firmante de la firma contenida en el
		// diccionario de firma
		stringBuilder.append(getTimestampsByType(TimestampType.SIGNER, targetSignatureDictionaryType));

		// Obtenemos los sellos de tiempo contenidos en diccionarios de sello de
		// tiempo posteriores al diccionario de firma
		stringBuilder.append(getTimestampsByType(TimestampType.ARCHIVE, targetSignatureDictionaryType));

		return stringBuilder.toString();
	}

	/**
	 * {@inheritDoc}
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
				// Recorremos la lista de diccionarios de firma
				for (SignatureDictionaryType signatureDictionaryType: listSignatureDictionaries) {
					// Imprimimos la información del tipo de sello de tiempo
					// actual para el diccionario de firma actual
					stringBuilder.append(getTimestampsByType(timestampType, signatureDictionaryType));
				}
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * {@inheritDoc}
	 * @see es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser#getTimestamps(java.util.List, es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier)
	 */
	@Override
	public final String getTimestamps(List<TimestampType> timestampTypes, SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		// Recorremos todos los certificados firmantes de las firmas contenidas
		// en los diccionarios de firma hasta encontrar el certificado firmante
		// objetivo
		SignatureDictionaryType targetSignatureDictionaryType = getSignatureDictionaryType(signatureIdentifier);

		// Si se han indicado los tipos de sello de tiempo que procesar y hemos
		// encontrado el firmante indicado
		if (timestampTypes != null && targetSignatureDictionaryType != null) {
			// Recorremos la lista de tipos de sello de tiempo y obtenemos la
			// información de los mismos por tipo
			for (TimestampType timestampType: timestampTypes) {
				stringBuilder.append(getTimestampsByType(timestampType, targetSignatureDictionaryType));
			}
		}
		return stringBuilder.toString();
	}
}
