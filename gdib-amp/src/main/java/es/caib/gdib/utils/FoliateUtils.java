package es.caib.gdib.utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoCarpetaIndizada;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoCarpetas;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoDocIndizado;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoDocReferenciado;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoDocsIndizados;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoDocsReferenciados;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoExpReferenciados;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoExpediente;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoExpedientes;
import es.caib.gdib.ws.xsd.expediente.indice.TipoEstadoExpediente;
import es.caib.gdib.ws.xsd.expediente.indice.TipoExpediente;
import es.caib.gdib.ws.xsd.expediente.indice.TipoExpedienteReferenciado;
import es.caib.gdib.ws.xsd.expediente.indice.TipoFuncionHash;
import es.caib.gdib.ws.xsd.expediente.indice.TipoIndiceElectronico;
import es.caib.gdib.ws.xsd.expediente.indice.TipoMetadatosExpediente;
import es.gob.afirma.signature.SigningException;
import es.gob.afirma.utils.CryptoUtil;

public class FoliateUtils {
	private static final Logger LOGGER = Logger.getLogger(FoliateUtils.class);
	
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

	private static final Boolean DEFAULT_ADD_EXCHANGE_FILES_VALUE = Boolean.FALSE;
	
	private Boolean addExchangeFiles;
	
	private String addExchangeFilesPropValue;
	
	public FoliateUtils(){
		try{    				
			this.addExchangeFiles = Boolean.valueOf(addExchangeFilesPropValue);
		} catch(Exception e){
			this.addExchangeFiles = DEFAULT_ADD_EXCHANGE_FILES_VALUE;
		}
	}
	
	public TipoIndiceElectronico getContentFile(NodeRef fileNode) throws GdibException {
		TipoContenidoExpediente fileContent = null;
		TipoIndiceElectronico res = null;
		TipoExpediente file = null;
		
		try {
			fileContent = (TipoContenidoExpediente) getContainerNode(fileNode, 1);
			
			file = new TipoExpediente();
			file.setContenido(fileContent);
			file.setMetadatos(getFileMetadataCollection(fileNode));
			
			res = new TipoIndiceElectronico();
			res.setContenidoExpediente(file);
			res.setFechaGeneracion(utils.createXMLGregorianCalendar(new Date()));
		} catch (ParseException e) {
			throw exUtils.generateFileIndexException(fileNode.getId(),e.getMessage());
		} 
		
		return res;
	}
	
