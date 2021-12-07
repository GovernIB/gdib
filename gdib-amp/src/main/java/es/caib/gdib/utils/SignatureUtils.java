package es.caib.gdib.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import es.caib.gdib.ws.common.types.EemgdeSignatureProfile;
import es.caib.gdib.ws.common.types.EniSignatureType;
import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.impl.MigrationServiceSoapPortImpl;

public class SignatureUtils {
	private static final Logger LOGGER =  Logger.getLogger(SignatureUtils.class);

	//DSS SignatureType identifiers (@firma v6.1.1)
	private static final String CMS_DSS_URI_SIGNATURE_TYPE = "urn:ietf:rfc:3369";
	private static final String CMS_TST_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:CMSWithTST";
	private static final String XMLSIGNATURE_DSS_URI_SIGNATURE_TYPE = "urn:ietf:rfc:3275";
	private static final String CADES_DSS_URI_SIGNATURE_TYPE = "http://uri.etsi.org/01733/v1.7.3#";
	private static final String XADES_1_4_2_DSS_URI_SIGNATURE_TYPE = "http://uri.etsi.org/01903/v1.4.1#";
	private static final String XADES_DSS_URI_SIGNATURE_TYPE = "http://uri.etsi.org/01903/v1.3.2#";
	private static final String XADES_1_2_2_DSS_URI_SIGNATURE_TYPE = "http://uri.etsi.org/01903/v1.2.2#";
	private static final String XADES_1_1_1_DSS_URI_SIGNATURE_TYPE = "http://uri.etsi.org/01903/v1.1.1#";
	private static final String ODF_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:ODF";
	private static final String ODF_TST_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:ODFWithTST";
	private static final String PDF_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:PDF";
	private static final String PADES_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:PAdES";
	private static final String OOXML_DSS_URI_SIGNATURE_TYPE = "urn:afirma:dss:1.0:profile:XSS:forms:OOXML";

	//NuevosFormatos
	private static final String PDF_DSS_URI_PADESBASELINE= "http://uri.etsi.org/103172/v2.1.1#";
	private static final String CADES_DSS_URI_CADESBASELINE= "http://uri.etsi.org/103173/v2.2.1#";
	private static final String XADES_DSS_URI_XADESBASELINE= "http://uri.etsi.org/103171/v2.1.1#";
	//private static final String ASIC_DSS_URI_ASICASELINE= "http://uri.etsi.org/103172/v2.1.1#";
	//DSS SignatureForm identifiers (@firma v6.1.1)
	private static final String BASICO_DSS_URI_SIGNATURE_FORM = "urn:afirma:dss:1.0:profile:XSS:PAdES:1.2.1:forms:Basico";
	private static final String BES_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:BES";
	private static final String EPES_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:EPES";
	private static final String T_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-T";
	private static final String C_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-C";
	private static final String X_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X";
	private static final String X_1_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-1";
	private static final String X_2_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-2";
	private static final String X_L_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-L";
	private static final String X_L_1_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-L-1";
	private static final String X_L_2_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-L-2";
	private static final String A_DSS_URI_SIGNATURE_FORM = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-A";
	private static final String LTV_DSS_URI_SIGNATURE_FORM = "urn:afirma:dss:1.0:profile:XSS:PAdES:1.1.2:forms:LTV";

	private static final String DSS_URI_T_LVL_SIGNATURE_FORM = "urn:afirma:dss:1.0:profile:XSS:AdES:forms:T-Level";
	public static final String SIGNATURE_FORMAT_CONCATENATE_STRING = "_#_";

	private static Map<String,SignatureFormat> ENI_TRANSFORMED_FORMATS = new HashMap<String,SignatureFormat>();

	static{
		init();
	}

