package es.caib.gdib.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.xsd.expediente.eni.TipoExpediente;
import es.caib.gdib.ws.xsd.expediente.eni.indice.TipoIndice;
import es.caib.gdib.ws.xsd.expediente.eni.indice.contenido.TipoCarpetaIndizada;
import es.caib.gdib.ws.xsd.expediente.eni.indice.contenido.TipoDocumentoIndizado;
import es.caib.gdib.ws.xsd.expediente.eni.indice.contenido.TipoIndiceContenido;
import es.caib.gdib.ws.xsd.expediente.eni.indice.metadatos.EnumeracionEstados;
import es.caib.gdib.ws.xsd.expediente.eni.indice.metadatos.TipoMetadatos;
import es.caib.gdib.ws.xsd.expediente.eni.indice.metadatos.TipoMetadatos.Estado;
import es.gob.afirma.signature.SigningException;
import es.gob.afirma.utils.CryptoUtil;

public class FoliateEniUtils {
	private static final Logger LOGGER = Logger.getLogger(FoliateUtils.class);
	
	private static final String DEFAULT_ENI_NTI_VERSION = "http://administracionelectronica.gob.es/ENI/XSD/v1.0/expediente-e";
	
	@Autowired
	@Qualifier("NodeService")
	private NodeService nodeService;

	@Autowired
	@Qualifier("ContentService")
	private ContentService contentService;

    @Autowired
    private GdibUtils utils;

	@Autowired
	@Qualifier("exUtils")
	private ExUtils exUtils;
	
	private Boolean addExchangeFiles;
	
	private String addExchangeFilesPropValue;
	
	public FoliateEniUtils() throws DatatypeConfigurationException {
		try{    				
			this.addExchangeFiles = Boolean.valueOf(addExchangeFilesPropValue);
		} catch(Exception e){
			this.addExchangeFiles = ConstantUtils.DEFAULT_ADD_EXCHANGE_FILES_VALUE;
		}
	}

	public TipoExpediente getExchangeFile(NodeRef fileNode) throws GdibException{
		TipoExpediente res;
		TipoIndice index;

		TipoIndiceContenido contentIndex = (TipoIndiceContenido) getContainerNode(fileNode, 1);
		if(contentIndex == null){
			Serializable sNumExp = nodeService.getProperty(fileNode, ConstantUtils.PROP_ID_QNAME); 
			String numExp = (sNumExp!=null? (String) sNumExp : "");
			GdibException exc = exUtils.fileIsEmptyException(numExp);
			
			throw exc;
		}
		
		index = new TipoIndice();
		index.setId(ConstantUtils.INDEX_ID_ATT_VALUE);
		index.setIndiceContenido(contentIndex);
		
		res = new TipoExpediente();
		res.setId(ConstantUtils.FILE_ID_ATT_PREFIX+fileNode.getId());
		res.setIndice(index);
		res.setMetadatosExp(getMetadatosExpedienteENI(fileNode));
		
		return res;
	}

	private TipoMetadatos getMetadatosExpedienteENI(NodeRef node) throws GdibException {
		TipoMetadatos metadata = new TipoMetadatos();
		
		metadata.setId(ConstantUtils.METADATA_ID_ATT_PREFIX+node.getId());
		Serializable sNumExp = nodeService.getProperty(node, ConstantUtils.PROP_ID_QNAME); 
		String numExp = (sNumExp!=null? (String) sNumExp : "");
		metadata.setIdentificador(numExp);

		Serializable sestado = nodeService.getProperty(node, ConstantUtils.PROP_ESTADO_EXP_QNAME);
		String estado = (sestado!=null? (String) sestado : "");
		Estado estadoExp = metadata.getEstado();
		if(estadoExp == null){
			estadoExp = new Estado();
		}
		estadoExp.setValue(EnumeracionEstados.fromValue(estado));
		metadata.setEstado(estadoExp);
		
		Date fechaApertura = (Date) nodeService.getProperty(node, ConstantUtils.PROP_FECHA_INICIO_QNAME);
		if(fechaApertura != null){
			metadata.setFechaAperturaExpediente(utils.createXMLGregorianCalendar(fechaApertura));
		}

		Serializable sIdTramite = nodeService.getProperty(node, ConstantUtils.PROP_ID_TRAMITE_QNAME);
		String idTramite = (sIdTramite!=null? (String) sIdTramite : "");
		metadata.setClasificacion(idTramite);

		Serializable svNti = nodeService.getProperty(node, ConstantUtils.PROP_V_NTI_QNAME); 
		String vNti = (svNti!=null? (String) svNti : DEFAULT_ENI_NTI_VERSION);
		metadata.setVersionNTI(vNti);

		//Campos con multiples valores
		//interesados_exp
		Serializable interesados = nodeService.getProperty(node, ConstantUtils.PROP_INTERESADOS_EXP_QNAME);
		List<String> lint = new ArrayList<String>();
		if(interesados != null ){
			if ( interesados instanceof List){
				lint = (List<String>) interesados;
			}else{
				lint.add(interesados.toString());
			}
		}
		metadata.getInteresado().addAll(lint);
		
		//eni:organo
		Serializable organos = nodeService.getProperty(node, ConstantUtils.PROP_ORGANO_QNAME);
		List<String> lorg = new ArrayList<String>();
		if(organos != null ){
			if ( organos instanceof List){
				lorg = (List<String>) organos;
			}else{
				lorg.add(organos.toString());
			}
		}
		metadata.getOrgano().addAll(lorg);
		
		return metadata;
	}
	
