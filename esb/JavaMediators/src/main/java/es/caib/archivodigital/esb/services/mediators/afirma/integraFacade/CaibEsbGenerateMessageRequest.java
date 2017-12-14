// Copyright (C) 2012-13 MINHAP, Gobierno de España
// This program is licensed and may be used, modified and redistributed under the terms
// of the European Public License (EUPL), either version 1.1 or (at your
// option) any later version as soon as they are approved by the European Commission.
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// or implied. See the License for the specific language governing permissions and
// more details.
// You should have received a copy of the EUPL1.1 license
// along with this program; if not, you may find it at
// http://joinup.ec.europa.eu/software/page/eupl/licence-eupl

/**
 * <b>File:</b><p>es.gob.afirma.integraFacade.GenerateMessageRequest.java.</p>
 * <b>Description:</b><p>Class that manages the generation of request messages to invoke the DSS web services of @Firma.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * <b>Date:</b><p>26/11/2014.</p>
 * @author Gobierno de España.
 * @version 1.0, 26/11/2014.
 */
package es.caib.archivodigital.esb.services.mediators.afirma.integraFacade;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import es.caib.archivodigital.esb.services.mediators.afirma.i18n.CaibEsbLanguage;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.CaibEsbSignatureFormatDetector;
import es.caib.archivodigital.esb.services.mediators.afirma.utils.CaibEsbBase64Coder;
import es.caib.archivodigital.esb.services.mediators.afirma.utils.CaibEsbUtilsXML;
import es.gob.afirma.i18n.ILogConstantKeys;
import es.gob.afirma.integraFacade.pojo.ArchiveRequest;
import es.gob.afirma.integraFacade.pojo.BatchVerifyCertificateRequest;
import es.gob.afirma.integraFacade.pojo.BatchVerifySignatureRequest;
import es.gob.afirma.integraFacade.pojo.CoSignRequest;
import es.gob.afirma.integraFacade.pojo.CounterSignRequest;
import es.gob.afirma.integraFacade.pojo.OptionalParameters;
import es.gob.afirma.integraFacade.pojo.PendingRequest;
import es.gob.afirma.integraFacade.pojo.ServerSignerRequest;
import es.gob.afirma.integraFacade.pojo.UpgradeSignatureRequest;
import es.gob.afirma.integraFacade.pojo.VerificationReport;
import es.gob.afirma.integraFacade.pojo.VerifyCertificateRequest;
import es.gob.afirma.integraFacade.pojo.VerifySignatureRequest;
import es.gob.afirma.transformers.TransformersException;
import es.gob.afirma.utils.DSSConstants.DSSTagsRequest;
import es.gob.afirma.utils.GenericUtils;
import es.gob.afirma.utils.NumberConstants;
import es.gob.afirma.utils.UtilsFileSystem;

/**
 * <p>Class that manages the generation of request messages to invoke the DSS web services of @Firma.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * @version 1.0, 26/11/2014.
 */
public final class CaibEsbGenerateMessageRequest {

	/**
	 *  Attribute that represents the object that manages the log of the class.
	 */
	private static final Logger LOGGER = Logger.getLogger(CaibEsbGenerateMessageRequest.class);

	/**
	 * Constant attribute that identifies the URI corresponding to identifier level of detail "FORMAL_DOCUMENT".
	 */
	public static final String FORMAL_DOCUMENT = "urn:afirma:dss:1.0:profile:XSS:SigPolicyDocument:FormalDocument";

	/**
	 * Constant attribute that identifies the URI corresponding to identifier level of detail "SIGNATURE_TIMESTAMP".
	 */
	public static final String SIGNATURE_TIMESTAMP = "urn:afirma:dss:1.0:profile:XSS:SignatureProperty:SignatureTimeStamp";
	
	/**
	 * Constructor method for the class GenerateMessageRequest.java.
	 */
	private CaibEsbGenerateMessageRequest() {
	}

