package es.caib.gdib.ws.iface;

import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.common.types.SignatureValidationReport;
import es.caib.gdib.ws.common.types.XmlSignatureMode;
import es.caib.gdib.ws.exception.GdibException;

/**
 * Interfaz que define los metodos para la generación, evoluci�n y validaci�n de firmas electr�nicas en
 * el Sistema de Gesti�n Documental, as� como la validaci�n de certificados electr�nicos.
 * 
 * @author RICOH
 */
public interface SignatureService {
	
	/**
	 * Realiza una firma CAdES v1.7.3 expl�cita de un archivo en un determinado formato (BES, EPES, T, C , X, XL o A).
	 * @param document documento a firmar.
	 * @param signatureFormat formato de firma. Opcional, si no es informado se solicitar� la generaci�n de una firma electr�nica formato CAdES-BES.
	 * @param signaturePoliciyIdentifier identificador de la pol�tica de firma. Opcional.
	 * @return firma electr�nica formato CAdES v1.7.3 generada.
	 * @throws GdibException si ocurre alg�n error en la generaci�n de la firma electr�nica.
	 */
	byte [] signCadesDocument(byte[] document,SignatureFormat signatureFormat, String signaturePoliciyIdentifier) throws GdibException;
	
	/**
	 * Realiza una firma PAdES de un archivo PDF, v1.1.2 o v1.2.1, en un determinado formato (BASIC, BES, EPES o LTV).
	 * @param document documento PDF a firmar.
	 * @param signatureFormat formato de firma. Opcional, si no es informado se solicitar� la generaci�n de una firma electr�nica formato PAdES-BES.
	 * @param signaturePoliciyIdentifier identificador de la pol�tica de firma. Opcional. 
	 * @return firma electr�nica formato PAdES generada.
	 * @throws GdibException si ocurre alg�n error en la generaci�n de la firma electr�nica.
	 */	
	byte [] signPadesDocument(byte[] document,SignatureFormat signatureFormat, String signaturePoliciyIdentifier) throws GdibException;
	
	/**
	 * Realiza una firma XAdES v1.3.2 de un archivo XML en un determinado formato (BES, EPES, T, C , X, XL o A), y modo dado (DETACHED, ENVELOPED o ENVELOPING).
	 * @param document documento XML a firmar.
	 * @param signatureFormat formato de firma XML. Opcional, si no es informado se solicitar� la generaci�n de una firma electr�nica formato XAdES-BES.
	 * @param xmlSignatureMode modo en el que es realizada la firma XAdES: DETACHED, ENVELOPED o ENVELOPING. Por defecto, ENVELOPED.
	 * @param signaturePoliciyIdentifier identificador de la pol�tica de firma. Opcional. 
	 * @return firma electr�nica XAdES v1.3.2.
	 * @throws GdibException si ocurre alg�n error en la generaci�n de la firma electr�nica.
	 */
	byte [] signXadesDocument(final byte[] document, SignatureFormat signatureFormat, XmlSignatureMode xmlSignatureMode,
			String signaturePoliciyIdentifier) throws GdibException;
	
	/**
	 * Evoluciona una firma electr�nica a un formato avanzado superior.
	 * @param signature firma electr�nica a evolucionar.
	 * @param upgradedSignatureFormat formato de firma avanzado al que ser� evolucionada la firma electr�nica.
	 * @return firma electr�nica evolucionada a un formato avanzado superior.
	 * @throws GdibException si ocurre alg�n error en la evoluci�n de la firma electr�nica.
	 */
	byte [] upgradeSignature(final byte[] signature, SignatureFormat upgradedSignatureFormat) throws GdibException;
	
	/**
	 * Valida una firma electr�nica.
	 * @param document datos firmados. Solo es necesario informarlo en caso de firmas explicitas o detached.
	 * @param signature firma a validar.
	 * @return informe de validaci�n de la firma, detallando su validez.
	 * @throws GdibException si ocurre alg�n error en la evoluci�n de la firma electr�nica.
	 */
	SignatureValidationReport verifySignature(byte[] document, byte[] signature) throws GdibException;
}
