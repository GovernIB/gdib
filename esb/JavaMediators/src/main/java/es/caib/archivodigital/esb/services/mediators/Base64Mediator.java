package es.caib.archivodigital.esb.services.mediators;

import org.apache.log4j.Logger;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import es.caib.archivodigital.esb.services.mediators.afirma.utils.CaibEsbBase64Coder;
import es.gob.afirma.transformers.TransformersException;


public class Base64Mediator extends AbstractMediator  {

	/**
	 *  Attribute that represents the object that manages the log of the class.
	 */
	private static final Logger LOGGER = Logger.getLogger(Base64Mediator.class);

	private static final String DECODE_BASE64_OPERATION = "decode";
	private static final String ENCODE_BASE64_OPERATION = "encode";	
	private static final String BASE64_SOURCE_PARAM_NAME = "base64Source";
	private static final String BASE64_RES_PARAM_NAME = "base64Res";
	private static final String DEFAULT_CHARSET = "UTF-8";
	
	private String operation;
	private String charset = DEFAULT_CHARSET;
	
	@Override
	public boolean mediate(MessageContext synCtx) {
		boolean res = Boolean.FALSE;
		String base64Res,base64Source;
		try {			
			base64Source = (String) synCtx.getProperty(BASE64_SOURCE_PARAM_NAME);
			
			LOGGER.debug("base64Source: " + base64Source);
			
			if(base64Source != null && operation != null){
				if(DECODE_BASE64_OPERATION.equalsIgnoreCase(operation)){
					base64Res = CaibEsbBase64Coder.decodeBase64(base64Source,charset);
					synCtx.setProperty(BASE64_RES_PARAM_NAME,base64Res);
					res = Boolean.TRUE;
				} else if(ENCODE_BASE64_OPERATION.equalsIgnoreCase(operation)){
					base64Res = CaibEsbBase64Coder.encodeBase64(base64Source,charset);
					synCtx.setProperty(BASE64_RES_PARAM_NAME,base64Res);
					res = Boolean.TRUE;
				}
			} else {
				LOGGER.error("[Mediador Base64] - No se informo la cadena a codificar/descodificar y/o la operación a realizar.");
			}
		} catch (TransformersException e) {
			LOGGER.error("[Mediador Base64] - Se produjo un error al codificar/descodificar en base64. Mensaje de error: " + e.getMessage());
		} catch(Exception e){
			String excMsg = "[Mediador Base64] Error al codificar/descodificar en base64: " + e.getMessage();
			LOGGER.error(excMsg,e);
		}
		
		return res;
	}


	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}


	public String getCharset() {
		return charset;
	}


	public void setCharset(String charset) {
		this.charset = charset;
	}
}
