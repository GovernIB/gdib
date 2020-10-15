package es.caib.gdib.schedulejobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

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

	private boolean active; // cronjob activo o no
	private String lucene_query;// Lucene query para obtener los indices
	private String cert_serial;// Serial identifier del certificado de TSA a renovar
	private String tmpDir; // Directorio temporal donde realizar el upgradeo de firma
	private int end_hour; // Hora de fin de trabajo programado ( si la sobrepasa se acaba el trabajo )
	private int start_hour; // hora de inicio del trabajo programdo (limite inferior de ejecuci�n)
	private int max_items; // parametro opcional para limitar el n�mero de resultados de la query
	@Autowired
	private GdibUtils utils;

	/**
	 * M�todo que dispara el m�todo en base al par�metro upgrade.active del fichero
	 * schedule-job-upgrade.properties
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
	 * M�todo que ejecuta el trabajo de upgradeo de �ndices
	 * 
	 * @throws GdibException wrapper para propagar la excepci�n
	 */
	public void run() throws GdibException {
		List<NodeRef> upgradeDocs;
		NodeRef nodeTmp = utils.idToNodeRef(tmpDir);
		FileInfo tmpParentFileInfo = fileFolderService.create(nodeTmp, "tmpIndexFolder", ContentModel.TYPE_FOLDER);

		// Lista para comprobar vigencias
		List<SubTypeDocInfo> resealInfo = subTypeDocUtil.getReselladoInfo();
		Map<String, String> timeLimitMap = new HashMap<>();
		// Relleno el mapa con la informaci�n por Codigo clasificaci�n
		for (SubTypeDocInfo stdi : resealInfo) {
			timeLimitMap.put(stdi.getDocumentarySeries(), stdi.getTimeLimit());
			LOGGER.debug("Inserted into Map KEY" + stdi.getDocumentarySeries() + " TIMELIMT " + stdi.getTimeLimit());
		}

		NodeRef tmpFolder = tmpParentFileInfo.getNodeRef();
		// Si ocurre alg�n error ( la carpeta solo debe crearse para llevar a cabo el
		// trabajo temporal)
		if (tmpFolder == null)
			throw new GdibException("Ocurri� un error creando el directorio temporal");

		upgradeDocs = null;

		try {
			LOGGER.debug("Obteniendo los �ndices");
			// Get Index To Apply TSA upgrade
			upgradeDocs = getDocumentsToUpgrade();
		} catch (GdibException e) {
			LOGGER.error("Error obteniendo los documentos. Excepcion : " + e.getMessage());
			throw new GdibException("Error obteniendo los documentos. Excepcion : " + e.getMessage());
		}

		if (!CollectionUtils.isEmpty(upgradeDocs)) {
			for (NodeRef doc : upgradeDocs) {
				GregorianCalendar calendar = new GregorianCalendar(); // creates a new calendar instance
				int hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
				if (hour >= end_hour && hour < start_hour)
					break;
				// DOC ES �NDICE ORIGINAL
				try {

					ChildAssociationRef rmParentChild = nodeService.getPrimaryParent(doc);
					if (rmParentChild == null)
						continue;
					NodeRef rmParent = rmParentChild.getParentRef();
					String cod_clasif = (String) nodeService.getProperty(rmParent,
							ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
					if (cod_clasif == null) {
						LOGGER.debug("No se encontr� codigo de clasificaci�n en expediente :" + rmParent.getId());
					}
					LOGGER.debug("Comprobando vigencia de expediente " + rmParent.getId()
							+ " con codigo de clasificaci�n " + cod_clasif);
					
					Set<QName> toSearch = new HashSet<>();
					toSearch.add(ConstantUtils.TYPE_FILE_INDEX_QNAME);
					// Obtengo lista de �ndices antiguos que dejar�n de ser v�lidos al completar el
					// proceso
					List<ChildAssociationRef> listaHijos = nodeService.getChildAssocs(rmParent, toSearch);

					if (!checkExpedientReseal(
							(Date) nodeService.getProperty(rmParent, ConstantUtils.PROP_FECHA_FIN_EXP_QNAME),
							timeLimitMap.get(cod_clasif))) {
						for (ChildAssociationRef oldIndex : listaHijos)
						{
							//Recuperamos �ltimo �ndice v�lido y lo seteamos a SI_PERMANENTE
							if ("SI".equals(
									nodeService.getProperty(oldIndex.getChildRef(), ConstantUtils.PROP_INDEX_VALID_QNAME)))
								{
									nodeService.setProperty(oldIndex.getChildRef(), ConstantUtils.PROP_INDEX_VALID_QNAME, "NO");
									break;
								}


						}
						updateOldIndexesValidity(listaHijos);
						LOGGER.debug("No debe resellarse ya que ha pasado el periodo de vigencia");
						continue;
					}

					// copyToTemp
					LOGGER.debug("Upgradeando el sello el documento: " + doc.getId());

					// Mover a carpeta temporal y devuelvo NodeRef de carpeta temporal
					NodeRef tmpExpFolder = this.moveToTmp(rmParent, tmpFolder);
					// Actualizo firma y devuelvo NodeRefs de indices resellados
					List<NodeRef> listIndexes = this.upgradeTSASeal(tmpExpFolder);

					// Actualizo los metadatos necesarios- Se realiza aqui por que asi aseguras el haber generado los nuevos sellos
					updateOldIndexesValidity(listaHijos);

					for (NodeRef newIndex : listIndexes) {
						//nodeService.setProperty(newIndex, ConstantUtils.PROP_INDEX_CERT_DATE_QNAME,
							//	ISO8601DateFormat.format(new Date()));
						nodeService.setProperty(newIndex, ConstantUtils.PROP_INDEX_VALID_QNAME, "SI");
						nodeService.setProperty(newIndex, ConstantUtils.PROP_FECHA_FIN_EXP_QNAME,
								nodeService.getProperty(rmParent, ConstantUtils.PROP_FECHA_FIN_EXP_QNAME));
						nodeService.setProperty(newIndex, ConstantUtils.PROP_FASE_ARCHIVO_QNAME,
								nodeService.getProperty(rmParent, ConstantUtils.PROP_FASE_ARCHIVO_QNAME));

					}

					// Lo devuelvo al RM cambiandole el nombre
					returnToRMExp(listIndexes, rmParent);
					LOGGER.debug("After Return TO RMEXP");

					// Borro la carpeta del indice
					nodeService.deleteNode(tmpExpFolder);

				} catch (GdibException e) {
					LOGGER.error("Error realizando el ugradeo del indice (" + doc.getId() + "). " + e.getMessage());
				} catch (Exception e) {
					LOGGER.error("Error realizando el upgradeo del indice (" + doc.getId() + "). " + e.getMessage());
				}
			}
		}
		// Finalizaci�n del trabajo de upgradeo de �ndices
		LOGGER.debug("Upgrade Finalizado");

		nodeService.deleteNode(tmpFolder);

	}
	/**
	 * M�todo que actualiza el metadato de indice valido de los �ndices que posean de propiedad SI
	 * @param listaHijos Lista de �ndices hijos de cada expediente
	 */
	private void updateOldIndexesValidity(List<ChildAssociationRef> listaHijos) {
		
		for (ChildAssociationRef oldIndex : listaHijos) {
			if ("NO".equals(
					nodeService.getProperty(oldIndex.getChildRef(), ConstantUtils.PROP_INDEX_VALID_QNAME))
					||
					"SI_PERMANENTE".equals(
							nodeService.getProperty(oldIndex.getChildRef(), ConstantUtils.PROP_INDEX_VALID_QNAME))
					)
				continue;

			//nodeService.setProperty(oldIndex.getChildRef(), ConstantUtils.PROP_INDEX_CERT_DATE_QNAME,
				//	ISO8601DateFormat.format(new Date()));
			nodeService.setProperty(oldIndex.getChildRef(), ConstantUtils.PROP_INDEX_VALID_QNAME, "NO");
		}
		
	}

	/**
	 * M�todo que ejecuta la operaci�n de upgradeo/resellado de la firma de un
	 * �ndice
	 * 
	 * @param indexIdentifier Nodo del expediente en el espacio temporal
	 * @throws GdibException      wrapper para propagar la excepci�n
	 * @throws IOException
	 * @throws ContentIOException
	 */
	private List<NodeRef> upgradeTSASeal(NodeRef tempParentRef) throws GdibException, ContentIOException, IOException {

		// Check Cert
		List<NodeRef> listaIndices = new ArrayList<NodeRef>();
		LOGGER.debug("Actualizando firmas de los indices de la carpeta temporal :" + tempParentRef.getId());
		Set<QName> toSearch = new HashSet<>();
		toSearch.add(ConstantUtils.TYPE_FILE_INDEX_QNAME);
		List<ChildAssociationRef> listaHijos = nodeService.getChildAssocs(tempParentRef, toSearch);

		if (!listaHijos.isEmpty()) {
			for (ChildAssociationRef it : listaHijos) {
				if (!"SI".equals(
						(String) nodeService.getProperty(it.getChildRef(), ConstantUtils.PROP_INDEX_VALID_QNAME))
						)
					continue;

				LOGGER.debug("Actualizando firma del nodo : " + it.getChildRef().getId());

				byte[] signature = utils
						.getByteArrayFromHandler(utils.getDataHandler(it.getChildRef(), ContentModel.PROP_CONTENT));

				byte[] newSignature;
				try {
					newSignature = signatureService.upgradeSignature(signature, SignatureFormat.XAdES_A);
				} catch (GdibException e) {
					LOGGER.debug(e.getMessage());
					continue;
				}

				// Afirma5ServiceInvokerFacade.getInstance().invokeService(newSignature,
				// "validate", method, serviceProperties)
				try {
					Document toParseXml = obtenerDocumentDeByte(newSignature);
					toParseXml.getDocumentElement().normalize();
					// LOGGER.debug(parseTimeStamp(toParseXml));

					String certValue = utils.parseTimeStampASN1(toParseXml);
					Date certValidity  = utils.parseTimeStampASN1CertCad(toParseXml);
					nodeService.setProperty(it.getChildRef(), ConstantUtils.PROP_INDEX_CERT_QNAME, certValue);
					nodeService.setProperty(it.getChildRef(), ConstantUtils.PROP_INDEX_CERT_DATE_QNAME, ISO8601DateFormat.format(certValidity));
					LOGGER.debug("Validez del certificado : " + ISO8601DateFormat.format(certValidity));
				} catch (Exception e) {

					LOGGER.debug("Excepcion leyendo XML : " + e.getMessage());
					// active = false;

					throw new GdibException(e.getMessage());
				}

				// Actualizo la firma como contenido
				DataHandler dh = new DataHandler(new InputStreamDataSource(new ByteArrayInputStream(newSignature)));

				utils.setUnsecureDataHandler(it.getChildRef(), ConstantUtils.PROP_CONTENT, dh,
						MimetypeMap.MIMETYPE_BINARY);
				LOGGER.debug("Firma actualizada");

				listaIndices.add(it.getChildRef());

				// break;
			}
		}
		// Devuelvo una lista con los nodeRefs de los �ndices actualizados

		return listaIndices;
	}

	/**
	 * M�todo que ejecuta la b�squeda de aquellos �ndices que deban ser resellados.
	 * La consulta se especifica en el fichero schedule-job-upgrade.properties, en
	 * la property lucene_query
	 * 
	 * @return List<NodeRef> Lista con aquellos �ndices que deban ser resellados
	 * @throws GdibException Wrapper de cualquier excepci�n para propagar
	 */
	private List<NodeRef> getDocumentsToUpgrade() throws GdibException {
		List<NodeRef> result = null;
		// List<SubTypeDocInfo> resealInfo = subTypeDocUtil.getReselladoInfo();
		// String luceneQuery = upgradeIndexPropertiesFilter.getProperty(typeDoc,
		// LUCENE_QUERY_TEMPLATE);
		final SearchParameters params = new SearchParameters();
		params.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		params.setLanguage(SearchService.LANGUAGE_LUCENE);
		int queryResultLength = 0;

		final StringBuilder query = new StringBuilder(400);

		Formatter formatterDocumento = new Formatter(query);
		if (cert_serial != null)
			formatterDocumento.format(lucene_query, cert_serial).toString();
		else
			formatterDocumento.format(lucene_query).toString();

		query.trimToSize();
		LOGGER.debug("Query Upgrading Indexes: " + query.toString());

		params.setQuery(query.toString());

		params.setMaxItems(max_items);
		// query.append(luceneQuery);
		// query.trimToSize();
		// LOGGER.debug("Query: " + lucene_query);

		// params.setQuery(query.toString());

		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(params);
			if (resultSet != null && resultSet.length() > 0) {
				queryResultLength += resultSet.length();
				result = resultSet.getNodeRefs();
				LOGGER.debug("Encontrados " + resultSet.length() + " indices para upgradeSignature");

			}
			LOGGER.info("N�mero de documentos obtenidos al ejecutar la consulta Lucene de upgradear: "
					+ queryResultLength + ".");
		}catch(Exception e){
			LOGGER.error("Ocurrio un error haciendo query de upgradeo de indices :"+query.toString());
			throw new GdibException("Ocurrio un error haciendo query de upgradeo de indices :"+query.toString());
			
		}finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}

		// }

		return result;
	}

	/**
	 * M�todo para migrar un expediente a una carpeta temporal.
	 * 
	 * @param original  Nodo del expediente original a copiar al directorio temporal
	 * @param tmpFolder Referencia al nodo raiz de la carpetatemporal
	 * @return NodeRef of new expedient in TMP folder
	 * @throws GdibException
	 */
	private NodeRef moveToTmp(NodeRef original, NodeRef tmpFolder) throws GdibException {
		NodeRef res = null;

		try {
			// FileInfo resFI = fileFolderService.copy(original, tmpFolder, null);
			// res = resFI.getNodeRef();
			res = importUtils.importExpedientWithTarget(original, tmpFolder);
		} catch (Exception e) {
			LOGGER.debug("UpgradeIndexJob -- moveToTmp:" + e.getMessage());
			throw new GdibException(e.getMessage());
		}
		return res;
	}

	/**
	 * M�todo para migrar una lista de NodeRefs(�ndices) a un expediente en el RM,
	 * previamente obtenidos del mismo, para llevar a cabo el trabajo programado
	 * UpgradeIndex.
	 * 
	 * @param List<NodeRef> updatedIndexes Lista contenedora de los NodeRef de los
	 *                      �ndices a exportar.
	 * @param NodeRef       parent NodeRef destino al que devolver los �ndices
	 * @throws GdibException
	 */
	private void returnToRMExp(List<NodeRef> updatedIndexes, NodeRef parent) throws GdibException {

		try {
			for (NodeRef it : updatedIndexes) {
				changeIndexName(it);
				if (exportUtils == null)
					LOGGER.debug("export Utils was not injected.");
				exportUtils.createRMRecord(it, parent);
			}

		} catch (Exception e) {
			for (StackTraceElement err : e.getStackTrace())
				LOGGER.error(err.getFileName() + " line " + err.getLineNumber());
			LOGGER.debug("Excepcion devolviendo indices al RM: " + e.getMessage());
			throw new GdibException(e.getMessage());

		}
	}

	/**
	 * M�todo privado para cambiar el nombre de un �ndice. Al crearse los �ndices,
	 * se les concatena al final del nombre la fecha en formato YYYYMMDDHHmm , por
	 * lo que la cambiamos y ponemos la fecha con el mismo formato del d�a de
	 * ejecuci�n.
	 * 
	 * @param indexToChange NodeRef del �ndice a cambiar el nombre
	 */
	private void changeIndexName(NodeRef indexToChange) {
		// Change property CM:NAME
		String nodeRefName = (String) nodeService.getProperty(indexToChange, ConstantUtils.PROP_NAME);
		int posExtension = nodeRefName.lastIndexOf(".");
		StringBuilder sb = new StringBuilder();
		sb.append(nodeRefName.substring(0, posExtension - 12));
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmm");
		String strDate = dateFormat.format(date);
		sb.append(strDate);
		sb.append(".xml");
		LOGGER.debug(sb.toString());
		nodeService.setProperty(indexToChange, ConstantUtils.PROP_NAME, sb.toString());

	}

	/**
	 * M�todo auxiliar para parsear un documento XML en forma de byte Array
	 * 
	 * @param documentoXml byte[] que contiene el el resultado de firma
	 * @return Document contenedor del resultado de parsear el xml
	 * @throws Exception en caso de que no sea un XML.
	 */
	private Document obtenerDocumentDeByte(byte[] documentoXml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(documentoXml));
	}

	/**
	 * M�todo auxiliar para saber si un expediente se debe resellar
	 * 
	 * @param fechaFinExp  fecha cierre del expediente
	 * @param diasVigencia dias de vigencia por serie documental
	 * @return true si debe resellarse
	 */
	private boolean checkExpedientReseal(Date fechaFinExp, String diasVigencia) {
		Calendar docLifeTimeCal;
		// Si diasVigencia es nulo se entiende que ha de resellarse
		if (diasVigencia == null)
			return true;
		docLifeTimeCal = Calendar.getInstance();
		docLifeTimeCal.setTime(fechaFinExp);
		docLifeTimeCal.add(Calendar.DAY_OF_YEAR, Integer.valueOf(diasVigencia));
		boolean res = docLifeTimeCal.before(jobRunDate);
		LOGGER.debug("Checking if " + docLifeTimeCal.toString() + " is before than " + jobRunDate.toString());
		return res;

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

	public void setEnd_hour(int end_hour) {
		this.end_hour = end_hour;
	}

	public void setStart_hour(int start_hour) {
		this.start_hour = start_hour;
	}

	public void setMax_items(int max_items) {
		this.max_items = max_items;
	}

	public void setCert_serial(String cert_serial) {
		this.cert_serial = cert_serial;
	}

}
