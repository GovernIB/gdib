package es.caib.gdib.ws.impl;

import org.apache.log4j.Logger;

import es.caib.gdib.utils.FilterPlaceholderProperties;
import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.common.types.SignatureValidationReport;
import es.caib.gdib.ws.common.types.ValidationStatus;
import es.caib.gdib.ws.common.types.XmlSignatureMode;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.SignatureService;
import es.gob.afirma.integraFacade.IntegraFacadeWSDSS;
import es.gob.afirma.integraFacade.pojo.ServerSignerRequest;
import es.gob.afirma.integraFacade.pojo.ServerSignerResponse;
import es.gob.afirma.integraFacade.pojo.SignatureFormatEnum;
import es.gob.afirma.integraFacade.pojo.UpgradeSignatureRequest;
import es.gob.afirma.integraFacade.pojo.VerifySignatureRequest;
import es.gob.afirma.integraFacade.pojo.VerifySignatureResponse;
import es.gob.afirma.utils.DSSConstants;

public class AfirmaV6SignatureServiceImpl implements SignatureService {

	private static final Logger LOGGER = Logger.getLogger(AfirmaV6SignatureServiceImpl.class);
	
	private static final String INTEGRA_PREFIX = "integra";
	
	private static final String SERVER_CERT_ALIAS_PROP_NAME = "serverCertAlias";
	
	private static final String AFIRMA_APP_ID_PROP_NAME = "afirmaAppId";
	
	/**
	 * Formatos de firma electrónica CAdES admitidos para generación.
	 */
	private static SignatureFormat [] SUPPORTED_CADES_FORMATS = {SignatureFormat.CAdES_BES, 
			SignatureFormat.CAdES_EPES,	SignatureFormat.CAdES_T, SignatureFormat.CAdES_X, 
			SignatureFormat.CAdES_X1, SignatureFormat.CAdES_X2, SignatureFormat.CAdES_XL, 
			SignatureFormat.CAdES_XL1, SignatureFormat.CAdES_XL2, SignatureFormat.CAdES_A};
	
	/**
	 * Formatos de firma electrónica XAdES admitidos para generación.
	 */
	private static SignatureFormat [] SUPPORTED_XADES_FORMATS = {SignatureFormat.XAdES_BES, 
			SignatureFormat.XAdES_EPES,	SignatureFormat.XAdES_T, SignatureFormat.XAdES_X, 
			SignatureFormat.XAdES_X1, SignatureFormat.XAdES_X2, SignatureFormat.XAdES_XL, 
			SignatureFormat.XAdES_XL1, SignatureFormat.XAdES_XL2, SignatureFormat.XAdES_A};
	
	/**
	 * Formatos de firma electrónica PAdES admitidos para generación.
	 */
	private static SignatureFormat [] SUPPORTED_PADES_FORMATS = {SignatureFormat.PAdES_Basic, 
			SignatureFormat.PAdES_BES,SignatureFormat.PAdES_EPES,SignatureFormat.PAdES_LTV};
	
	/**
	 * Valor por defecto de parámetros de configuración para la integración con @firma, mediante Integr@. Los paramétros están establecidos en el archivo gdib-amp.properties (Prefijo: gdib.afirma).
	 *   
	 *   - Alias de certificado de servidor.
	 *   - Identificador de aplicación @firma.
	 */
	private FilterPlaceholderProperties afirmaIntegraDefaultProperties;

	/**
	 * Alias del certificado de servidor empleado en la generación de firma.
	 */
	private String serverCertAlias;
	
	/**
	 * Identificador de la aplicación @firma empleada para la integración.
	 */
	private String afirmaAppId;
	
