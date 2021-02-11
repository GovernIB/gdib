package es.caib.gdib.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.common.types.SignatureValidationReport;
import es.caib.gdib.ws.common.types.XmlSignatureMode;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.SignatureService;
import es.gob.afirma.integraFacade.pojo.DetailLevelEnum;
import es.gob.afirma.integraFacade.pojo.OptionalParameters;
import es.gob.afirma.integraFacade.pojo.VerificationReport;


/**
 * Clase responsable de efectuar una firma electr�nica XAdES v1.3.2 extendida de archivado (A), sobre un �ndice electr�nico interno del Govern de les Illes Balears v1.0.
 * 
 * @author RICOH
 *
 */
public class CaibIndexV10SignerImpl implements AdministrativeProcessingIndexSigner {

	private static final Logger LOGGER =  Logger.getLogger(CaibIndexV10SignerImpl.class);
	
	//Constantes que representan nodos del �ndice electr�nico relacionados 
	//con la generaci�n de la firma electr�nica	
	private static final String CAIB_INDEX_NAMESPACE ="urn:es:caib:archivodigital:gestiondocumental:expediente-e:indice-e:1.0";

	private static final String DIGITAL_SIGNATURE_NAMESPACE ="http://www.w3.org/2000/09/xmldsig#";
	
	private static final String CAIB_SIGNATURE_XML_ELEMENT = "firma";
    
    private static final String CAIB_SIGNATURE_TYPE_XML_ELEMENT = "tipoFirma";
        
    private static final String CAIB_SIGNATURE_CONTENT_XML_ELEMENT = "contenidoFirma";
    
    private static final String DS_SIGNATURE_XML_ELEMENT = "Signature";
	
    private static final String INDEX_CHARSET = "UTF-8";

    private static final String DEFAULT_CAIB_SIGNATURE_TYPE_XML_ELEMENT_VALUE = "TF01";
    
    private static final SignatureFormat DEFAULT_CAIB_INDEX_SIGNATURE_FORMAT = SignatureFormat.XAdES_A;
    
    private static final String DEFAULT_SIGNATURE_POLICY_ID = null;
    
    private static final XmlSignatureMode DEFAULT_XADES_SIGNATURE_MODE = XmlSignatureMode.ENVELOPED;
    
    private static final Boolean DEFAULT_MOVE_SIGNATURE_ELEMENT = Boolean.TRUE;
    
    /**
     * Formato de firma del la firma electr�nica del �ndice.
     */
    private SignatureFormat indexSignatureFormat;
	
    /**
     * Identificador de la pol�tica de firma
     */
    private String signaturePolicyId;
    
    /**
     * Modo en el que es generada una firma electr�nica XAdES.
     */
    private XmlSignatureMode xadesSignatureMode;
    
    /**
     * Tipo de firma que es informado en el �ndice.
     */
    private String signatureTypeValue;
    
    /**
     * Especifica si se desplazar� el nodo de firma.
     */
    private Boolean moveSignature;
    
    /**
     * Par�metros de configuraci�n.
     */
    private Properties confParams;
	
    /**
     * Servicio interno de firma.
     */
    private SignatureService signatureService;
    
    
    public CaibIndexV10SignerImpl(){
    	super();
    	this.indexSignatureFormat = DEFAULT_CAIB_INDEX_SIGNATURE_FORMAT;
    	this.signaturePolicyId = DEFAULT_SIGNATURE_POLICY_ID;
    	this.xadesSignatureMode = DEFAULT_XADES_SIGNATURE_MODE;
    	this.signatureTypeValue = DEFAULT_CAIB_SIGNATURE_TYPE_XML_ELEMENT_VALUE;
    	this.moveSignature = DEFAULT_MOVE_SIGNATURE_ELEMENT;
    	this.confParams = new Properties();
    }
    
    public CaibIndexV10SignerImpl(Properties confParams){
    	this();   
    	loadConfParameters(confParams);
    }
    
