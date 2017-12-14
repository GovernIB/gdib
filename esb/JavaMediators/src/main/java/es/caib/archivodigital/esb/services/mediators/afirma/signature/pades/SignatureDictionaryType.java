package es.caib.archivodigital.esb.services.mediators.afirma.signature.pades;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Class that contains information about a signature contained inside of a signature dictionary of a PDF document.
 * 
 * @author RICOH
 *
 */
public class SignatureDictionaryType implements Serializable, Comparable<SignatureDictionaryType> {

	/**
	 * Class serial version.
	 */
	private static final long serialVersionUID = -3979564082367480180L;

	/**
	 * Attribute that represents the name of the signature dictionary.
	 */
	private String name;

	/**
	 * Attribute that represents the number of the revision of the PDF document when the signature dictionary was added.
	 */
	private Integer revision;

	/**
	 * Attribute that represents the signing certificate of the signature contained inside.
	 */
	private X509Certificate signingCertificate = null;

	/**
	 * Attribute that represents the list of time-stamps contained inside of the <code>signature-time-stamp</code> attribute of the first signer of the signature included
	 * into the signature dictionary.
	 */
	private List<TimeStampType> listSignatureTimeStamps = null;

	/**
	 * Attribute that represents the list of time-stamps contained inside of the Document Time-stamp dictionaries with a revision number major than the revision number
	 * of the signature dictionary.
	 */
	private List<TimeStampType> listArchiveTimeStamps = null;

	/**
	 * Constructor method for the class SignatureDictionaryType.java.
	 * @param nameParam Attribute that represents the name of the signature dictionary.
	 * @param revisionParam Attribute that represents the number of the revision of the PDF document when the signature dictionary was added.
	 * @param signingCertificateParam Attribute that represents the signing certificate of the signature contained inside.
	 * @param listSignatureTimeStampsParam  Attribute that represents the list of time-stamps contained inside of the <code>signature-time-stamp</code> attribute of the first signer 
	 * of the signature included into the signature dictionary.
	 * @param listArchiveTimeStampsParam Attribute that represents the list of time-stamps contained inside of the Document Time-stamp dictionaries with a revision number major 
	 * than the revision number of the signature dictionary.
	 */
	public SignatureDictionaryType(String nameParam, Integer revisionParam, X509Certificate signingCertificateParam, List<TimeStampType> listSignatureTimeStampsParam, List<TimeStampType> listArchiveTimeStampsParam) {
		super();
		this.name = nameParam;
		this.revision = revisionParam;
		this.signingCertificate = signingCertificateParam;
		this.listSignatureTimeStamps = listSignatureTimeStampsParam;
		this.listArchiveTimeStamps = listArchiveTimeStampsParam;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(SignatureDictionaryType o) {
		return revision.compareTo(o.getRevision());
	}

	/**
	 * Gets the value of the attribute {@link #name}.
	 * @return the value of the attribute {@link #name}.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the value of the attribute {@link #name}.
	 * @param nameParam The value for the attribute {@link #name}.
	 */
	public final void setName(String nameParam) {
		this.name = nameParam;
	}

	/**
	 * Gets the value of the attribute {@link #revision}.
	 * @return the value of the attribute {@link #revision}.
	 */
	public final Integer getRevision() {
		return revision;
	}

	/**
	 * Sets the value of the attribute {@link #revision}.
	 * @param revisionParam The value for the attribute {@link #revision}.
	 */
	public final void setRevision(Integer revisionParam) {
		this.revision = revisionParam;
	}

	/**
	 * Gets the value of the attribute {@link #signingCertificate}.
	 * @return the value of the attribute {@link #signingCertificate}.
	 */
	public final X509Certificate getSigningCertificate() {
		return signingCertificate;
	}

	/**
	 * Sets the value of the attribute {@link #signingCertificate}.
	 * @param signingCertificateParam The value for the attribute {@link #signingCertificate}.
	 */
	public final void setSigningCertificate(X509Certificate signingCertificateParam) {
		this.signingCertificate = signingCertificateParam;
	}

	/**
	 * Gets the value of the attribute {@link #listSignatureTimeStamps}.
	 * @return the value of the attribute {@link #listSignatureTimeStamps}.
	 */
	public final List<TimeStampType> getListSignatureTimeStamps() {
		return listSignatureTimeStamps;
	}

	/**
	 * Sets the value of the attribute {@link #listSignatureTimeStamps}.
	 * @param listSignatureTimeStampsParam The value for the attribute {@link #listSignatureTimeStamps}.
	 */
	public final void setListSignatureTimeStamps(List<TimeStampType> listSignatureTimeStampsParam) {
		this.listSignatureTimeStamps = listSignatureTimeStampsParam;
	}

	/**
	 * Gets the value of the attribute {@link #listArchiveTimeStamps}.
	 * @return the value of the attribute {@link #listArchiveTimeStamps}.
	 */
	public final List<TimeStampType> getListArchiveTimeStamps() {
		return listArchiveTimeStamps;
	}

	/**
	 * Sets the value of the attribute {@link #listArchiveTimeStamps}.
	 * @param listArchiveTimeStampsParam The value for the attribute {@link #listArchiveTimeStamps}.
	 */
	public final void setListArchiveTimeStamps(List<TimeStampType> listArchiveTimeStampsParam) {
		this.listArchiveTimeStamps = listArchiveTimeStampsParam;
	}

}
