package es.gob.afirma.utils;

import java.io.IOException;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;

import es.caib.archivodigital.esb.services.mediators.afirma.signature.SignatureParseException;

/**
 * Class that manages operations related to PDF documents.
 * 
 * @author RICOH
 *
 */
public final class PDFUtils {

	/**
	 *  Constant attribute that represents the string to identify the value for the key <i>SubFilter</i> of a Document Time-stamp dictionary.
	 */
	public static final String TST_SUBFILTER_VALUE = "ETSI.RFC3161";

	/**
	 * Constant attribute that represents the string to identify the type of a Document Time-stamp.
	 */
	public static final String DOC_TIME_STAMP_DICTIONARY_NAME = "DocTimeStamp";

	/**
	 *  Constant attribute that represents the value for the key <i>SubFilter</i> of the signature dictionary for a
	 *  PAdES Enhanced signature.
	 */
	public static final PdfName CADES_SUBFILTER_VALUE = new PdfName("ETSI.CAdES.detached");

	/**
	 * Constructor method for the class PDFUtils.java.
	 */
	private PDFUtils() {
	}

	/**
	 * Method that obtains an object to read a PDF document.
	 * @param pdfDocument Parameter that represents the PDF document.
	 * @return an object that allows to read a PDF document.
	 * @throws SignatureParseException If the method fails.
	 */
	public static PdfReader getReader(byte[ ] pdfDocument) throws SignatureParseException {
		// Comprobamos que se ha indicado el documento PDF
		if (pdfDocument == null) {
			throw new IllegalArgumentException("Error en parámetro de entrada: No se ha indicado el documento PDF");
		}
		try {
			return new PdfReader(pdfDocument);
		} catch (IOException e) {
			throw new SignatureParseException("Se ha producido un error leyendo el documento PDF.", e);
		}
	}

	/**
	 * Method that checks if a signature dictionary contains a PKCS#7 signature (true) or a CAdES signature (false).
	 * @param dictionary Parameter that represents the signature dictionary.
	 * @return a boolean that indicates if the signature dictionary contains a PKCS#7 signature (true) or a CAdES signature (false).
	 */
	public static boolean containsPKCS7Signature(PdfDictionary dictionary) {
		// Comprobamos que se ha indicado el diccionario de firma
		if (dictionary == null) {
			throw new IllegalArgumentException("Error en parámetro de entrada: No se ha indicado el diccionario de firma");
		}
		// Comprobamos si la firma contenida en el diccionario de firma es de
		// tipo PKCS#7
		PdfName subFilterValue = (PdfName) dictionary.get(PdfName.SUBFILTER);
		if (!subFilterValue.equals(PdfName.ADBE_PKCS7_DETACHED) && !subFilterValue.equals(PdfName.ADBE_PKCS7_SHA1) && !subFilterValue.equals(CADES_SUBFILTER_VALUE)) {
			return true;
		}
		return false;
	}

	/**
	 * Method that obtains the signature message contained inside of a signature dictionary of a PDF document.
	 * @param dictionary Parameter that represents the signature dictionary.
	 * @param signatureName Parameter that represents the name of the signature dictionary.
	 * @return an object that represents the signature message.
	 * @throws UtilsException If the method fails.
	 */
	public static CMSSignedData getCMSSignature(PdfDictionary dictionary, String signatureName) throws UtilsException {
		// Comprobamos que se ha indicado el diccionario de firma
		if (dictionary == null) {
			throw new IllegalArgumentException("Error en parámetro de entrada: No se ha indicado el diccionario de firma");
		}
		// Metemos en una variable el contenido de la clave
		// /Contents, o
		// lo que es lo mismo, la firma
		byte[ ] contents = dictionary.getAsString(PdfName.CONTENTS).getOriginalBytes();
		try {
			// Obtenemos los datos firmados
			CMSSignedData signedData = new CMSSignedData(contents);

			// Comprobamos que la firma tiene al menos un firmante
			if (signedData.getSignerInfos().getSigners().size() == 0) {
				throw new UtilsException("La firma asociada al diccionario de firma [" + signatureName + "] no posee ningún un firmante.");
			}
			// Devolvemos los datos firmados
			return signedData;
		} catch (CMSException e) {
			throw new UtilsException("No ha sido posible obtener los datos de la firma contenida en el diccionario de firma [" + signatureName + "].", e);
		}
	}
}
