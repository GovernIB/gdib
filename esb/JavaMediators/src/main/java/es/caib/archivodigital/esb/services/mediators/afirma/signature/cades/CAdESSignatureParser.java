package es.caib.archivodigital.esb.services.mediators.afirma.signature.cades;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;

import es.caib.archivodigital.esb.services.mediators.afirma.TimestampType;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureFormat;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParseException;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParser;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureUtils;
import es.caib.archivodigital.esb.services.mediators.afirma.utils.CaibEsbBase64Coder;
import es.gob.afirma.integraFacade.pojo.Result;
import es.gob.afirma.transformers.TransformersException;

/**
 * Clase responsable de extraer información de firmas electrónicas CAdES v1.7.3 no retornada por el servicio
 * DSSAfirmaVerify de la plataforma @firma v6, y requerida por el servicio de validación de documentos (sellos de
 * tiempo X y A).
 * 
 * @author RICOH
 *
 */
public class CAdESSignatureParser extends SignatureParser {

	/**
	 *  Attribute that represents the object that manages the log of the class.
	 */
	private static final Logger LOGGER = Logger.getLogger(CAdESSignatureParser.class);

	private static final String CHARSET_NAME = "UTF-8";

	/**
	 * Firma electrónica a parsear.
	 */
	private CMSSignedData cmsSignedData;

	/**
	 * Parseador sellos de tiempo T
	 */
	private TimestampParser timestampParser;

	/**
	 * Parseador sellos de tiempo X
	 */
	private TimestampParser custodyTimestampParser;

	/**
	 * Parseador sellos de tiempo A
	 */
	private TimestampParser archiveTimestampParser;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public CAdESSignatureParser(SignatureFormat signatureFormat, String base64Signature) throws SignatureParseException {
		super(signatureFormat, base64Signature);

		List<ASN1ObjectIdentifier> listSignatureTimeStampOIDs = new ArrayList<ASN1ObjectIdentifier>();
		listSignatureTimeStampOIDs.add(SignatureConstants.timeStampToken);

		List<ASN1ObjectIdentifier> listReferencesTimeStampOIDs = new ArrayList<ASN1ObjectIdentifier>();
		listReferencesTimeStampOIDs.add(SignatureConstants.esc_timestamp);
		listReferencesTimeStampOIDs.add(SignatureConstants.cert_crl_timestamp);

		List<ASN1ObjectIdentifier> listArchiveTimeStampOIDs = new ArrayList<ASN1ObjectIdentifier>();
		listArchiveTimeStampOIDs.add(SignatureConstants.archive_timestamp);

		timestampParser = new TimestampParser(listSignatureTimeStampOIDs);
		custodyTimestampParser = new TimestampParser(listReferencesTimeStampOIDs);
		archiveTimestampParser = new TimestampParser(listArchiveTimeStampOIDs);

		try {
			cmsSignedData = new CMSSignedData(CaibEsbBase64Coder.decodeBase64(base64Signature.getBytes(CHARSET_NAME)));
		} catch (UnsupportedEncodingException | CMSException
				| TransformersException e) {
			throw new SignatureParseException("Se produjo un error al construir el objeto CMSSignedData que " + "representa la firma electrónica. Error: " + e.getMessage(), e);
		}
	}

	@Override
	public String getAllSignatures(String signatureFormat, Result dssResult, String extraInfo) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();
		SignerInformationStore signers;

		signers = cmsSignedData.getSignerInfos();

		for (Iterator<SignerInformation> it = signers.getSigners().iterator(); it.hasNext();) {
			SignerInformation signerInformation = it.next();

			stringBuilder.append(getSignature(signatureFormat, dssResult, extraInfo, signerInformation));
		}

