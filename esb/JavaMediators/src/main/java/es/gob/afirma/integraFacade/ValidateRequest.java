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
 * <b>File:</b><p>es.gob.afirma.integraFacade.ValidateRequest.java.</p>
 * <b>Description:</b><p>Class that manages the verification of the requests for @Firma web services.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * <b>Date:</b><p>16/12/2014.</p>
 * @author Gobierno de España.
 * @version 1.0, 16/12/2014.
 */
package es.gob.afirma.integraFacade;

import es.caib.archivodigital.esb.services.mediators.afirma.i18n.CaibEsbLanguage;
import es.gob.afirma.i18n.ILogConstantKeys;
import es.gob.afirma.integraFacade.pojo.ArchiveRequest;
import es.gob.afirma.integraFacade.pojo.BatchVerifyCertificateRequest;
import es.gob.afirma.integraFacade.pojo.BatchVerifySignatureRequest;
import es.gob.afirma.integraFacade.pojo.CoSignRequest;
import es.gob.afirma.integraFacade.pojo.ContentRequest;
import es.gob.afirma.integraFacade.pojo.CounterSignRequest;
import es.gob.afirma.integraFacade.pojo.DocumentRequest;
import es.gob.afirma.integraFacade.pojo.PendingRequest;
import es.gob.afirma.integraFacade.pojo.Repository;
import es.gob.afirma.integraFacade.pojo.ServerSignerRequest;
import es.gob.afirma.integraFacade.pojo.UpgradeSignatureRequest;
import es.gob.afirma.integraFacade.pojo.VerifyCertificateRequest;
import es.gob.afirma.integraFacade.pojo.VerifySignatureRequest;
import es.gob.afirma.utils.GeneralConstants;
import es.gob.afirma.utils.GenericUtils;

/**
 * <p>Class that manages the verification of the requests for @Firma web services.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * @version 1.0, 16/12/2014.
 */
public final class ValidateRequest {