	@Override
	public byte[] signCadesDocument(byte[] document, SignatureFormat signatureFormat, String signaturePoliciyIdentifier)
			throws GdibException {
		byte [] res = null;
		ServerSignerRequest serSigReq;
		SignatureFormatEnum dssSignatureFormat;
		res = null;
		
		if(document == null || document.length == 0){
			throw new GdibException("Firma electrónica CAdES: Documento nulo o vacío.");
		}
		
		checkSignatureFormat(signatureFormat,SUPPORTED_CADES_FORMATS);
		dssSignatureFormat = translateSignatureFormat(signatureFormat);
		
		serSigReq = new ServerSignerRequest();
		serSigReq.setDocument(document);
		serSigReq.setKeySelector(getOperationServerCertAlias());
		serSigReq.setApplicationId(getOperationAfirmaAppId());		
		serSigReq.setSignatureFormat((dssSignatureFormat == null?SignatureFormatEnum.CAdES_BES:dssSignatureFormat));		
		serSigReq.setIgnoreGracePeriod(false);
		if(signaturePoliciyIdentifier != null && !signaturePoliciyIdentifier.isEmpty()){
			serSigReq.setSignaturePolicyIdentifier(signaturePoliciyIdentifier);
		}
		
		res = sign(serSigReq);
		
		return res;
	}

	@Override
	public byte[] signPadesDocument(byte[] document, SignatureFormat signatureFormat, String signaturePoliciyIdentifier)
			throws GdibException {
		byte [] res = null;
		ServerSignerRequest serSigReq;
		SignatureFormatEnum dssSignatureFormat;
		res = null;
		
		if(document == null || document.length == 0){
			throw new GdibException("Firma electrónica PAdES: Documento nulo o vacío.");
		}
		
		checkSignatureFormat(signatureFormat,SUPPORTED_PADES_FORMATS);
		dssSignatureFormat = translateSignatureFormat(signatureFormat);
		
		serSigReq = new ServerSignerRequest();
		serSigReq.setDocument(document);
		serSigReq.setKeySelector(getOperationServerCertAlias());
		serSigReq.setApplicationId(getOperationAfirmaAppId());		
		serSigReq.setSignatureFormat((dssSignatureFormat == null?SignatureFormatEnum.PAdES_BES:dssSignatureFormat));		
		serSigReq.setIgnoreGracePeriod(false);
		if(signaturePoliciyIdentifier != null && !signaturePoliciyIdentifier.isEmpty()){
			serSigReq.setSignaturePolicyIdentifier(signaturePoliciyIdentifier);
		}
		
		res = sign(serSigReq);
		
		return res;
	}

	@Override
	public byte[] signXadesDocument(byte[] document, SignatureFormat signatureFormat, XmlSignatureMode xmlSignatureMode,
			String signaturePoliciyIdentifier) throws GdibException {
		byte [] res = null;
		ServerSignerRequest serSigReq;
		SignatureFormatEnum dssSignatureFormat;
		String dssXmlSignatureMode;
		
		res = null;
		
		if(document == null || document.length == 0){
			throw new GdibException("Firma electrónica XML: Documento nulo o vacío.");
		}
		
		checkSignatureFormat(signatureFormat,SUPPORTED_XADES_FORMATS);
		dssSignatureFormat = translateSignatureFormat(signatureFormat);
		dssXmlSignatureMode = translateXmlSignatureMode(xmlSignatureMode);		
		
		serSigReq = new ServerSignerRequest();
		serSigReq.setDocument(document);
		serSigReq.setKeySelector(getOperationServerCertAlias());
		serSigReq.setApplicationId(getOperationAfirmaAppId());		
		serSigReq.setSignatureFormat((dssSignatureFormat == null?SignatureFormatEnum.XAdES_BES:dssSignatureFormat));		
		serSigReq.setXmlSignatureMode((dssXmlSignatureMode == null || dssXmlSignatureMode.isEmpty()?
				DSSConstants.XmlSignatureMode.ENVELOPED:dssXmlSignatureMode));
		serSigReq.setIgnoreGracePeriod(false);
		if(signaturePoliciyIdentifier != null && !signaturePoliciyIdentifier.isEmpty()){
			serSigReq.setSignaturePolicyIdentifier(signaturePoliciyIdentifier);
		}
		
		res = sign(serSigReq);
		
		return res;
	}

