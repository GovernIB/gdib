package es.caib.gdib.ws.common.types;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import es.caib.gdib.utils.AdministrativeProcessingIndexSigner;
import es.caib.gdib.utils.AdministrativeProcessingIndexSignerFactory;
import es.caib.gdib.utils.ExUtils;
import es.caib.gdib.utils.FoliateEniUtils;
import es.caib.gdib.utils.FoliateUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.InputStreamDataSource;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.xsd.expediente.eni.TipoExpediente;
import es.caib.gdib.ws.xsd.expediente.indice.TipoIndiceElectronico;


public class IndiceElectronicoManager {
	@Autowired
	@Qualifier("exUtils")
	private ExUtils exUtils;

    @Autowired
    private GdibUtils utils;

	@Autowired
	@Qualifier("foliateUtils")
    private FoliateUtils foliateUtils;

	@Autowired
	@Qualifier("foliateEniUtils")
    private FoliateEniUtils foliateEniUtils;

	@Autowired
	@Qualifier("NodeService")
	private NodeService nodeService;

	@Autowired
    private AdministrativeProcessingIndexSignerFactory indexSignerFactory;
	
	public IndiceElectronicoManager() {
	}

	public TipoIndiceElectronico getIndiceElectronico(NodeRef nodeRefExp) throws GdibException {
		return foliateUtils.getContentFile(nodeRefExp);
	}

	public TipoExpediente getExpedienteElectronicoENI(NodeRef nodeRefExp) throws GdibException {
		return foliateEniUtils.getExchangeFile(nodeRefExp);
	}

	public DataHandler generateXML(byte[] signedIndex) throws GdibException {
		DataHandler dataHandler = null;

		dataHandler = new DataHandler(new InputStreamDataSource(new ByteArrayInputStream(signedIndex)));

		return dataHandler;
	}

	/**
	 * Firma el indice electrónico de un expediente.
	 *
	 * @param document
	 *            documento XML que aloja el índice electrónico de expediente
	 *            que será firmado.
	 * @param indexType
	 *            tipo de índice electrónico a generar.
	 * @param optionalParams
	 *            parámetros opcionales que pudieran ser requeridos para la
	 *            firma del índice. Opcional.
	 * @return objeto Java que representa el índice electrónico firmado.
	 * @throws GdibException
	 *             si ocurre algún error al firmar el índice electrónico de un
	 *             expediente.
	 *             
	 *             OJO CUIDADO
	 *             TODO: RESTABLECER LOS COMENTARIOS!!!!!!!!!!
	 *             
	 */
	public byte[] signXmlIndex(Object objIndex, final String indexType, Map<String, Object> optionalParams)
			throws GdibException {
		byte[] res, document;
		AdministrativeProcessingIndexSigner indexSigner;
		
		document = null;
		//TODO:CUIDADOOOOOOOOOOOOOOOOOOOOO
		indexSigner = indexSignerFactory.createIndexSigner(indexType, null);

		switch (indexType) {
		case AdministrativeProcessingIndexSignerFactory.CAIB_INDEX_V10:

			document = marshalIndexs(TipoIndiceElectronico.class, indexType, objIndex);

			break;
		case AdministrativeProcessingIndexSignerFactory.ENI_INDEX_V10:
			document = marshalIndexs(TipoExpediente.class, indexType, objIndex);

			break;
		default:
			throw new GdibException("El tipo de índice " + indexType + " no es soportado.");
		}
		//TODO: CUIDADOOOOOOOOOOOOOOOOOOOO
		res = indexSigner.generateIndexSignature(document, optionalParams);
		//res = document;

		return res;
	}

