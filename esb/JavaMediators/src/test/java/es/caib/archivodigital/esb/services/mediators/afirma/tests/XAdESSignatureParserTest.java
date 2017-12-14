package es.caib.archivodigital.esb.services.mediators.afirma.tests;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import es.caib.archivodigital.esb.services.mediators.afirma.TimestampType;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureFormat;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureIdentifier;
import es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser;
import es.gob.afirma.integraFacade.pojo.Result;
import es.gob.afirma.utils.CertificateUtils;
import es.gob.afirma.utils.UtilsFileSystem;

/**
 * Class that manages the tests associated to {@link es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser} class.
 * 
 * @author RICOH
 *
 */
public class XAdESSignatureParserTest {

	/**
	 * Method that tests all the methods of {@link es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser} class.
	 * @param args Null.
	 */
	public static void main(String[ ] args) {

		/*
		 * Tests sobre firma XAdES-BES con la estructura:
		 * 
		 * Documento
		 * 		|
		 * 		|__ Firmante1: serversigner (XAdES-BES)
		 * 				|
		 * 				|__ Firmante2: tsaserver (XAdES-BES)
		 * 						|
		 * 						|__ Firmante3: ricohsigner (XAdES-BES)
		 */
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("INICIO TESTS XAdES-BES");
		testXAdES("XAdES-BES", "XAdES/xadesBES.xml");
		System.out.println("FIN TESTS XAdES-BES");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		/*
		 * Tests sobre firma XAdES-T con la estructura:
		 * 
		 * Documento
		 * 		|
		 * 		|__ Firmante1: serversigner (XAdES-T)
		 * 				|
		 * 				|__ Firmante2: tsaserver (XAdES-T)
		 * 						|
		 * 						|__ Firmante3: ricohsigner (XAdES-T)
		 */
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("INICIO TESTS XAdES-T");
		testXAdES("XAdES-T", "XAdES/xadesT.xml");
		System.out.println("FIN TESTS XAdES-T");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		/*
		 * Tests sobre firma XAdES-X1 con la estructura:
		 * 
		 * Documento
		 * 		|
		 * 		|__ Firmante1: serversigner (XAdES-X1)
		 * 				|
		 * 				|__ Firmante2: tsaserver (XAdES-X1)
		 * 						|
		 * 						|__ Firmante3: ricohsigner (XAdES-X1)
		 */
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("INICIO TESTS XAdES-X1");
		testXAdES("XAdES-X1", "XAdES/xadesX1.xml");
		System.out.println("FIN TESTS XAdES-X1");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		/*
		 * Tests sobre firma XAdES-X2 con la estructura:
		 * 
		 * Documento
		 * 		|
		 * 		|__ Firmante1: serversigner (XAdES-X2)
		 * 				|
		 * 				|__ Firmante2: tsaserver (XAdES-X2)
		 * 						|
		 * 						|__ Firmante3: ricohsigner (XAdES-X2)
		 */
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("INICIO TESTS XAdES-X2");
		testXAdES("XAdES-X2", "XAdES/xadesX2.xml");
		System.out.println("FIN TESTS XAdES-X2");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		/*
		 * Tests sobre firma XAdES-XL1 con la estructura:
		 * 
		 * Documento
		 * 		|
		 * 		|__ Firmante1: serversigner (XAdES-XL1)
		 * 				|
		 * 				|__ Firmante2: tsaserver (XAdES-XL1)
		 * 						|
		 * 						|__ Firmante3: ricohsigner (XAdES-XL1)
		 */
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("INICIO TESTS XAdES-XL1");
		testXAdES("XAdES-XL1", "XAdES/xadesXL1.xml");
		System.out.println("FIN TESTS XAdES-XL1");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		/*
		 * Tests sobre firma XAdES-XL2 con la estructura:
		 * 
		 * Documento
		 * 		|
		 * 		|__ Firmante1: serversigner (XAdES-XL2)
		 * 				|
		 * 				|__ Firmante2: tsaserver (XAdES-XL2)
		 * 						|
		 * 						|__ Firmante3: ricohsigner (XAdES-XL2)
		 */
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("INICIO TESTS XAdES-XL2");
		testXAdES("XAdES-XL2", "XAdES/xadesXL2.xml");
		System.out.println("FIN TESTS XAdES-XL2");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		/*
		 * Tests sobre firma XAdES-A con la estructura:
		 * 
		 * Documento
		 * 		|
		 * 		|__ Firmante1: serversigner (XAdES-A construída sobre XAdES-XL1)
		 * 				|
		 * 				|__ Firmante2: tsaserver (XAdES-XL1)
		 * 						|
		 * 						|__ Firmante3: ricohsigner (XAdES-XL1)
		 */
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("INICIO TESTS XAdES-A1");
		testXAdES("XAdES-A", "XAdES/xadesA1.xml");
		System.out.println("FIN TESTS XAdES-A1");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");

		/*
		 * Tests sobre firma XAdES-A con la estructura:
		 * 
		 * Documento
		 * 		|
		 * 		|__ Firmante1: serversigner (XAdES-A construída sobre XAdES-XL2)
		 * 				|
		 * 				|__ Firmante2: tsaserver (XAdES-XL2)
		 * 						|
		 * 						|__ Firmante3: ricohsigner (XAdES-XL2)
		 */
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
		System.out.println("INICIO TESTS XAdES-A2");
		testXAdES("XAdES-A", "XAdES/xadesA2.xml");
		System.out.println("FIN TESTS XAdES-A2");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------");
	}