	private Object getContainerNode(NodeRef nodeRef, int numDocs) throws ParseException, GdibException {
		Boolean isFile = Boolean.FALSE;
		Object res = null;

		List<ChildAssociationRef> childNodes = nodeService.getChildAssocs(nodeRef); 
		
		QName nodeType = nodeService.getType(nodeRef);
		if(utils.isType(nodeType,ConstantUtils.TYPE_EXPEDIENTE_QNAME)){
			res = new TipoContenidoExpediente();				
			isFile = Boolean.TRUE;
		} else if (utils.isType(nodeType,ConstantUtils.TYPE_AGREGACION_DOC_QNAME)){
			res = new TipoContenidoCarpetaIndizada();
		}
		
		if(childNodes != null && !childNodes.isEmpty()){
			
			
			for(Iterator<ChildAssociationRef> it = childNodes.iterator();it.hasNext();){
		
				ChildAssociationRef childAssociationRef = it.next();
				NodeRef node = childAssociationRef.getChildRef();
				QName childNodeType = nodeService.getType(node);
				// documento
				if (utils.isType(childNodeType, ConstantUtils.TYPE_DOCUMENTO_QNAME)) {				
					//No se añaden al índice los documentos en estado borrador
					if(!nodeService.hasAspect(node, ConstantUtils.ASPECT_BORRADOR_QNAME)){
						// compruebo si el documento tiene como padre primario el expediente donde estoy creando el indice				
						if(utils.isInFolder(nodeRef, node)){
							// documento
							TipoContenidoDocsIndizados tcdi = null;
							if(isFile){								
								tcdi = ((TipoContenidoExpediente) res).getDocumentosIndizados();								
							} else {
								tcdi = ((TipoContenidoCarpetaIndizada) res).getDocumentosIndizados();
							}
							
							tcdi = getDocumentContent(tcdi, node, numDocs);
							numDocs++;
							
							if(isFile){								
								((TipoContenidoExpediente) res).setDocumentosIndizados(tcdi);								
							} else {
								((TipoContenidoCarpetaIndizada) res).setDocumentosIndizados(tcdi);
							}
						}else{
							// documento referenciado
							TipoContenidoDocsReferenciados tcdr = null;
							if(isFile){								
								tcdr = ((TipoContenidoExpediente) res).getDocumentosReferenciados();								
							} else {
								tcdr = ((TipoContenidoCarpetaIndizada) res).getDocumentosReferenciados();
							}

							tcdr = getRefDocumentContent(tcdr,node);
							
							if(isFile){								
								((TipoContenidoExpediente) res).setDocumentosReferenciados(tcdr);
							} else {
								((TipoContenidoCarpetaIndizada) res).setDocumentosReferenciados(tcdr);
							}
						}
					}
				} else if(utils.isType(childNodeType,ConstantUtils.TYPE_EXPEDIENTE_QNAME)){
					//Subexpedeinte
					if(utils.isInFolder(nodeRef, node)){
						// subexpediente
						TipoContenidoExpedientes tce = null;
						if(isFile){								
							tce = ((TipoContenidoExpediente) res).getSubexpedientesIndizados();								
						} else {
							tce = ((TipoContenidoCarpetaIndizada) res).getSubexpedientes();
						}
						
						tce = getSubfileContent(tce, node, numDocs);
						
						if(isFile){								
							((TipoContenidoExpediente) res).setSubexpedientesIndizados(tce);
						} else {
							((TipoContenidoCarpetaIndizada) res).setSubexpedientes(tce);
						}
						
						numDocs += getDocsInFile(tce,node.getId());
					}else{
						// expediente enlazado
						TipoContenidoExpReferenciados tcer = null;
						if(isFile){								
							tcer = ((TipoContenidoExpediente) res).getExpedientesReferenciados();								
						} else {
							tcer = ((TipoContenidoCarpetaIndizada) res).getExpedientesReferenciados();
						}
						
						tcer = getRefSubfileContent(tcer, node);

						if(isFile){								
							((TipoContenidoExpediente) res).setExpedientesReferenciados(tcer);
						} else {
							((TipoContenidoCarpetaIndizada) res).setExpedientesReferenciados(tcer);
						}
					}
				} else if(utils.isType(childNodeType,ConstantUtils.TYPE_AGREGACION_DOC_QNAME)){
					// agrupacion
					//No se verifica si la carpeta está enlazada puesto que estas no pueden ser
					//referenciadas
					TipoContenidoCarpetas tcc = null;
					if(isFile){								
						tcc = ((TipoContenidoExpediente) res).getCarpetasIndizadas();								
					} else {
						tcc = ((TipoContenidoCarpetaIndizada) res).getCarpetasIndizadas();
					}
					
					tcc = getFolderContent(tcc, node, numDocs);
					
					if(isFile){								
						((TipoContenidoExpediente) res).setCarpetasIndizadas(tcc);
					} else {
						((TipoContenidoCarpetaIndizada) res).setCarpetasIndizadas(tcc);
					}

					numDocs += getDocsInFolder(tcc,node.getId());
				} else if(utils.isType(childNodeType,ConstantUtils.TYPE_FOLDER)){
					String nodeName =  (String) nodeService.getProperty(node,ConstantUtils.PROP_NAME);
					if(addExchangeFiles && ConstantUtils.EXPEDIENT_EXPORT_FOLDER_NAME.equals(nodeName)){
						//Si esta activada la incorporación al índice de los expedientes de intercambio, y el nombre de la carpeta es el de la carpeta
						//que incluye los expediente de intercambio generados, se añaden al índice los nodos hijos de esta carpeta
						List<ChildAssociationRef> childExportNodes = nodeService.getChildAssocs(node); 
						
						if(childExportNodes != null && !childExportNodes.isEmpty()){
							for(Iterator<ChildAssociationRef> exportIt = childExportNodes.iterator();it.hasNext();){
								
								TipoContenidoExpedientes etce = null;
								if(isFile){								
									etce = ((TipoContenidoExpediente) res).getSubexpedientesIndizados();								
								} else {
									etce = ((TipoContenidoCarpetaIndizada) res).getSubexpedientes();
								}
								
								etce = getSubfileContent(etce, exportIt.next().getChildRef(), numDocs);
								
								if(isFile){								
									((TipoContenidoExpediente) res).setSubexpedientesIndizados(etce);
								} else {
									((TipoContenidoCarpetaIndizada) res).setSubexpedientes(etce);
								}

								numDocs += getDocsInFile(etce,node.getId());
							}
						}
						
					}
				}
			}
		}
		
		return res;
	}
	
