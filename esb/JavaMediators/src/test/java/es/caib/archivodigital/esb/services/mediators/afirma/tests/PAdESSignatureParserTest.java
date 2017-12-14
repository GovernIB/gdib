package es.caib.archivodigital.esb.services.mediators.afirma.tests;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import es.caib.archivodigital.esb.services.mediators.afirma.TimestampType;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureFormat;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.pades.PAdESSignatureParser;
import es.gob.afirma.integraFacade.pojo.Result;
import es.gob.afirma.utils.CertificateUtils;
import es.gob.afirma.utils.UtilsFileSystem;

/**
 * Class that manages the tests associated to {@link es.caib.archivodigital.esb.services.mediators.afirma.signature.pades.PAdESSignatureParser} class.
 * 
 * @author RICOH
 *
 */
public class PAdESSignatureParserTest {

	/**
	 * Method that tests all the methods of {@link es.caib.archivodigital.esb.services.mediators.afirma.signature.pades.PAdESSignatureParser} class.
	 * @param args Null.
	 */
	public static void main(String[ ] args) {
		/*
		 * Tests sobre firma PDF
		 */
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("INICIO TESTS PDF");
		testPAdES("PDF", "PAdES/PDF.pdf");
		System.out.println("FIN TESTS PDF");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		 /*
		 * Tests sobre firma PAdES-Basic
		 */
		 System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		 System.out.println("INICIO TESTS PAdES-Basic");
		 testPAdES("PAdES-Basic", "PAdES/padesBasic.pdf");
		 System.out.println("FIN TESTS PAdES-Basic");
		 System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		 /*
		 * Tests sobre firma PAdES-LTV que contiene un diccionario de sello de
		 tiempo y un diccionario de firma con formato PAdES-Basic
		 */
		 System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		 System.out.println("INICIO TESTS PAdES-LTV (1 DICCIONARIO DE SELLO DE TIEMPO + 1 DICCIONARIO DE FIRMA PAdES-Basic)");
		 testPAdES("PAdES-LTV", "PAdES/padesBasicLTV.pdf");
		 System.out.println("FIN TESTS PAdES-LTV (1 DICCIONARIO DE SELLO DE TIEMPO + 1 DICCIONARIO DE FIRMA PAdES-Basic)");
		 System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		
		 /*
		 * Tests sobre firma PAdES-BES
		 */
		 System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		 System.out.println("INICIO TESTS PAdES-BES");
		 testPAdES("PAdES-BES", "PAdES/padesBES.pdf");
		 System.out.println("FIN TESTS PAdES-BES");
		 System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		
		 /*
		 * Tests sobre firma PAdES-LTV que contiene un diccionario de sello de
		 tiempo y un diccionario de firma con formato PAdES-BES
		 */
		 System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		 System.out.println("INICIO TESTS PAdES-LTV (1 DICCIONARIO DE SELLO DE TIEMPO + 1 DICCIONARIO DE FIRMA PAdES-BES)");
		 testPAdES("PAdES-LTV", "PAdES/padesBESLTV.pdf");
		 System.out.println("FIN TESTS PAdES-LTV (1 DICCIONARIO DE SELLO DE TIEMPO + 1 DICCIONARIO DE FIRMA PAdES-BES)");
		 System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		
		 /*
		 * Tests sobre firma PAdES-LTV que contiene 2 diccionarios de sello de
		 tiempo y un diccionario de firma con formato PAdES-BES
		 */
		 System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		 System.out.println("INICIO TESTS PAdES-LTV (2 DICCIONARIOS DE SELLO DE TIEMPO + 1 DICCIONARIO DE FIRMA PAdES-BES)");
		 testPAdES("PAdES-LTV", "PAdES/padesBESLTVDoble.pdf");
		 System.out.println("FIN TESTS PAdES-LTV (2 DICCIONARIOS DE SELLO DE TIEMPO + 1 DICCIONARIO DE FIRMA PAdES-BES)");
		 System.out.println("---------------------------------------------------------------------------------------------------------------------------");
	}

