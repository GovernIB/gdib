package es.caib.archivodigital.esb.services.mediators.afirma.transformers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

import es.caib.archivodigital.esb.services.mediators.afirma.i18n.CaibEsbLanguage;
import es.gob.afirma.i18n.ILogConstantKeys;
import es.gob.afirma.transformers.TransformersConstants;
import es.gob.afirma.transformers.TransformersException;

/**
 * Clase responsable de la lectura del archivo de configuración transformers.properties.
 */
public final class CaibEsbTransformersProperties {

	/**
	 * Atributo para la escritura de trazas de ejecución.
	 */
	private static Logger logger = Logger.getLogger(CaibEsbTransformersProperties.class);
	
	/**
	 * Conjunto de parámetros de configuración.
	 */
	private static Properties properties = new Properties();
	
	/**
	 * Constructor method for the class TransformersProperties.java.
	 */
	private CaibEsbTransformersProperties() {
	}

	static {
		init();
	}

	/**
	 * Gets the value of the attribute {@link #properties}.
	 * @return the value of the attribute {@link #properties}.
	 */
	public static Properties getTransformersProperties() {
		init();
		return properties;
	}

	/**
	 * Method that initializes {@link #properties} with all the related properties.
	 */
	private static synchronized void init() {

		try {
			
			if(properties == null || (properties != null && properties.size() == 0)){
				logger.debug(CaibEsbLanguage.getResIntegra(ILogConstantKeys.TP_LOG001));
				
				properties.load(getPropertiesResource());

				logger.debug(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.TP_LOG002, new Object[ ] { properties }));
			}
		} catch (TransformersException e) {
			String errorMsg = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.TP_LOG004, new Object[ ] { TransformersConstants.TRANSFORMERS_FILE_PROPERTIES });
			logger.error(errorMsg, e);
			properties = new Properties();
		} catch (IOException e) {
			String errorMsg = CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.TP_LOG004, new Object[ ] { TransformersConstants.TRANSFORMERS_FILE_PROPERTIES });
			logger.error(errorMsg, e);
			properties = new Properties();
		}
	}

	/**
	 * Method that obtains {@link #properties} as a input stream.
	 * @return {@link #properties} as a file
	 * @throws URISyntaxException If the method fails.
	 */
	private static InputStream getPropertiesResource() throws TransformersException {
		InputStream res;

		res = CaibEsbTransformersProperties.class.getResourceAsStream("/" + TransformersConstants.TRANSFORMERS_FILE_PROPERTIES);
		if (res == null) {
			throw new TransformersException(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.TP_LOG005, new Object[ ] { TransformersConstants.TRANSFORMERS_FILE_PROPERTIES }));
		}

		return res;
	}

	/**
	 * Method that obtains the properties related to the transform of request messages.
	 * @param serviceName Parameter that represents the name of the web service defined to obtain the related properties.
	 * @param method Parameter that represents the name of the method.
	 * @param version Parameter that represents the version of the web service.
	 * @return the set of related properties.
	 */
	public static Properties getMethodRequestTransformersProperties(String serviceName, String method, String version) {
		Properties res;

		logger.debug(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.TP_LOG006, new Object[ ] { serviceName }));

		res = getMethodTransformersProperties(serviceName, method, version, TransformersConstants.REQUEST_CTE);

		return res;
	}

	/**
	 * Method that obtains the properties related to the transform of response messages.
	 * @param serviceName Parameter that represents the name of the web service defined to obtain the related properties.
	 * @param method Parameter that represents the name of the method.
	 * @param version Parameter that represents the version of the web service.
	 * @return the set of related properties.
	 */
	public static Properties getMethodResponseTransformersProperties(String serviceName, String method, String version) {
		Properties res;

		logger.debug(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.TP_LOG007, new Object[ ] { serviceName }));

		res = getMethodTransformersProperties(serviceName, method, version, TransformersConstants.RESPONSE_CTE);

		return res;
	}

	/**
	 * Method that obtains the properties related to the parser of request and response messages.
	 * @param serviceName Parameter that represents the name of the web service defined to obtain the related properties.
	 * @param method Parameter that represents the name of the method.
	 * @param version Parameter that represents the version of the web service.
	 * @return the set of related properties.
	 */
	public static Properties getMethodParseTransformersProperties(String serviceName, String method, String version) {
		Properties res;

		logger.debug(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.TP_LOG008, new Object[ ] { serviceName }));

		res = getMethodTransformersProperties(serviceName, method, version, TransformersConstants.PARSER_CTE);

		return res;
	}

	/**
	 * Method that obtains the properties related to the transformation of XML parameters for a web service.
	 * @param serviceName Parameter that represents the name of the web service defined to obtain the related properties.
	 * @param method Parameter that represents the name of the method.
	 * @param version Parameter that represents the version of the web service.
	 * @return the set of related properties.
	 */
	public static Properties getMethodTransformersProperties(String serviceName, String method, String version) {
		Properties res;

		logger.debug(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.TP_LOG008, new Object[ ] { serviceName }));

		res = getMethodTransformersProperties(serviceName, method, version, null);

		return res;
	}

	/**
	 * Method that obtains the properties related to the input parameters.
	 * @param serviceName Parameter that represents the name of the web service defined to obtain the related properties.
	 * @param method Parameter that represents the name of the method.
	 * @param version Parameter that represents the version of the web service.
	 * @param type Parameter that represents the type of the elements to include on the properties to retrieve.
	 * @return the set of related properties.
	 */
	private static Properties getMethodTransformersProperties(String serviceName, String method, String version, String type) {
		Enumeration<?> enumeration;
		Properties res;
		String header, key;

		res = new Properties();
		header = serviceName + "." + method + "." + version + "." + (type == null ? "" : type);
		enumeration = getTransformersProperties().propertyNames();

		while (enumeration.hasMoreElements()) {
			key = (String) enumeration.nextElement();

			if (key.startsWith(header)) {
				res.put(key, properties.getProperty(key));
			}
		}

		logger.debug(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.TP_LOG002, new Object[ ] { res }));

		return res;
	}
}