	private static void init(){

		//XAdES
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF02.getName().toUpperCase(), SignatureFormat.XAdES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF02.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.BES.getName().toUpperCase(), SignatureFormat.XAdES_BES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF02.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.EPES.getName().toUpperCase(), SignatureFormat.XAdES_EPES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF02.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.T.getName().toUpperCase(), SignatureFormat.XAdES_T);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF02.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.C.getName().toUpperCase(), SignatureFormat.XAdES_C);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF02.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.X.getName().toUpperCase(), SignatureFormat.XAdES_X);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF02.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.XL.getName().toUpperCase(), SignatureFormat.XAdES_XL);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF02.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.A.getName().toUpperCase(), SignatureFormat.XAdES_A);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF03.getName().toUpperCase(), SignatureFormat.XAdES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF03.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.BES.getName().toUpperCase(), SignatureFormat.XAdES_BES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF03.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.EPES.getName().toUpperCase(), SignatureFormat.XAdES_EPES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF03.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.T.getName().toUpperCase(), SignatureFormat.XAdES_T);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF03.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.C.getName().toUpperCase(), SignatureFormat.XAdES_C);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF03.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.X.getName().toUpperCase(), SignatureFormat.XAdES_X);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF03.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.XL.getName().toUpperCase(), SignatureFormat.XAdES_XL);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF03.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.A.getName().toUpperCase(), SignatureFormat.XAdES_A);

		//CAdES
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF04.getName().toUpperCase(), SignatureFormat.CAdES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF04.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.BES.getName().toUpperCase(), SignatureFormat.CAdES_BES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF04.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.EPES.getName().toUpperCase(), SignatureFormat.CAdES_EPES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF04.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.T.getName().toUpperCase(), SignatureFormat.CAdES_T);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF04.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.C.getName().toUpperCase(), SignatureFormat.CAdES_C);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF04.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.X.getName().toUpperCase(), SignatureFormat.CAdES_X);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF04.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.XL.getName().toUpperCase(), SignatureFormat.CAdES_XL);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF04.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.A.getName().toUpperCase(), SignatureFormat.CAdES_A);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF05.getName().toUpperCase(), SignatureFormat.CAdES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF05.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.BES.getName().toUpperCase(), SignatureFormat.CAdES_BES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF05.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.EPES.getName().toUpperCase(), SignatureFormat.CAdES_EPES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF05.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.T.getName().toUpperCase(), SignatureFormat.CAdES_T);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF05.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.C.getName().toUpperCase(), SignatureFormat.CAdES_C);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF05.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.X.getName().toUpperCase(), SignatureFormat.CAdES_X);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF05.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.XL.getName().toUpperCase(), SignatureFormat.CAdES_XL);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF05.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.A.getName().toUpperCase(), SignatureFormat.CAdES_A);

		//PAdES
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF06.getName().toUpperCase(), SignatureFormat.PAdES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF06.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.BES.getName().toUpperCase(), SignatureFormat.PAdES_BES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF06.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.EPES.getName().toUpperCase(), SignatureFormat.PAdES_EPES);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF06.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.LTV.getName().toUpperCase(), SignatureFormat.PAdES_LTV);
		ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF06.getName().toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				EemgdeSignatureProfile.T.getName().toUpperCase(), SignatureFormat.PAdES_T);

		//ENI_TRANSFORMED_FORMATS.put(EniSignatureType.TF06.getName().toUpperCase(), SignatureFormat.PAdES_T);
	}

	/**
	 * Method that obtains the format of a signature.
	 * @param signatureType Parameter that represents the DSS signature type Identifier (URI).
	 * @param signatureForm Parameter that represents the DSS signature form Identifier (URI).
	 * @return the signature format of the element.
	 */
	public static SignatureFormat dssSigntureFormatToInernalSignatureFormat(String signatureType, String signatureForm) {
		// Por defecto definimos que el formato no está reconocido
		SignatureFormat res = SignatureFormat.UNRECOGNIZED;
        LOGGER.debug(String.format("Tipus firma: %s, Signature form: %s", signatureType, signatureForm));
        
		switch(signatureType){
			case XADES_DSS_URI_XADESBASELINE:
			case XADES_1_4_2_DSS_URI_SIGNATURE_TYPE:
			case XADES_DSS_URI_SIGNATURE_TYPE:
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
					case DSS_URI_T_LVL_SIGNATURE_FORM:
						res = SignatureFormat.XAdES_T;
				}
				break;
			case CADES_DSS_URI_SIGNATURE_TYPE:
			case CADES_DSS_URI_CADESBASELINE:
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
					case DSS_URI_T_LVL_SIGNATURE_FORM:
						res = SignatureFormat.CAdES_T;
				}
				break;
			case PDF_DSS_URI_SIGNATURE_TYPE:
				res = SignatureFormat.PDF;
				break;
			case PADES_DSS_URI_SIGNATURE_TYPE:
			case PDF_DSS_URI_PADESBASELINE:
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
					case DSS_URI_T_LVL_SIGNATURE_FORM:
						res = SignatureFormat.PAdES_T;
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
	 * Obtiene a partir del tipo de firma ENI, basado en certificado digital, y perfil de firma eEMGDE, el formato de firma equivalente.
	 * @param signatureType tipo de firma ENI ("TF02", "TF03", etc.).
	 * @param signatureForm perfil de firma eEMGDE ("EPES", "T", "LTV", etc.).
	 * @return Formato de firma interno. Si no es reconocido el formato de firma, retorna SignatureFormat.UNRECOGNIZED.
	 * @throws GdibException Si no es informado algún parámetro de entrada.
	 */
	public static SignatureFormat eniSigntureFormatToInernalSignatureFormat(String signatureType, String signatureForm) throws GdibException {
		// Por defecto definimos que el formato no está reconocido
		SignatureFormat res;
		LOGGER.debug("Checking EniSignaturFormatToInternalSignatueFormat with signatureTyp ="+signatureType+" and stringformat = " +signatureForm);
		EniSignatureType eniSignatureType = EniSignatureType.valueOf(signatureType.toUpperCase());

		EemgdeSignatureProfile eemgdeSignatureProfile = EemgdeSignatureProfile.valueOf(signatureForm);

		if(eniSignatureType == null){
			throw new GdibException("Tipo de firma ENI, " + signatureType + ", no reconocido.");
		}

		if(eemgdeSignatureProfile == null){
			throw new GdibException("Perfil o modo de firma eEMGDE, " + signatureForm + ", no reconocido.");
		}


		String signatureFormatString = signatureType.toUpperCase() + SIGNATURE_FORMAT_CONCATENATE_STRING +
				signatureForm.toUpperCase();

		res = ENI_TRANSFORMED_FORMATS.get(signatureFormatString);

		if(res == null){
			res = SignatureFormat.UNRECOGNIZED;
		}

		return res;
	}

	/**
	 * Obtiene a partir del tipo de firma ENI, basado en certificado digital, y perfil de firma eEMGDE, el formato de firma equivalente.
	 * @param signatureFormat Foprmato de firma tipo ENI ("TF02_#_T", "TF02_#_LTV", etc.).
	 * @return Formato de firma interno. Si no es reconocido el formato de firma, retorna SignatureFormat.UNRECOGNIZED.
	 * @throws GdibException Si no es informado el parámetro de entrada.
	 */
	public static SignatureFormat eniSigntureFormatToInernalSignatureFormat(String signatureFormat) throws GdibException {
		// Por defecto definimos que el formato no está reconocido
		SignatureFormat res;

		if(signatureFormat == null){
			throw new GdibException("Formato de firma ENI no informado.");
		}
		LOGGER.debug("Checking eniSigntureFormatToInernalSignatureFormat with signatureFormat =" + signatureFormat);
		res = ENI_TRANSFORMED_FORMATS.get(signatureFormat);

		if(res == null){
			res = SignatureFormat.UNRECOGNIZED;
		}

		return res;
	}

	/**
	 * Obtiene a partir del formato de firma ENI completo, basado en certificado digital, el perfil de firma avanzado.
	 * @param signatureFormat Formato de firma tipo ENI ("TF02_#_T", "TF02_#_LTV", etc.).
	 * @return Perfil de firma avanzado eEMGDE ("T", "LTV", "A", etc.).
	 * @throws GdibException Si no es informado el parámetro de entrada.
	 */
	public static EemgdeSignatureProfile getEniSignatureProfile(String signatureFormat) throws GdibException {
		// Por defecto definimos que el formato no está reconocido
		EemgdeSignatureProfile res = null;
		String [] signatureFormatParts;

		if(signatureFormat == null || signatureFormat.isEmpty()){
			throw new GdibException("Formato de firma ENI no informado.");
		}

		signatureFormatParts =  signatureFormat.split(SIGNATURE_FORMAT_CONCATENATE_STRING);
		if(signatureFormatParts.length == 2 && !signatureFormatParts[1].isEmpty()){
			res = EemgdeSignatureProfile.valueOf(signatureFormatParts[1].toUpperCase());
		}

		return res;
	}

}