	private TipoDocumentoIndizado getDocNode(NodeRef node, int numDocs) throws GdibException {

		TipoDocumentoIndizado res = new TipoDocumentoIndizado();

		res.setId(ConstantUtils.DOC_ID_ATT_PREFIX+node.getId());
		res.setIdentificadorDocumento(node.getId());

		res.setFuncionResumen(CryptoUtil.HASH_ALGORITHM_SHA256);

		ContentReader cr = contentService.getReader(node, ContentModel.PROP_CONTENT);
		try{
			byte[] docContent = IOUtils.toByteArray(cr.getContentInputStream());
			byte [] hashDoc = CryptoUtil.digest(CryptoUtil.HASH_ALGORITHM_SHA256,docContent);

			res.setValorHuella(Base64.encodeBase64String(hashDoc));
		}catch(IOException e){
			LOGGER.error(e);
		} catch (SigningException e) {
			LOGGER.error(e);
		}

		Date fecha_inicio_prop = (Date) nodeService.getProperty(node, ConstantUtils.PROP_FECHA_INICIO_QNAME);

		if (fecha_inicio_prop != null){
			res.setFechaIncorporacionExpediente(utils.createXMLGregorianCalendar(fecha_inicio_prop));
		}

		res.setOrdenDocumentoExpediente(Integer.toString(numDocs));
		
		return res;
	}

	
	private Object getContainerNode(NodeRef nodeRef, int numDocs) throws GdibException {
		Boolean contentFound = Boolean.FALSE;
		Boolean isFile = Boolean.FALSE;
		Object res = null;

		List<ChildAssociationRef> childNodes = nodeService.getChildAssocs(nodeRef); 
		
		if(childNodes != null && !childNodes.isEmpty()){
			QName nodeType = nodeService.getType(nodeRef);
			if(utils.isType(nodeType,ConstantUtils.TYPE_EXPEDIENTE_QNAME)){
				res = new TipoIndiceContenido();
				((TipoIndiceContenido) res).setId(ConstantUtils.INDEX_FILE_CONTENT_ID_ATT_PREFIX+nodeRef.getId());
				((TipoIndiceContenido) res).setFechaIndiceElectronico(utils.createXMLGregorianCalendar(new Date()));
				isFile = Boolean.TRUE;
			} else if (utils.isType(nodeType,ConstantUtils.TYPE_AGREGACION_DOC_QNAME)){
				res = new TipoCarpetaIndizada();
				((TipoCarpetaIndizada) res).setId(ConstantUtils.FOLDER_ID_ATT_PREFIX+nodeRef.getId());
				((TipoCarpetaIndizada) res).setIdentificadorCarpeta(nodeRef.getId());
			}

			for(Iterator<ChildAssociationRef> it = childNodes.iterator();it.hasNext();){
				ChildAssociationRef childAssociationRef = it.next();
				NodeRef node = childAssociationRef.getChildRef();
				QName childNodeType = nodeService.getType(node);
				
				// Solo se añaden al índice nodos pertenecientes al expediente, excluyendo los nodos referenciados
				if(utils.isInFolder(nodeRef, node)){
					// documento
					if (utils.isType(childNodeType, ConstantUtils.TYPE_DOCUMENTO_QNAME)) {						
						//No se incluyen documentos en estado borrador
						if(!nodeService.hasAspect(node, ConstantUtils.ASPECT_BORRADOR_QNAME)){
							TipoDocumentoIndizado docIndizado =  getDocNode(node,numDocs);
							
							if(isFile){							
								((TipoIndiceContenido) res).getDocumentoIndizadoOrExpedienteIndizadoOrCarpetaIndizada().add(docIndizado);
							} else {
								((TipoCarpetaIndizada) res).getDocumentoIndizadoOrExpedienteIndizadoOrCarpetaIndizada().add(docIndizado);
							}
	
							numDocs++;
							contentFound = Boolean.TRUE;
						}
					} else if(utils.isType(childNodeType,ConstantUtils.TYPE_EXPEDIENTE_QNAME)){					
						// subexpediente
						TipoIndiceContenido indiceContenido = (TipoIndiceContenido) getContainerNode(node, numDocs);
						if(indiceContenido != null){
							if(isFile){									
								((TipoIndiceContenido) res).getDocumentoIndizadoOrExpedienteIndizadoOrCarpetaIndizada().add(indiceContenido);
							} else {									
								((TipoCarpetaIndizada) res).getDocumentoIndizadoOrExpedienteIndizadoOrCarpetaIndizada().add(indiceContenido);
							}
	
							numDocs += getDocsInFileOrFolder(indiceContenido);
							contentFound = Boolean.TRUE;							
						}
							
					} else if(utils.isType(childNodeType,ConstantUtils.TYPE_AGREGACION_DOC_QNAME)){
							// agregacionDoc
							TipoCarpetaIndizada carpertaIndizada = (TipoCarpetaIndizada) getContainerNode(node,numDocs);
							if(carpertaIndizada != null){
								if(isFile){									
									((TipoIndiceContenido) res).getDocumentoIndizadoOrExpedienteIndizadoOrCarpetaIndizada().add(carpertaIndizada);
								} else {									
									((TipoCarpetaIndizada) res).getDocumentoIndizadoOrExpedienteIndizadoOrCarpetaIndizada().add(carpertaIndizada);
								}
								numDocs += getDocsInFileOrFolder(carpertaIndizada);
								contentFound = Boolean.TRUE;
							}
					} else if(utils.isType(childNodeType,ConstantUtils.TYPE_FOLDER)){
						//Carpeta
						String nodeName =  (String) nodeService.getProperty(node,ConstantUtils.PROP_NAME);
						if(addExchangeFiles && ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME.equals(nodeName)){
							//Si esta activada la incorporación al índice de los expedientes de intercambio, y el nombre de la carpeta es el de la carpeta
							//que incluye los expediente de intercambio generados, se añaden al índice los nodos hijos de esta carpeta
							List<ChildAssociationRef> childExportNodes = nodeService.getChildAssocs(node); 
							
							if(childExportNodes != null && !childExportNodes.isEmpty()){
								for(Iterator<ChildAssociationRef> exportIt = childExportNodes.iterator();it.hasNext();){
									Object obj = getContainerNode(exportIt.next().getChildRef(), numDocs);
									if(obj != null){
										if(isFile){									
											((TipoIndiceContenido) res).getDocumentoIndizadoOrExpedienteIndizadoOrCarpetaIndizada().add(obj);
										} else {									
											((TipoCarpetaIndizada) res).getDocumentoIndizadoOrExpedienteIndizadoOrCarpetaIndizada().add(obj);
										}
										numDocs += getDocsInFileOrFolder(obj);
										contentFound = Boolean.TRUE;
									}
								}
							}
							
						}
					}
				}
			}
			
			if(!contentFound){
				res = null;
			}
		}
		
		return res;
	}