	@Override
	public byte[] upgradeSignature(byte[] signature, SignatureFormat upgradedSignatureFormat) throws GdibException {
		byte[] res;
		SignatureFormatEnum dssSignatureFormat;
		UpgradeSignatureRequest upgSigReq;

		res = null;
		
		if(signature == null || signature.length == 0){
			throw new GdibException("Evolución de firma electrónica: Firma electrónica nula o vacía.");
		}
		
		dssSignatureFormat = translateSignatureFormat(upgradedSignatureFormat);
		
		if(dssSignatureFormat == null){
			throw new GdibException("Formato de firma electrónica " + upgradedSignatureFormat.getName() + " no soportado  para evolucionar una firma electrónica.");
		}
		
		upgSigReq = new UpgradeSignatureRequest();
		upgSigReq.setSignature(signature);
		upgSigReq.setApplicationId(getOperationAfirmaAppId());
		upgSigReq.setSignatureFormat(dssSignatureFormat);
		ServerSignerResponse serSigRes = IntegraFacadeWSDSS.getInstance().upgradeSignature(upgSigReq);

		LOGGER.debug("Resultado evolución firma:");
		if (serSigRes == null) {
			throw new GdibException("No se obtuvo respuesta en la invocación del servicio DSSAfirmaVerify de la plataforma @firma "
					+ "para evolucionar una firma electrónica al formato " + upgradedSignatureFormat.getName() + ".");
		}
		LOGGER.debug("SersigRes asyncResponse: " + serSigRes.getAsyncResponse());
		LOGGER.debug("SersigRes transactionId: " + serSigRes.getIdTransaction());
		LOGGER.debug("SersigRes signatureFormat: " + serSigRes.getSignatureFormat());
		if (serSigRes.getResult() == null) {
			throw new GdibException("La respuesta retornada por el servicio DSSAfirmaVerify de la plataforma @firma no incluye el resultado de la operación para "
					+ "evolucionar una firma electrónica al formato " + upgradedSignatureFormat.getName() + ".");
		}
		
		LOGGER.debug("SersigRes Result major: " + serSigRes.getResult().getResultMajor());
	    LOGGER.debug("SersigRes Result minor: " + serSigRes.getResult().getResultMinor());
	    LOGGER.debug("SersigRes Result message: " + serSigRes.getResult().getResultMessage());
		
		if (!"urn:oasis:names:tc:dss:1.0:resultmajor:Success".equals(serSigRes.getResult().getResultMajor())) {
			throw new GdibException("Se obtuvo una respuesta erréonea en la invocación del servicio DSSAfirmaSign de la plataforma @firma para evolucionar una firma electrónica al formato " + 
					upgradedSignatureFormat.getName() + ". \n\t Código (Major): " + serSigRes.getResult().getResultMajor() + " \n\t Código (Minor): " + 
					serSigRes.getResult().getResultMinor() + "\n\t Observaciones: " + serSigRes.getResult().getResultMessage() + ".");
		}

		if (serSigRes.getSignature() == null) {
			throw new GdibException("La respuesta retornada por el servicio DSSAfirmaVerify de la plataforma @firma no incluye la firma electrónica evolucionada al "
					+ "formato " + upgradedSignatureFormat.getName() + ".");
		}

		res = serSigRes.getSignature();

		return res;
	}