    private void loadConfParameters(Properties confParams){
    	this.confParams = new Properties();
    	String propValue;
    	
    	LOGGER.info("Utilidad de firma de indice inicializada con los siguientes par�metros de configuraci�n: " + confParams.toString());
    	
    	if(confParams != null && !confParams.isEmpty()){
    		this.confParams.putAll(confParams);
    		
    		propValue = this.confParams.getProperty(AdministrativeProcessingIndexSignerFactory.MIN_INDEX_SIGNATURE_FORM_PROP_NAME);		
    		if(propValue != null && !propValue.isEmpty()){
    			SignatureFormat sf;
				try {
					sf = SignatureUtils.eniSigntureFormatToInernalSignatureFormat(propValue);
					this.indexSignatureFormat = (sf==null?DEFAULT_CAIB_INDEX_SIGNATURE_FORMAT:sf);
				} catch (GdibException e) {
					this.indexSignatureFormat = DEFAULT_CAIB_INDEX_SIGNATURE_FORMAT;
				}
    		}

    		propValue = this.confParams.getProperty(AdministrativeProcessingIndexSignerFactory.SIGNATURE_POLICY_ID_PROP_NAME);		
    		if(propValue != null && !propValue.isEmpty()){
    			this.signaturePolicyId = propValue;
    		}
    		
    		propValue = this.confParams.getProperty(AdministrativeProcessingIndexSignerFactory.XADES_MODE_PROP_NAME);		
    		if(propValue != null && !propValue.isEmpty()){
    			XmlSignatureMode xsm = XmlSignatureMode.valueOf(propValue);
    			this.xadesSignatureMode = (xsm==null?DEFAULT_XADES_SIGNATURE_MODE:xsm);
    		}
    		
    		propValue = this.confParams.getProperty(AdministrativeProcessingIndexSignerFactory.SIGNATURE_TYPE_XML_ELEMENT_PROP_NAME);		
    		if(propValue != null && !propValue.isEmpty()){
    			this.signatureTypeValue = propValue;
    		}
    		
    		propValue = this.confParams.getProperty(AdministrativeProcessingIndexSignerFactory.MOVE_SIGNATURE_PROP_NAME);		
    		if(propValue != null && !propValue.isEmpty()){
    			try{    				
    				this.moveSignature = Boolean.valueOf(propValue);
    			} catch(Exception e){
    				this.moveSignature = DEFAULT_MOVE_SIGNATURE_ELEMENT;
    			}    			
    		}
    	}
    }
    
	@Override
	public byte[] generateIndexSignature(byte[] document, Map<String, Object> optionalParams) throws GdibException {
		byte[] res;
		
		res = preSingProcess(document);
		res = signatureService.signXadesDocument(res, this.indexSignatureFormat, this.xadesSignatureMode, this.signaturePolicyId);
		
		
		
		SignatureValidationReport resValide=  signatureService.verifySignature(null,res);
		
		
		if(this.moveSignature && XmlSignatureMode.ENVELOPED.equals(this.xadesSignatureMode)){
			res = postSingProcess(res);
		}
		
		return res;
	}