	/**
	 * Constructor method for the class ValidateRequest.java.
	 */
	private ValidateRequest() {
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the server signature service are correct.
	 * @param serSigReq Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateServerSignerRequest(ServerSignerRequest serSigReq) {
		String result = null;

		if ((serSigReq.getDocumentHash() != null && !GenericUtils.assertArrayValid(serSigReq.getDocumentHash().getDigestValue())) && !GenericUtils.assertArrayValid(serSigReq.getDocument()) && (!GenericUtils.assertStringValue(serSigReq.getDocumentId()) || GenericUtils.checkNullValues(serSigReq.getDocumentRepository()))) {
			result = CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG018);
		} else if (!GenericUtils.assertStringValue(serSigReq.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		} else if (!GenericUtils.assertStringValue(serSigReq.getKeySelector())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.KEY_SELECTOR });
		} else if (serSigReq.getDocumentRepository() != null && (!GenericUtils.assertStringValue(serSigReq.getDocumentRepository().getId()) || !GenericUtils.assertStringValue(serSigReq.getDocumentRepository().getObject()))) {
			result = CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG021);
		}
		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the server co-signature service are correct.
	 * @param coSigReq Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateCoSignRequest(CoSignRequest coSigReq) {
		String result = null;
		if (!GenericUtils.assertStringValue(coSigReq.getTransactionId()) && (!GenericUtils.assertArrayValid(coSigReq.getSignature()) || !GenericUtils.assertArrayValid(coSigReq.getDocument())) && (coSigReq.getSignatureRepository() == null || coSigReq.getDocumentRepository() == null)) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG023, new Object[ ] { GeneralConstants.TRANSACTION_ID, GeneralConstants.DOC_REPOSITORY, GeneralConstants.SIG_REPOSITORY });
		} else if (!GenericUtils.assertStringValue(coSigReq.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		} else if (!GenericUtils.assertStringValue(coSigReq.getKeySelector())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.KEY_SELECTOR });
		} else if (checkRepository(coSigReq.getDocumentRepository())) {
			result = CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG024);
		} else if (checkRepository(coSigReq.getSignatureRepository())) {
			result = CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG025);
		}

		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the server counter-signature service are correct.
	 * @param couSigReq Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateCounterSignRequest(CounterSignRequest couSigReq) {
		String result = null;
		if (!GenericUtils.assertStringValue(couSigReq.getTransactionId()) && !GenericUtils.assertArrayValid(couSigReq.getSignature()) && couSigReq.getSignatureRepository() == null) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG026, new Object[ ] { GeneralConstants.TRANSACTION_ID, GeneralConstants.SIG_REPOSITORY, GeneralConstants.SIGNATURE });
		} else if (!GenericUtils.assertStringValue(couSigReq.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		} else if (!GenericUtils.assertStringValue(couSigReq.getKeySelector())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.KEY_SELECTOR });
		}
		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the verify signature service are correct.
	 * @param verSigReq Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateVerifySignerRequest(VerifySignatureRequest verSigReq) {
		String result = null;
		// signature y signatureRepository no pueden ser las dos nulas
		if (!GenericUtils.assertArrayValid(verSigReq.getSignature()) && verSigReq.getSignatureRepository() == null) {
			result = CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG029);
		} else if (!GenericUtils.assertStringValue(verSigReq.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		}

		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the upgrade signature service are correct.
	 * @param upgSigReq Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateUpgradeSignatureRequest(UpgradeSignatureRequest upgSigReq) {
		String result = null;
		// no puede estar a null signature y signatureRepository
		if (!GenericUtils.assertArrayValid(upgSigReq.getSignature()) && GenericUtils.checkNullValues(upgSigReq.getSignatureRepository())) {
			result = CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG029);
		} else if (!GenericUtils.checkNullValues(upgSigReq.getSignatureRepository()) && upgSigReq.getTransactionId() == null) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.TRANSACTION_ID });
		} else if (!GenericUtils.assertStringValue(upgSigReq.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		}
		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the verify certificate service are correct.
	 * @param verCerReq Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateVerifyCertificateRequest(VerifyCertificateRequest verCerReq) {
		String result = null;
		// no puede estar a null signature y signatureRepository
		if (!GenericUtils.assertArrayValid(verCerReq.getCertificate()) && GenericUtils.checkNullValues(verCerReq.getCertificateRepository())) {
			result = CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG029);
		} else if (!GenericUtils.assertStringValue(verCerReq.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		}
		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the verify signatures on batch service are correct.
	 * @param batVerSigReq Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateBatchVerifySignatureRequest(BatchVerifySignatureRequest batVerSigReq) {
		String result = null;
		if (!GenericUtils.assertStringValue(batVerSigReq.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		} else if (batVerSigReq.getListVerifySignature() == null || batVerSigReq.getListVerifySignature().isEmpty()) {
			result = CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG034);
		}
		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the verify certificates on batch service are correct.
	 * @param batVerCerReq Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateBatchVerifyCertificateRequest(BatchVerifyCertificateRequest batVerCerReq) {
		String result = null;
		if (!GenericUtils.assertStringValue(batVerCerReq.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		} else if (batVerCerReq.getListVerifyCertificate() == null || batVerCerReq.getListVerifyCertificate().isEmpty()) {
			result = CaibEsbLanguage.getResIntegra(ILogConstantKeys.IFWS_LOG035);
		}
		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the async processes of sign and verify service are correct.
	 * @param pendingRequest Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validatePendingRequest(PendingRequest pendingRequest) {
		String result = null;
		if (!GenericUtils.assertStringValue(pendingRequest.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		} else if (!GenericUtils.assertStringValue(pendingRequest.getResponseId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.RESPONSE_ID });
		}

		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the archive signatures retrieve service are correct.
	 * @param archiveRequest Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateArchiveRequest(ArchiveRequest archiveRequest) {
		String result = null;
		if (!GenericUtils.assertStringValue(archiveRequest.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		} else if (!GenericUtils.assertStringValue(archiveRequest.getTransactionId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.TRANSACTION_ID });
		}

		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the services related to the content of a document are correct.
	 * @param conReq Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateContentRequest(ContentRequest conReq) {
		String result = null;
		if (!GenericUtils.assertStringValue(conReq.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		} else if (!GenericUtils.assertStringValue(conReq.getTransactionId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.TRANSACTION_ID });
		}
		return result;
	}

	/**
	 * Method that validates whether the attributes for the request message to invoke the services related to retrieve a document are correct.
	 * @param docReq Parameter that represents the request message.
	 * @return a message with the result of the validation.
	 */
	public static String validateDocumentRequest(DocumentRequest docReq) {
		String result = null;

		if (!GenericUtils.assertStringValue(docReq.getApplicationId())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.APPLICATION_ID });
		} else if (!GenericUtils.assertArrayValid(docReq.getDocument())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.DOCUMENT });
		} else if (!GenericUtils.assertStringValue(docReq.getName())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.NAME });
		} else if (!GenericUtils.assertStringValue(docReq.getType())) {
			result = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.IFWS_LOG019, new Object[ ] { GeneralConstants.TYPE });
		}
		return result;
	}

	/**
	 * Method that validates whether the information related to locate a document repository or a document manager is correct (true) or not (false).
	 * @param repository Parameter that represents the information related to locate a document repository or a document manager.
	 * @return a boolean that indicates whether the information related to locate a document repository or a document manager is correct (true) or not (false).
	 */
	private static boolean checkRepository(Repository repository) {

		if (!GenericUtils.checkNullValues(repository) && (GenericUtils.assertStringValue(repository.getId()) || GenericUtils.assertStringValue(repository.getObject()))) {
			return true;
		}
		return false;
	}

}