	private TipoContenidoExpedientes getSubfileContent(TipoContenidoExpedientes content, NodeRef node, int numDocs) throws ParseException, GdibException {
		TipoContenidoExpedientes res = content;
		if(res == null){
			res = new TipoContenidoExpedientes();
		}
		TipoExpediente exp = this.getSubfile(node,numDocs);
		res.getSubexpediente().add(exp);

		return res;
	}

	private TipoExpediente getSubfile(NodeRef node, int numDocs) throws ParseException, GdibException {
		TipoExpediente exp = new TipoExpediente();
		exp.setId(ConstantUtils.FILE_ID_ATT_PREFIX+node.getId());

		exp.setMetadatos(this.getFileMetadataCollection(node));
		exp.setContenido((TipoContenidoExpediente) this.getContainerNode(node,numDocs));
		
		return exp;
	}

	private TipoMetadatosExpediente getFileMetadataCollection(NodeRef node) throws GdibException{
		TipoMetadatosExpediente metadata = new TipoMetadatosExpediente();
		
		String numExp = (nodeService.getProperty(node, ConstantUtils.PROP_ID_QNAME)!=null? (String) nodeService.getProperty(node, ConstantUtils.PROP_ID_QNAME) : "");
		metadata.setNumero(numExp);

		String estado = (nodeService.getProperty(node, ConstantUtils.PROP_ESTADO_EXP_QNAME)!=null? (String) nodeService.getProperty(node, ConstantUtils.PROP_ESTADO_EXP_QNAME) : "");
		metadata.setEstado(TipoEstadoExpediente.fromValue(estado));

		Date fechaApertura = (Date) nodeService.getProperty(node, ConstantUtils.PROP_FECHA_INICIO_QNAME);
		if(fechaApertura != null){
			metadata.setFechaApertura(utils.createXMLGregorianCalendar(fechaApertura));
		}
		
		Date fechaCierre = (Date) nodeService.getProperty(node, ConstantUtils.PROP_FECHA_FIN_EXP_QNAME);
		if(fechaCierre != null){
			metadata.setFechaCierre(utils.createXMLGregorianCalendar(fechaCierre));
		}

		return metadata;
	}

	private TipoContenidoCarpetas getFolderContent(TipoContenidoCarpetas content, NodeRef node, int numDocs) throws ParseException, GdibException {
		TipoContenidoCarpetas res = content;
		if(res == null){
			res = new TipoContenidoCarpetas();
		}
		TipoContenidoCarpetaIndizada folder = getFolder(node,numDocs);
		res.getCarpetaIndizada().add(folder);
		
		return res;
	}

	private TipoContenidoCarpetaIndizada getFolder(NodeRef node, int numDocs) throws ParseException, GdibException {
		TipoContenidoCarpetaIndizada res = (TipoContenidoCarpetaIndizada) getContainerNode(node,numDocs);
		
		res.setId(ConstantUtils.FOLDER_ID_ATT_PREFIX+node.getId());
		res.setNombre((String)nodeService.getProperty(node, ConstantUtils.PROP_NAME));
		
		return res;
	}

