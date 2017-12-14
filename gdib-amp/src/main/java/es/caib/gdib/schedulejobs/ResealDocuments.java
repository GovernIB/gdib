package es.caib.gdib.schedulejobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import javax.activation.DataHandler;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
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
import org.springframework.extensions.surf.util.ISO8601DateFormat;

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.ExUtils;
import es.caib.gdib.utils.FilterPlaceholderProperties;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.InputStreamDataSource;
import es.caib.gdib.utils.SignatureUtils;
import es.caib.gdib.utils.SubTypeDocInfo;
import es.caib.gdib.utils.SubTypeDocUtil;
import es.caib.gdib.utils.iface.CaibConstraintsUtilsInterface;
import es.caib.gdib.utils.iface.EniModelUtilsInterface;
import es.caib.gdib.ws.common.types.Content;
import es.caib.gdib.ws.common.types.EniSignatureType;
import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.SignatureService;

public class ResealDocuments {


    private static final Logger LOGGER = Logger.getLogger(ResealDocuments.class);

    public static final String STRING_SPLIT = ",";
    public static final String SIGNATURE_FORMAT_CONCATENATE_STRING = "-";
    private static final CharSequence QNAME_PREFIX_SEPARATOR = ":";
    private static final CharSequence QNAME_PREFIX_SEPARATOR_PROPERTY = "_";
    public static final String ALL_SIGNATURE_FORMAT_STRING = "*";

    private static final String FIRMA_MIGRACION = ".firmaMigracion";
    private static final String LIMIT_PERIOD_METHOD = "limit_period_method";
    private static final String RESEAL_FREQ = "reseal_freq";
    private static final String SIGN_FORMAT_ATT = "signformatatt";
    private static final String SIGN_PROFILE_ATT = "signprofileatt";
    private static final String RESEAL_DATE_ATT = "resealdateatt";
    private static final String LUCENE_QUERY_TEMPLATE = "lucene.query.template";

    private static final String CLASIFICATION = "clasification";
    private static final String MIGRATION = "migration";
    /**
     * Parámetro de configuración mediante el que se especifica los formatos de firma electrónica avanzada, basada en certificados, que
     * pueden ser evolucionados a un formato de firma de archivado, siendo la firma a evolucionar ya un formato de archivado. Esta es
     * propiedad es requerida debido a una limitación que presenta la plataforma @firma v6.1.1 que no admite la evolución de firmas de
     * archivado para formatos de firma electrónica CAdES-A y XAdES, aunque si lo permite para PAdES-LTV.
     *
     * Estos formatos serán especificados en notación ENI/eEMGDE, es decir, mediante la concatenación de los valores asignables a los metadatos
     * eni:tipoFirma y eni:perfil_firma del modelo de datos, separados por un guión: "TF02-EPES", "TF02-T", "TF03-C", "TF03-XL", "TF06-LTV", etc.
     * Si no se especifica valor, se supone perfil BES. El valor del parámetro puede ser una lista de elementos separados por comas, la cadena
     * vacía o la cadena "*". En estos dos últimos casos, el proceso de resellado intentará evolucionar todos los formatos de firma admitidos por ENI.
     *
     */
    public static final String SIGNATURE_TYPES_ARCHIVED_TO_UPGRADE_ATT = "signature_types_archived_to_upgrade";
    private String signatureTypeArchivedToUpgrade;

    // fecha de ejecucion del proceso de resellado
    private Date jobRunDate;

    private FilterPlaceholderProperties resealDocumentsPropertiesFilter;
    private NodeService nodeService;
    private SearchService searchService;
    private GdibUtils utils;
    private ExUtils exUtils;
    private SignatureService signatureService;
    private SubTypeDocUtil subTypeDocUtil;

    private RetryingTransactionHelper txnHelper;

    private String typeDoc;
    private boolean active;

    public void execute() {
        // compruebo si el job esta activo, por la property "reseal.active"
        if (active) {
            LOGGER.info("Lanzando el cronjob - Resealing Documents Job");
            jobRunDate = new Date();
            try{
            	run();
            }catch(GdibException e){
            	LOGGER.error("Ha ocurrido un error. " + e.getMessage());
            }
            LOGGER.info("El cronjob ha finalizado");
        } else {
            LOGGER.info("El cronjob no esta activo");
        }
    }

