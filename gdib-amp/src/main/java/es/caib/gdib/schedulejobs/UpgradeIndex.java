package es.caib.gdib.schedulejobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.gdib.rm.utils.ExportUtils;
import es.caib.gdib.rm.utils.ImportUtils;
import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.FilterPlaceholderProperties;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.InputStreamDataSource;
import es.caib.gdib.utils.SubTypeDocInfo;
import es.caib.gdib.utils.SubTypeDocUtil;
import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.SignatureService;

public class UpgradeIndex {
	private static final Logger LOGGER = Logger.getLogger(UpgradeIndex.class);
	public static final String STRING_SPLIT = ",";

	private Date jobRunDate;

	private FilterPlaceholderProperties upgradeIndexPropertiesFilter;
	private NodeService nodeService;
	private SubTypeDocUtil subTypeDocUtil;
	private SearchService searchService;
	private FileFolderService fileFolderService;
	private SignatureService signatureService;
	private ImportUtils importUtils;
	private ExportUtils exportUtils;
	
	private boolean active; // Whether cron is active or not
	private String lucene_query;// Lucene query to obtain indexes
	private String tmpDir; // Tmp dir to export Indexes and upgrade Signature

	@Autowired
	private GdibUtils utils;

	/**
	 * Método que dispara el método en base al parámetro upgrade.active del fichero schedule-job-upgrade.properties
	 */
	public void execute() {
		// compruebo si el job esta activo, por la property "upgrade.active"
		if (active) {
			LOGGER.info("Lanzando el cronjob - UpgradeIndex Job");
			jobRunDate = new Date();
			try {
				run();
			} catch (GdibException e) {
				LOGGER.error("Ha ocurrido un error. " + e.getMessage());
			}
			LOGGER.info("El cronjob ha finalizado");
		} 
	}

	/**
	 * Método que ejecuta el trabajo de upgradeo de índices
	 * 
	 * @throws GdibException wrapper para propagar la excepción
	 */
	public void run() throws GdibException {
		List<NodeRef> upgradeDocs;
		NodeRef nodeTmp = utils.idToNodeRef(tmpDir);
		FileInfo tmpParentFileInfo = fileFolderService.create(nodeTmp, "tmpIndexFolder", ContentModel.TYPE_FOLDER);
		NodeRef tmpFolder = tmpParentFileInfo.getNodeRef();
		//Si ocurre algún error ( la carpeta solo debe crearse para llevar a cabo el trabajo temporal)
		if(tmpFolder == null)
			throw new GdibException("Ocurrió un error creando el directorio temporal");
		
		upgradeDocs = null;

		try {
			LOGGER.debug("Obtiendo los índices");
			// Get Index To Apply TSA upgrade
			upgradeDocs = getDocumentsToUpgrade();
		} catch (GdibException e) {
			LOGGER.error("Error obteniendo los documentos. Excepcion : " + e.getMessage());
			throw new GdibException("Error obteniendo los documentos. Excepcion : " + e.getMessage());
		}

		if (!CollectionUtils.isEmpty(upgradeDocs)) {
			for (NodeRef doc : upgradeDocs) {

				// DOC ES ÍNDICE ORIGINAL
				try {
					LOGGER.debug("Upgradeando el sello el documento: " + doc.getId());

					ChildAssociationRef rmParentChild = nodeService.getPrimaryParent(doc);
					if (rmParentChild == null)
						continue;
					NodeRef rmParent = rmParentChild.getParentRef();
					// copyToTemp
					// Mover a carpeta temporal y devuelvo NodeRef de carpeta temporal
					NodeRef tmpExpFolder = this.moveToTmp(rmParent,tmpParentFileInfo.getNodeRef());
					// Actualizo firma y devuelvo NodeRefs de indices resellados
					List<NodeRef> listIndexes= this.upgradeTSASeal(tmpExpFolder);
					//Actualizo metadatos de los antiguos y nuevos
					
					// Return to original and change name
					returnToRMExp(listIndexes,rmParent);
					
					//Actualizo metadatos de los antiguos y nuevos
					
					//Delete tmp folder					
					nodeService.deleteNode(tmpParentFileInfo.getNodeRef());
					//Add Metadata
					
					active = false;
					break;
				} catch (GdibException e) {
					LOGGER.error(
							"Error realizando el ugradeo del indice (" + doc.getId() + "). " + e.getMessage());
					active = false;
					break;
				} catch (Exception e) {
					LOGGER.error(
							"Error realizando el upgradeo del indice (" + doc.getId() + "). " + e.getMessage());
					active = false;
					break;
				}
			}
		}
		//Finalización del trabajo de upgradeo de índices
		LOGGER.debug("Upgrade Finalizado");
	}

