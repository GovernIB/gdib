package es.caib.gdib.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.common.types.XmlSignatureMode;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.SignatureService;


public class EniIndexV10SignerImpl implements AdministrativeProcessingIndexSigner {

	private static final Logger LOGGER =  Logger.getLogger(EniIndexV10SignerImpl.class);
	
	private static final String ENI_DIGITAL_SIGNATURE_NAMESPACE ="http://administracionelectronica.gob.es/ENI/XSD/v1.0/firma";
	
	private static final String ENI_ELECTRONIC_INDEX_NAMESPACE ="http://administracionelectronica.gob.es/ENI/XSD/v1.0/expediente-e/indice-e";
	
	private static final String DIGITAL_SIGNATURE_NAMESPACE ="http://www.w3.org/2000/09/xmldsig#";
	
	private static final String ENI_INDEX_XML_ELEMENT = "indice";
	
	private static final String ENI_SIGNATURES_XML_ELEMENT = "firmas";
	
	private static final String ENI_SIGNATURE_XML_ELEMENT = "firma";
	
	private static final String ENI_SIGNATURE_XML_ID_ATT = "Id";
	
	private static final String ENI_SIGNATURE_XML_REF_ATT = "ref";
    
    private static final String ENI_SIGNATURE_TYPE_XML_ELEMENT = "TipoFirma";
    
    private static final String ENI_SIGNATURE_CONTENT_XML_ELEMENT = "ContenidoFirma";
    
    private static final String ENI_CERT_SIGNATURE_CONTENT_XML_ELEMENT = "FirmaConCertificado";
    
    private static final String DS_SIGNATURE_XML_ELEMENT = "Signature";
	
    private static final String INDEX_CHARSET = "UTF-8";
    
    private static final String DOC_MAP_ENTRY = "doc";
    
    private static final String ID_MAP_ENTRY = "id";

    private static final String DEFAULT_ENI_SIGNATURE_TYPE_XML_ELEMENT_VALUE = "TF03";
    
    private static final SignatureFormat DEFAULT_ENI_INDEX_SIGNATURE_FORMAT = SignatureFormat.XAdES_T;
   
    private static final String DEFAULT_SIGNATURE_POLICY_ID = null;
    
    private static final XmlSignatureMode DEFAULT_XADES_SIGNATURE_MODE = XmlSignatureMode.ENVELOPED;
    
    private static final Boolean DEFAULT_MOVE_SIGNATURE_ELEMENT = Boolean.TRUE;
    
    /**
     * Formato de firma del la firma electrónica del índice.
     */
    private SignatureFormat indexSignatureFormat;
	
    /**
     * Identificador de la política de firma
     */
    private String signaturePolicyId;
    
    /**
     * Modo en el que es generada una firma electrónica XAdES.
     */
    private XmlSignatureMode xadesSignatureMode;
    
    /**
     * Servicio interno de firma.
     */
    private SignatureService signatureService;
    
    /**
     * Tipo de firma que es informado en el índice.
     */
    private String signatureTypeValue;
    
    /**
     * Especifica si se desplazará el nodo de firma.
     */
    private Boolean moveSignature;
    
    /**
     * Parámetros de configuración.
     */
    private Properties confParams;
    
    public EniIndexV10SignerImpl(){
    	super();
    	this.indexSignatureFormat = DEFAULT_ENI_INDEX_SIGNATURE_FORMAT;
    	this.signaturePolicyId = DEFAULT_SIGNATURE_POLICY_ID;
    	this.xadesSignatureMode = DEFAULT_XADES_SIGNATURE_MODE;
    	this.signatureTypeValue = DEFAULT_ENI_SIGNATURE_TYPE_XML_ELEMENT_VALUE;
    	this.moveSignature = DEFAULT_MOVE_SIGNATURE_ELEMENT;
    	this.confParams = new Properties();
    }
    
    public EniIndexV10SignerImpl(Properties confParams){
    	this();   
    	loadConfParameters(confParams);
    }
    
