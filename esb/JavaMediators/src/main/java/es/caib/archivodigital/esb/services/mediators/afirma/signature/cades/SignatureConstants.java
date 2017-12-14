package es.caib.archivodigital.esb.services.mediators.afirma.signature.cades;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/**
 * Definición de constantes empleadas desde las clases encargadas de la 
 * extracción de información de firmas electrónicas.
 * 
 * @author RICOH
 *
 */
public interface SignatureConstants {
	// Definición de atributos CMS y CAdES
    public static ASN1ObjectIdentifier message_digest = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.4");
	public static ASN1ObjectIdentifier signing_certificate = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.12");
	public static ASN1ObjectIdentifier timeStampToken = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.14");
	public static ASN1ObjectIdentifier signing_certificateV2 = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.47");
	public static ASN1ObjectIdentifier complete_certificate_references = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.21");
	public static ASN1ObjectIdentifier complete_revocation_references = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.22");
	public static ASN1ObjectIdentifier esc_timestamp = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.25");
	public static ASN1ObjectIdentifier cert_crl_timestamp = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.26");
	public static ASN1ObjectIdentifier complete_certificate_values = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.23");
	public static ASN1ObjectIdentifier complete_revocation_values = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.24");
	public static ASN1ObjectIdentifier archive_timestamp = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.48");
	public static ASN1ObjectIdentifier obsolete_archive_timestamp = new ASN1ObjectIdentifier(
			"1.2.840.113549.1.9.16.2.27");
}
