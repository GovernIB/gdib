package es.caib.gdib.ws.iface;

import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.common.types.SignatureValidationReport;
import es.caib.gdib.ws.common.types.XmlSignatureMode;
import es.caib.gdib.ws.exception.GdibException;

/**
 * Interfaz que define los metodos para la generacion, evolucion y validacion de firmas electronicas en
 * el Sistema de Gestion Documental, asi como la validacion de certificados electronicos.
 * 
 * @author RICOH
 */
public interface SignatureService {
	
	/**
	 * Realiza una firma CAdES v1.7.3 explicita de un archivo en un determinado formato (BES, EPES, T, C , X, XL o A).
	 * @param document documento a firmar.
	 * @param signatureFormat formato de firma. Opcional, si no es informado se solicitara la generacion de una firma electronica formato CAdES-BES.
	 * @param signaturePoliciyIdentifier identificador de la politica de firma. Opcional.
	 * @return firma electronica formato CAdES v1.7.3 generada.
	 * @throws GdibException si ocurre algun error en la generacion de la firma electronica.
	 */
	byte [] signCadesDocument(byte[] document,SignatureFormat signatureFormat, String signaturePoliciyIdentifier) throws GdibException;
	
	/**
	 * Realiza una firma PAdES de un archivo PDF, v1.1.2 o v1.2.1, en un determinado formato (BASIC, BES, EPES o LTV).
	 * @param document documento PDF a firmar.
	 * @param signatureFormat formato de firma. Opcional, si no es informado se solicitara la generacion de una firma electronica formato PAdES-BES.
	 * @param signaturePoliciyIdentifier identificador de la politica de firma. Opcional.
	 * @return firma electronica formato PAdES generada.
	 * @throws GdibException si ocurre algun error en la generacion de la firma electronica.
	 */	
	byte [] signPadesDocument(byte[] document,SignatureFormat signatureFormat, String signaturePoliciyIdentifier) throws GdibException;
	
	/**
	 * Realiza una firma XAdES v1.3.2 de un archivo XML en un determinado formato (BES, EPES, T, C , X, XL o A), y modo dado (DETACHED, ENVELOPED o ENVELOPING).
	 * @param document documento XML a firmar.
	 * @param signatureFormat formato de firma XML. Opcional, si no es informado se solicitara la generacion de una firma electronica formato XAdES-BES.
	 * @param xmlSignatureMode modo en el que es realizada la firma XAdES: DETACHED, ENVELOPED o ENVELOPING. Por defecto, ENVELOPED.
	 * @param signaturePoliciyIdentifier identificador de la politica de firma. Opcional.
	 * @return firma electronica XAdES v1.3.2.
	 * @throws GdibException si ocurre algun error en la generacion de la firma electronica.
	 */
	byte [] signXadesDocument(final byte[] document, SignatureFormat signatureFormat, XmlSignatureMode xmlSignatureMode,
			String signaturePoliciyIdentifier) throws GdibException;
	
	/**
	 * Evoluciona una firma electronica a un formato avanzado superior.
	 * @param signature firma electronica a evolucionar.
	 * @param upgradedSignatureFormat formato de firma avanzado al que sera evolucionada la firma electronica.
	 * @return firma electronica evolucionada a un formato avanzado superior.
	 * @throws GdibException si ocurre algun error en la evolucion de la firma electronica.
	 */
	byte [] upgradeSignature(final byte[] signature, SignatureFormat upgradedSignatureFormat) throws GdibException;
	
	/**
	 * Valida una firma electronica.
	 * @param document datos firmados. Solo es necesario informarlo en caso de firmas explicitas o detached.
	 * @param signature firma a validar.
	 * @return informe de validacion de la firma, detallando su validez.
	 * @throws GdibException si ocurre algun error en la evolucion de la firma electronica.
	 */
	SignatureValidationReport verifySignature(byte[] document, byte[] signature) throws GdibException;
}
