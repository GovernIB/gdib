package es.caib.gdib.utils;

import java.util.Map;
import java.util.Properties;

import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.SignatureService;

/**
 * Interfaz que define los metodos que deberán implementar las diferentes implementaciones 
 * responsables de la firma de índices electrónicos de expedientes.
 * 
 * @author RICOH
 *
 */
public interface AdministrativeProcessingIndexSigner {

	/**
	 * Firma un índice electrónico mediante un sello de organismo o certificado electrónico de servidor.
	 * @param document índice electrónico a firmar.
	 * @param optionalParams parámetros adicionales requeridos para la firma del índice.
	 * @return índice electrónico firmado.
	 * @throws GdibException si ocurre algún error en la firma del índice.
	 */
	byte [] generateIndexSignature(byte [] document, Map<String,Object> optionalParams) throws GdibException;
	
	/**
	 * Establece el servicio responsable de firmar el índice electrónico.	
	 * @param signatureService servicio de firma electrónica.
	 */
	void setSignatureService(SignatureService signatureService);
	
	/**
	 * Establece los parámetros de configuración para la generación de la firma electrónica del índice.	
	 * @param confParameters parámetros de configuración.
	 */
	void setConfParameters(Properties confParameters);
	
}
