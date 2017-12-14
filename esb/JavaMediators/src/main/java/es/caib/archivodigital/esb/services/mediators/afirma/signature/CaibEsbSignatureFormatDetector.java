package es.caib.archivodigital.esb.services.mediators.afirma.signature;

import javax.xml.crypto.dsig.XMLSignature;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import es.caib.archivodigital.esb.services.mediators.afirma.i18n.CaibEsbLanguage;
import es.caib.archivodigital.esb.services.mediators.afirma.utils.CaibEsbUtilsXML;
import es.gob.afirma.i18n.ILogConstantKeys;
import es.gob.afirma.signature.xades.IXMLConstants;

public final class CaibEsbSignatureFormatDetector {

	//DSS SignatureType identifiers
	public static final String CMS_DSS_URI_SIGNATURE_TYPE = "urn:ietf:rfc:3369";
	public static final String CMS_TST_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:CMSWithTST";
	public static final String XMLSIGNATURE_DSS_URI_SIGNATURE_TYPE = "urn:ietf:rfc:3275";
	public static final String CADES_DSS_URI_SIGNATURE_TYPE = "http://uri.etsi.org/01733/v1.7.3#";
	public static final String XADES_1_4_2_DSS_URI_SIGNATURE_TYPE = "http://uri.etsi.org/01903/v1.4.1#";
	public static final String XADES_1_3_2_DSS_URI_SIGNATURE_TYPE = "http://uri.etsi.org/01903/v1.3.2#";
	public static final String XADES_1_2_2_DSS_URI_SIGNATURE_TYPE = "http://uri.etsi.org/01903/v1.2.2#";
	public static final String XADES_1_1_1_DSS_URI_SIGNATURE_TYPE = "http://uri.etsi.org/01903/v1.1.1#";
	public static final String ODF_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:ODF"; 
	public static final String ODF_TST_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:ODFWithTST";
	public static final String PDF_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:PDF";
	public static final String PADES_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:PAdES";
	public static final String OOXML_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:OOXML";

	//DSS SignatureForm identifiers
	public static final String BASICO_DSS_URI_SIGNATURE_FORM = "urn:afirma:dss:1.0:profile:XSS:PAdES:1.2.1:forms:Basico";
	public static final String BES_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:BES";
	public static final String EPES_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:EPES";
	public static final String T_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-T";
	public static final String C_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-C";
	public static final String X_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X";
	public static final String X_1_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-1";
	public static final String X_2_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-2";
	public static final String X_L_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-L";
	public static final String X_L_1_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-L-1";
	public static final String X_L_2_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-L-2";
	public static final String A_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-A";
	public static final String LTV_DSS_URI_SIGNATURE_FORM = "urn:afirma:dss:1.0:profile:XSS:PAdES:1.1.2:forms:LTV";

	/**
	 * Method that obtains the format of a signature.
	 * @param signatureType Parameter that represents the DSS signature type Identifier (URI).
	 * @param signatureForm Parameter that represents the DSS signature form Identifier (URI).
	 * @return the signature format of the element. 
	 */
	public static SignatureFormat getSignatureFormat(String signatureType, String signatureForm) {
		// Comprobamos que se ha indicado el elemento a comprobar
		if (signatureType == null || signatureForm == null) {
			throw new IllegalArgumentException(CaibEsbLanguage.getResIntegra(ILogConstantKeys.SFD_LOG001));
		}

		// Por defecto definimos que el formato no está reconocido
		SignatureFormat res = SignatureFormat.UNRECOGNIZED;

		switch(signatureType){
			case XADES_1_4_2_DSS_URI_SIGNATURE_TYPE:
			case XADES_1_3_2_DSS_URI_SIGNATURE_TYPE:
			case XADES_1_2_2_DSS_URI_SIGNATURE_TYPE:
			case XADES_1_1_1_DSS_URI_SIGNATURE_TYPE:
				res = SignatureFormat.XAdES;
				switch(signatureForm){
					case BES_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_BES;
						break;
					case EPES_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_EPES;
						break;
					case T_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_T;
						break;
					case C_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_C;
						break;
					case X_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_X;
						break;
					case X_1_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_X1;
						break;
					case X_2_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_X2;
						break;
					case X_L_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_XL;
						break;
					case X_L_1_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_XL1;
						break;
					case X_L_2_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_XL2;
						break;
					case A_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_A;
						break;
				}
				break;
			case CADES_DSS_URI_SIGNATURE_TYPE:
				res = SignatureFormat.CAdES;
				switch(signatureForm){
					case BES_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_BES;
						break;
					case EPES_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_EPES;
						break;
					case T_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_T;
						break;
					case C_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_C;
						break;
					case X_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_X;
						break;
					case X_1_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_X1;
						break;
					case X_2_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_X2;
						break;
					case X_L_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_XL;
						break;
					case X_L_1_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_XL1;
						break;
					case X_L_2_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_XL2;
						break;
					case A_DSS_URI_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_A;
						break;
				}
				break;
			case PDF_DSS_URI_SIGNATURE_TYPE:
				res = SignatureFormat.PDF;
				break;
			case PADES_DSS_URI_SIGNATURE_TYPE:
				res = SignatureFormat.PAdES;
				switch(signatureForm){
				case BASICO_DSS_URI_SIGNATURE_FORM:
					res = SignatureFormat.PAdES_Basic;
					break;
				case BES_DSS_URI_SIGNATURE_FORM:
					res = SignatureFormat.PAdES_BES;
					break;
				case EPES_DSS_URI_SIGNATURE_FORM:
					res = SignatureFormat.PAdES_EPES;
					break;
				case LTV_DSS_URI_SIGNATURE_FORM:
					res = SignatureFormat.PAdES_LTV;
					break;
			}
			break;
			case XMLSIGNATURE_DSS_URI_SIGNATURE_TYPE:
				res = SignatureFormat.XML_SIGNATURE;
				break;
			case ODF_DSS_URI_SIGNATURE_TYPE: 
			case ODF_TST_DSS_URI_SIGNATURE_TYPE:
			case CMS_DSS_URI_SIGNATURE_TYPE:
			case CMS_TST_DSS_URI_SIGNATURE_TYPE:
			case OOXML_DSS_URI_SIGNATURE_TYPE:
				//No soportados por el sistema
				break;
		}

		return res;
	}
	
	/**
	 * Method that indicates whether a signature is XML (true) or not (false).
	 * @param signature Parameter that represents the signature to check.
	 * @return a boolean that indicates whether a signature is XML (true) or not (false).
	 */
	public static boolean isXMLFormat(byte[ ] signature) {
		try {
			Document doc = CaibEsbUtilsXML.getDocumentFromXML(signature);
			NodeList nl = null;
			if (doc != null) {
				nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, IXMLConstants.ELEMENT_SIGNATURE);
				if (nl.getLength() > 0) {
					return true;
				} else {
					nl = doc.getElementsByTagName(IXMLConstants.ELEMENT_SIGNATURE);
					if (nl.getLength() > 0) {
						return true;
					}
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
