package es.caib.archivodigital.esb.services.mediators.afirma.signature.xades;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Class that contains information about a time-stamp contained inside of a XML signature.
 * 
 * @author RICOH
 *
 */
public class XAdESTimeStampType implements Serializable, Comparable<XAdESTimeStampType> {

	/**
	 * Class serial version.
	 */
	private static final long serialVersionUID = -8968015143858334326L;

	/**
	 * Attribute that represents the value of the <code>Id</code> attribute.
	 */
	private String id;

	/**
	 * Attribute that represents the signing certificate of the time-stamp contained inside.
	 */
	private X509Certificate tstCertificate = null;

	/**
	 * Attribute that represents the generation time of the time-stamp.
	 */
	private Date timestampGenerationDate;

	/**
	 * Constructor method for the class XAdESTimeStampType.java.
	 * @param idParam Parameter that represents the value of the <code>Id</code> attribute.
	 * @param tstCertificateParam Parameter that represents the signing certificate of the time-stamp contained inside.
	 * @param timestampGenerationDateParam Parameter that represents the generation time of the time-stamp.
	 */
	public XAdESTimeStampType(String idParam, X509Certificate tstCertificateParam, Date timestampGenerationDateParam) {
		super();
		this.id = idParam;
		this.tstCertificate = tstCertificateParam;
		this.timestampGenerationDate = timestampGenerationDateParam;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(XAdESTimeStampType o) {
		return timestampGenerationDate.compareTo(o.getTimestampGenerationDate());
	}

	/**
	 * Gets the value of the attribute {@link #id}.
	 * @return the value of the attribute {@link #id}.
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Sets the value of the attribute {@link #id}.
	 * @param idParam The value for the attribute {@link #id}.
	 */
	public final void setId(String idParam) {
		this.id = idParam;
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
