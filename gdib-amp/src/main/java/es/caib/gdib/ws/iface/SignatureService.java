package es.caib.gdib.ws.iface;

import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.common.types.SignatureValidationReport;
import es.caib.gdib.ws.common.types.XmlSignatureMode;
import es.caib.gdib.ws.exception.GdibException;

/**
 * Interfaz que define los metodos para la generación, evolución y validación de firmas electrónicas en 
 * el Sistema de Gestión Documental, así como la validación de certificados electrónicos.
 * 
 * @author RICOH
 */
public interface SignatureService {
	
	/**
	 * Realiza una firma CAdES v1.7.3 explícita de un archivo en un determinado formato (BES, EPES, T, C , X, XL o A).
	 * @param document documento a firmar.
	 * @param signatureFormat formato de firma. Opcional, si no es informado se solicitará la generación de una firma electrónica formato CAdES-BES.
	 * @param signaturePoliciyIdentifier identificador de la política de firma. Opcional.
	 * @return firma electrónica formato CAdES v1.7.3 generada.
	 * @throws GdibException si ocurre algún error en la generación de la firma electrónica.
	 */
	byte [] signCadesDocument(byte[] document,SignatureFormat signatureFormat, String signaturePoliciyIdentifier) throws GdibException;
	
	/**
	 * Realiza una firma PAdES de un archivo PDF, v1.1.2 o v1.2.1, en un determinado formato (BASIC, BES, EPES o LTV).
	 * @param document documento PDF a firmar.
	 * @param signatureFormat formato de firma. Opcional, si no es informado se solicitará la generación de una firma electrónica formato PAdES-BES.
	 * @param signaturePoliciyIdentifier identificador de la política de firma. Opcional. 
	 * @return firma electrónica formato PAdES generada.
	 * @throws GdibException si ocurre algún error en la generación de la firma electrónica.
	 */	
	byte [] signPadesDocument(byte[] document,SignatureFormat signatureFormat, String signaturePoliciyIdentifier) throws GdibException;
	
	/**
	 * Realiza una firma XAdES v1.3.2 de un archivo XML en un determinado formato (BES, EPES, T, C , X, XL o A), y modo dado (DETACHED, ENVELOPED o ENVELOPING).
	 * @param document documento XML a firmar.
	 * @param signatureFormat formato de firma XML. Opcional, si no es informado se solicitará la generación de una firma electrónica formato XAdES-BES.
	 * @param xmlSignatureMode modo en el que es realizada la firma XAdES: DETACHED, ENVELOPED o ENVELOPING. Por defecto, ENVELOPED.
	 * @param signaturePoliciyIdentifier identificador de la política de firma. Opcional. 
	 * @return firma electrónica XAdES v1.3.2.
	 * @throws GdibException si ocurre algún error en la generación de la firma electrónica.
	 */
	byte [] signXadesDocument(final byte[] document, SignatureFormat signatureFormat, XmlSignatureMode xmlSignatureMode,
			String signaturePoliciyIdentifier) throws GdibException;
	
	/**
	 * Evoluciona una firma electrónica a un formato avanzado superior.
	 * @param signature firma electrónica a evolucionar.
	 * @param upgradedSignatureFormat formato de firma avanzado al que será evolucionada la firma electrónica.
	 * @return firma electrónica evolucionada a un formato avanzado superior.
	 * @throws GdibException si ocurre algún error en la evolución de la firma electrónica.
	 */
	byte [] upgradeSignature(final byte[] signature, SignatureFormat upgradedSignatureFormat) throws GdibException;
	
	/**
	 * Valida una firma electrónica.
	 * @param document datos firmados. Solo es necesario informarlo en caso de firmas explicitas o detached.
	 * @param signature firma a validar.
	 * @return informe de validación de la firma, detallando su validez.
	 * @throws GdibException si ocurre algún error en la evolución de la firma electrónica.
	 */
	SignatureValidationReport verifySignature(byte[] document, byte[] signature) throws GdibException;
}