	private TipoContenidoExpReferenciados getRefSubfileContent(TipoContenidoExpReferenciados content, NodeRef node) {
		TipoContenidoExpReferenciados res = content;
		if(res == null){
			res = new TipoContenidoExpReferenciados();
		}
		TipoExpedienteReferenciado exp = this.getRefSubfile(node);
		res.getExpReferenciado().add(exp);
		
		return res;
	}

	private TipoExpedienteReferenciado getRefSubfile(NodeRef node) {
		String eniIdPropValue,expNumber;
		
		TipoExpedienteReferenciado exp = new TipoExpedienteReferenciado();
		exp.setId(ConstantUtils.FILE_ID_ATT_PREFIX+node.getId());
		
		eniIdPropValue = (String) nodeService.getProperty(node, ConstantUtils.PROP_ID_QNAME);
		expNumber = (eniIdPropValue!=null? eniIdPropValue : "");
		
		exp.setNumero(expNumber);
		
		return exp;
	}

	private TipoContenidoDocsReferenciados getRefDocumentContent(TipoContenidoDocsReferenciados content, NodeRef node){
		TipoContenidoDocsReferenciados res = content;
		if(res == null){
			res = new TipoContenidoDocsReferenciados();
		}
		TipoContenidoDocReferenciado doc = this.getRefDocument(node);
		res.getDocReferenciado().add(doc);
		
		return res;
	}

	private TipoContenidoDocReferenciado getRefDocument(NodeRef node){
		TipoContenidoDocReferenciado doc = new TipoContenidoDocReferenciado();
		doc.setId(ConstantUtils.DOC_ID_ATT_PREFIX+node.getId());
		doc.setNombre((String)nodeService.getProperty(node, ConstantUtils.PROP_NAME));
		return doc;
	}

	private TipoContenidoDocsIndizados getDocumentContent(TipoContenidoDocsIndizados docsContent, NodeRef node, int numDocs) throws GdibException{
		TipoContenidoDocsIndizados res = docsContent;
		if(res == null){
			res = new TipoContenidoDocsIndizados();
		}
		TipoContenidoDocIndizado doc = getDocument(node,numDocs);
		res.getDocIndizado().add(doc);
		
		return res;
	}

	private TipoContenidoDocIndizado getDocument(NodeRef node, int numDocs) throws GdibException{
		TipoContenidoDocIndizado doc = new TipoContenidoDocIndizado();

		doc.setId(ConstantUtils.DOC_ID_ATT_PREFIX+node.getId());
		doc.setNombre((String)nodeService.getProperty(node, ConstantUtils.PROP_NAME));
		
		Date fechaIncorporacion = (Date) nodeService.getProperty(node, ConstantUtils.PROP_FECHA_INICIO_QNAME);
		if(fechaIncorporacion != null){
			doc.setFechaIncorporacion(utils.createXMLGregorianCalendar(fechaIncorporacion));
		}
		doc.setOrdenDocumento(numDocs);
		
		doc.setFuncionHash(TipoFuncionHash.SHA_256);
		
		ContentReader cr = contentService.getReader(node, ContentModel.PROP_CONTENT);
		try{
			byte[] docContent = IOUtils.toByteArray(cr.getContentInputStream());
			byte [] hashDoc = CryptoUtil.digest(CryptoUtil.HASH_ALGORITHM_SHA256,docContent);
		
			doc.setHash(Base64.encodeBase64String(hashDoc));
		}catch(IOException e){
			LOGGER.error(e);
		} catch (SigningException e) {
			LOGGER.error(e);
		}
		return doc;
	}
	
