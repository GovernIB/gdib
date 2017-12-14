package es.caib.archivodigital.esb.services.mediators.afirma.signature;

import es.caib.archivodigital.esb.services.mediators.afirma.signature.cades.CAdESSignatureParser;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.pades.PAdESSignatureParser;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser;

public class SignatureParserFactory {

	public static SignatureParser createSignatureParser(SignatureFormat signatureFormat, String base64Signature) throws SignatureParseException {
		SignatureParser res = null;
		
		if(signatureFormat.getName().startsWith(SignatureFormat.CAdES.getName())){
			res = new CAdESSignatureParser(signatureFormat, base64Signature);
		} else if(signatureFormat.getName().startsWith(SignatureFormat.XAdES.getName())){
			res = new XAdESSignatureParser(signatureFormat, base64Signature);
		} else if(signatureFormat.getName().startsWith(SignatureFormat.PAdES.getName())){
			res = new PAdESSignatureParser(signatureFormat, base64Signature);
		} else {
			throw new SignatureParseException("No se obtuvo parseador de firma para el formato " + signatureFormat.getName());
		}
		
		return res;
	}
	
}
