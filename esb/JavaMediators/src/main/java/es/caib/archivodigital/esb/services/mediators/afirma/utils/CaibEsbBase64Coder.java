package es.caib.archivodigital.esb.services.mediators.afirma.utils;

import org.apache.commons.codec.binary.Base64;

import es.caib.archivodigital.esb.services.mediators.afirma.i18n.CaibEsbLanguage;
import es.gob.afirma.i18n.ILogConstantKeys;
import es.gob.afirma.transformers.TransformersException;

/**
 * Clase de utilidaddes para la codificación de archivos en base 64.
 */
public final class CaibEsbBase64Coder {

	/**
	 * Constructor method for the class Base64Coder.java.
	 */
	private CaibEsbBase64Coder() {
	}

	/**
	 * Method that encodes data on Base64.
	 * @param data Parameter that represents the data to encode.
	 * @return the data encoded on Base64.
	 * @throws TransformersException If the method fails.
	 */
	public static byte[ ] encodeBase64(byte[ ] data) throws TransformersException {
		if (data == null) {
			throw new TransformersException(CaibEsbLanguage.getResIntegra(ILogConstantKeys.BC_LOG001));
		}
		try {
			byte[ ] result = Base64.encodeBase64(data);
			return result == null ? data : result;
		} catch (Exception e) {
			throw new TransformersException(CaibEsbLanguage.getResIntegra(ILogConstantKeys.BC_LOG002), e);
		}
	}

	/**
	 * Method that decodes data encoded on Base64.
	 * @param data Parameter that represents the data to decode.
	 * @return the decoded data.
	 * @throws TransformersException If the method fails.
	 */
	public static byte[ ] decodeBase64(byte[ ] data) throws TransformersException {
		return Base64.decodeBase64(data);
	}

	/**
	 * Method that checks if data is encoded on Base64.
	 * @param data Parameter that represents the data to check.
	 * @return a boolean that defines if the data is encoded on Base64 (true) or not (false).
	 */

	public static boolean isBase64Encoded(byte[ ] data) {
		return Base64.isArrayByteBase64(data);
	}

	/**
	 * Method that encodes a string on Base64.
	 * @param data Parameter that represents the string to encode.
	 * @return the string encoded on Base64.
	 * @throws TransformersException If the method fails.
	 */
	public static String encodeBase64(String data, String charsetName) throws TransformersException {
		if (data == null) {
			throw new TransformersException(CaibEsbLanguage.getResIntegra(ILogConstantKeys.BC_LOG001));
		}
		try {
			return new String(Base64.encodeBase64(data.getBytes(charsetName)),charsetName);
		} catch (Exception e) {
			throw new TransformersException(e);
		}
	}

	/**
	 * Method that decodes a string encoded on Base64.
	 * @param data Parameter that represents the string to decode.
	 * @return the decoded string.
	 * @throws TransformersException If the method fails.
	 */
	public static String decodeBase64(String data, String charsetName) throws TransformersException {
		if (data == null) {
			throw new TransformersException(CaibEsbLanguage.getResIntegra(ILogConstantKeys.BC_LOG001));
		}
		try {
			return new String(Base64.decodeBase64(data.getBytes(charsetName)),charsetName);
		} catch (Exception e) {
			throw new TransformersException(e);
		}
	}

}