	@Override
	public SignatureValidationReport verifySignature(byte[] document, byte[] signature) throws GdibException {
		SignatureValidationReport res;
		VerifySignatureRequest verSigReq;
		VerifySignatureResponse verSigRes;
		res = null;
		LOGGER.debug("Iniciando servicio de validación de firma electrónica...");
		LOGGER.debug("Validando parámetros de entrada...");
		
		if(signature == null || signature.length == 0){
			throw new GdibException("Validación de firma electrónica: Firma electrónica nula o vacía.");
		}
		
		if(document == null || document.length == 0){
			LOGGER.debug("El documento informado en la operación de validación de firma electrónica es vacío, se procede a validar una firma implícita.");
		}
		LOGGER.debug("Parámetros de entrada validados...");
		LOGGER.debug("Formando petición servicio DSSAfirmaVerify ...");
		verSigReq = new VerifySignatureRequest();

		verSigReq.setSignature(signature);
		verSigReq.setApplicationId(getOperationAfirmaAppId());
		if(document != null){
			verSigReq.setDocument(document);
		}
		verSigReq.setSignature(signature);		
		LOGGER.debug("Petición servicio DSSAfirmaVerify formada.");
		
		LOGGER.debug("Invocando servicio DSSAfirmaVerify ...");
		verSigRes = IntegraFacadeWSDSS.getInstance().verifySignature(verSigReq);
		LOGGER.debug("Procesando respuesta servicio DSSAfirmaVerify ...");

		if (verSigRes == null) {
			throw new GdibException("No se obtuvo respuesta en la invocación del servicio DSSAfirmaVerify de la plataforma @firma "
					+ "para validar una firma electrónica.");
		}
		LOGGER.debug("SersigRes signatureFormat: " + verSigRes.getSignatureFormat());
		if (verSigRes.getResult() == null) {
			throw new GdibException("La respuesta retornada por el servicio DSSAfirmaVerify de la plataforma @firma no incluye el resultado de la operación para "
					+ "validar una firma electrónica.");
		}
		
		LOGGER.debug("SersigRes Result major: " + verSigRes.getResult().getResultMajor());
	    LOGGER.debug("SersigRes Result minor: " + verSigRes.getResult().getResultMinor());
	    LOGGER.debug("SersigRes Result message: " + verSigRes.getResult().getResultMessage());
		
	    res = new SignatureValidationReport();	    
	    res.setValidationMessage(verSigRes.getResult().getResultMessage());
	    if(verSigRes.getSignatureFormat() != null && !verSigRes.getSignatureFormat().isEmpty()){
	    	int index = verSigRes.getSignatureFormat().indexOf("-");
	    	
	    	String signatureType = verSigRes.getSignatureFormat().substring(0, index);
	    	res.setSignatureType(signatureType);
	    	LOGGER.debug("Familia a la que pertenece la firma electrónica validada: " + signatureType);
	    	if(index != -1){
	    		String signatureForm = verSigRes.getSignatureFormat().substring(index+1,verSigRes.getSignatureFormat().length());
	    		LOGGER.debug("Formato avanzado de la firma electrónica validada: " + signatureForm);
	    		res.setSignatureForm(signatureForm);
	    		
	    	}
	    }
	    
		if ("urn:afirma:dss:1.0:profile:XSS:resultmajor:ValidSignature".equals(verSigRes.getResult().getResultMajor())) {
			res.setValidationStatus(ValidationStatus.CORRECTO);
			res.setDetailedValidationStatus(verSigRes.getResult().getResultMinor());
		} else if("urn:afirma:dss:1.0:profile:XSS:resultmajor:InvalidSignature".equals(verSigRes.getResult().getResultMajor())){
			res.setValidationStatus(ValidationStatus.NO_CORRECTO);
			res.setDetailedValidationStatus(verSigRes.getResult().getResultMinor());
		} else if("urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError".equals(verSigRes.getResult().getResultMajor())){
			res.setValidationStatus(ValidationStatus.NO_CORRECTO);
			res.setDetailedValidationStatus("Código (Major): " + verSigRes.getResult().getResultMajor() + " \n\t Código (Minor): " + 
					verSigRes.getResult().getResultMinor());
		} else if("urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError".equals(verSigRes.getResult().getResultMajor())){
			res.setValidationStatus(ValidationStatus.NO_CORRECTO);
			res.setDetailedValidationStatus("Código (Major): " + verSigRes.getResult().getResultMajor() + " \n\t Código (Minor): " + 
					verSigRes.getResult().getResultMinor());
		} else if("urn:oasis:names:tc:dss:1.0:resultmajor:InsufficientInformation".equals(verSigRes.getResult().getResultMajor())){
			res.setValidationStatus(ValidationStatus.NO_CORRECTO);
			res.setDetailedValidationStatus("Código (Major): " + verSigRes.getResult().getResultMajor() + " \n\t Código (Minor): " + 
					verSigRes.getResult().getResultMinor());
		} else if("urn:oasis:names:tc:dss:1.0:resultmajor:Warning".equals(verSigRes.getResult().getResultMajor())){			
			res.setDetailedValidationStatus("Código (Major): " + verSigRes.getResult().getResultMajor() + " \n\t Código (Minor): " + 
					verSigRes.getResult().getResultMinor());
		}

		return res;
	}
	
