package es.caib.archivodigital.esb.services.mediators.afirma;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.synapse.MessageContext;

import es.caib.archivodigital.esb.services.mediators.afirma.integraFacade.pojo.CaibEsbCertificatePathValidity;
import es.caib.archivodigital.esb.services.mediators.afirma.integraFacade.pojo.CaibEsbIndividualSignatureReport;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier;

/**
 * Clase de utilidades para los mediadores desarrollados para la invocación de servicios DSS publicados por @firma v6,
 * y el posterior procesamiento de las firmas para la extracción de información no retornada por los servicios de la
 * plataforma @firma v6.
 * @author RICOH
 *
 */
public class AfirmaMediatorUtils {

	public static final String AFIRMA_APP_NAME_PROPERTY_NAME = "afirmaAppName";
	public static final String DOC_TYPE_PROPERTY_NAME = "docType";
	public static final String DOC_SIGNATURE_PROPERTY_NAME = "docSignature";
	public static final String VALCERT_SIGNATURE_PROPERTY_NAME = "valCertDocSignature";
	public static final String DOC_CONTENT_PROPERTY_NAME = "docContent";
	public static final String TRANSFORMED_ASPECT_PROPERTY_NAME = "transformedAspect";
	public static final String DOC_CONTENT_SIGNATURE_PROPERTY_NAME = "docSignatureContent";
	public static final String ENI_DOC_TYPE = "eni:documento";
	public static final String GDIB_MIGR_DOC_TYPE = "gdib:documentoMigrado";
	public static final String TRANSFORMED_ASPECT = "gdib:transformado";
	public static final String ISO_8601_EXTENDED_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String ZULU_TIME_ZONE = "Zulu";
	
	private static final SimpleDateFormat dateFormater;
	static{
		dateFormater = new SimpleDateFormat(ISO_8601_EXTENDED_DATE_FORMAT);
		dateFormater.setTimeZone(TimeZone.getTimeZone(ZULU_TIME_ZONE));
	}
	
	/**
	 * Obtiene la respresentación de una fecha en formato ISO-8601 extendido (Zulu).
	 * @param date fecha
	 * @return respresentación de una fecha en formato ISO-8601 extendido (Zulu).
	 */
	public static String formatDate(Date date){
		String res = dateFormater.format(date);
		
		return res;
	}
	
	/**
	 * Obtiene una fecha a partir de su representación en formato ISO-8601 extendido (Zulu).
	 * @param date respresentación de una fecha en formato ISO-8601 extendido (Zulu).
	 * @return fecha correspondiente a la cadena que respresenta una fecha en formato ISO-8601 extendido (Zulu).
	 * @throws ParseException Si la cadena no está bien formada.
	 */
	public static Date parseDate(String date) throws ParseException{
		Date res = dateFormater.parse(date);
		
		return res;
	}
	
	public static String getDocContent(MessageContext synCtx){
		String res = null;
		String docType = (String) synCtx.getProperty(DOC_TYPE_PROPERTY_NAME);
		String transformedAspect = (String) synCtx.getProperty(TRANSFORMED_ASPECT_PROPERTY_NAME);
		
		if(ENI_DOC_TYPE.equals(docType) && !TRANSFORMED_ASPECT.equals(transformedAspect)){
			res = (String) synCtx.getProperty(DOC_CONTENT_PROPERTY_NAME);
		} else if(GDIB_MIGR_DOC_TYPE.equals(docType) || 
				(ENI_DOC_TYPE.equals(docType) && TRANSFORMED_ASPECT.equals(transformedAspect))){
    		res = (String) synCtx.getProperty(DOC_CONTENT_SIGNATURE_PROPERTY_NAME);
    	}
		
		return res;
	}
	
	/**
	 * Obtiene el identificador de una firma electrónica a partir de la información incluida en la respuesta del
	 * servicio DSSAfirmaVerify.
	 * @param isr informe de firma electrónica individual retornado por el servicio DSSAfirmaVerify, 
	 * tag vr:IndividualSignatureReport.
	 * @return el identificador de una firma electrónica verificada y retornada en la respuesta del
	 * servicio DSSAfirmaVerify.
	 */
	public static SignatureIdentifier getSignatureIdentifier(CaibEsbIndividualSignatureReport isr){
		BigInteger certSerialNumber;
		Date lastTstGenTime;
		SignatureIdentifier res = null;
		String certIssuerName, signatureTimeStampGenTime;
		
		
		CaibEsbCertificatePathValidity signerCert = isr.getSignatureCertificatesValidity().getSignerCertificate();
		if(signerCert != null){
			certSerialNumber = signerCert.getX509SerialNumber();
			certIssuerName = signerCert.getX509IssuerName();
			res = new SignatureIdentifier(certSerialNumber,certIssuerName,null);
			
			try {
				signatureTimeStampGenTime = getSignatureTimeStampGenTime(isr);
				
				if(signatureTimeStampGenTime != null){
					lastTstGenTime = parseDate(signatureTimeStampGenTime);
					res = new SignatureIdentifier(certSerialNumber,certIssuerName,lastTstGenTime.getTime());
				}
			} catch (ParseException e) {
				//No hacer nada
			}
		}
		
		return res;
	}
	
	/**
	 * Obtiene el instante en el que es realizado un sello de tiempo incluido en la respuesta del
	 * servicio DSSAfirmaVerify.
	 * @param isr informe de firma electrónica individual retornado por el servicio DSSAfirmaVerify, 
	 * tag vr:IndividualSignatureReport.
	 * @return instante en el que es realizado un sello de tiempo incluido en la respuesta del
	 * servicio DSSAfirmaVerify.
	 */
	private static String getSignatureTimeStampGenTime(CaibEsbIndividualSignatureReport isr){
		String res = null;
		
		if(isr.getDetailedReport() != null && isr.getDetailedReport().getSignatureTimeStamp() != null){
			res = isr.getDetailedReport().getSignatureTimeStamp().getGenTime();
		}
		
		return res;
	}
	
}