		return stringBuilder.toString();
	}

	@Override
	public String getSignature(String signatureFormat, Result dssResult, String extraInfo, SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		String res = "";

		SignerInformation signerInformation = getSignerInformation(signatureIdentifier);
		if (signerInformation != null) {
			res = getSignature(signatureFormat, dssResult, extraInfo, signerInformation);
		}

		return res;
	}

	private String getSignature(String signatureFormat, Result dssResult, String extraInfo, SignerInformation signerInformation) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<csgd:signaturesDetailedInfo>");

		// Resultado de validación
		stringBuilder.append(buildResultResponse(dssResult));
		// Formato de firma electrónica
		stringBuilder.append("<csgd:signatureFormat>");
		stringBuilder.append(signatureFormat);
		stringBuilder.append("</csgd:signatureFormat>");

		// Certificado firmante
		stringBuilder.append(getSignerCerts(signerInformation));

		// Sellos de tiempo
		stringBuilder.append(getTimestamps(signerInformation));

		// Información adicional a incluir
		if (extraInfo != null && !extraInfo.isEmpty()) {
			stringBuilder.append(extraInfo);
		}

		stringBuilder.append("</csgd:signaturesDetailedInfo>");

		return stringBuilder.toString();
	}

	@Override
	public String getSignerCerts() throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			// Obtenemos todos los certificados de la firma
			Collection<X509CertificateHolder> certHolderCollection = cmsSignedData.getCertificates().getMatches(null);

			Iterator<X509CertificateHolder> certIt = certHolderCollection.iterator();
			while (certIt.hasNext()) {
				X509Certificate certificate = transformX509CertificateHolder(certIt.next());
				stringBuilder.append(buildValidateCertificateResults(certificate));
			}

		} catch (CertificateException e) {
			String excMessage = "No fue posible obtener alguno de los certificados firmantes de la firma: " + e.getMessage() + ".";
			LOGGER.error(excMessage, e);
			throw new SignatureParseException(excMessage, e);
		}

		return stringBuilder.toString();
	}

	@Override
	public String getSignerCerts(SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		String res = "";

		SignerInformation signerInformation = getSignerInformation(signatureIdentifier);
		if (signerInformation != null) {
			res = getSignerCerts(signerInformation);
		}

		return res;
	}

	private String getSignerCerts(SignerInformation signerInformation) throws SignatureParseException {
		String res;

		X509Certificate certificate = getSigningCertificate(cmsSignedData.getCertificates().getMatches(signerInformation.getSID()));
		res = buildValidateCertificateResults(certificate);

		return res;
	}

	@Override
	public String getTimestamps() throws SignatureParseException {
		String res = "";

		// Sellos de tiempo T
		timestampParser.setTimestampTokens(cmsSignedData);
		res = parseTimestamps(timestampParser.getTimestampTokens(), TimestampType.SIGNER);

		// Sello de tiempo X
		custodyTimestampParser.setTimestampTokens(cmsSignedData);
		res += parseTimestamps(custodyTimestampParser.getTimestampTokens(), TimestampType.CUSTODY);

		// Sellos de tiempo A
		archiveTimestampParser.setTimestampTokens(cmsSignedData);
		res += parseTimestamps(archiveTimestampParser.getTimestampTokens(), TimestampType.ARCHIVE);

		return res;
	}

	@Override
	public String getTimestamps(List<TimestampType> timestampTypes) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		if (timestampTypes != null) {
			for (TimestampType timestampType: timestampTypes) {
				if (TimestampType.SIGNER.equals(timestampType)) {
					timestampParser.setTimestampTokens(cmsSignedData);
					stringBuilder.append(parseTimestamps(timestampParser.getTimestampTokens(), TimestampType.SIGNER));
				} else if (TimestampType.CUSTODY.equals(timestampType)) {
					custodyTimestampParser.setTimestampTokens(cmsSignedData);
					stringBuilder.append(parseTimestamps(custodyTimestampParser.getTimestampTokens(), TimestampType.CUSTODY));
				} else if (TimestampType.ARCHIVE.equals(timestampType)) {
					archiveTimestampParser.setTimestampTokens(cmsSignedData);
					stringBuilder.append(parseTimestamps(archiveTimestampParser.getTimestampTokens(), TimestampType.ARCHIVE));
				}
			}
		}

		return stringBuilder.toString();
	}

	@Override
	public String getTimestamps(SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		String res = "";

		SignerInformation signerInformation = getSignerInformation(signatureIdentifier);
		if (signerInformation != null) {
			res = getTimestamps(signerInformation);
		}
		return res;
	}

	private String getTimestamps(SignerInformation signerInformation) throws SignatureParseException {
		String res = "";

		// Sellos de tiempo T
		timestampParser.setTimestampTokens(signerInformation);
		res = parseTimestamps(timestampParser.getTimestampTokens(), TimestampType.SIGNER);

		// Sello de tiempo X
		custodyTimestampParser.setTimestampTokens(signerInformation);
		res += parseTimestamps(custodyTimestampParser.getTimestampTokens(), TimestampType.CUSTODY);

		// Sellos de tiempo A
		archiveTimestampParser.setTimestampTokens(signerInformation);
		res += parseTimestamps(archiveTimestampParser.getTimestampTokens(), TimestampType.ARCHIVE);

		return res;
	}

	@Override
	public String getTimestamps(List<TimestampType> timestampTypes, SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();
		SignerInformation signerInformation = getSignerInformation(signatureIdentifier);

		if ((timestampTypes != null && !timestampTypes.isEmpty()) && signerInformation != null) {
			for (TimestampType timestampType: timestampTypes) {
				if (TimestampType.SIGNER.equals(timestampType)) {
					timestampParser.setTimestampTokens(signerInformation);
					stringBuilder.append(parseTimestamps(timestampParser.getTimestampTokens(), TimestampType.SIGNER));
				} else if (TimestampType.CUSTODY.equals(timestampType)) {
					custodyTimestampParser.setTimestampTokens(signerInformation);
					stringBuilder.append(parseTimestamps(custodyTimestampParser.getTimestampTokens(), TimestampType.CUSTODY));
				} else if (TimestampType.ARCHIVE.equals(timestampType)) {
					archiveTimestampParser.setTimestampTokens(signerInformation);
					stringBuilder.append(parseTimestamps(archiveTimestampParser.getTimestampTokens(), TimestampType.ARCHIVE));
				}
			}
		}

		return stringBuilder.toString();
	}

	private String parseTimestamps(List<TimeStampToken> timestampTokens, TimestampType timestampType) throws SignatureParseException {
		StringBuilder stringBuilder = new StringBuilder();

		if (timestampTokens != null && !timestampTokens.isEmpty()) {
			for (TimeStampToken tst: timestampTokens) {
				Collection<X509CertificateHolder> certHolderCollection = tst.getCertificates().getMatches(tst.getSID());
				X509Certificate certificate = getSigningCertificate(certHolderCollection);
				stringBuilder.append(buildTimeStampResult(tst.getTimeStampInfo().getGenTime(), timestampType, certificate));
			}
		}

		return stringBuilder.toString();
	}

	private SignerInformation getSignerInformation(SignatureIdentifier signatureIdentifier) throws SignatureParseException {
		Boolean signerFound = Boolean.FALSE;
		SignerInformation res = null;
		SignerInformationStore signers;

		try {
			if (signatureIdentifier == null) {
				throw new SignatureParseException("Se produjo un error al construir el objeto CAdESSignatureParser, " + "no fue informado el identificador del certificado firmante.");
			}
			signers = cmsSignedData.getSignerInfos();

			for (Iterator<SignerInformation> it = signers.getSigners().iterator(); !signerFound && it.hasNext();) {
				SignerInformation signerInformation = it.next();

				if (isSelectedSignature(signatureIdentifier, signerInformation)) {
					res = signerInformation;
					signerFound = Boolean.TRUE;
				}
			}

			if (!signerFound) {
				throw new SignatureParseException("Se produjo un error al construir el objeto CAdESSignatureParser, " + "el identificador del certificado firmante informado no se encuentra en la firma electrónica.");
			}

		} catch (SignatureParseException e) {

		}

		return res;
	}

	private Boolean isSelectedSignature(SignatureIdentifier signatureIdentifier, SignerInformation signerInformation) throws SignatureParseException {
		Boolean res = Boolean.FALSE;
		Collection<X509CertificateHolder> certHolderCollection = cmsSignedData.getCertificates().getMatches(signerInformation.getSID());
		X509Certificate cert = getSigningCertificate(certHolderCollection);

		if (cert.getSerialNumber().equals(signatureIdentifier.getSerialNumber()) && SignatureUtils.canonicalizeX500Principal(cert.getIssuerX500Principal().toString()).equals(signatureIdentifier.getIssuerName())) {
			if (signatureIdentifier.getLastTimestampGenTime() != null) {
				// Comprobar el sello de tiempo
				timestampParser.setTimestampTokens(signerInformation);
				List<Long> genTimes = timestampParser.getTimestampTokensGenTimes();

				if (!genTimes.isEmpty() && genTimes.get(0).equals(signatureIdentifier.getLastTimestampGenTime())) {
					// La listas de sellos de tiempo extraida de la firma está
					// ordenada descendentemente, dado que @firma
					// solo devuelve el sello de tiempo más reciente
					res = Boolean.TRUE;
				}
			} else {
				res = Boolean.TRUE;
			}
		}

		return res;
	}

	/**
	 * Metodo que obtiene el certificado firmante de una firma electrónica CAdES o TimeStampToken.
	 * @param store conjunto de certificados de la firma.
	 * @param certHolderCollection colección de X509CertificateHolder.
	 * @return objeto que representa el certificado firmante.
	 * @throws SignatureException si ocurre algun error al obtener el certificado firmante
	 */
	public static X509Certificate getSigningCertificate(final Collection<X509CertificateHolder> certHolderCollection) throws SignatureParseException {
		X509Certificate res = null;
		X509CertificateHolder certHolder = null;

		try {
			Iterator<X509CertificateHolder> certIt = certHolderCollection.iterator();
			if (certIt.hasNext()) {
				certHolder = certIt.next();
			}

			res = transformX509CertificateHolder(certHolder);
		} catch (CertificateException e) {
			String excMessage = "No fue posible obtener el certificado firmante de la firma: " + e.getMessage() + ".";
			LOGGER.error(excMessage, e);
			throw new SignatureParseException(excMessage, e);
		}
		LOGGER.debug("Signing Certificate recuperado: " + res.getSubjectX500Principal());

		return res;
	}

	/**
	 * Metodo que transforma un objeto X509CertificateHolder en un objeto X509Certificate
	 * @param certHolder objeto X509CertificateHolder
	 * @return un objeto X509Certificate
	 * @throws CertificateException si ocurre algun error en la transformación.
	 */
	public static X509Certificate transformX509CertificateHolder(final X509CertificateHolder certHolder) throws CertificateException {
		X509Certificate res = null;

		res = new JcaX509CertificateConverter().getCertificate(certHolder);

		return res;
	}
}