	/**
	 * Solicita la generación de una firma de servidor delegada a la plataforma @firma mediante la invocación del servicio DSSAfirmaSign.
	 * @param ServerSignerRequest características de la firma electrónica: formato, certificado firmante, etc..
	 * @return firma electrónica generada.
	 * @throws GdibException si ocurre algún error en la invocación del servicio DSSAfirmaSign de la plataforma @firma o se retorna error desde la plataforma.
	 */
	private static byte[] sign(ServerSignerRequest serSigReq) throws GdibException {
		byte[] res = null;
		
		ServerSignerResponse serSigRes = IntegraFacadeWSDSS.getInstance().sign(serSigReq);

		LOGGER.debug("Resultado firma de servidor delegada en @firma:");
		if (serSigRes == null) {
			throw new GdibException("No se obtuvo respuesta en la invocación del servicio DSSAfirmaSign de la plataforma @firma.");
		}
		LOGGER.debug("SersigRes asyncResponse: " + serSigRes.getAsyncResponse());
		LOGGER.debug("SersigRes transactionId: " + serSigRes.getIdTransaction());
		LOGGER.debug("SersigRes signatureFormat: " + serSigRes.getSignatureFormat());
		if (serSigRes.getResult() == null) {
			throw new GdibException("La respuesta retornada por el servicio DSSAfirmaSign de la plataforma @firma no incluye el resultado de la operación.");
		}
		
		LOGGER.debug("SersigRes Result major: " + serSigRes.getResult().getResultMajor());
	    LOGGER.debug("SersigRes Result minor: " + serSigRes.getResult().getResultMinor());
	    LOGGER.debug("SersigRes Result message: " + serSigRes.getResult().getResultMessage());
		
		if (!"urn:oasis:names:tc:dss:1.0:resultmajor:Success".equals(serSigRes.getResult().getResultMajor())) {
			throw new GdibException("Se obtuvo una respuesta erréonea en la invocación del servicio DSSAfirmaSign de la plataforma @firma. \n\t Código (Major): " + 
					serSigRes.getResult().getResultMajor() + " \n\t Código (Minor): " + serSigRes.getResult().getResultMinor() + "\n\t Observaciones: " + 
					serSigRes.getResult().getResultMessage() + ".");
		}

		if (serSigRes.getSignature() == null) {
			throw new GdibException("La respuesta retornada por el servicio DSSAfirmaSign de la plataforma @firma no incluye la firma electrónica.");
		}

		res = serSigRes.getSignature();
		
		return res;
	}

	
	/**
	 * Verifica que el formato de firma solicitado se encuentra entre los admitidos.
	 * @param signatureFormat formato de firma electrónica.
	 * @param allowedSignatureFormats conjunto de formatos de firma admitidos.
	 * @throws GdibException si el formato de firma no se encuentra entre los habilitados. 
	 */
	private static void checkSignatureFormat(SignatureFormat signatureFormat, SignatureFormat [] allowedSignatureFormats) throws GdibException {
		Boolean found = Boolean.FALSE;
		for(int i=0;!found && i<allowedSignatureFormats.length; i++){
			found = signatureFormat.equals(allowedSignatureFormats[i]);
		}
		
		if(!found){
			throw new GdibException("El formato de firma electrónica " + signatureFormat.getName() + " no está soportado para la operación solicitada.");
		}
	}
	