    public void run() throws GdibException{
        List<NodeRef> resealDocuments;
        String auxType,aTypeDoc;
        // realizar la busqueda de los documentos a resellar, la busqueda se realiza por tipo de documento
        String[] typeDocArray = typeDoc.split(STRING_SPLIT);
        for (int i = 0; i < typeDocArray.length; i++) {
            resealDocuments = null;
            aTypeDoc = typeDocArray[i].replace(QNAME_PREFIX_SEPARATOR, QNAME_PREFIX_SEPARATOR_PROPERTY);
            try {
                LOGGER.debug("Obtiendo los ficheros de tipo " + aTypeDoc );
                resealDocuments = this.getDocumentsToResealing(aTypeDoc);
            }catch(GdibException e){
                LOGGER.error("Error obteniendo los documentos del tipo (" + aTypeDoc + "). " + e.getMessage());
                throw new GdibException("Error obteniendo los documentos del tipo (" + aTypeDoc + "). " + e.getMessage());
            }

            if(!CollectionUtils.isEmpty(resealDocuments)){
	            for (NodeRef doc : resealDocuments) {
	                try {
	                	LOGGER.debug("Resellando el documento: " + doc.getId());
	                	QName nodeType = nodeService.getType(doc);
	                	if (utils.isType(nodeType, ConstantUtils.TYPE_FIRMA_MIGRACION_QNAME)){
	                		auxType = ConstantUtils.CAIB_MODEL_PREFIX + QNAME_PREFIX_SEPARATOR + ConstantUtils.TYPE_FIRMA_MIGRACION;
	                		aTypeDoc = auxType.replace(QNAME_PREFIX_SEPARATOR, QNAME_PREFIX_SEPARATOR_PROPERTY);
	                	}
	                	this.resealDocument(doc, aTypeDoc);
	                }catch (GdibException e) {
	                    LOGGER.error("Error realizando el resellado del documento ("+doc.getId()+"). " + e.getMessage());
	                }catch (Exception e){
	                	LOGGER.error("Error realizando el resellado del documento ("+doc.getId()+"). " + e.getMessage());
	                }
	            }
            }
        }
    }

    /**
     * Obtengo los documentos que tienen que resellarse.
     *
     * @param typeDoc
     *            tipo de documentos que se tiene que buscar
     * @return lista con los documentos a resellar
     * @throws
     * @throws GdibException
     */
    protected List<NodeRef> getDocumentsToResealing(String typeDoc) throws GdibException{
        List<NodeRef> res = new ArrayList<NodeRef>();
        String limitPeriodMethod = resealDocumentsPropertiesFilter.getProperty(typeDoc, LIMIT_PERIOD_METHOD);

        LOGGER.debug("Filtrando los resultado por el metodo, limite de periodo: " + limitPeriodMethod );
        switch (limitPeriodMethod) {
            case CLASIFICATION:
                res = this.searchEniDocuments(typeDoc);
                break;
            case MIGRATION:
                res = this.searchMigratedDocuments(typeDoc);
                break;
            default:

                break;
        }

        LOGGER.info("Documentos a resellar: " + res.size());

        return res;
    }