	private static int getDocsInFolder(TipoContenidoCarpetas carpetas, String nodeId){
		int res = 0; 		
		Boolean found = Boolean.FALSE;
		
		if(!CollectionUtils.isEmpty(carpetas.getCarpetaIndizada())){
			for(Iterator<TipoContenidoCarpetaIndizada> it = carpetas.getCarpetaIndizada().iterator();!found && it.hasNext();){
				TipoContenidoCarpetaIndizada folder = it.next();
				
				if(folder.getId().equals(ConstantUtils.FOLDER_ID_ATT_PREFIX+nodeId)){
					res = getDocsInFolderRec(folder);		
					found = Boolean.TRUE;
				}
			}
		}
		
		return res;
	}
	
	
	private static int getDocsInFolderRec(TipoContenidoCarpetaIndizada carpeta){
		int res = 0;
		
		if(carpeta.getDocumentosIndizados() != null && 
				!CollectionUtils.isEmpty(carpeta.getDocumentosIndizados().getDocIndizado())){
			res += carpeta.getDocumentosIndizados().getDocIndizado().size();
		}
		
		if(carpeta.getCarpetasIndizadas() != null && 
				!CollectionUtils.isEmpty(carpeta.getCarpetasIndizadas().getCarpetaIndizada())){
			for(TipoContenidoCarpetaIndizada c : carpeta.getCarpetasIndizadas().getCarpetaIndizada()){
				res += getDocsInFolderRec(c);
			}
			
		}
		if(carpeta.getSubexpedientes() != null && 
				!CollectionUtils.isEmpty(carpeta.getSubexpedientes().getSubexpediente())){
			for(TipoExpediente e : carpeta.getSubexpedientes().getSubexpediente()){
				res += getDocsInFileRec(e);
			}
		}
		
		return res;
	}
	
	private static int getDocsInFile(TipoContenidoExpedientes contenidoExp, String nodeId){
		int res = 0; 		
		Boolean found = Boolean.FALSE;
		
		if(!CollectionUtils.isEmpty(contenidoExp.getSubexpediente())){
			for(Iterator<TipoExpediente> it = contenidoExp.getSubexpediente().iterator();!found && it.hasNext();){
				TipoExpediente exp = it.next();
				
				if(exp.getId().equals(ConstantUtils.FILE_ID_ATT_PREFIX+nodeId)){
					res = getDocsInFileRec(exp);		
					found = Boolean.TRUE;
				}
			}
		}
		
		return res;
	}
	
	private static int getDocsInFileRec(TipoExpediente exp){
		int res = 0;
		
		if(exp.getContenido() != null && 
				exp.getContenido().getDocumentosIndizados() != null &&
						!CollectionUtils.isEmpty(exp.getContenido().getDocumentosIndizados().getDocIndizado())){
			res += exp.getContenido().getDocumentosIndizados().getDocIndizado().size();
		}
		
		if(exp.getContenido() != null &&
				exp.getContenido().getCarpetasIndizadas() != null &&
						!CollectionUtils.isEmpty(exp.getContenido().getCarpetasIndizadas().getCarpetaIndizada())){
			for(TipoContenidoCarpetaIndizada c : exp.getContenido().getCarpetasIndizadas().getCarpetaIndizada()){
				res += getDocsInFolderRec(c);
			}
			
		}
		
		if(exp.getContenido() != null && 
				exp.getContenido().getSubexpedientesIndizados() != null &&
						!CollectionUtils.isEmpty(exp.getContenido().getSubexpedientesIndizados().getSubexpediente())){
			for(TipoExpediente e : exp.getContenido().getSubexpedientesIndizados().getSubexpediente()){
				res += getDocsInFileRec(e);
			}
		}
		
		return res;
	}
	
	/**
	 * @return the addExchangeFilesPropValue
	 */
	public String getAddExchangeFilesPropValue() {
		return addExchangeFilesPropValue;
	}

	/**
	 * @param addExchangeFilesPropValue the addExchangeFilesPropValue to set
	 */
	public void setAddExchangeFilesPropValue(String addExchangeFilesPropValue) {
		this.addExchangeFilesPropValue = addExchangeFilesPropValue;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setUtils(GdibUtils utils) {
		this.utils = utils;
	}

	public void setExUtils(ExUtils exUtils) {
		this.exUtils = exUtils;
	}

	/**
	 * @param contentService the contentService to set
	 */
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

}