    /**
	 * A�ade el nodo XML caibexpind:firma al nodo ra�z del documento, previo a la realizaci�n de la firma del �ndice electr�nico, estableciendo el tipo de firma TF01 y un caracter espacio en blanco como contenido de la firma. 
	 * @param document documento XML que representa el �ndice electr�nico de un expediente. 
	 * @return array de bytes que representa el contenido del documento XML modificado.
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
	 * @throws Exception si ocurre algún error al a�adir el nodo XML caibexpind:firma al nodo ra�z del documento.
	 */
	private byte[] preSingProcess(byte [] document) throws GdibException {
		byte [] res = null;
		Map<String,String> prefixes = new HashMap<String,String>();
		String excMsg;
		try{
			Document doc = XmlUtils.byteArrayToXmlDocument(document);

			LOGGER.debug("Se inicia la incorporación del nodo " + CAIB_SIGNATURE_CONTENT_XML_ELEMENT + 
					", con contenido vacío, al nodo raíz del documento.");
			Element rootNode = doc.getDocumentElement();
			XmlUtils.getPrefixesRecursive(rootNode, prefixes);
			
			NodeList signatureNodeList = rootNode.getElementsByTagNameNS(CAIB_INDEX_NAMESPACE,CAIB_SIGNATURE_XML_ELEMENT);
			
			if(signatureNodeList.getLength() > 0){
				excMsg = "Se encontro al menos un elemento XML /IndiceElectronico/firma. "
						+ "No debe existir ninguno previo a la generaci�n de la firma.";
				LOGGER.error(excMsg);
				throw new GdibException(excMsg);
			}
			LOGGER.debug("Se genera el nodo " + CAIB_SIGNATURE_CONTENT_XML_ELEMENT + 
					", con contenido vacío.");
			String caibNamespacePrefix = (prefixes.get(CAIB_INDEX_NAMESPACE) == null?"":prefixes.get(CAIB_INDEX_NAMESPACE)+":");
			Element signatureTypeNode = doc.createElementNS(CAIB_INDEX_NAMESPACE,caibNamespacePrefix+CAIB_SIGNATURE_TYPE_XML_ELEMENT);
			signatureTypeNode.setTextContent(this.signatureTypeValue);
			Element signatureContentNode = doc.createElementNS(CAIB_INDEX_NAMESPACE,caibNamespacePrefix+CAIB_SIGNATURE_CONTENT_XML_ELEMENT);
			signatureContentNode.setTextContent(" ");
	
			Element signatureNode = doc.createElementNS(CAIB_INDEX_NAMESPACE,caibNamespacePrefix+CAIB_SIGNATURE_XML_ELEMENT);
			signatureNode.appendChild(signatureTypeNode);
			signatureNode.appendChild(signatureContentNode);
			
			LOGGER.debug("Nodo " + CAIB_SIGNATURE_CONTENT_XML_ELEMENT + 
					", con contenido vacío, generado correctamente.");
			LOGGER.debug("Se a�ade el nodo " + CAIB_SIGNATURE_CONTENT_XML_ELEMENT + 
					", con contenido vacío, como hijo del nodo ra�z del documento.");
			rootNode.appendChild(signatureNode);
			LOGGER.debug("Añadido el nodo " + CAIB_SIGNATURE_CONTENT_XML_ELEMENT + 
					", con contenido vacío, como hijo del nodo ra�z del documento.");
			
			LOGGER.debug("Actualizando contenido documento XML en memoria.");
			//Se transforma el XML a un array de bytes
			res = XmlUtils.xmlDocumentToByteArray(doc,INDEX_CHARSET);
			
			LOGGER.debug("Finalizada la incorporación del nodo " + CAIB_SIGNATURE_CONTENT_XML_ELEMENT + 
					", con contenido vacío, al nodo raíz del documento con éxito.");
		
		} catch (TransformerException | UnsupportedEncodingException e){
			excMsg = "Se produjo un error al transformar un documento XML en array de bytes: " + e.getMessage();
			LOGGER.error(excMsg,e);
			throw new GdibException(excMsg,e);
		} catch(ParserConfigurationException | SAXException | IOException e){
			excMsg = "Se produjo un error al transformar un array de bytes en documento XML: " + e.getMessage();
			LOGGER.error(excMsg,e);
			throw new GdibException(excMsg,e);	
		} catch(GdibException e){
			throw e;
		}
		
		return res;
	}
    
