package es.gob.afirma.utils;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Store;

/**
 * Class that provides methods for managing certificates and private keys.
 * 
 * @author RICOH
 *
 */
public final class CertificateUtils {

	/**
	 * Constructor method for the class CertificateUtils.java.
	 */
	private CertificateUtils() {
	}

	/**
	 * Method that retrieves a certificate from the bytes array.
	 * @param cert Parameter that represents the certificate to retrieve.
	 * @return an object that represents the certificate.
	 * @throws UtilsException If the certificate cannot be retrieved.
	 */
	public static X509Certificate getCertfromByteArray(byte[ ] cert) throws UtilsException {
		if (cert == null) {
			throw new IllegalArgumentException("Error en parámetro de entrada: No se ha indicado el certificado.");
		}
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate x509cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert));
			return x509cert;
		} catch (Exception e) {
			throw new UtilsException("El certificado no ha podido ser obtenido.", e);
		}
	}

	/**
	 * Method that obtain the the signing certificate of a time-stamp.
	 * @param tst Parameter that represents the time-stamp.
	 * @return an object that represents the certificate.
	 * @throws UtilsException If the method fails.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static X509Certificate getSigningCertificate(TimeStampToken tst) throws UtilsException {
		if (tst == null) {
			throw new IllegalArgumentException("Error en parámetro de entrada: No se ha indicado el sello de tiempo.");
		}
		try {
			Store certStore = tst.getCertificates();
			Collection<X509CertificateHolder> collectionSigningCertificate = (Collection<X509CertificateHolder>) certStore.getMatches(tst.getSID());

			if (collectionSigningCertificate.size() != 1) {
				throw new UtilsException("No ha sido posible localizar el certificado con el que se ha generado el sello de tiempo dentro del almacén de certificados de la firma del sello de tiempo.");
			}
			return new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getCertificate(collectionSigningCertificate.iterator().next());
		} catch (Exception e) {
			throw new UtilsException("No se pueden obtener los certificados del almacén de certificados de la firma del sello de tiempo.", e);
		}
	}
}