	/**
	 * Método que ejecuta la operación de upgradeo/resellado de la firma de un
	 * índice
	 * 
	 * @param indexIdentifier Nodo del expediente en el espacio temporal
	 * @throws GdibException wrapper para propagar la excepción
	 * @throws IOException 
	 * @throws ContentIOException 
	 */
	private List<NodeRef> upgradeTSASeal(NodeRef tempParentRef) throws GdibException, ContentIOException, IOException {

		// Check Cert
		List<NodeRef> listaIndices = new ArrayList<NodeRef>();
		LOGGER.debug("Actualizando firmas de los indices de la carpeta temporal :" + tempParentRef.getId());
		Set<QName> toSearch = new HashSet<>();
		toSearch.add(ConstantUtils.TYPE_FILE_INDEX_QNAME);
		List<ChildAssociationRef> listaHijos  = nodeService.getChildAssocs(tempParentRef, toSearch);
		if(!listaHijos.isEmpty())
		{	
			for(ChildAssociationRef it : listaHijos)
			{
				//Call Upgrade Signature
				LOGGER.debug("Actualizando firma del nodo : "+it.getChildRef().getId());
				//retrieve answer data (check serial)
				
    			byte[] signature = utils.getByteArrayFromHandler(utils.getDataHandler(it.getChildRef(), ContentModel.PROP_CONTENT));
    			//signatureService.verifySignature(null, signature);
    			//byte[] newSignature  = signatureService.signXadesDocument(signature, SignatureFormat.XAdES_A, null, null);
    			byte[] newSignature = signatureService.upgradeSignature(signature, SignatureFormat.XAdES_A);
    			active = false;
    			//set new Signature
    			signatureService.verifySignature(null, newSignature);
    	    	DataHandler dh = new DataHandler(new InputStreamDataSource(new ByteArrayInputStream(newSignature)));

                utils.setUnsecureDataHandler(it.getChildRef(), ConstantUtils.PROP_CONTENT, dh, MimetypeMap.MIMETYPE_BINARY);
                LOGGER.debug("Firma actualizada");
                
                
                listaIndices.add(it.getChildRef());
                
    			//break;
			}
		}
		// Upgrade Signature / call SignXades Document

		return listaIndices;
	}

	/**
	 * Método que ejecuta la búsqueda de aquellos índices que deban ser resellados.
	 * La consulta se especifica en el fichero schedule-job-upgrade.properties, en
	 * la property lucene_query
	 * 
	 * @return List<NodeRef> Lista con aquellos índices que deban ser resellados
	 * @throws GdibException Wrapper de cualquier excepción para propagar
	 */
	private List<NodeRef> getDocumentsToUpgrade() throws GdibException {
		List<NodeRef> result = null;
		List<SubTypeDocInfo> resealInfo = subTypeDocUtil.getReselladoInfo();
		// String luceneQuery = upgradeIndexPropertiesFilter.getProperty(typeDoc,
		// LUCENE_QUERY_TEMPLATE);
		final SearchParameters params = new SearchParameters();
		params.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		params.setLanguage(SearchService.LANGUAGE_LUCENE);
		int queryResultLength = 0;
		// +(TYPE:"gdib:indiceExpediente") AND @eni\\:cod_clasificacion:"%s"
		for (SubTypeDocInfo info : resealInfo) {
			final StringBuilder query = new StringBuilder(400);

			Formatter formatterDocumento = new Formatter(query);

			formatterDocumento.format(lucene_query, info.getDocumentarySeries()).toString();

			query.trimToSize();
			LOGGER.debug("Query Upgrading Indexes: " + lucene_query);

			params.setQuery(lucene_query);

			// query.append(luceneQuery);
			query.trimToSize();
			LOGGER.debug("Query: " + lucene_query);

			// params.setQuery(query.toString());

			ResultSet resultSet = null;
			try {
				resultSet = searchService.query(params);
				if (resultSet != null && resultSet.length() > 0) {
					queryResultLength += resultSet.length();
					result = resultSet.getNodeRefs();
					LOGGER.debug("Found " + resultSet.length() + " total indexes from cod serie:"
							+ info.getDocumentarySeries() + " to upgradeSignature");

				}
				LOGGER.info("Número de documentos obtenidos al ejecutar la consulta Lucene de upgradear: "
						+ queryResultLength + ".");
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}
			break;

		}

		return result;
	}
	