	public static Object unmarshalIndexs(Class classz, byte [] source) throws GdibException {
    	Object res = null;
       	JAXBContext jaxbContext = null;
       	Unmarshaller unmarshaller;

    	try {
			jaxbContext = JAXBContext.newInstance(classz);

			unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> jaxbElement = (JAXBElement<?>) unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(source)),classz);
			res = jaxbElement.getValue();
    	} catch (JAXBException e) {
    		throw new GdibException("No fue posible deserializar un índice electrónico (Tipo: "+classz.getName()+"). Error: " + e.getMessage(),e);
		}  catch (Exception e) {
    		throw new GdibException("No fue posible deserializar un índice electrónico (Tipo: "+classz.getName()+"). Error: " + e.getMessage(),e);
		}

    	return res;
    }

	/*public byte[] marshalEniFile(es.caib.gdib.ws.xsd.expediente.eni.TipoExpediente file) throws GdibException {
    	byte[] res = null;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	JAXBContext jaxbContext = null;
       	Marshaller marshaller;

    	try {
    		jaxbContext = JAXBContext.newInstance(file.getClass());
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
			
			es.caib.gdib.ws.xsd.expediente.eni.ObjectFactory eniObjfact = new es.caib.gdib.ws.xsd.expediente.eni.ObjectFactory();
			JAXBElement<es.caib.gdib.ws.xsd.expediente.eni.TipoExpediente> jaxbElement = eniObjfact.createExpediente(file);
			marshaller.marshal(jaxbElement,baos);

			res = baos.toByteArray();
    	} catch (JAXBException e) {
    		throw new GdibException("No fue posible serializar un expediente electrónico ENI. Error: " + e.getMessage(),e);
		} catch (Exception e) {
			throw new GdibException("No fue posible serializar un expediente electrónico ENI. Error: " + e.getMessage(),e);
		}

    	return res;
    }*/
	
	private static byte[] marshalIndexs(Class classz, String indexType, Object index) throws GdibException {
    	byte[] res = null;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	JAXBContext jaxbContext = null;
       	Marshaller marshaller;

    	try {
    		jaxbContext = JAXBContext.newInstance(classz);
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);

    		switch(indexType){
				case AdministrativeProcessingIndexSignerFactory.CAIB_INDEX_V10:
					es.caib.gdib.ws.xsd.expediente.indice.ObjectFactory objectFactory = new es.caib.gdib.ws.xsd.expediente.indice.ObjectFactory();
					JAXBElement<TipoIndiceElectronico> jaxbElement = objectFactory.createIndiceElectronico((TipoIndiceElectronico) index);
					marshaller.marshal(jaxbElement,baos);
					break;
				case AdministrativeProcessingIndexSignerFactory.ENI_INDEX_V10:
					es.caib.gdib.ws.xsd.expediente.eni.ObjectFactory eniIndexObjfact = new es.caib.gdib.ws.xsd.expediente.eni.ObjectFactory();
					JAXBElement<TipoExpediente> jaxbElement_eni = eniIndexObjfact.createExpediente((TipoExpediente) index);
					marshaller.marshal(jaxbElement_eni,baos);
					break;
				default:
					throw new GdibException("El tipo de índice " + indexType + " no es soportado.");
			}

			res = baos.toByteArray();
    	} catch (JAXBException e) {
    		throw new GdibException("No fue posible serializar un índice electrónico (Tipo: "+classz.getName()+"). Error: " + e.getMessage(),e);
		} catch (Exception e) {
			throw new GdibException("No fue posible serializar un índice electrónico (Tipo: "+classz.getName()+"). Error: " + e.getMessage(),e);
		}

    	return res;
    }

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public FoliateUtils getFoliateUtils() {
		return foliateUtils;
	}

	public void setFoliateUtils(FoliateUtils foliateUtils) {
		this.foliateUtils = foliateUtils;
	}

	public ExUtils getExUtils() {
		return exUtils;
	}

	public void setExUtils(ExUtils exUtils) {
		this.exUtils = exUtils;
	}

	/**
	 * @return the indexSignerFactory
	 */
	public AdministrativeProcessingIndexSignerFactory getIndexSignerFactory() {
		return indexSignerFactory;
	}

	/**
	 * @param indexSignerFactory the indexSignerFactory to set
	 */
	public void setIndexSignerFactory(AdministrativeProcessingIndexSignerFactory indexSignerFactory) {
		this.indexSignerFactory = indexSignerFactory;
	}

	public GdibUtils getUtils() {
		return utils;
	}

	public void setUtils(GdibUtils utils) {
		this.utils = utils;
	}

	public FoliateEniUtils getFoliateEniUtils() {
		return foliateEniUtils;
	}

	public void setFoliateEniUtils(FoliateEniUtils foliateEniUtils) {
		this.foliateEniUtils = foliateEniUtils;
	}

}