	/**
	 * Method that generates a XML request message to invoke the server signature service.
	 * @param serSigReq Parameter that allows to generate the sign request.
	 * @return a map with the parameters related to the server signature request.
	 */
	public static Map<String, Object> generateServerSignerRequest(ServerSignerRequest serSigReq) {
		// se crea el mensaje de petición a partir del párametro de entrada
		Map<String, Object> inputParameters = new HashMap<String, Object>();

		// documento a firmar.
		byte[ ] document = serSigReq.getDocument();
		if (document != null) {
			String encodedDocumentToSign = null;
			try {
				encodedDocumentToSign = new String(CaibEsbBase64Coder.encodeBase64(document));
			} catch (TransformersException e) {
				LOGGER.error(CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG044), e);
			}
			if (CaibEsbUtilsXML.isXMLFormat(document)) {
				// si es xml, se incluye en dss:Base64XML, codificado en base64
				inputParameters.put(DSSTagsRequest.BASE64XML, encodedDocumentToSign);
			} else {
				// si no es xml
				inputParameters.put(DSSTagsRequest.BASE64DATA, encodedDocumentToSign);
			}
		} else if (serSigReq.getDocumentHash() != null) {
			inputParameters.put(DSSTagsRequest.DIGEST_METHOD_ATR_ALGORITHM, serSigReq.getDocumentHash().getDigestMethod());
			try {
				inputParameters.put(DSSTagsRequest.DOCUMENTHASH_VALUE, new String(CaibEsbBase64Coder.encodeBase64(serSigReq.getDocumentHash().getDigestValue())));
			} catch (TransformersException e) {
				LOGGER.error(CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG051), e);
			}
		} else {
			inputParameters.put(DSSTagsRequest.DOCUMENT_ARCHIVE_ID, serSigReq.getDocumentId());
			inputParameters.put(DSSTagsRequest.INPUTDOC_GETCONTENTSTREAM_REPOID, serSigReq.getDocumentRepository().getId());
			inputParameters.put(DSSTagsRequest.INPUTDOC_GETCONTENTSTREAM_OBJECTID, serSigReq.getDocumentRepository().getObject());
		}

		inputParameters.put(DSSTagsRequest.CLAIMED_IDENTITY, serSigReq.getApplicationId());
		inputParameters.put(DSSTagsRequest.KEY_SELECTOR, serSigReq.getKeySelector());

		// se trata el campo signatureFormat
		if (serSigReq.getSignatureFormat() != null) {
			inputParameters.put(DSSTagsRequest.SIGNATURE_TYPE, serSigReq.getSignatureFormat().getUriType());
			inputParameters.put(DSSTagsRequest.SIGNATURE_FORM, serSigReq.getSignatureFormat().getUriFormat() == null ? "" : serSigReq.getSignatureFormat().getUriFormat());
		}

		// hashAlgorithm
		if (serSigReq.getHashAlgorithm() != null) {
			inputParameters.put(DSSTagsRequest.HASH_ALGORITHM, serSigReq.getHashAlgorithm().getUri());
		}

		// signaturePolicyIdentifier
		if (GenericUtils.assertStringValue(serSigReq.getSignaturePolicyIdentifier())) {
			inputParameters.put(DSSTagsRequest.SIGPOL_SIGNATURE_POLICY_IDENTIFIER, serSigReq.getSignaturePolicyIdentifier());
		}

		// xmlSignatureMode
		if (serSigReq.getXmlSignatureMode() != null) {
			inputParameters.put(DSSTagsRequest.XML_SIGNATURE_MODE, serSigReq.getXmlSignatureMode());
		}

		// ignoreGracePeriod
		if (serSigReq.isIgnoreGracePeriod()) {
			inputParameters.put(DSSTagsRequest.IGNORE_GRACE_PERIOD, "");
		}

		return inputParameters;

	}

	/**
	 * Method that generates a XML request message to invoke the server co-signature service.
	 * @param coSigReq Parameter that allows to generate the cosign request.
	 * @return a map with the parameters related to the server co-signature request.
	 */
	public static Map<String, Object> generateCoSignRequest(CoSignRequest coSigReq) {
		// se crea el mensaje de petición a partir del párametro de entrada
		Map<String, Object> inputParameters = new HashMap<String, Object>();

		// transactionId
		if (GenericUtils.assertStringValue(coSigReq.getTransactionId())) {
			inputParameters.put(DSSTagsRequest.DOCUMENT_ARCHIVE_ID, coSigReq.getTransactionId());
		} else if (GenericUtils.assertArrayValid(coSigReq.getSignature())) {
			String encodedDoc = null;
			try {
				encodedDoc = new String(CaibEsbBase64Coder.encodeBase64(coSigReq.getDocument()));
			} catch (TransformersException e) {
				LOGGER.error(CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG044), e);
			}
			if (encodedDoc != null) {
				
				List<Map<String, Object>> documentList = new ArrayList<Map<String, Object>>();
				Map<String, Object> docMap = new HashMap<String, Object>();
				
    			if (CaibEsbUtilsXML.isXMLFormat(coSigReq.getDocument())) {
    				
    				docMap.put(DSSTagsRequest.BASE64XML_LAST, encodedDoc);
    				documentList.add(docMap);
    				incorporateSignatureImplicitCoCounterSign(inputParameters, coSigReq.getSignature(), documentList);
    					
    			} else {
    					
    				docMap.put(DSSTagsRequest.BASE64DATA_LAST, encodedDoc);
    				documentList.add(docMap);		
    				incorporateSignatureImplicitCoCounterSign(inputParameters, coSigReq.getSignature(), documentList);
    			
    			}
    			
    			Map<String, Object>[] documents = documentList.toArray(new HashMap[documentList.size()]);
				inputParameters.put(DSSTagsRequest.DOCUMENT, documents);
			}
			
		} else {
			// localización de la firma en gestor documental
			inputParameters.put(DSSTagsRequest.INPUTDOC_GETCONTENTSTREAM_REPOID, coSigReq.getDocumentRepository().getId());
			inputParameters.put(DSSTagsRequest.INPUTDOC_GETCONTENTSTREAM_OBJECTID, coSigReq.getDocumentRepository().getObject());
			inputParameters.put(DSSTagsRequest.INPUTDOC_SIGNATURE_GETCONTENTSTREAM_REPOID, coSigReq.getSignatureRepository().getId());
			inputParameters.put(DSSTagsRequest.INPUTDOC_SIGNATURE_GETCONTENTSTREAM_OBJECTID, coSigReq.getSignatureRepository().getObject());
		}
		inputParameters.put(DSSTagsRequest.CLAIMED_IDENTITY, coSigReq.getApplicationId());
		inputParameters.put(DSSTagsRequest.KEY_SELECTOR, coSigReq.getKeySelector());
		inputParameters.put(DSSTagsRequest.PARALLEL_SIGNATURE,  "");

		// hashAlgorithm
		if (coSigReq.getHashAlgorithm() != null) {
			inputParameters.put(DSSTagsRequest.HASH_ALGORITHM, coSigReq.getHashAlgorithm().getUri());
		}

		// signaturePolicyIdentifier
		if (GenericUtils.assertStringValue(coSigReq.getSignaturePolicyIdentifier())) {
			inputParameters.put(DSSTagsRequest.SIGPOL_SIGNATURE_POLICY_IDENTIFIER, coSigReq.getSignaturePolicyIdentifier());
		}
		// ignoreGracePeriod
		if (coSigReq.isIgnoreGracePeriod()) {
			inputParameters.put(DSSTagsRequest.IGNORE_GRACE_PERIOD, "");
		}

		return inputParameters;

	}

	/**
	 * Method that generates a XML request message to invoke the server counter-signature service.
	 * @param couSigReq Parameter that allows to generate the countersign request.
	 * @return a map with the parameters related to the server counter-signature request.
	 */
	public static Map<String, Object> generateCounterSignRequest(CounterSignRequest couSigReq) {
		// se crea el mensaje de petición a partir del párametro de entrada
		Map<String, Object> inputParameters = new HashMap<String, Object>();

		// localización de la firma origen en @firma
		if (GenericUtils.assertStringValue(couSigReq.getTransactionId())) {
			inputParameters.put(DSSTagsRequest.DOCUMENT_ARCHIVE_ID, couSigReq.getTransactionId());
		} else if (GenericUtils.assertArrayValid(couSigReq.getSignature())) {
			List<Map<String, Object>> documentList = new ArrayList<Map<String, Object>>();
			
			incorporateSignatureImplicitCoCounterSign(inputParameters, couSigReq.getSignature(), documentList);
			if (documentList.size() > 0) {
				Map<String, Object>[] documents = documentList.toArray(new HashMap[documentList.size()]);
				inputParameters.put(DSSTagsRequest.DOCUMENT, documents);
			}
		} else {
			// localización de la firma en gestor documental
			inputParameters.put(DSSTagsRequest.INPUTDOC_SIGNATURE_GETCONTENTSTREAM_REPOID, couSigReq.getSignatureRepository().getId());
			inputParameters.put(DSSTagsRequest.INPUTDOC_SIGNATURE_GETCONTENTSTREAM_OBJECTID, couSigReq.getSignatureRepository().getObject());
		}
		inputParameters.put(DSSTagsRequest.CLAIMED_IDENTITY, couSigReq.getApplicationId());
		inputParameters.put(DSSTagsRequest.KEY_SELECTOR, couSigReq.getKeySelector());
		inputParameters.put(DSSTagsRequest.COUNTER_SIGNATURE, "");

		// hashAlgorithm
		if (couSigReq.getHashAlgorithm() != null) {
			inputParameters.put(DSSTagsRequest.HASH_ALGORITHM, couSigReq.getHashAlgorithm().getUri());
		}

		// signaturePolicyIdentifier
		if (GenericUtils.assertStringValue(couSigReq.getSignaturePolicyIdentifier())) {
			inputParameters.put(DSSTagsRequest.SIGPOL_SIGNATURE_POLICY_IDENTIFIER, couSigReq.getSignaturePolicyIdentifier());
		}
		// ignoreGracePeriod
		if (couSigReq.isIgnoreGracePeriod()) {
			inputParameters.put(DSSTagsRequest.IGNORE_GRACE_PERIOD, "");
		}

		// targetSigner
		if (couSigReq.getTargetSigner() != null) {
			String encodedTargetSigner = null;
			try {
				encodedTargetSigner = new String(CaibEsbBase64Coder.encodeBase64(couSigReq.getTargetSigner()));
			} catch (TransformersException e) {
				LOGGER.error(CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG045), e);
			}
			inputParameters.put(DSSTagsRequest.TARGET_SIGNER, encodedTargetSigner);
		}

		return inputParameters;

	}

	/**
	 * Method that adds to a XML request message to invoke the verify signature service the original signed document.
	 * @param inputParameters Parameter that represents a map with the parameters related to the verify signature request.
	 * @param verSigReq Parameter that allows to generate the verify signature request.
	 */
	private static void encodeOriginalSignedDocument(Map<String, Object> inputParameters, VerifySignatureRequest verSigReq) {
		byte[ ] document = verSigReq.getDocument();
		if (document != null) {
			String encodedSignedDocument = null;
			try {
				encodedSignedDocument = new String(CaibEsbBase64Coder.encodeBase64(document));
			} catch (TransformersException e) {
				LOGGER.error(CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG046), e);
			}
			if (CaibEsbUtilsXML.isXMLFormat(document)) {
				// si es xml, se incluye en dss:base64XML el documento
				// codificado en Base64
				inputParameters.put(DSSTagsRequest.BASE64XML, encodedSignedDocument);
			} else {
				// si no es xml, se incluye en dss:base64DATA el documento
				// codificado en Base64
				inputParameters.put(DSSTagsRequest.BASE64DATA, encodedSignedDocument);
			}

		} else {
			if (verSigReq.getDocumentRepository() != null) {
				inputParameters.put(DSSTagsRequest.INPUTDOC_GETCONTENTSTREAM_REPOID, verSigReq.getDocumentRepository().getId());
				inputParameters.put(DSSTagsRequest.INPUTDOC_GETCONTENTSTREAM_OBJECTID, verSigReq.getDocumentRepository().getObject());
			}
		}
	}

	/**
	 * Method that generates a XML request message to invoke the verify signature service.
	 * @param verSigReq Parameter that allows to generate the verify signature request.
	 * @return a map with the parameters related to the verify signature request.
	 */
	public static Map<String, Object> generateVerifySignRequest(VerifySignatureRequest verSigReq) {
		// se crea el mensaje de petición a partir del párametro de entrada
		Map<String, Object> inputParameters = new HashMap<String, Object>();

		// signatureObject
		// si es firma implícita
		String encodedDoc = null;
		if (verSigReq.getSignature() != null) {
			if (verSigReq.getDocument() == null) {
				incorporateSignatureImplicit(inputParameters, verSigReq.getSignature());
			} else { 
    			
    			try {
    				encodedDoc = new String(CaibEsbBase64Coder.encodeBase64(verSigReq.getDocument()));
    			} catch (TransformersException e) {
    				LOGGER.error(CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG044), e);
    			}
    			if (encodedDoc != null) {
    				
    				List<Map<String, Object>> documentList = new ArrayList<Map<String, Object>>();
    				Map<String, Object> docMap = new HashMap<String, Object>();
    				
        			if (CaibEsbUtilsXML.isXMLFormat(verSigReq.getDocument())) {
        				
        				docMap.put(DSSTagsRequest.BASE64XML_LAST, encodedDoc);
        				documentList.add(docMap);
        				incorporateSignatureImplicitVerifySign(inputParameters, verSigReq.getSignature(), documentList);
        					
        			} else {
        					
        				docMap.put(DSSTagsRequest.BASE64DATA_LAST, encodedDoc);
        				documentList.add(docMap);		
        				incorporateSignatureImplicitVerifySign(inputParameters, verSigReq.getSignature(), documentList);
        			
        			}
        			
        			Map<String, Object>[] documents = documentList.toArray(new HashMap[documentList.size()]);
    				inputParameters.put(DSSTagsRequest.DOCUMENT, documents);
    			}
			}
		} else {
			// se encuentra en un repositorio
			inputParameters.put(DSSTagsRequest.SIGNATURE_OTHER_GETCONTENTSTREAM_REPOID, verSigReq.getSignatureRepository().getId());
			inputParameters.put(DSSTagsRequest.SIGNATURE_OTHER_GETCONTENTSTREAM_OBJECTID, verSigReq.getSignatureRepository().getObject());
		}

		// inputDocuments
		// documento original firmado
		if (encodedDoc == null) {
			encodeOriginalSignedDocument(inputParameters, verSigReq);
		}

		// documentHash
		if (verSigReq.getDocumentHash() != null) {
			if (verSigReq.getDocumentHash().getDigestMethod() != null) {
				inputParameters.put(DSSTagsRequest.DIGEST_METHOD_ATR_ALGORITHM, verSigReq.getDocumentHash().getDigestMethod());
			}
			String encodedSignedDocumentHash = null;
			try {
				encodedSignedDocumentHash = new String(CaibEsbBase64Coder.encodeBase64(verSigReq.getDocumentHash().getDigestValue()));
			} catch (TransformersException e) {
				LOGGER.error(CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG047), e);
			}

			inputParameters.put(DSSTagsRequest.DOCUMENTHASH_VALUE, encodedSignedDocumentHash);
		}

		// applicationId
		inputParameters.put(DSSTagsRequest.CLAIMED_IDENTITY, verSigReq.getApplicationId());

		// returnVerificationReport
		if (verSigReq.getVerificationReport() != null) {
			inputParameters.put(DSSTagsRequest.INCLUDE_CERTIFICATE, verSigReq.getVerificationReport().getIncludeCertificateValues().toString());
			inputParameters.put(DSSTagsRequest.INCLUDE_REVOCATION, verSigReq.getVerificationReport().getIncludeRevocationValues().toString());
			if (verSigReq.getVerificationReport().getReportDetailLevel() != null) {
				inputParameters.put(DSSTagsRequest.REPORT_DETAIL_LEVEL, verSigReq.getVerificationReport().getReportDetailLevel().getUri());
			}
		}

		// optionalParameters
		OptionalParameters optParam = verSigReq.getOptionalParameters();
		if (optParam != null) {
			incorporateOptionalParameter(inputParameters, optParam);
		}

		return inputParameters;

	}

	/**
	 * Method that generates a XML request message to invoke the upgrade signature service.
	 * @param upgSigReq Parameter that allows to generate the upgrade signature request.
	 * @return a map with the parameters related to the upgrade signature request.
	 */
	public static Map<String, Object> generateUpgradeSignatureRequest(UpgradeSignatureRequest upgSigReq) {
		Map<String, Object> inputParameters = new HashMap<String, Object>();

		// signatureObject
		// si es firma implícita
		if (upgSigReq.getSignature() != null) {
			incorporateSignatureImplicit(inputParameters, upgSigReq.getSignature());
		} else {
			// se encuentra en un repositorio
			// se tiene la localización de la firma en el repositorio y la
			// id de la transacción
			inputParameters.put(DSSTagsRequest.SIGNATURE_ARCHIVE_ID, upgSigReq.getTransactionId());
			inputParameters.put(DSSTagsRequest.SIGNATURE_OTHER_GETCONTENTSTREAM_REPOID, upgSigReq.getSignatureRepository().getId());
			inputParameters.put(DSSTagsRequest.SIGNATURE_OTHER_GETCONTENTSTREAM_OBJECTID, upgSigReq.getSignatureRepository().getObject());
		}

		// applicationId
		inputParameters.put(DSSTagsRequest.CLAIMED_IDENTITY, upgSigReq.getApplicationId());

		// si signatureFormat no es nula, se indica el formato en el elemento
		// ReturnUpdatedSignature
		if (upgSigReq.getSignatureFormat() != null) {
			inputParameters.put(DSSTagsRequest.RETURN_UPDATED_SIGNATURE_ATR_TYPE, upgSigReq.getSignatureFormat().getUriFormat());
		}

		// targetSigner
		if (upgSigReq.getTargetSigner() != null) {
			String encodedTargetSigner = null;
			try {
				encodedTargetSigner = new String(CaibEsbBase64Coder.encodeBase64(upgSigReq.getTargetSigner()));
			} catch (TransformersException e) {
				LOGGER.error(CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG045), e);
			}

			inputParameters.put(DSSTagsRequest.TARGET_SIGNER, encodedTargetSigner);
		}

		// ignoreGracePeriod
		if (upgSigReq.isIgnoreGracePeriod()) {
			inputParameters.put(DSSTagsRequest.IGNORE_GRACE_PERIOD, "");
		}

		return inputParameters;
	}

	/**
	 * Method that generates a XML request message to invoke the verify certificate service.
	 * @param verCerReq Parameter that allows to generate the verify certificate request.
	 * @return a map with the parameters related to the verify certificate request.
	 */
	public static Map<String, Object> generateVerifyCertificateRequest(VerifyCertificateRequest verCerReq) {
		Map<String, Object> inputParameters = new HashMap<String, Object>();

		// optionalInputs/ dss:ClaimedIdentity (applicationId)
		if (verCerReq.getApplicationId() != null) {
			inputParameters.put(DSSTagsRequest.CLAIMED_IDENTITY, verCerReq.getApplicationId());
		}

		// optionalInputs/ afxp:ReturnReadableCertificateInfo
		// (returnRedeableCertificateInfo)
		if (verCerReq.getReturnReadableCertificateInfo()) {
			inputParameters.put(DSSTagsRequest.RETURN_READABLE_CERT_INFO, "");
		}

		// optionalInputs/
		// vr:ReturnVerificationReport
		VerificationReport verRep = verCerReq.getReturnVerificationReport();
		if (verRep != null) {
			// vr:ReturnVerificationReport/vr:CheckOptions/vr:checkCertificateStatus
			if (verRep.getCheckCertificateStatus()) {
				inputParameters.put(DSSTagsRequest.CHECK_CERTIFICATE_STATUS, Boolean.TRUE.toString());
			}
			// vr:ReportOptions/vr:includeCertificateValues
			if (verRep.getIncludeCertificateValues()) {
				inputParameters.put(DSSTagsRequest.INCLUDE_CERTIFICATE, Boolean.TRUE.toString());
			}
			// vr:ReportOptions/vr:includeRevocationValue
			if (verRep.getIncludeRevocationValues()) {
				inputParameters.put(DSSTagsRequest.INCLUDE_REVOCATION, Boolean.TRUE.toString());
			}
			// vr:ReportOptions/vr:ReportDetailLevel
			if (verRep.getReportDetailLevel() != null) {
				inputParameters.put(DSSTagsRequest.REPORT_DETAIL_LEVEL, verRep.getReportDetailLevel().getUri());
			}
		}

		// dss:signatureObject/dss:other/ds:x5090Data
		if (verCerReq.getCertificate() != null) {
			// inputParameters.put(DSSTagsRequest.X509_CERTIFICATE, new
			// String(UtilsBase64.encodeBytes(verCerReq.getSignature())));
			inputParameters.put(DSSTagsRequest.X509_CERTIFICATE, UtilsFileSystem.getFileBase64Encoded(verCerReq.getCertificate()));
		} else {
			inputParameters.put(DSSTagsRequest.X509_DATA_GETCONTENTSTREAM_REPOID, verCerReq.getCertificateRepository().getId());
			inputParameters.put(DSSTagsRequest.X509_DATA_GETCONTENTSTREAM_OBJECTID, verCerReq.getCertificateRepository().getObject());
		}
		return inputParameters;
	}

	/**
	 * Method that generates a XML request message to invoke the verify signatures on batch service.
	 * @param batVerSigReq Parameter that allows to generate the verify signatures on batch request.
	 * @return a map with the parameters related to the verify signatures on batch request.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> generateBatchVerifySignatureRequest(BatchVerifySignatureRequest batVerSigReq) {
		Map<String, Object> inputParameters = new HashMap<String, Object>();

		// afxp:Request/dss:VerifyRequest
		List<VerifySignatureRequest> listSignatures = batVerSigReq.getListVerifySignature();

		Map<String, Object>[ ] listSigMap = new HashMap[listSignatures.size()];

		int i = 0;
		for (VerifySignatureRequest verSigReq: listSignatures) {
			Map<String, Object> mapSig = generateVerifySignRequest(verSigReq);
			// se quita la aplicación si estuviera en la petición
			if (mapSig.containsKey(DSSTagsRequest.CLAIMED_IDENTITY)) {
				mapSig.remove(DSSTagsRequest.CLAIMED_IDENTITY);
			}
			// se le añade idRequest para identificar la firma
			mapSig.put(DSSTagsRequest.VERIFY_REQUEST_ATTR_REQUEST_ID, String.valueOf(Math.random() * NumberConstants.INT_9999));
			listSigMap[i] = mapSig;
			i++;
		}

		inputParameters.put(DSSTagsRequest.VERIFY_REQUEST, listSigMap);

		// optionalInputs/ dss:ClaimedIdentity (applicationId)
		inputParameters.put(DSSTagsRequest.CLAIMED_IDENTITY, batVerSigReq.getApplicationId());

		// se indica el tipo de petición en lote realizada.
		inputParameters.put(DSSTagsRequest.BATCH_REQUEST_ATTR_TYPE, DSSTagsRequest.BATCH_VERIFY_SIGN_TYPE);

		return inputParameters;
	}

	/**
	 * Method that generates a XML request message to invoke the verify certificates on batch service.
	 * @param batVerCerReq Parameter that allows to generate the verify certificates on batch request.
	 * @return a map with the parameters related to the verify certificates on batch request.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> generateBatchVerifyCertificateRequest(BatchVerifyCertificateRequest batVerCerReq) {
		Map<String, Object> inputParameters = new HashMap<String, Object>();

		// afxp:Request/dss:VerifyRequest
		List<VerifyCertificateRequest> listCertificates = batVerCerReq.getListVerifyCertificate();
		Map<String, Object>[ ] listCertMap = new HashMap[listCertificates.size()];

		int i = 0;
		for (VerifyCertificateRequest verCerReq: listCertificates) {
			Map<String, Object> mapCer = generateVerifyCertificateRequest(verCerReq);
			// se quita la aplicación si estuviera en la petición
			if (mapCer.containsKey(DSSTagsRequest.CLAIMED_IDENTITY)) {
				mapCer.remove(DSSTagsRequest.CLAIMED_IDENTITY);
			}
			// se le añade idRequest para identificar el certificado
			mapCer.put(DSSTagsRequest.VERIFY_REQUEST_ATTR_REQUEST_ID, String.valueOf(Math.random() * NumberConstants.INT_9999));
			listCertMap[i] = mapCer;
			i++;
		}

		inputParameters.put(DSSTagsRequest.VERIFY_REQUEST, listCertMap);

		// optionalInputs/ dss:ClaimedIdentity (applicationId)
		inputParameters.put(DSSTagsRequest.CLAIMED_IDENTITY, batVerCerReq.getApplicationId());

		// se indica el tipo de petición en lote realizada.
		inputParameters.put(DSSTagsRequest.BATCH_REQUEST_ATTR_TYPE, DSSTagsRequest.BATCH_VERIFY_CERT_TYPE);
		return inputParameters;
	}

	/**
	 * Method that generates a XML request message to invoke the asynchronous processes of sign and verify service.
	 * @param pendingRequest Parameter that allows to generate the asynchronous processes of sign and verify request.
	 * @return a map with the parameters related to the async processes of sign and verify request.
	 */
	public static Map<String, Object> generatePendingRequest(PendingRequest pendingRequest) {
		Map<String, Object> inputParameters = new HashMap<String, Object>();

		// optionalInputs/ dss:ClaimedIdentity (applicationId)
		inputParameters.put(DSSTagsRequest.CLAIMED_IDENTITY, pendingRequest.getApplicationId());

		// optionalInputs/ dss: async:ResponseID
		inputParameters.put(DSSTagsRequest.ASYNC_RESPONSE_ID, pendingRequest.getResponseId());

		return inputParameters;
	}

	/**
	 * Method that generates a XML request message to invoke the archive signatures retrieve service.
	 * @param archiveRequest Parameter that allows to generate the archive signatures retrieve request.
	 * @return a map with the parameters related to the archive signatures retrieve request.
	 */
	public static Map<String, Object> generateArchiveRequest(ArchiveRequest archiveRequest) {
		Map<String, Object> inputParameters = new HashMap<String, Object>();

		// optionalInputs/ dss:ClaimedIdentity (applicationId)
		inputParameters.put(DSSTagsRequest.CLAIMED_IDENTITY, archiveRequest.getApplicationId());

		// optionalInputs/ dss: async:ResponseID
		inputParameters.put(DSSTagsRequest.ARCHIVE_IDENTIFIER, archiveRequest.getTransactionId());

		return inputParameters;
	}

	/**
	 * Method that adds an implicit signature to a request message.
	 * @param inputParameters Parameter that represents the request as a map of elements.
	 * @param signature Parameter that represents the signature.
	 */
	private static void incorporateSignatureImplicit(Map<String, Object> inputParameters, byte[ ] signature) {
		try {

			// si no es XML, se guarda en Base64Signature codificado en base 64
			if (!CaibEsbSignatureFormatDetector.isXMLFormat(signature)) {
				inputParameters.put(DSSTagsRequest.SIGNATURE_BASE64, new String(CaibEsbBase64Coder.encodeBase64(signature)));
				// inputParameters.put(DSSTagsRequest.SIGNATURE_PTR_ATR_WHICH,
				// String.valueOf(Math.random() * NumberConstants.INT_9999));
				// inputParameters.put(DSSTagsRequest.DOCUMENT_ATR_ID,
				// String.valueOf(Math.random() * NumberConstants.INT_9999));
			} else {
				// se comprueba si es enveloping, enveloped o detached
				DocumentBuilderFactory dBFactory = DocumentBuilderFactory.newInstance();
				dBFactory.setNamespaceAware(true);

				// Lectura y parseo de la firma xml.
				Document signDoc;
				signDoc = dBFactory.newDocumentBuilder().parse(new ByteArrayInputStream(signature));
				Element elto = signDoc.getDocumentElement();
				if("Signature".equals(elto.getLocalName()) && XMLSignature.XMLNS.equals(elto.getNamespaceURI())){
					// si es enveloping, sin codificar en ds:Signature
					inputParameters.put(DSSTagsRequest.SIGNATURE_OBJECT, new String(signature));
				} else {
					// si es enveloped o detached, se incluye en ds:SignaturePtr
					// referencia al elemento dss:BaseXML
					String idSignaturePtr = String.valueOf(Math.random() * NumberConstants.INT_9999);
					inputParameters.put(DSSTagsRequest.SIGNATURE_PTR_ATR_WHICH, idSignaturePtr);
					inputParameters.put(DSSTagsRequest.DOCUMENT_ATR_ID, idSignaturePtr);
					// en dss:document/dss:base64XML se guarda la firma
					// codificada en base64
					inputParameters.put(DSSTagsRequest.BASE64XML, new String(CaibEsbBase64Coder.encodeBase64(signature)));
				}
			}

		} catch (SAXException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		} catch (IOException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		} catch (ParserConfigurationException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		} catch (TransformersException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		}
	}
	
	/**
	 * Method that adds an implicit signature to a request message for coSign.
	 * @param inputParameters Parameter that represents the request as a map of elements.
	 * @param signature Parameter that represents the signature.
	 */
	private static void incorporateSignatureImplicitCoCounterSign(Map<String, Object> inputParameters, byte[ ] signature, List<Map<String, Object>> documentList) {
		try {

			Map<String, Object> docMap = new HashMap<String, Object>();
			// si no es XML, se guarda en Base64Signature codificado en base 64
			if (!CaibEsbSignatureFormatDetector.isXMLFormat(signature)) {
				inputParameters.put(DSSTagsRequest.INPUTDOC_SIGNATURE_BASE64SIGNATURE, new String(CaibEsbBase64Coder.encodeBase64(signature)));
			} else {
				// se comprueba si es enveloping, enveloped o detached
				DocumentBuilderFactory dBFactory = DocumentBuilderFactory.newInstance();
				dBFactory.setNamespaceAware(true);

				// Lectura y parseo de la firma xml.
				Document signDoc;
				signDoc = dBFactory.newDocumentBuilder().parse(new ByteArrayInputStream(signature));
				Element elto = signDoc.getDocumentElement();
				if("Signature".equals(elto.getLocalName()) && XMLSignature.XMLNS.equals(elto.getNamespaceURI())){
					// si es enveloping, sin codificar en ds:Signature
					inputParameters.put(DSSTagsRequest.SIGNATURE_OBJECT, new String(signature));
				} else {
					// si es enveloped o detached, se incluye en ds:SignaturePtr
					// referencia al elemento dss:BaseXML
					String idSignaturePtr = String.valueOf(Math.random() * NumberConstants.INT_9999);
					
    				docMap.put(DSSTagsRequest.BASE64XML_LAST, new String(CaibEsbBase64Coder.encodeBase64(signature)));
    				docMap.put(DSSTagsRequest.DOCUMENT_ATR_ID_LAST, idSignaturePtr);
    				inputParameters.put(DSSTagsRequest.INPUTDOC_SIGNATURE_SIGNATURE_PTR_ATR_WHICH, idSignaturePtr);
    					
    				documentList.add(docMap);
				}
			}

		} catch (SAXException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		} catch (IOException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		} catch (ParserConfigurationException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		} catch (TransformersException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		}
	}
	
	/**
	 * Method that adds an implicit signature to a request message for verify a signature.
	 * @param inputParameters Parameter that represents the request as a map of elements.
	 * @param signature Parameter that represents the signature.
	 */
	private static void incorporateSignatureImplicitVerifySign(Map<String, Object> inputParameters, byte[ ] signature, List<Map<String, Object>> documentList) {
		try {

			Map<String, Object> docMap = new HashMap<String, Object>();
			// si no es XML, se guarda en Base64Signature codificado en base 64
			if (!CaibEsbSignatureFormatDetector.isXMLFormat(signature)) {
				inputParameters.put(DSSTagsRequest.SIGNATURE_BASE64, new String(CaibEsbBase64Coder.encodeBase64(signature)));
			} else {
				// se comprueba si es enveloping, enveloped o detached
				DocumentBuilderFactory dBFactory = DocumentBuilderFactory.newInstance();
				dBFactory.setNamespaceAware(true);

				// Lectura y parseo de la firma xml.
				Document signDoc;
				signDoc = dBFactory.newDocumentBuilder().parse(new ByteArrayInputStream(signature));
				Element elto = signDoc.getDocumentElement();
				if("Signature".equals(elto.getLocalName()) && XMLSignature.XMLNS.equals(elto.getNamespaceURI())){
					// si es enveloping, sin codificar en ds:Signature
					inputParameters.put(DSSTagsRequest.SIGNATURE_OBJECT, new String(signature));
				} else {
					// si es enveloped o detached, se incluye en ds:SignaturePtr
					// referencia al elemento dss:BaseXML
					String idSignaturePtr = String.valueOf(Math.random() * NumberConstants.INT_9999);
					
    				docMap.put(DSSTagsRequest.BASE64XML_LAST, new String(CaibEsbBase64Coder.encodeBase64(signature)));
    				docMap.put(DSSTagsRequest.DOCUMENT_ATR_ID_LAST, idSignaturePtr);
    				inputParameters.put(DSSTagsRequest.INPUTDOC_SIGNATURE_SIGNATURE_PTR_ATR_WHICH, idSignaturePtr);
    					
    				documentList.add(docMap);
				}
			}
		} catch (SAXException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		} catch (IOException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		} catch (ParserConfigurationException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		} catch (TransformersException e) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG030, new Object[ ] { e.getMessage() }));
		}
	}

	/**
	 * Method that adds optional parameters to a request for the verify signature service.
	 * @param inputParameters Parameter that represents the request as a map of elements.
	 * @param optParams Parameter that represents the optional elements to add.
	 */
	private static void incorporateOptionalParameter(Map<String, Object> inputParameters, OptionalParameters optParams) {
		// returnReadableCertificateInfo
		if (optParams.isReturnReadableCertificateInfo()) {
			inputParameters.put(DSSTagsRequest.RETURN_READABLE_CERT_INFO, "");
		}

		// additionalReportOption
		if (optParams.isAdditionalReportOption()) {
			inputParameters.put(DSSTagsRequest.ADDICIONAL_REPORT_OPT_SIGNATURE_TIMESTAMP, CaibEsbGenerateMessageRequest.SIGNATURE_TIMESTAMP);
		}

		// returnProcessingDetails
		if (optParams.isReturnProcessingDetails()) {
			inputParameters.put(DSSTagsRequest.RETURN_PROCESSING_DETAILS, "");
		}

		// returnSignPolicyDocument
		if (optParams.isReturnSignPolicyDocument()) {
			// si se incluye, se incluye en la petición una URI que identifica
			// el tipo de documento de la politica que se desea obtener.
			inputParameters.put(DSSTagsRequest.RETURN_SIGN_POLICY_DOCUMENT, CaibEsbGenerateMessageRequest.FORMAL_DOCUMENT);
		}

		// returnSignedDataInfo
		if (optParams.isReturnSignedDataInfo()) {
			inputParameters.put(DSSTagsRequest.RETURN_SIGNED_DATA_INFO, "");
		}
	}

}