    private void loadConfParameters(Properties confParams){
    	this.confParams = new Properties();
    	String propValue;
    	
    	LOGGER.info("Utilidad de firma de indice inicializada con los siguientes parámetros de configuración: " + confParams.toString());
    	
    	if(confParams != null && !confParams.isEmpty()){
    		this.confParams.putAll(confParams);
    		
    		propValue = this.confParams.getProperty(AdministrativeProcessingIndexSignerFactory.MIN_INDEX_SIGNATURE_FORM_PROP_NAME);		
    		if(propValue != null && !propValue.isEmpty()){
    			SignatureFormat sf;
				try {
					sf = SignatureUtils.eniSigntureFormatToInernalSignatureFormat(propValue);
					this.indexSignatureFormat = (sf==null?DEFAULT_ENI_INDEX_SIGNATURE_FORMAT:sf);
				} catch (GdibException e) {
					this.indexSignatureFormat = DEFAULT_ENI_INDEX_SIGNATURE_FORMAT;
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
		Map<String,Object> preSingProcessRes;
		String signatureIdAttValue;
		
		preSingProcessRes = preSingProcess(document,optionalParams);
		res = (byte[]) preSingProcessRes.get(DOC_MAP_ENTRY);
		
		res = signatureService.signXadesDocument(res, this.indexSignatureFormat, this.xadesSignatureMode, this.signaturePolicyId);
		
		if(this.moveSignature && XmlSignatureMode.ENVELOPED.equals(this.xadesSignatureMode)){
			signatureIdAttValue = (String) preSingProcessRes.get(ID_MAP_ENTRY);
			res = postSingProcess(res,signatureIdAttValue);
		}
		
		return res;
	}

    /**
	 * Añade un nodo XML enids:firmas/enids:firma al nodo raíz del documento, previo a la realización de la firma del índice electrónico, 
	 * estableciendo el tipo de firma TF03 y un caracter espacio en blanco como contenido de la firma. 
	 * @param document documento XML que representa el índice electrónico de un expediente.
	 * @param optionalParams configuración adicional. 
	 * @return mapa con dos entradas que incluyen el documento procesado para ser firmado ("doc"), y el identificador del 
	 * nodo enids:firma que incluirá la firma generada ("id"), respectivamente.
	 * @throws GdibException si ocurre algún error al añadir el nodo XML caibexpind:firma al nodo raíz del documento.
	 */
	private Map<String,Object> preSingProcess(byte [] document, Map<String, Object> optionalParams) throws GdibException {
		byte [] updatedDoc = null;
		Element signaturesNode;
		Map<String,String> prefixes = new HashMap<String,String>();
		Map<String,Object> res;		
		String excMsg,idAttValue,refAttValue;
		
		res = new HashMap<String,Object>();
		
		try{
			Document doc = XmlUtils.byteArrayToXmlDocument(document);
			Element rootNode = doc.getDocumentElement();
			XmlUtils.getPrefixesRecursive(rootNode, prefixes);
			
			String eniIndexNamespacePrefix = (prefixes.get(ENI_ELECTRONIC_INDEX_NAMESPACE) == null?"":prefixes.get(ENI_ELECTRONIC_INDEX_NAMESPACE)+":");
			NodeList indexNodeList = rootNode.getElementsByTagName(eniIndexNamespacePrefix+ENI_INDEX_XML_ELEMENT);
			
			if(indexNodeList == null || (indexNodeList != null && indexNodeList.getLength() != 1)){
				excMsg = "Se encontraron " + (indexNodeList == null?"0":indexNodeList.getLength()) + " elementos " + ENI_INDEX_XML_ELEMENT + ", "
						+ "pero el esquema ENI solo admite la existencia de un único nodo "+ENI_INDEX_XML_ELEMENT+".";
				LOGGER.error(excMsg);
				throw new GdibException(excMsg);				
			}
			
			Element eniIndexNode = (Element) indexNodeList.item(0);
			
			LOGGER.debug("Se inicia la incorporación del nodo " + ENI_SIGNATURES_XML_ELEMENT + 
					", con contenido vacío, al nodo " + eniIndexNode.getTagName() + " del documento.");
			
			signaturesNode = null;
			String eniSignNamespacePrefix = (prefixes.get(ENI_DIGITAL_SIGNATURE_NAMESPACE) == null?"":prefixes.get(ENI_DIGITAL_SIGNATURE_NAMESPACE)+":");
			
			NodeList signaturesNodeList = rootNode.getElementsByTagName(eniSignNamespacePrefix+ENI_SIGNATURES_XML_ELEMENT);
			
			if(signaturesNodeList.getLength() == 1){
				LOGGER.debug("Se encontro al menos un elemento XML " + ENI_SIGNATURES_XML_ELEMENT + "."
						+ "No es la primera firma del índice, no requiere la creación del nodo.");
				signaturesNode = (Element) signaturesNodeList.item(0);
			} else if(signaturesNodeList.getLength() > 1){
				excMsg = "Se encontro mas de un elemento XML " + ENI_SIGNATURES_XML_ELEMENT + ". "
						+ "Puede existir un nodo como máximo previo a la generación de la firma.";
				LOGGER.error(excMsg);
				throw new GdibException(excMsg);				
			} else {
				signaturesNode = doc.createElementNS(ENI_DIGITAL_SIGNATURE_NAMESPACE,eniSignNamespacePrefix+ENI_SIGNATURES_XML_ELEMENT);
			}
			
			
			
			LOGGER.debug("Se genera el nodo " + ENI_SIGNATURE_XML_ELEMENT + 
					", con contenido vacío.");
			Element signatureTypeNode = doc.createElementNS(ENI_DIGITAL_SIGNATURE_NAMESPACE,eniSignNamespacePrefix+ENI_SIGNATURE_TYPE_XML_ELEMENT);
			signatureTypeNode.setTextContent(this.signatureTypeValue);
			Element certSignatureContentNode = doc.createElementNS(ENI_DIGITAL_SIGNATURE_NAMESPACE,eniSignNamespacePrefix+ENI_CERT_SIGNATURE_CONTENT_XML_ELEMENT);
			certSignatureContentNode.setTextContent(" ");
			Element signatureContentNode = doc.createElementNS(ENI_DIGITAL_SIGNATURE_NAMESPACE,eniSignNamespacePrefix+ENI_SIGNATURE_CONTENT_XML_ELEMENT);
			signatureContentNode.appendChild(certSignatureContentNode);
	
			Element signatureNode = doc.createElementNS(ENI_DIGITAL_SIGNATURE_NAMESPACE,eniSignNamespacePrefix+ENI_SIGNATURE_XML_ELEMENT);
			idAttValue = UUID.randomUUID().toString();
			signatureNode.setAttribute(ENI_SIGNATURE_XML_ID_ATT, ConstantUtils.SIGNATURE_ID_ATT_PREFIX+idAttValue);
			refAttValue = (String) optionalParams.get(ConstantUtils.INDEX_ID_ATT_KEY);
			LOGGER.debug("Identificador del nodo a firmar: " + refAttValue + ".");
			if(refAttValue != null){
				signatureNode.setAttribute(ENI_SIGNATURE_XML_REF_ATT, "#"+refAttValue);
			}
			signatureNode.appendChild(signatureTypeNode);
			signatureNode.appendChild(signatureContentNode);
			
			LOGGER.debug("Nodo " + ENI_SIGNATURE_XML_ELEMENT + 
					", con contenido vacío, generado correctamente.");
			LOGGER.debug("Se añade el nodo " + ENI_SIGNATURE_XML_ELEMENT + 
					", con contenido vacío, como hijo del nodo "+ENI_SIGNATURES_XML_ELEMENT+".");
			signaturesNode.appendChild(signatureNode);
			LOGGER.debug("Añadido el nodo " + ENI_SIGNATURE_XML_ELEMENT + 
					", con contenido vacío, como hijo del nodo "+ENI_SIGNATURES_XML_ELEMENT+".");
			LOGGER.debug("Se añade el nodo " + ENI_SIGNATURES_XML_ELEMENT + 
					", con contenido vacío, como hijo del nodo " + eniIndexNode.getTagName() +" del documento.");
			eniIndexNode.appendChild(signaturesNode);
			LOGGER.debug("Añadido el nodo " + ENI_SIGNATURES_XML_ELEMENT + 
					", con contenido vacío, como hijo del nodo raíz del documento.");
			
			LOGGER.debug("Actualizando contenido documento XML en memoria.");
			//Se transforma el XML a un array de bytes
			updatedDoc = XmlUtils.xmlDocumentToByteArray(doc,INDEX_CHARSET);
			
			LOGGER.debug("Finalizada la incorporación del nodo " + ENI_SIGNATURES_XML_ELEMENT + 
					", con contenido vacío, al nodo raíz del documento con éxito.");
			
			res.put(ID_MAP_ENTRY,ConstantUtils.SIGNATURE_ID_ATT_PREFIX+idAttValue);
			res.put(DOC_MAP_ENTRY,updatedDoc);
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
	 * Traslada el nodo ds:Signature desde el nodo raíz del documento al nodo eniexpind:indice/enids:firmas/enids:firma/enids:FirmaConCertificado. 
	 * @param signature documento XML que representa el índice electrónico de un expedeinte e incluye la firma XAdES-A v1.3.2 enveloped como hijo del 
	 * nodo raíz del documento.
	 * @param signatureIdAttValue identificador del nodo enids:firma donde debe ser incluida la firma electrónica generada.
	 * @return array de bytes que representa el contenido del documento XML modificado.
	 * @throws GdibException si ocurre algún error al trasladar el nodo ds:Signature.
	 */
	private byte[] postSingProcess(byte [] signature, String signatureIdAttValue) throws GdibException {
		byte [] res;
		Boolean signatureFound;
		Map<String,String> prefixes = new HashMap<String,String>();
		String excMsg;
		
		try{
			LOGGER.debug("Se inicia el traslado del nodo " + DS_SIGNATURE_XML_ELEMENT + " a su ruta definitiva: " + 
					ENI_SIGNATURES_XML_ELEMENT + "/" + ENI_SIGNATURE_XML_ELEMENT + ".");
			Document doc = XmlUtils.byteArrayToXmlDocument(signature);	
			
			Element rootNode = doc.getDocumentElement();
			XmlUtils.getPrefixesRecursive(rootNode, prefixes);
			
			LOGGER.debug("Obteniendo el nodo " + DS_SIGNATURE_XML_ELEMENT + ".");
			String dsNamespacePrefix = (prefixes.get(DIGITAL_SIGNATURE_NAMESPACE) == null?"":prefixes.get(DIGITAL_SIGNATURE_NAMESPACE)+":");
			String eniSignNamespacePrefix = (prefixes.get(ENI_DIGITAL_SIGNATURE_NAMESPACE) == null?"":prefixes.get(ENI_DIGITAL_SIGNATURE_NAMESPACE)+":");
			
			NodeList dsSignatureNodeList = rootNode.getElementsByTagName(dsNamespacePrefix+DS_SIGNATURE_XML_ELEMENT);
			
			if(dsSignatureNodeList.getLength() < 1){
				excMsg = "No se encontro el elemento XML " + DS_SIGNATURE_XML_ELEMENT + ".";
				LOGGER.error(excMsg);
				throw new GdibException(excMsg);
			}
			
			Element dsSignatureNode = (Element) dsSignatureNodeList.item(0);
			
			LOGGER.debug("Nodo " + dsSignatureNode.getTagName() + " recuperado del documento.");
			LOGGER.debug("Obteniendo nodos " + ENI_CERT_SIGNATURE_CONTENT_XML_ELEMENT + ".");
			NodeList certSignatureNodeList = rootNode.getElementsByTagName(eniSignNamespacePrefix+ENI_CERT_SIGNATURE_CONTENT_XML_ELEMENT);
			
			if(certSignatureNodeList.getLength() < 1){
				excMsg = "No se encontraron elementos XML " + ENI_CERT_SIGNATURE_CONTENT_XML_ELEMENT + ".";
				LOGGER.error(excMsg);
				throw new GdibException(excMsg);
			}
			
			LOGGER.debug("Buscando nodo " + ENI_CERT_SIGNATURE_CONTENT_XML_ELEMENT + " para nodo "+ENI_SIGNATURE_XML_ELEMENT+" cuyo atributo " + 
					ENI_SIGNATURE_XML_ID_ATT + " tiene el valor " + signatureIdAttValue + ".");
			
			signatureFound = Boolean.FALSE;
			for(int i=0; !signatureFound && i<certSignatureNodeList.getLength();i++){
				Element certSignatureContentNode = (Element) certSignatureNodeList.item(i);
				Node signatureNode = certSignatureContentNode.getParentNode().getParentNode();
				
				if(signatureNode.getNodeType() == Node.ELEMENT_NODE && 
						signatureIdAttValue.equals(((Element) signatureNode).getAttribute(ENI_SIGNATURE_XML_ID_ATT))){
					LOGGER.debug("Nodo " + ENI_CERT_SIGNATURE_CONTENT_XML_ELEMENT + " para nodo "+ENI_SIGNATURE_XML_ELEMENT+" cuyo atributo " + 
							ENI_SIGNATURE_XML_ID_ATT + " tiene el valor " + signatureIdAttValue + ".");
					LOGGER.debug("Añadiendo nodo " + DS_SIGNATURE_XML_ELEMENT + " como hijo del nodo " + ENI_CERT_SIGNATURE_CONTENT_XML_ELEMENT + ".");
					Element dsSignatureCloneNode = (Element) dsSignatureNode.cloneNode(true);
					certSignatureContentNode.appendChild(dsSignatureCloneNode);
					LOGGER.debug("Eliminando nodo " + DS_SIGNATURE_XML_ELEMENT + " como hijo del nodo raíz del documento.");
					rootNode.removeChild(dsSignatureNode);
					
					signatureFound = Boolean.TRUE;
				}
			}
			
			if(!signatureFound){
				excMsg = "No se encontro el nodo " + ENI_CERT_SIGNATURE_CONTENT_XML_ELEMENT + " perteneciente al nodo " + ENI_SIGNATURE_XML_ELEMENT + 
						", con valor " + signatureIdAttValue +" en el atributo " + ENI_SIGNATURE_XML_ID_ATT + ", donde incluir la firma del índice generada.";
				LOGGER.error(excMsg);
				throw new GdibException(excMsg);
			}
				
			LOGGER.debug("Actualizando contenido documento XML en memoria.");
			
			//Se transforma el XML a un array de bytes
			res = XmlUtils.xmlDocumentToByteArray(doc,INDEX_CHARSET);
			
			LOGGER.debug("Traslado del nodo " + DS_SIGNATURE_XML_ELEMENT + " a su ruta definitiva, " + 
					ENI_SIGNATURES_XML_ELEMENT + "/" + ENI_SIGNATURE_XML_ELEMENT + ", finalizado con éxito.");
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