	/**
	 * Obtiene el identificador DSS del formato de firma electrónica requerido por el API Integr@ de @firma.
	 * @param signatureFormat formato de firma para el que se desea obtener el identificador DSS del formato de firma electrónica.
	 * @return formato de firma electrónica requerido por el API Integr@ de @firma.
	 */
	private static SignatureFormatEnum translateSignatureFormat(SignatureFormat signatureFormat) {
		SignatureFormatEnum res;
		
		res = null;
		
		switch(signatureFormat){
			case CAdES_BES:
				res = SignatureFormatEnum.CAdES_BES;
				break;
			case CAdES_EPES:
				res = SignatureFormatEnum.CAdES_EPES;
				break;
			case CAdES_T:
				res = SignatureFormatEnum.CAdES_T;
				break;
			case CAdES_X:
				res = SignatureFormatEnum.CAdES_X;
				break;
			case CAdES_X1:
				res = SignatureFormatEnum.CAdES_X1;
				break;
			case CAdES_X2:
				res = SignatureFormatEnum.CAdES_X2;
				break;
			case CAdES_XL:
				res = SignatureFormatEnum.CAdes_XL;
				break;
			case CAdES_XL1:
				res = SignatureFormatEnum.CAdES_XL1;
				break;
			case CAdES_XL2:
				res = SignatureFormatEnum.CAdES_XL2;
				break;
			case CAdES_A:
				res = SignatureFormatEnum.CAdES_A;
				break;
			case XAdES_BES:
				res = SignatureFormatEnum.XAdES_BES;
				break;
			case XAdES_EPES:
				res = SignatureFormatEnum.XAdES_EPES;
				break;
			case XAdES_T:
				res = SignatureFormatEnum.XAdES_T;
				break;
			case XAdES_X:
				res = SignatureFormatEnum.XAdES_X;
				break;
			case XAdES_X1:
				res = SignatureFormatEnum.XAdES_X1;
				break;
			case XAdES_X2:
				res = SignatureFormatEnum.XAdES_X2;
				break;
			case XAdES_XL:
				res = SignatureFormatEnum.XAdES_XL;
				break;
			case XAdES_XL1:
				res = SignatureFormatEnum.XAdES_XL1;
				break;
			case XAdES_XL2:
				res = SignatureFormatEnum.XAdES_XL2;
				break;
			case XAdES_A:
				res = SignatureFormatEnum.XAdES_A;
				break;
			case PAdES_Basic:
				res = SignatureFormatEnum.PAdES;
				break;
			case PAdES_BES:
				res = SignatureFormatEnum.PAdES_BES;
				break;
			case PAdES_EPES:
				res = SignatureFormatEnum.PAdES_EPES;
				break;
			case PAdES_LTV:
				res = SignatureFormatEnum.PAdES_LTV;
				break;
		}

		return res;
	}
	
	private String getOperationServerCertAlias(){
		String res = this.serverCertAlias;
		
		if(res == null || res.isEmpty()){
			res = afirmaIntegraDefaultProperties.getProperty(INTEGRA_PREFIX, SERVER_CERT_ALIAS_PROP_NAME);
		} 

		return res;
	}
	
	private String getOperationAfirmaAppId(){
		String res = this.afirmaAppId;
		
		if(res == null || res.isEmpty()){
			res = afirmaIntegraDefaultProperties.getProperty(INTEGRA_PREFIX, AFIRMA_APP_ID_PROP_NAME);
		}

		return res;
	}
	
	/**
	 * @param afirmaIntegraDefaultProperties the afirmaIntegraDefaultProperties to set
	 */
	public void setAfirmaIntegraDefaultProperties(FilterPlaceholderProperties afirmaIntegraDefaultProperties) {
		this.afirmaIntegraDefaultProperties = afirmaIntegraDefaultProperties;
	}

	/**
	 * Obtiene el identificador DSS del formato de firma electrónica requerido por el API Integr@ de @firma.
	 * @param xmlSignatureMode modo de firma electrónica XML: ENVELOPED, ENVELOPING o DETACHED.
	 * @return modo de firma electrónica XML requerido por el API Integr@ de @firma.
	 */
	private static String translateXmlSignatureMode(XmlSignatureMode xmlSignatureMode) {
		String res;
		
		res = null;
		
		switch(xmlSignatureMode){
			case DETACHED:
				res = DSSConstants.XmlSignatureMode.DETACHED;
				break;
			case ENVELOPED:
				res = DSSConstants.XmlSignatureMode.ENVELOPED;
				break;
			case ENVELOPING:
				res = DSSConstants.XmlSignatureMode.ENVELOPING;
				break;
		}
		
		return res;
	}
}