    /**
     * Obtiene una lista de posibles documentos clasificados a resellar (con metadato eni:cod_clasificacion). La búsqueda se realiza
     * con lucene y cada busqueda es distinta según la clasificación documental
     *
     * @param typeDoc
     *            tipo de documentos que se tiene que buscar
     * @return lista con los documentos
     */
    @SuppressWarnings("resource")
    private List<NodeRef> searchEniDocuments(String typeDoc) throws GdibException {
    	Date maxPeriodResealDate = getNextResealDate(typeDoc);
    	int queryResultLength = 0;
    	List<NodeRef> res = new ArrayList<NodeRef>();
    	List<SubTypeDocInfo> resealInfo = subTypeDocUtil.getReselladoInfo();
    	String luceneQuery = resealDocumentsPropertiesFilter.getProperty(typeDoc, LUCENE_QUERY_TEMPLATE);

    	final SearchParameters params = new SearchParameters();
        params.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        params.setLanguage(SearchService.LANGUAGE_LUCENE);

        //+(TYPE:"gdib:documentoMigrado" OR TYPE:"eni:documento") AND NOT ASPECT:"gdib:borrador" AND @eni\\:cod_clasificacion:"%s"
        //AND @eni\\:fecha_sellado:[MIN TO "%s"] AND NOT @eni\\:perfil_firma:"A"
    	for (SubTypeDocInfo info : resealInfo) {
    		final StringBuilder query = new StringBuilder(400);
            Formatter formatterDocumento = new Formatter(query);

            formatterDocumento.format(luceneQuery,
            		info.getDocumentarySeries(),
            		ISO8601DateFormat.format(maxPeriodResealDate),
            		info.getDocumentarySeries())
            		.toString();

            query.trimToSize();
            LOGGER.debug("Query: " + query);

            params.setQuery(query.toString());

            ResultSet resultSet = null;
            try {
                resultSet = searchService.query(params);
                if (resultSet != null && resultSet.length() > 0) {
                	queryResultLength = resultSet.length();

                    for (NodeRef node : resultSet.getNodeRefs()) {
                    	try{
                    		QName nodeType = nodeService.getType(node);
                    		if (utils.isType(nodeType, ConstantUtils.TYPE_DOCUMENTO_QNAME)){
                    			if(checkEniDocResealingConditions(node,info)) {
	                    			//Documento ENI
	                    			res.add(node);
                    			}
                    		} else if(utils.isType(nodeType, ConstantUtils.TYPE_DOCUMENTO_MIGRADO_QNAME)){
                    			//Se obtine el nodo que representa la firma de migración
                    			NodeRef migrationSignatureNode =  getMigratedDocSignature(node);

                    			if(checkMigratedDocResealingConditions(migrationSignatureNode, typeDoc, maxPeriodResealDate)){
                    				//Documento migrado
                    				res.add(migrationSignatureNode);
                    			}
	                    	}
                    	} catch (GdibException e){
                    		LOGGER.warn("Sucedio un error al comprobar si un documento puede ser resellado ("+node.getId()+"): " + e.getMessage(),e);
                    	}
					}
                }
                LOGGER.info( "Número de documentos obtenidos al ejecutar la consulta Lucene: " + queryResultLength + ".");
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
		}

    	return res;
    }

    /**
     * Obtiene una lista de posibles documentos migrados a resellar. La búsqueda se realiza
     * con lucene.
     *
     * @param typeDoc
     *            tipo de documentos que se tiene que buscar
     * @return lista con los documentos migrados candidatos a ser resellados
     */
    @SuppressWarnings("resource")
    private List<NodeRef> searchMigratedDocuments(String typeDoc){
    	List<NodeRef> res = new ArrayList<NodeRef>();
    	Date maxPeriodResealDate = getNextResealDate(typeDoc);
    	int queryResultLength = 0;
    	//+TYPE:"gdib:documentoMigrado" -ASPECT:"gdib:transformado" -@eni\\:cod_clasificacion +(@gdib\\:vigente:"true" OR @gdib\\:fecha_fin_vigencia:[NOW TO MAX])
    	String luceneQuery = resealDocumentsPropertiesFilter.getProperty(typeDoc, LUCENE_QUERY_TEMPLATE);

        // incluyo los parametros de la busqueda
        final SearchParameters params = new SearchParameters();
        params.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        params.setLanguage(SearchService.LANGUAGE_LUCENE);

        final StringBuilder query = new StringBuilder(300);
        Formatter formatterDocumento = new Formatter(query);

        formatterDocumento.format(luceneQuery).toString();

        query.trimToSize();
        LOGGER.debug("Query: " + query);

        params.setQuery(query.toString());

        ResultSet resultSet = null;
        try {
            resultSet = searchService.query(params);
            if (resultSet != null && resultSet.length() > 0) {
            	queryResultLength = resultSet.length();
            	//Se obtiene documentos migrados, pero la firma se encuentra en nodos de tipo gdib:firmaMigracion
                for (NodeRef node : resultSet.getNodeRefs()) {
                	try{
                		NodeRef migrationSignatureNode =  getMigratedDocSignature(node);

                		if(checkMigratedDocResealingConditions(migrationSignatureNode,typeDoc,maxPeriodResealDate)){
                			res.add(migrationSignatureNode);
                		}
                	} catch(Exception e){
                		LOGGER.warn("Sucedio un error al comprobar si un documento migrado puede ser resellado ("+node.getId()+"): " + e.getMessage(),e);
                	}
                }
            }

            LOGGER.info( "Número de documentos obtenidos al ejecutar la consulta Lucene: " + queryResultLength + ".");
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return res;
    }


    /**
     * Obtiene la fecha límite en la que una familia de documentos debe ser resellada a partir de la fecha de ejecución del proceso, y el periodo máximo
     * que puede transcurrir sin ser resellado un documento, fijado por el parámetro reseal.<type_doc>.reseal_freq
     * @param typeDoc familia de documentos: eni:documento o gdib:documentoMigrado.
     * @return la fecha límite en la que una familia de documentos debe ser resellada
     */
    private Date getNextResealDate(String typeDoc){
    	Date res = null;
    	Integer resealFreq;
    	String resealFreqConfParam;

        Calendar resealDateCal = Calendar.getInstance();
        resealDateCal.setTime(jobRunDate);

        resealFreqConfParam = resealDocumentsPropertiesFilter.getProperty(typeDoc, RESEAL_FREQ);
        try{
        	resealFreq = Integer.parseInt(resealFreqConfParam);
            resealDateCal.add(Calendar.DAY_OF_YEAR, -resealFreq);
        } catch(Exception e){
        	LOGGER.warn("No fue posible obtener el periodo máximo sin resellar para los documentos de tipo " + typeDoc +
        			", a partir del parametro de connfiguración reseal." + typeDoc + "." + RESEAL_FREQ + "(valor parámetro: " +
        			resealFreqConfParam + "): " + e.getMessage());
        }
        res = resealDateCal.getTime();

        return res;
    }


    /**
     * Comprueba que un documento cumple las condiciones necesarias para ser resellado. Estas son las siguientes:
     *
     * 	- El formato de firma del documento puede ser evolucionado a un formato de firma de archivado.
     *  - Si se encuentra en RM, se comprueba que no haya expirado el plazo de los valores primarios.
     *
     * @param node documento a resellar
     * @param info Información asociada a la clasificación y valoración documental.
     * @return true, si el documento cumple las condiciones necesarias para ser resellado. En caso contrario, false.
     * @throws GdibException Si ocurre algún error
     */
    private Boolean checkEniDocResealingConditions(NodeRef node, SubTypeDocInfo info) throws GdibException {
    	Boolean res = Boolean.FALSE;
    	Calendar docLifeTimeCal;
    	Integer timeLimit = null;
    	String archivePhase,signatureForm,signatureType;

    	signatureForm = (String) nodeService.getProperty(node, ConstantUtils.PROP_PERFIL_FIRMA_QNAME);
        signatureType = (String) nodeService.getProperty(node, ConstantUtils.PROP_TIPO_FIRMA_QNAME);

        SignatureFormat signatureFormat = SignatureUtils.eniSigntureFormatToInernalSignatureFormat(signatureType, signatureForm);

		if(SignatureFormat.UNRECOGNIZED.equals(signatureFormat)){
			throw new GdibException("Error resellando el documento " + node.getId() + ", no fue posible detectar el formato de firma del documento (Tipo: "
					+signatureType+", Modo: " + signatureForm + ").");
		}

    	if(canBeUpgrade(signatureType,signatureForm)){
	    	archivePhase = (String) nodeService.getProperty(node, EniModelUtilsInterface.PROP_FASE_ARCHIVO_QNAME);
	    	if(CaibConstraintsUtilsInterface.FASE_ARCHIVO_ACTIVO.equals(archivePhase)){
	    		//El documento se encuentra en DM, por lo que debe ser resellado, si cumple condiciones de firma.
	    		res = Boolean.TRUE;
	    	} else {
	    		//El documento se encuentra en RM
	    		//Es necesario comprobar el plazo de prescripción de los valores primarios, propiedad eni:plazo
	    		try{
	    			timeLimit = Integer.parseInt(info.getTimeLimit());
	    			if(timeLimit >= 0){
	    				// si la fecha de entrada en RM + el plazo del valor primario es anterior o igual
	    				//a la fecha de ejecución del proceso, se resella el documento
		    			Date docRmAdmissionDate = getDocRmAdmissionDate(node);

		    			docLifeTimeCal = Calendar.getInstance();
		    			docLifeTimeCal.setTime(docRmAdmissionDate);
		    			docLifeTimeCal.add(Calendar.DAY_OF_YEAR, timeLimit);
	                	res = docLifeTimeCal.before(jobRunDate);
	    			} else {
	    				res = Boolean.TRUE;
	    			}
	    		} catch(Exception e){
	    			//Se entiende que no ha sido establecido un tiempo máximo de vida del valor primario del documento
	    			res = Boolean.TRUE;
	    		}
	    	}
    	}

    	return res;
    }

    /**
     * Comprueba que un documento migrado cumple las condiciones necesarias para ser resellado. Estas son las siguientes:
     *
     * 	- El formato de firma del documento puede ser evolucionado a un formato de firma de archivado.
     *  - Si ya se ha cumplido el periodo máximo sin ser resellado.
     *
     * @param node firma de migración del documento migrado a resellar
     * @param maxPeriodResealDate fecha maxima del periodo en el que debe ser resellado el documento migrado.
     * @return true, si el documento cumple las condiciones necesarias para ser resellado. En caso contrario, false.
     * @throws GdibException Si ocurre algún error
     */
    private Boolean checkMigratedDocResealingConditions(NodeRef node, String typeDoc, Date maxPeriodResealDate) throws GdibException {
    	Boolean res = Boolean.FALSE;
    	Calendar calendar;
    	Date lastResealDate;
    	String signatureForm,signatureType;

    	signatureForm = (String) nodeService.getProperty(node, ConstantUtils.PROP_PERFIL_FIRMA_QNAME);
        signatureType = (String) nodeService.getProperty(node, ConstantUtils.PROP_TIPO_FIRMA_QNAME);

        SignatureFormat signatureFormat = SignatureUtils.eniSigntureFormatToInernalSignatureFormat(signatureType, signatureForm);

		if(SignatureFormat.UNRECOGNIZED.equals(signatureFormat)){
			throw new GdibException("Error resellando el documento " + node.getId() + ", no fue posible detectar el formato de firma del documento (Tipo: "
					+signatureType+", Modo: " + signatureForm + ").");
		}

    	if(canBeUpgrade(signatureType,signatureForm)){
    		//Se obtiene el metadato que aloja la fecha del último resellado del documento
    		String resealDateProp = resealDocumentsPropertiesFilter.getProperty(typeDoc, RESEAL_DATE_ATT);
    		lastResealDate = (Date) nodeService.getProperty(node, GdibUtils.createQName(resealDateProp));
	    	if(lastResealDate == null){
	    		//El documento aún no ha sido resellado
	    		res = Boolean.TRUE;
	    	} else {
	    		//El documento fue resellado, es necesario verificar que ha pasado el tiempo establecido para su resellado
	    		calendar = Calendar.getInstance();
	    		calendar.setTime(maxPeriodResealDate);
	    		res = calendar.after(lastResealDate);
	    	}
    	}

    	return res;
    }

    /**
     * Obtiene la fecha de ingreso de un documento en RM
     * @param node documento
     * @return fecha de ingreso de un documento en RM. Actualmente esta se corresponde con la fecha de cierre del expediente al que pertenece (eni:fecha_fin_exp).
     */
    private Date getDocRmAdmissionDate(NodeRef node) throws GdibException {
    	Date res = null;
    	//Se obtiene la fecha eni:fecha_fin_exp del documento, la cual es establecida en el cierre del expediente al que pertenece
    	res = (Date) nodeService.getProperty(node, ConstantUtils.PROP_FECHA_FIN_EXP_QNAME);

    	if(res == null){
    		throw new GdibException("Error resellando el documento " + node.getId() + ", no fue posible obtener la fecha de ingreso en RM.");
    	}

    	return res;
    }

	/**
	 * Recibiendo un nodo de migracion, obtengo la firma de migracion, que es otro nodo que esta en la misma
	 * carpeta y de nombre el mismo pero terminado por ".firmaMigracion"
	 *
	 * @param node noderef de quien buscar su firma de migracion
	 * @return node firma migracion
	 */
	private NodeRef getMigratedDocSignature(NodeRef node){
		NodeRef parent = nodeService.getPrimaryParent(node).getParentRef();
		String nodeName = (String) nodeService.getProperty(node, ConstantUtils.PROP_NAME);

		NodeRef res = nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS,
				nodeName + FIRMA_MIGRACION);

		return res;
	}


    /**
     * Realiza el resellado de una documento, actualizando su firma al nuevo
     * formato y cambia los metadatos de fecha de sellado y perfil de firma
     *
     * @param node
     *            nodo a resellar
     * @param typeDoc
     *            tipo del nodo
     * @throws GdibException
     * @throws ContentIOException
     * @throws IOException
     */
    protected void resealDocument(NodeRef node, String typeDoc) throws GdibException{
        // genero la firma sobre los documentos --> metodo que devuelve la nueva firma evolucionada (byte[])
        byte[] resealSignature = resealSignature(node);

        // modifico la firma de los documentos y los metadatos
        this.updateResealData(node, resealSignature, typeDoc);

        // informamos en el log el resellado
        LOGGER.info("Resellado el doc con id " + node.getId());
    }

    /**
     * Actualización de la firma electrónica de archivo del documento.
     * Diferenciando que si el documento es migrado, la firma es la firma de
     * migracion, que es un nodo diferente
     *
     * @param node
     *            node a resellar
     * @return la nueva firma resellada
     * @throws GdibException
     */
    protected byte[] resealSignature(NodeRef node) throws GdibException{
        byte[] res;

        // si el documento es migrado la firma se tiene que coger la firma de migracion y sino la firma del documento
        byte[] signature = null;


        LOGGER.debug("Obtengo la firma antigua del documento");
        if (utils.isType(nodeService.getType(node), ConstantUtils.TYPE_FIRMA_MIGRACION_QNAME)) {
            // documento migrado
            // en los documentos migrados la firma es un nodo aparte, por lo que se tiene que coger el contenido de ese nodo
        	LOGGER.debug("Es un nodo de migracion");
            Content content = utils.getContent(node, ConstantUtils.PROP_CONTENT);
			if(content == null){
				throw new GdibException("No se ha podido recuperar la firma de migracion del nodo ("+node.getId()+")");
			}

			signature = utils.getByteArrayFromHandler(content.getData());

        } else {
            // documento eni
        	LOGGER.debug("Es un nodo de documento ENI");
            Content content = utils.getContent(node, ConstantUtils.PROP_FIRMA_QNAME);
			if(content == null){
				throw new GdibException("No se ha podido recuperar la firma del nodo ("+node.getId()+")");
			}
			signature = utils.getByteArrayFromHandler(content.getData());
        }

        String signatureForm = (String) nodeService.getProperty(node, ConstantUtils.PROP_PERFIL_FIRMA_QNAME);
        String signatureType = (String) nodeService.getProperty(node, ConstantUtils.PROP_TIPO_FIRMA_QNAME);
        LOGGER.debug("Se invoca el servicio de @firma para la evolución de la firma electrónica, formato: " + signatureType + " " + signatureForm + ".");

        res = doResealSignature(node.getId(), signature, signatureType, signatureForm);

        LOGGER.debug("Se ha generado la nueva firma");
        return res;
    }

    /**
	 * Actualizacion de la firma electronica de archivo del documento.
	 * @param nodeId Identificador del nodo a resellar.
	 * @param signature firma del documento
	 * @param signatureType formato de firma.
	 * @param signatureForm perfil o modo de firma avanzado.
	 * @return
	 */
	private byte[] doResealSignature(String nodeId, byte[] signature, String signatureType, String signatureForm) throws GdibException {
		byte[] res = null;
		SignatureFormat finalSignatureFormat = null;

		if(signature == null){
			throw new GdibException("Error resellando el documento " + nodeId + ", no fue informada firma electronica del documento.");
		}

		EniSignatureType eniSignatureType = EniSignatureType.valueOf(signatureType.toUpperCase());

		if(eniSignatureType == null){
			throw new GdibException("Error resellando el documento " + nodeId + ", no fue posible detectar el formato de firma del documento (Tipo: "
					+signatureType+", Modo: " + signatureForm + ").");
		}


		if(EniSignatureType.TF02.equals(eniSignatureType) || EniSignatureType.TF03.equals(eniSignatureType)){
			finalSignatureFormat = SignatureFormat.XAdES_A;
		} else if(EniSignatureType.TF04.equals(eniSignatureType) || EniSignatureType.TF05.equals(eniSignatureType)){
			finalSignatureFormat = SignatureFormat.CAdES_A;
		} else if(EniSignatureType.TF06.equals(eniSignatureType)){
			finalSignatureFormat = SignatureFormat.PAdES_LTV;
		} else {
			throw new GdibException("Error resellando el documento " + nodeId + ", formato de firma electronica informado"
					+ " no soportado por el sistema (" +  signatureType + ").");
		}

		res = signatureService.upgradeSignature(signature, finalSignatureFormat);

		return res;
	}

	/**
	 * Verifica si el formato de firma avanzado puede ser evolucionado a un formato de firma de archivado,
	 * en funcion de la configuracion del proceso de resellado (parametro signature_types_archived_to_upgrade).
	 * @param signatureType Tipo de firma electronica, formato ENI ("TF02", "TF03", etc.).
	 * @param signatureForm perfil o modo de firma avanzado, formato ENI ("EPES","T","X", etc.). Si no se especifica valor,
	 * se supone perfil BES.
	 * @return true, si el formato de firma avanzado puede ser evolucionado a un formato de firma de archivado.
	 * En caso contrario, false.
	 */
	private Boolean canBeUpgrade(String signatureType, String signatureForm){
		LOGGER.debug("Verifico que el formado de firma (tipo:"+signatureType+", perfil "+signatureForm+") "
				+ "pueda ser evolucionado a un formato de archivado");
		Boolean res = Boolean.FALSE;
		String signatureFormat;
		String [] staToUpgrade;

		if(signatureTypeArchivedToUpgrade != null &&
				!signatureTypeArchivedToUpgrade.isEmpty() &&
					!signatureTypeArchivedToUpgrade.equals(ALL_SIGNATURE_FORMAT_STRING)){

			staToUpgrade = signatureTypeArchivedToUpgrade.split(STRING_SPLIT);
			signatureFormat = signatureType;
			if(signatureForm != null && !signatureForm.isEmpty()){
				signatureFormat += SIGNATURE_FORMAT_CONCATENATE_STRING + signatureForm;
			}
			signatureFormat = signatureFormat.toUpperCase();

			LOGGER.debug("Formato actual de la firma: " + signatureFormat);

			if(staToUpgrade != null && staToUpgrade.length > 0){
				for(int i=0;!res && i<staToUpgrade.length; i++){
					if(staToUpgrade[i].toUpperCase().equals(signatureFormat)){
						LOGGER.debug("Formato admitido para ser evolucionado a formato de firma de archivo: " + signatureFormat);
						res = Boolean.TRUE;
					}
				}
			}
		} else {
			//No se han especificado formatos especificos, por lo que la firma puede ser evolucionada
			res = Boolean.TRUE;
		}



		return res;
	}

    /**
     * Actualiza el valor de la firma resellada en el nodo, teniendo en cuanta
     * que si es un documento migrado se actualiza la firma de migracion que es
     * un nodo aparte. Tambien se actualizan los metadatos de la fecha de
     * resellado y el perfil de firma
     *
     * @param node
     *            nodo al que se tiene que actualizar la firma
     * @param resealSignature
     *            la nueva firma resellada
     * @param typeDoc
     *            tipo del documento que se esta resellando
     * @throws GdibException
     * @throws ContentIOException
     * @throws IOException
     */
    protected void updateResealData(final NodeRef node, final byte[] resealSignature, final String typeDoc) throws GdibException{
        // aplico la nueva firma
        if(resealSignature == null){
            throw new GdibException("No se ha generado la nueva firma de resellado (null)");
        }

        RetryingTransactionCallback<Void> callback = new RetryingTransactionCallback<Void>()
        {
           public Void execute() throws Throwable
           {
        	   updateSignature(node, resealSignature);

               updateResealDate(node, typeDoc);

               updateSignatureProfile(node, typeDoc);
               return null;
           }
        };
        try
        {
           txnHelper.doInTransaction(callback);
        }
        catch (Exception e)
        {
           throw e;
        }

        // meterlo todo dentro de una transaccion
//        updateSignature(node, resealSignature);
//
//        updateResealDate(node, typeDoc);
//
//        updateSignatureProfile(node, typeDoc);
    }

    private void updateSignature(NodeRef node, byte[] resealSignature) throws GdibException{
    	LOGGER.debug("Guardo la nueva firma");
    	DataHandler signature = new DataHandler(new InputStreamDataSource(new ByteArrayInputStream(resealSignature)));
    	LOGGER.debug("Preparo la nueva firma");
        if (utils.isType(nodeService.getType(node), ConstantUtils.TYPE_FIRMA_MIGRACION_QNAME)) {
            try{
            	LOGGER.debug("Es un nodo de migracion, guardo la nueva firma como metadato contenido");
                utils.setUnsecureDataHandler(node, ConstantUtils.PROP_CONTENT, signature, MimetypeMap.MIMETYPE_BINARY);
                LOGGER.debug("Firma actualizada");
            }catch(IOException exception){
                throw exUtils.setContentException(node.getId(),exception);
            }

        } else {
            try{
            	LOGGER.debug("Es un nodo de eni, guardo la nueva firma como metadato firma");
                utils.setUnsecureDataHandler(node, ConstantUtils.PROP_FIRMA_QNAME, signature, MimetypeMap.MIMETYPE_BINARY);
                LOGGER.debug("Firma actualizada");
            }catch(IOException exception){
                throw exUtils.setContentException(node.getId(),exception);
            }
        }
    }

	/**
	 * Actualizo el nodo con la fecha de la realizacion del nuevo resellado
	 *
	 * @param node
	 *            nodo a actualizar
	 * @throws GdibException
	 */
	private void updateResealDate(NodeRef node, String typeDoc) throws GdibException {
		String resealDateAtt = resealDocumentsPropertiesFilter.getProperty(typeDoc, RESEAL_DATE_ATT);
		nodeService.setProperty(node, GdibUtils.createQName(resealDateAtt), ISO8601DateFormat.format(this.jobRunDate));
		LOGGER.debug("Actualizo a el metadato (" + resealDateAtt + ") con la fecha de ejecucion del proceso "
				+ ISO8601DateFormat.format(this.jobRunDate));
	}

	/**
	 * Actualizo el nodo con el nuevo perfil de firma Si el tipo de firma es
	 * TF02,TF03,TF04,TF05 a perfil de firma A Si el tipo es TF06 al perfil de
	 * firma LTV
	 *
	 * @param node
	 *            nodo a actualizar
	 * @throws GdibException
	 */
	private void updateSignatureProfile(NodeRef node, String typeDoc) throws GdibException {
		// actualizo el perfil de firma
		String signProfileAtt = resealDocumentsPropertiesFilter.getProperty(typeDoc, SIGN_PROFILE_ATT);
		String signFormatAtt = resealDocumentsPropertiesFilter.getProperty(typeDoc, SIGN_FORMAT_ATT);
		String signFormatValue = (String) nodeService.getProperty(node, GdibUtils.createQName(signFormatAtt));

		switch (signFormatValue.toUpperCase()) {
			case "TF02":
			case "TF03":
			case "TF04":
			case "TF05":
				nodeService.setProperty(node, GdibUtils.createQName(signProfileAtt), ConstantUtils.PERFIL_FIRMA_A);
				LOGGER.debug("Actualizo a el metadato (" + signProfileAtt + ") al valor "
						+ ConstantUtils.PERFIL_FIRMA_A);
				break;
			case "TF06":
				nodeService.setProperty(node, GdibUtils.createQName(signProfileAtt), ConstantUtils.PERFIL_FIRMA_LTV);
				LOGGER.debug("Actualizo a el metadato (" + signProfileAtt + ") al valor "
						+ ConstantUtils.PERFIL_FIRMA_LTV);
				break;
		}
	}

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setResealingDocumentsPropertiesFilter(FilterPlaceholderProperties resealingDocumentsPropertiesFilter) {
        this.resealDocumentsPropertiesFilter = resealingDocumentsPropertiesFilter;
    }

    public void setTypeDoc(String typeDoc) {
        this.typeDoc = typeDoc;
    }

    public void setSubTypeDocUtil(SubTypeDocUtil subTypeDocUtil) {
		this.subTypeDocUtil = subTypeDocUtil;
	}

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setUtils(GdibUtils utils) {
        this.utils = utils;
    }

    public void setSignatureTypeArchivedToUpgrade(String signatureTypeArchivedToUpgrade) {
        this.signatureTypeArchivedToUpgrade = signatureTypeArchivedToUpgrade;
    }

    public void setSignatureService(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    public void setExUtils(ExUtils exUtils) {
        this.exUtils = exUtils;
    }

    public void setResealDocumentsPropertiesFilter(FilterPlaceholderProperties resealDocumentsPropertiesFilter) {
        this.resealDocumentsPropertiesFilter = resealDocumentsPropertiesFilter;
    }

    public void setTxnHelper(RetryingTransactionHelper txnHelper) {
		this.txnHelper = txnHelper;
	}
}