	private int getDocsInFileOrFolder(Object fileOrFolderContent){
		int res = 0;
		List<Object> contents = null;
		
		if(fileOrFolderContent != null){
			if(fileOrFolderContent instanceof TipoCarpetaIndizada){
				contents = ((TipoCarpetaIndizada) fileOrFolderContent).getDocumentoIndizadoOrExpedienteIndizadoOrCarpetaIndizada();
			} else if(fileOrFolderContent instanceof TipoIndiceContenido){
				contents = ((TipoIndiceContenido) fileOrFolderContent).getDocumentoIndizadoOrExpedienteIndizadoOrCarpetaIndizada();
			}
		
			if(contents != null && !contents.isEmpty()){

				for(Object obj : contents){
					
					if(obj instanceof TipoDocumentoIndizado){
						res++;
					} else {
						res += getDocsInFileOrFolder(obj);
					}
					
				}
			}
		}
		
		return res;
	}
	
	public String getAddExchangeFilesPropValue() {
		return addExchangeFilesPropValue;
	}

	public void setAddExchangeFilesPropValue(String addExchangeFilesPropValue) {
		this.addExchangeFilesPropValue = addExchangeFilesPropValue;
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setExUtils(ExUtils exUtils) {
		this.exUtils = exUtils;
	}

	public void setUtils(GdibUtils utils) {
		this.utils = utils;
	}

	/**
	 * @param contentService the contentService to set
	 */
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
}
