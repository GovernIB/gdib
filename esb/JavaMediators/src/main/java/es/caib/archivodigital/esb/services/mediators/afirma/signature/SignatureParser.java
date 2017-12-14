package es.caib.archivodigital.esb.services.mediators.afirma.signature;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import es.caib.archivodigital.esb.services.mediators.afirma.AfirmaMediatorUtils;
import es.caib.archivodigital.esb.services.mediators.afirma.TimestampType;
import es.gob.afirma.integraFacade.pojo.Result;

/**
 * Clase abstracta responsable de la extracción de información de firmas electrónicas no retornada por el 
 * servicio DSSAfirmaVerify de la plataforma @firma v6, y requerida por el servicio de validación de documentos. 
 *  
 * @author RICOH
 *
 */
public abstract class SignatureParser {

	private SignatureFormat signatureFormat;
	
	private String base64Signature;

	public SignatureParser(SignatureFormat signatureFormat, 
			String base64Signature) {
		super();
		this.signatureFormat = signatureFormat;
		this.base64Signature = base64Signature;
	}
	
	public abstract String getAllSignatures(String signatureFormat, Result dssResult, String extraInfo) throws SignatureParseException;
	
	public abstract String getSignature(String signatureFormat, Result dssResult, String extraInfo, 
			SignatureIdentifier signatureIdentifier) throws SignatureParseException;
	
	public abstract String getSignerCerts() throws SignatureParseException;
	
	public abstract String getSignerCerts(SignatureIdentifier signatureIdentifier) throws SignatureParseException;
	
	public abstract String getTimestamps() throws SignatureParseException;

	public abstract String getTimestamps(SignatureIdentifier signatureIdentifier) throws SignatureParseException;
	
	public abstract String getTimestamps(List<TimestampType> timestampTypes) throws SignatureParseException;
	
	public abstract String getTimestamps(List<TimestampType> timestampTypes, 
			SignatureIdentifier signatureIdentifier) throws SignatureParseException;
	
	public String buildTimeStampResult(Date genTime, TimestampType timestampType, X509Certificate certificate){
		StringBuilder stringBuilder = new StringBuilder();

		if(genTime != null && timestampType != null && certificate != null){
			stringBuilder.append("<csgd:timestamps>");
			stringBuilder.append("<csgd:type>" + timestampType.getName() + "</csgd:type>");
			stringBuilder.append("<csgd:genTime>");
			stringBuilder.append(AfirmaMediatorUtils.formatDate(genTime));
			stringBuilder.append("</csgd:genTime>");			
			stringBuilder.append(buildValidateCertificateResults(certificate));			
			stringBuilder.append("</csgd:timestamps>");
		}
		
		return stringBuilder.toString();
	}
	
	public String buildValidateCertificateResults(X509Certificate certificate){
		StringBuilder stringBuilder = new StringBuilder();
		String res = "";
		if(certificate != null){
			stringBuilder.append("<csgd:validateCertificateResults>");
			//Resultado validación
			stringBuilder.append("<csgd:result>");
			stringBuilder.append("<csgd:code>");
			stringBuilder.append("urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:ValidStatus");
			stringBuilder.append("</csgd:code>");
			stringBuilder.append("<csgd:message></csgd:message>");
			stringBuilder.append("</csgd:result>");
			//Campos certificado
			stringBuilder.append("<csgd:certificateFields>");
			stringBuilder.append("<csgd:alias>serialNumber</csgd:alias>");
			stringBuilder.append("<csgd:value>");
			stringBuilder.append(certificate.getSerialNumber().toString());
			stringBuilder.append("</csgd:value>");
			stringBuilder.append("</csgd:certificateFields>");
			stringBuilder.append("<csgd:certificateFields>");
			stringBuilder.append("<csgd:alias>issuerName</csgd:alias>");
			stringBuilder.append("<csgd:value>");
			stringBuilder.append(certificate.getIssuerX500Principal().getName(X500Principal.RFC2253));
			stringBuilder.append("</csgd:value>");
			stringBuilder.append("</csgd:certificateFields>");
			stringBuilder.append("<csgd:certificateFields>");
			stringBuilder.append("<csgd:alias>subject</csgd:alias>");
			stringBuilder.append("<csgd:value>");
			stringBuilder.append(certificate.getSubjectX500Principal().getName(X500Principal.RFC2253));
			stringBuilder.append("</csgd:value>");
			stringBuilder.append("</csgd:certificateFields>");
			stringBuilder.append("</csgd:validateCertificateResults>");
			
			res = stringBuilder.toString();
		}
		
		return res;
		
	}
	
	public static String buildResultResponse(Result dssResult)  {
		StringBuilder stringBuilder;
		String res = null;

		if(dssResult != null){
			stringBuilder =  new StringBuilder();
			stringBuilder.append("<csgd:result>");
			stringBuilder.append("<csgd:resultMajor>");
			stringBuilder.append(dssResult.getResultMajor());
			stringBuilder.append("</csgd:resultMajor>");
			
			if(dssResult.getResultMinor() != null && !dssResult.getResultMinor().isEmpty()){
				stringBuilder.append("<csgd:resultMinor>");
				stringBuilder.append(dssResult.getResultMinor());
				stringBuilder.append("</csgd:resultMinor>");
			}
			
			if(dssResult.getResultMessage() != null && !dssResult.getResultMessage().isEmpty()){
				stringBuilder.append("<csgd:resultMessage>");
				stringBuilder.append(dssResult.getResultMessage());
				stringBuilder.append("</csgd:resultMessage>");
			}
			stringBuilder.append("</csgd:result>");
			res = stringBuilder.toString();
		}
		
		return res;
	}
	
	public SignatureFormat getSignatureFormat() {
		return signatureFormat;
	}

	public String getBase64Signature() {
		return base64Signature;
	}

}
