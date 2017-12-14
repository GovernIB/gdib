package es.caib.archivodigital.esb.services.mediators.afirma.signature.pades;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Class that contains information about a time-stamp contained inside of a signature included into a <code>signature-time-stamp</code> attribute of the first signer of the signature
 * contained inside of a dictionary of a PDF document.
 * 
 * @author RICOH
 *
 */
public class TimeStampType implements Serializable, Comparable<TimeStampType> {

	/**
	 * Class serial version.
	 */
	private static final long serialVersionUID = -8470590726974820429L;

	/**
	 * Attribute that represents the signing certificate of the time-stamp.
	 */
	private X509Certificate tstCertificate = null;

	/**
	 * Attribute that represents the generation time of the time-stamp.
	 */
	private Date timestampGenerationDate;

	/**
	 * Constructor method for the class TimeStampType.java.
	 * @param tstCertificateParam Parameter that represents the signing certificate of the time-stamp.
	 * @param timestampGenerationDateParam Parameter that represents the generation time of the time-stamp.
	 */
	public TimeStampType(X509Certificate tstCertificateParam, Date timestampGenerationDateParam) {
		super();
		this.tstCertificate = tstCertificateParam;
		this.timestampGenerationDate = timestampGenerationDateParam;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(TimeStampType o) {
		return timestampGenerationDate.compareTo(o.getTimestampGenerationDate());
	}

	/**
	 * Gets the value of the attribute {@link #tstCertificate}.
	 * @return the value of the attribute {@link #tstCertificate}.
	 */
	public final X509Certificate getTstCertificate() {
		return tstCertificate;
	}

	/**
	 * Sets the value of the attribute {@link #tstCertificate}.
	 * @param tstCertificateParam The value for the attribute {@link #tstCertificate}.
	 */
	public final void setTstCertificate(X509Certificate tstCertificateParam) {
		this.tstCertificate = tstCertificateParam;
	}

	/**
	 * Gets the value of the attribute {@link #timestampGenerationDate}.
	 * @return the value of the attribute {@link #timestampGenerationDate}.
	 */
	public final Date getTimestampGenerationDate() {
		return timestampGenerationDate;
	}

	/**
	 * Sets the value of the attribute {@link #timestampGenerationDate}.
	 * @param timestampGenerationDateParam The value for the attribute {@link #timestampGenerationDate}.
	 */
	public final void setTimestampGenerationDate(Date timestampGenerationDateParam) {
		this.timestampGenerationDate = timestampGenerationDateParam;
	}

}