	/**
	 * Traslada el nodo ds:Signature desde el nodo ra�z del documento al nodo caibexpind:IndiceElectronico/caibexpind:firma/caibexpind:contenidoFirma. 
	 * @param signature documento XML que representa el �ndice electr�nico de un expedeinte e incluye la firma XAdES-A v1.3.2 enveloped como hijo del 
	 * nodo ra�z del documento.
	 * @return array de bytes que representa el contenido del documento XML modificado.
	 * @throws GdibException si ocurre algún error al trasladar el nodo ds:Signature.
	 */
	private byte[] postSingProcess(byte [] signature) throws GdibException {
		byte [] res;
		Map<String,String> prefixes = new HashMap<String,String>();
		String excMsg;
		
		try{
			LOGGER.debug("Se inicia el traslado del nodo " + DS_SIGNATURE_XML_ELEMENT + " a su ruta definitiva: " + 
					CAIB_SIGNATURE_CONTENT_XML_ELEMENT + ".");
			Document doc = XmlUtils.byteArrayToXmlDocument(signature);	
			
			Element rootNode = doc.getDocumentElement();
			XmlUtils.getPrefixesRecursive(rootNode, prefixes);
			
			LOGGER.debug("Obteniendo el nodo " + DS_SIGNATURE_XML_ELEMENT + ".");
			String dsNamespacePrefix = (prefixes.get(DIGITAL_SIGNATURE_NAMESPACE) == null?"":prefixes.get(DIGITAL_SIGNATURE_NAMESPACE)+":");
			NodeList signatureNodeList = rootNode.getElementsByTagName(dsNamespacePrefix+DS_SIGNATURE_XML_ELEMENT);
			
			if(signatureNodeList.getLength() < 1){
				excMsg = "No se encontro el elemento XML " + DS_SIGNATURE_XML_ELEMENT + ".";
				LOGGER.error(excMsg);
				throw new GdibException(excMsg);
			}
			
			Element signatureNode = (Element) signatureNodeList.item(0);
			
			LOGGER.debug("Nodo " + signatureNode.getTagName() + " recuperado del documento.");
			LOGGER.debug("Obteniendo el nodo " + CAIB_SIGNATURE_CONTENT_XML_ELEMENT + ".");
			String caibNamespacePrefix = (prefixes.get(CAIB_INDEX_NAMESPACE) == null?"":prefixes.get(CAIB_INDEX_NAMESPACE)+":");
			NodeList signatureContentNodeList = rootNode.getElementsByTagName(caibNamespacePrefix+CAIB_SIGNATURE_CONTENT_XML_ELEMENT);
			
			if(signatureContentNodeList.getLength() < 1){
				excMsg = "No se encontro el elemento XML " + CAIB_SIGNATURE_CONTENT_XML_ELEMENT + ".";
				LOGGER.error(excMsg);
				throw new GdibException(excMsg);
			}
			
			Element signatureContentNode = (Element) signatureContentNodeList.item(0);
					
			LOGGER.debug("Nodo " + signatureNode.getTagName() + " recuperado del documento.");
			
			//Se añade el nodo /caibexpind:IndiceElectronico/ds:Signature al nodo 
			// /caibexpind:IndiceElectronico/caibexpind:firma/caibexpind:contenidoFirma
			Element signatureCloneNode = (Element) signatureNode.cloneNode(true);
			
			LOGGER.debug("Añadiendo nodo " + DS_SIGNATURE_XML_ELEMENT + " como hijo del nodo " + CAIB_SIGNATURE_CONTENT_XML_ELEMENT + ".");
			signatureContentNode.appendChild(signatureCloneNode);
			
			LOGGER.debug("Eliminando nodo " + DS_SIGNATURE_XML_ELEMENT + " como hijo del nodo ra�z del documento.");
			//Se elimina el nodo /caibexpind:IndiceElectronico/ds:Signature
			rootNode.removeChild(signatureNode);
	
			LOGGER.debug("Actualizando contenido documento XML en memoria.");
			res = XmlUtils.xmlDocumentToByteArray(doc,INDEX_CHARSET);
			//Se transforma el XML a un array de bytes
			
			LOGGER.debug("Traslado del nodo " + DS_SIGNATURE_XML_ELEMENT + " a su ruta definitiva, " + 
					CAIB_SIGNATURE_CONTENT_XML_ELEMENT + ", finalizado con éxito.");
		} catch (TransformerException | UnsupportedEncodingException e){
			excMsg = "Se produjo un error al transformar un documento XML en array de bytes: " + e.getMessage();
			LOGGER.error(excMsg,e);
			throw new GdibException(excMsg,e);
		} catch(ParserConfigurationException | SAXException | IOException e){
			excMsg = "Se produjo un error al transformar un array de bytes en documento XML: " + e.getMessage();
			LOGGER.error(excMsg,e);
			throw new GdibException(excMsg,e);	
		} catch(GdibException e){
			throw e;
		}
		
		return res;
	}

	public SignatureService getSignatureService() {
		return signatureService;
	}

	@Override
	public void setSignatureService(SignatureService signatureService) {
		this.signatureService = signatureService;
	}

	@Override
	public void setConfParameters(Properties confParameters) {
		loadConfParameters(confParams);
	}
}