	/**
	 * Method that tests a signed XML document with all the signatures contained.
	 * @param signatureFormat Parameter that represents the signature format.
	 * @param signaturePath Parameter that represents the path where to recover the signature to process.
	 */
	private static void testXAdES(String signatureFormat, String signaturePath) {
		try {
			String xmlDocumentBase64 = UtilsFileSystem.readFileBase64Encoded(signaturePath, true);
			SignatureFormat sf = SignatureFormat.XAdES_BES;
			if (signatureFormat.equals("XAdES-T")) {
				sf = SignatureFormat.XAdES_T;
			} else if (signatureFormat.equals("XAdES-X1")) {
				sf = SignatureFormat.XAdES_X1;
			} else if (signatureFormat.equals("XAdES-X2")) {
				sf = SignatureFormat.XAdES_X2;
			} else if (signatureFormat.equals("XAdES-XL1")) {
				sf = SignatureFormat.XAdES_XL1;
			} else if (signatureFormat.equals("XAdES-XL2")) {
				sf = SignatureFormat.XAdES_XL2;
			} else if (signatureFormat.equals("XAdES-A")) {
				sf = SignatureFormat.XAdES_A;
			}

			XAdESSignatureParser parser = new XAdESSignatureParser(sf, xmlDocumentBase64);

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

			byte[ ] tsaServerCertificateBytes = UtilsFileSystem.readFile("certificates/TsaServer.cer", true);
			X509Certificate tsaServerCertificate = CertificateUtils.getCertfromByteArray(tsaServerCertificateBytes);
			SignatureIdentifier tsaServerIdentifier = new SignatureIdentifier(tsaServerCertificate.getSerialNumber(), tsaServerCertificate.getIssuerX500Principal().toString(), null);

			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getSignature(String, Result, String, SignatureIdentifier)");
			System.out.println("TsaServer");
			System.out.println(parser.getSignature(signatureFormat, dssResult, null, tsaServerIdentifier));

			byte[ ] ricohSignerCertificateBytes = UtilsFileSystem.readFile("certificates/RicohSigner.cer", true);
			X509Certificate ricohSignerCertificate = CertificateUtils.getCertfromByteArray(ricohSignerCertificateBytes);
			SignatureIdentifier ricohSignerIdentifier = new SignatureIdentifier(ricohSignerCertificate.getSerialNumber(), ricohSignerCertificate.getIssuerX500Principal().toString(), null);

			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getSignature(String, Result, String, SignatureIdentifier)");
			System.out.println("RicohSigner");
			System.out.println(parser.getSignature(signatureFormat, dssResult, null, ricohSignerIdentifier));

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

			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getSignerCerts(SignatureIdentifier)");
			System.out.println("TsaServer");
			System.out.println(parser.getSignerCerts(tsaServerIdentifier));

			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getSignerCerts(SignatureIdentifier)");
			System.out.println("RicohSigner");
			System.out.println(parser.getSignerCerts(ricohSignerIdentifier));

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

			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getTimestamps(SignatureIdentifier)");
			System.out.println("TsaServer");
			System.out.println(parser.getTimestamps(tsaServerIdentifier));

			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getTimestamps(SignatureIdentifier)");
			System.out.println("RicohSigner");
			System.out.println(parser.getTimestamps(ricohSignerIdentifier));

			/*
			 * XAdESSignatureParser.getTimestamps(List<TimestampType>)
			 */
			List<TimestampType> listTimestampTypes = new ArrayList<TimestampType>();
			listTimestampTypes.add(TimestampType.SIGNER);
			listTimestampTypes.add(TimestampType.CUSTODY);
			listTimestampTypes.add(TimestampType.ARCHIVE);
			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getTimestamps(List<TimestampType>)");
			System.out.println(parser.getTimestamps(listTimestampTypes));

			/*
			 * XAdESSignatureParser.getTimestamps(List<TimestampType>, SignatureIdentifier)
			 */
			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getTimestamps(List<TimestampType>, SignatureIdentifier)");
			System.out.println("ServerSigner");
			System.out.println(parser.getTimestamps(listTimestampTypes, serverSignerIdentifier));

			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getTimestamps(List<TimestampType>, SignatureIdentifier)");
			System.out.println("TsaServer");
			System.out.println(parser.getTimestamps(listTimestampTypes, tsaServerIdentifier));

			System.out.println("\nMétodo es.caib.archivodigital.esb.services.mediators.afirma.signature.xades.XAdESSignatureParser.getTimestamps(List<TimestampType>, SignatureIdentifier)");
			System.out.println("RicohSigner");
			System.out.println(parser.getTimestamps(listTimestampTypes, ricohSignerIdentifier));

		} catch (Exception e) {
			System.out.println("El test ha fallado: " + e.getMessage());
		}
	}
}