	/**
	 * Método para migrar un expediente a una carpeta temporal.
	 * @param original Nodo del expediente original a copiar al directorio temporal
	 * @param tmpFolder Referencia al nodo raiz de la carpetatemporal
	 * @return
	 * @throws GdibException
	 */
	private NodeRef moveToTmp(NodeRef original,NodeRef tmpFolder) throws GdibException
    {
    	NodeRef res = null;

    	try {
			//FileInfo resFI = fileFolderService.copy(original, tmpFolder, null);
			//res = resFI.getNodeRef();
    		res = importUtils.importExpedientWithTarget(original, tmpFolder);
		} catch (Exception e) {
			LOGGER.debug("UpgradeIndexJob -- moveToTmp:"+e.getMessage());
			throw new GdibException(e.getMessage());
		}
    	return res;
    }
	
	/**
	 * Método para migrar una lista de NodeRefs(índices) a un expediente en el RM, previamente obtenidos del mismo, para llevar a cabo el trabajo
	 * programado UpgradeIndex.
	 * @param updatedIndexes Lista contenedora de los NodeRef de los índices a exportar.
	 * @param parent NodeRef destino al que devolver los índices
	 * @throws GdibException
	 */
	private void returnToRMExp(List<NodeRef> updatedIndexes, NodeRef parent) throws GdibException{
		
		try {
			for(NodeRef it : updatedIndexes)
			{
				changeIndexName(it);
				exportUtils.createRMRecord(it, parent);
			}
			
		}catch(Exception e)
		{
			LOGGER.debug("Excepcion devolviendo indices al RM: "+e.getMessage());
			throw new GdibException(e.getMessage());
			
		}	
	}
	/**
	 * Método privado para cambiar el nombre de un índice. Al crearse los índices, se les concatena
	 * al final del nombre la fecha en formato YYYYMMDDHHmm , por lo que la cambiamos y ponemos la fecha con el mismo formato
	 * del día de ejecución.
	 * @param indexToChange NodeRef del índice a cambiar el nombre
	 */
	private void changeIndexName(NodeRef indexToChange)
	{
		//Change property CM:NAME
		String nodeRefName = (String) nodeService.getProperty(indexToChange, ConstantUtils.PROP_NAME);
		int posExtension = nodeRefName.lastIndexOf(".");
		StringBuilder sb= new StringBuilder();
		sb.append(nodeRefName.substring(0,posExtension-12));
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmm");
		String strDate = dateFormat.format(date);
		sb.append(strDate);
		sb.append(".xml");
		LOGGER.debug(sb.toString());
		nodeService.setProperty(indexToChange, ConstantUtils.PROP_NAME, sb.toString());
		
	}
	
	public void setJobRunDate(Date jobRunDate) {
		this.jobRunDate = jobRunDate;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setUpgradeIndexPropertiesFilter(FilterPlaceholderProperties upgradeIndexPropertiesFilter) {
		this.upgradeIndexPropertiesFilter = upgradeIndexPropertiesFilter;
	}

	public void setSubTypeDocUtil(SubTypeDocUtil subTypeDocUtil) {
		this.subTypeDocUtil = subTypeDocUtil;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setLucene_query(String lucene_query) {
		this.lucene_query = lucene_query;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public void setImportUtils(ImportUtils importUtils) {
		this.importUtils = importUtils;
	}

	public void setSignatureService(SignatureService signatureService) {
		this.signatureService = signatureService;
	}

	public void setExportUtils(ExportUtils exportUtils) {
		this.exportUtils = exportUtils;
	}

}
