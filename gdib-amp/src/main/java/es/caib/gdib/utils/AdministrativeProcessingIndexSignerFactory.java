package es.caib.gdib.utils;

import java.util.Properties;

import org.apache.log4j.Logger;

import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.SignatureService;

/**
 * Clase que implementa el patrón Factoria para la obtención de la clase responsable de la firma del 
 * índice de un expediente. Las implementaciones actuales permiten firmar los siguientes tipos de índices:
 * 
 *     - Indice de foliación CAIB v1.0: urn:es:caib:archivodigital:gestiondocumental:expediente-e:indice-e:1.0
 *     - Indice de intercambio ENI v1.0: http://administracionelectronica.gob.es/ENI/XSD/v1.0/expediente-e/indice-e
 *     
 * @author RICOH
 *
 */
public class AdministrativeProcessingIndexSignerFactory {

	private static final Logger LOGGER =  Logger.getLogger(AdministrativeProcessingIndexSignerFactory.class);
	
	/**
	 * Constante que identifica el prefijo de los parámetros de configuración para índices CAIB de foliación v1.0.
	 */
	public static final String CAIB_INDEX_V10_PROP_PREFIX = "caibIndexV10";
	
	/**
	 * Constante que identifica el prefijo de los parámetros de configuración para índices ENI de exportación o intercambio v1.0.
	 */
	public static final String ENI_INDEX_V10_PROP_PREFIX = "eniIndexV10";
	
	/**
	 * Constante correspondiente al nombre del parámetro de configuración minIndexSignatureFormat 
	 */
	public static final String MIN_INDEX_SIGNATURE_FORM_PROP_NAME = "minIndexSignatureFormat";
	
	/**
	 * Constante correspondiente al nombre del parámetro de configuración signaturePolicyId 
	 */
	public static final String SIGNATURE_POLICY_ID_PROP_NAME = "signaturePolicyId";
	
	/**
	 * Constante correspondiente al nombre del parámetro de configuración xadesMode 
	 */
	public static final String XADES_MODE_PROP_NAME = "xadesMode";

	/**
	 * Constante correspondiente al nombre del parámetro de configuración signatureTypeValue 
	 */
	public static final String SIGNATURE_TYPE_XML_ELEMENT_PROP_NAME = "signatureFormat";
	
	/**
	 * Constante correspondiente al nombre del parámetro de configuración moveSignature 
	 */
	public static final String MOVE_SIGNATURE_PROP_NAME = "moveSignature";
	
	/**
	 * Constante que identifica índices CAIB de foliación v1.0.
	 */
	public static final String CAIB_INDEX_V10 = "urn:es:caib:archivodigital:gestiondocumental:expediente-e:indice-e:1.0";
	
	/**
	 * Constante que identifica índices ENI de exportación o intercambio v1.0.
	 */
	public static final String ENI_INDEX_V10 = "http://administracionelectronica.gob.es/ENI/XSD/v1.0/expediente-e/indice-e";

	/**
	 * Servicio de firma por defecto, basado en servicios DDS publicados por @firma.
	 */
	private SignatureService defaultSignatureService;
	
	
	/**
	 * Parámetros de configuración para la firma de índices electrónicos de expedientes establecida en el archivo gdib-amp.properties (Prefijo: gdib.repository.custody.exp). 
	 */
	private FilterPlaceholderProperties fileIndexSignatureProperties;
	

	/**
	 * Genera la clase responsable de generar la firma electrónica sobre un determinado tipo de índice.
	 * @param indexType tipo de índice.
	 * @param signatureService servicio de firma. Opcional.
	 * @return clase responsable de generar la firma electrónica sobre un determinado tipo de índice.
	 * @throws GdibException Si el tipo de índice no es soportado.
	 */
	public AdministrativeProcessingIndexSigner createIndexSigner(final String indexType, SignatureService signatureService) throws GdibException {
		AdministrativeProcessingIndexSigner res = null;

		switch(indexType){
			case CAIB_INDEX_V10:				
				res = new CaibIndexV10SignerImpl(extractSignatureIndexConfParams(CAIB_INDEX_V10_PROP_PREFIX));
				break;
			case ENI_INDEX_V10:				
				res = new EniIndexV10SignerImpl(extractSignatureIndexConfParams(ENI_INDEX_V10_PROP_PREFIX));
				break;
			default:
				throw new GdibException("El tipo de índice " + indexType + " no es soportado.");
		}
		
		res.setSignatureService((signatureService == null?defaultSignatureService:signatureService));
		
		return res;
	}

	
	private Properties extractSignatureIndexConfParams(String indexType){
		Properties res = new Properties();
		String propValue;

		propValue = fileIndexSignatureProperties.getProperty(indexType, MIN_INDEX_SIGNATURE_FORM_PROP_NAME);		
		if(propValue != null && !propValue.isEmpty()){
			res.setProperty(MIN_INDEX_SIGNATURE_FORM_PROP_NAME, propValue);
		}
		
		propValue = fileIndexSignatureProperties.getProperty(indexType, SIGNATURE_POLICY_ID_PROP_NAME);		
		if(propValue != null && !propValue.isEmpty()){
			res.setProperty(SIGNATURE_POLICY_ID_PROP_NAME, propValue);
		}

		propValue = fileIndexSignatureProperties.getProperty(indexType, XADES_MODE_PROP_NAME);		
		if(propValue != null && !propValue.isEmpty()){
			res.setProperty(XADES_MODE_PROP_NAME, propValue);
		}
		
		propValue = fileIndexSignatureProperties.getProperty(indexType, SIGNATURE_TYPE_XML_ELEMENT_PROP_NAME);		
		if(propValue != null && !propValue.isEmpty()){
			res.setProperty(SIGNATURE_TYPE_XML_ELEMENT_PROP_NAME, propValue);
		}

		propValue = fileIndexSignatureProperties.getProperty(indexType, MOVE_SIGNATURE_PROP_NAME);		
		if(propValue != null && !propValue.isEmpty()){
			res.setProperty(MOVE_SIGNATURE_PROP_NAME, propValue);
		}

		LOGGER.debug("Propiedades obtenidas del archivo de configuración para el tipo de indice " + indexType + 
				": " + res);
		
		return res;
	}
	
	/**
	 * @return the defaultSignatureService
	 */
	public SignatureService getDefaultSignatureService() {
		return defaultSignatureService;
	}

	/**
	 * @param defaultSignatureService the defaultSignatureService to set
	 */
	public void setDefaultSignatureService(SignatureService defaultSignatureService) {
		this.defaultSignatureService = defaultSignatureService;
	}

	public void setFileIndexSignatureProperties(FilterPlaceholderProperties fileIndexSignatureProperties) {
		this.fileIndexSignatureProperties = fileIndexSignatureProperties;
	}
	
}
