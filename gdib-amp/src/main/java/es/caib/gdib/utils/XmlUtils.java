package es.caib.gdib.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlUtils {

	private static final Logger LOGGER =  Logger.getLogger(XmlUtils.class);

	private static final String XMLNAMESPACE = "xmlns";
	
	public static Document byteArrayToXmlDocument(byte[] doc) throws ParserConfigurationException, SAXException, IOException {		
		Document res;
		LOGGER.debug("Se solicita transformar un array de bytes (tamaño: " + (doc==null?0:doc.length)+"), a documento XML.");
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		ByteArrayInputStream bais = new ByteArrayInputStream(doc);		
		res = builder.parse(bais);
		
		LOGGER.debug("Array de bytes transformado a documento XML, nodo raíz: " + res.getDocumentElement().getTagName());
		
		return res;
	}
	
	public static byte[] xmlDocumentToByteArray(Document doc, String charset) throws TransformerException, UnsupportedEncodingException {
		byte[] res = null;
		DOMSource domSource;
		StreamResult result;
		StringWriter writer;
		Transformer transformer;
		TransformerFactory tf = TransformerFactory.newInstance();
		
		LOGGER.debug("Se solicita transformar el documento XML " + doc + " a array de bytes, juego de caracteres " + charset + ".");
		
		domSource = new DOMSource(doc);		
		writer = new StringWriter();
		result = new StreamResult(writer);		
		transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		
		res = writer.toString().getBytes(charset);
		
		LOGGER.debug("Documento XML transformado a array de bytes: \n" + writer.toString());
		
		return res;
	}
	
	/**
	 * Get all prefixes defined, up to the root.
	 * 
	 * @param element
	 * @param prefixes
	 */
	public static void getPrefixesRecursive(Element element, Map<String,String> prefixes) {
		getPrefixes(element, prefixes);
	      
		NodeList nodeList = element.getChildNodes(); 
	    if(nodeList.getLength() > 0){
	    	for(int i=0;i<nodeList.getLength();i++){
	    		if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
	    			getPrefixesRecursive((Element) nodeList.item(i), prefixes);
	    		}
	    	}
	    }
	  }
	
	/**
	 * Get all prefixes defined on this element for the specified namespace.
	 *	 
	 * @param element
	 * @param prefixes
	 */
	private static void getPrefixes(Element element, Map<String,String> prefixes) {
		String xmlnamespaceDec = XMLNAMESPACE + ":";
		NamedNodeMap atts = element.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Node node = atts.item(i);
			if(node.getNodeValue() != null && prefixes.get(node.getNodeValue()) == null){
				String name = node.getNodeName();
				if (name != null && name.startsWith(xmlnamespaceDec)){
					prefixes.put(node.getNodeValue(),name.substring(xmlnamespaceDec.length(), name.length()));
				}
			}
		}
	}
	
}
