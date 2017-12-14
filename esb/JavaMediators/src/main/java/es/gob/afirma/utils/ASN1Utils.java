package es.gob.afirma.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Store;

/**
 * Class that manages operations related to ASN.1 objects.
 * 
 * @author RICOH
 *
 */
public final class ASN1Utils {

	/**
	 * Constructor method for the class ASN1Utils.java.
	 */
	private ASN1Utils() {
	}

	/**
	 * Method that obtains the singing certificate of a signer of a signature.
	 * @param signedData Parameter that represents the signed data.
	 * @param signerInformation Parameter that represents the information about the signer of the signature.
	 * @return an object that represents the signing certificate.
	 * @throws UtilsException If the certificate hasn't could be retrieved.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static X509Certificate getSigningCertificate(CMSSignedData signedData, SignerInformation signerInformation) throws UtilsException {
		// Comprobamos que se hayan indicado los parámetros de entrada
		if (signedData == null) {
			throw new IllegalArgumentException("Error en parámetro de entrada: No se han indicado los datos de la firma.");
		}
		if (signerInformation == null) {
			throw new IllegalArgumentException("Error en parámetro de entrada: No se ha indicado el firmante.");
		}
		try {
			// Obtenemos el conjunto de certificados de la firma
			Store store = signedData.getCertificates();

			// Obtenemos el certificado firmante
			Collection<X509CertificateHolder> certCollection = store.getMatches(signerInformation.getSID());
			Iterator<X509CertificateHolder> certIt = certCollection.iterator();
			X509CertificateHolder certHolder = certIt.next();
			return new JcaX509CertificateConverter().getCertificate(certHolder);
		} catch (CertificateException e) {
			throw new UtilsException("Se ha producido un error al tratar de obtener el certificado firmante.", e);
		}
	}

	/**
	 * Method that obtains the list of time-stamps contained inside of <code>signature-time-stamp</code> attributes of a signer.
	 * @param signerInformation Parameter that represents the information about the signer of the signature.
	 * @return a list with the found time-stamps.
	 * @throws UtilsException If the method fails.
	 */
	public static List<TimeStampToken> getListTimeStampTokensIntoSignatureTimeStampAtts(SignerInformation signerInformation) throws UtilsException {
		// Comprobamos que se ha indicado el firmante
		if (signerInformation == null) {
			throw new IllegalArgumentException("Error en parámetro de entrada: No se ha indicado el firmante.");
		}
		List<TimeStampToken> listTimeStamps = new ArrayList<TimeStampToken>();
		// Accedemos al conjunto de atributos no firmados
		AttributeTable unsignedAttrs = signerInformation.getUnsignedAttributes();

		// Si el firmante posee atributos no firmados
		if (unsignedAttrs != null) {
			// Accedemos a los atributos signature-time-stamp
			ASN1EncodableVector signatureTimeStampAttributes = unsignedAttrs.getAll(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);

			// Recorremos los atributos signature-time-stamp
			for (int i = 0; i < signatureTimeStampAttributes.size(); i++) {
				// Accedemos al atributo signature-time-stamp
				Attribute signatureTimeStampAttribute = (Attribute) signatureTimeStampAttributes.get(i);

				try {
					// Accedemos al sello de tiempo contenido en el atributo
					// signature-time-stamp y lo añadimos a la lista que
					// devolver
					listTimeStamps.add(new TimeStampToken(new CMSSignedData(signatureTimeStampAttribute.getAttrValues().getObjectAt(0).toASN1Primitive().getEncoded())));
				} catch (Exception e) {
					throw new UtilsException("El firmante posee un sello de tiempo mal formado dentro de un atributo no firmado signature-time-stamp.", e);
				}
			}
		}
		return listTimeStamps;
	}
}