	/**
	 * Method that tests a signed PDF document with all the signatures contained.
	 * @param signatureFormat Parameter that represents the signature format.
	 * @param signaturePath Parameter that represents the path where to recover the signature to process.
	 */
	private static void testPAdES(String signatureFormat, String signaturePath) {
		try {
			String pdfDocumentBase64 = UtilsFileSystem.readFileBase64Encoded(signaturePath, true);
			SignatureFormat sf = SignatureFormat.PDF;
			if (signatureFormat.equals("PAdES-Basic")) {
				sf = SignatureFormat.PAdES_Basic;
			} else if (signatureFormat.equals("PAdES-BES")) {
				sf = SignatureFormat.PAdES_BES;
			} else if (signatureFormat.equals("PAdES-LTV")) {
				sf = SignatureFormat.PAdES_LTV;
			}

			PAdESSignatureParser parser = new PAdESSignatureParser(sf, pdfDocumentBase64);

			Result dssResult = new Result();
			dssResult.setResultMajor("urn:afirma:dss:1.0:profile:XSS:resultmajor:ValidSignature");
			dssResult.setResultMessage("La firma es válida.");

			/*
			 * XAdESSignatureParser.getAllSignatures(String, Result, String)
			 */
			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getAllSignatures(String, Result, String)");
			System.out.println(parser.getAllSignatures(signatureFormat, dssResult, null));

			/*
			 * XAdESSignatureParser.getSignature(String, Result, String, SignatureIdentifier)
			 */
			byte[ ] serverSignerCertificateBytes = UtilsFileSystem.readFile("certificates/ServerSigner.crt", true);
			X509Certificate serverSignerCertificate = CertificateUtils.getCertfromByteArray(serverSignerCertificateBytes);
			SignatureIdentifier serverSignerIdentifier = new SignatureIdentifier(serverSignerCertificate.getSerialNumber(), serverSignerCertificate.getIssuerX500Principal().toString(), null);

			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getSignature(String, Result, String, SignatureIdentifier)");
			System.out.println("ServerSigner");
			System.out.println(parser.getSignature(signatureFormat, dssResult, null, serverSignerIdentifier));

			/*
			 * XAdESSignatureParser.getSignerCerts()
			 */
			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getSignerCerts()");
			System.out.println(parser.getSignerCerts());

			/*
			 * XAdESSignatureParser.getSignerCerts(SignatureIdentifier)
			 */
			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getSignerCerts(SignatureIdentifier)");
			System.out.println("ServerSigner");
			System.out.println(parser.getSignerCerts(serverSignerIdentifier));

			/*
			 * XAdESSignatureParser.getTimestamps()
			 */
			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getTimestamps()");
			System.out.println(parser.getTimestamps());

			/*
			 * XAdESSignatureParser.getTimestamps(SignatureIdentifier)
			 */
			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getTimestamps(SignatureIdentifier)");
			System.out.println("ServerSigner");
			System.out.println(parser.getTimestamps(serverSignerIdentifier));

			/*
			 * XAdESSignatureParser.getTimestamps(List<TimestampType>)
			 */
			List<TimestampType> listTimestampTypes = new ArrayList<TimestampType>();
			listTimestampTypes.add(TimestampType.SIGNER);
			listTimestampTypes.add(TimestampType.ARCHIVE);
			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getTimestamps(List<TimestampType>)");
			System.out.println(parser.getTimestamps(listTimestampTypes));

			/*
			 * XAdESSignatureParser.getTimestamps(List<TimestampType>, SignatureIdentifier)
			 */
			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getTimestamps(List<TimestampType>, SignatureIdentifier)");
			System.out.println("ServerSigner");
			System.out.println(parser.getTimestamps(listTimestampTypes, serverSignerIdentifier));

		} catch (Exception e) {
			System.out.println("El test ha fallado: " + e.getMessage());
		}
	}

}
