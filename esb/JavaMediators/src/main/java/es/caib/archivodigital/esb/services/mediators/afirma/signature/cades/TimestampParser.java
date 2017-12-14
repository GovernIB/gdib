package es.caib.archivodigital.esb.services.mediators.afirma.signature.cades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.tsp.TimeStampToken;

import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParseException;

/**
 * Clase responsable de parsear los atributos no firmados incluidos en firmas
 * electrónicas CAdES que incluyen sellos de tiempo, TimestampToken. Estos
 * atributos son los siguientes:
 * <ul>
 * <li>timeStampToken (OID 1.2.840.113549.1.9.16.2.14)</li>
 * <li>esc_timestamp (1.2.840.113549.1.9.16.2.25)</li>
 * <li>archive_timestamp (OID 1.2.840.113549.1.9.16.2.48)</li>
 * </ul>
 * 
 * @author RICOH
 *
 */
public class TimestampParser {

	private static final Logger LOGGER = Logger.getLogger(TimestampParser.class);
	private List<TimeStampToken> timestampTokens;
	private List<ASN1ObjectIdentifier> signatureAttributes;

	public TimestampParser(List<ASN1ObjectIdentifier> signatureAttributes) throws SignatureParseException {
		if (!signatureAttributes.contains(SignatureConstants.timeStampToken) && 
				!signatureAttributes.contains(SignatureConstants.esc_timestamp) && 
					!signatureAttributes.contains(SignatureConstants.cert_crl_timestamp) && 
						!signatureAttributes.contains(SignatureConstants.archive_timestamp)) {
			String excMessage = "Error al obtener el parseador de sellos de tiempo para firmas CAdES. Se ha encontrado un identificador de atributo informado que no se corresponde con un atributo que aloje sellos de tiempo.";

			LOGGER.error(excMessage);
			throw new SignatureParseException(excMessage);
		}

		this.timestampTokens = new ArrayList<TimeStampToken>();
		this.signatureAttributes = signatureAttributes;
	}

	/**
	 * Obtiene los sellos de tiempo, de un determinado tipo (T, X o A), de una
	 * firma electrónica CAdES.
	 * 
	 * @param cmsSignedData
	 *            firma electrónica CAdES.
	 * @throws SignatureParseException
	 *             si ocurre algún error al procesar la firma electrónica o
	 *             extraer sus sellos de tiempo.
	 */
	public void setTimestampTokens(CMSSignedData cmsSignedData) throws SignatureParseException {
		this.timestampTokens = new ArrayList<TimeStampToken>();
		for (Iterator<SignerInformation> it = cmsSignedData.getSignerInfos().getSigners().iterator(); it.hasNext();) {
			SignerInformation signerInformation = (SignerInformation) it.next();
			this.timestampTokens.addAll(loadTimestampTokens(signerInformation));
		}
	}

	/**
	 * Establece la relación de sellos de tiempo del parseador, de un
	 * determinado tipo (T, X o A), a partir de la información de un firmante
	 * incluido en una firma electrónica CAdES.
	 * 
	 * @param signerInformation
	 *            información de un firmante de una firma electrónica CAdES.
	 * @throws SignatureParseException
	 *             si ocurre algún error al procesar la información del firmante
	 *             o extraer sus sellos de tiempo.
	 */
	public void setTimestampTokens(SignerInformation signerInformation) throws SignatureParseException {
		this.timestampTokens = loadTimestampTokens(signerInformation);
	}

	/**
	 * Retorna una lista ordenada, descendientemente, de instantes de tiempo
	 * correspondientes al momento temporal en el que fueron generados los
	 * sellos de tiempo parseados.
	 * 
	 * @return lista ordenada, descendientemente, de instantes de tiempo
	 */
	public List<Long> getTimestampTokensGenTimes() {
		List<Long> res = new ArrayList<Long>();
		TimeStampToken timeStampToken;

		for (Iterator<TimeStampToken> it = this.timestampTokens.iterator(); it.hasNext();) {
			timeStampToken = it.next();
			res.add(Long.valueOf(timeStampToken.getTimeStampInfo().getGenTime().getTime()));
		}

		// Se ordena descendientemente la lista obtenida
		Collections.sort(res, Collections.reverseOrder());

		return res;
	}

	public List<TimeStampToken> getTimestampTokens() {
		return this.timestampTokens;
	}

	/**
	 * Obtiene la relación de sellos de tiempo del parseador, de un determinado
	 * tipo (T, X o A), a partir de la información de un firmante incluido en
	 * una firma electrónica CAdES.
	 * 
	 * @param signerInformation
	 *            información de un firmante de una firma electrónica CAdES.
	 * @throws SignatureParseException
	 *             si ocurre algún error al procesar la información del firmante
	 *             o extraer sus sellos de tiempo.
	 */
	private List<TimeStampToken> loadTimestampTokens(SignerInformation signerInformation) throws SignatureParseException {
		// Instanciamos la lista que devolver
		List<TimeStampToken> listTimeStamps = new ArrayList<TimeStampToken>();
		
		// Obtenemos los atributos no firmados
		AttributeTable unsignedAtts = signerInformation.getUnsignedAttributes();

		// Comprobamos que el firmante incluya atributos no firmados
		if (unsignedAtts == null) {
			String errorMsg = "Error al parsear la firma en busca de sellos de tiempo: El firmante con serialNumber " + signerInformation.getSID().getSerialNumber() + 
					" no tiene atributos no firmados.";
			LOGGER.warn(errorMsg);
		} else {
			// Recorremos la lista de atributos no firmados que recuperar
			for (ASN1ObjectIdentifier attributeOID: signatureAttributes) {
				// Recuperamos todos los atributos para el OID actual
				ASN1EncodableVector timeStampAttributes = unsignedAtts.getAll(attributeOID);
				if(timeStampAttributes != null && timeStampAttributes.size() > 0){
					// Recorremos los atributos recuperados
					for (int i = 0; i < timeStampAttributes.size(); i++) {
						// Accedemos al atributo
						Attribute timeStampAttribute = (Attribute) timeStampAttributes.get(i);
		
						try {
							// Accedemos al sello de tiempo contenido en el atributo
							// y lo añadimos a la lista a devolver
							if(timeStampAttribute.getAttrValues() != null && timeStampAttribute.getAttrValues().size() > 0){							
								listTimeStamps.add(new TimeStampToken(new CMSSignedData(timeStampAttribute.getAttrValues().getObjectAt(0).toASN1Primitive().getEncoded())));
							} else {
								LOGGER.warn("El atributo " + timeStampAttribute.getAttrType().getId() + " no posee sellos de tiempo(" + attributeOID.getId() + ").");
							}
						} catch (Exception e) {
							String errorMsg = "Error al construir un sello de tiempo (" + attributeOID.getId() + "): " + e.getMessage() + ".";
							LOGGER.error(errorMsg, e);
							throw new SignatureParseException(errorMsg, e);
						}
					}
				}
			}
		}
		
		// Devolvemos la lista de sellos de tiempo
		return listTimeStamps;
	}

}
