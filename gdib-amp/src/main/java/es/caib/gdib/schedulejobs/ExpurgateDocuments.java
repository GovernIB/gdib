package es.caib.gdib.schedulejobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.extensions.surf.util.ISO8601DateFormat;

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.FilterPlaceholderProperties;
import es.caib.gdib.utils.SubTypeDocInfo;
import es.caib.gdib.utils.SubTypeDocUtil;
import es.caib.gdib.utils.iface.CaibConstraintsUtilsInterface;
import es.caib.gdib.utils.iface.EniModelUtilsInterface;
import es.caib.gdib.ws.exception.GdibException;

public class ExpurgateDocuments {

    private static final Logger LOGGER = Logger.getLogger(ExpurgateDocuments.class);

    private static final String LUCENE_GROUP = "lucene";

    private static final String LUCENE_QUERY_TEMPLATE_PROP = "query.template";

    // fecha de ejecucion del proceso de resellado
    private Date jobRunDate;

    private FilterPlaceholderProperties expurgateDocumentsPropertiesFilter;

    private NodeService nodeService;

	private DictionaryService dictionaryService;

    private SearchService searchService;

    private SubTypeDocUtil subTypeDocUtil;

    private boolean active;

    public void execute() {
        // compruebo si el job esta activo, por la property "expurgate.active"
        if (active) {
            LOGGER.info("Lanzando el cronjob - Expurgate Documents Job");
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

    protected void run() throws GdibException {
    	String luceneQuery,newArchivedStatus;

    	luceneQuery = expurgateDocumentsPropertiesFilter.getProperty(LUCENE_GROUP, LUCENE_QUERY_TEMPLATE_PROP);
    	LOGGER.info("Se inicia la tarea programa de expurgo");

    	LOGGER.info("Se obtienen las series documentales cuyos expedientes y documentos pueden ser expurgados...");

    	//Se obtienen las series documentales cuyos expedientes y documentos pueden ser expurgados.
    	//Es decir, aquellas cuyo acción dictamianda es eliminación (Tipo dictamen 'ET' o 'EP').
    	List<SubTypeDocInfo> subTypeDocInfoList = subTypeDocUtil.getDocumentarySeriesToBeExpurgated();
    	for (SubTypeDocInfo subTypeDocInfo : subTypeDocInfoList) {
    		LOGGER.info("Buscando los documentos de la serie documental " +subTypeDocInfo.getDocumentarySeries()+ ","
    					+ " y subtipo "+subTypeDocInfo.getSubtypeDoc()+".");

    		List<NodeRef> filesToBeExpurgated = searchNodesToBeExpurgated(luceneQuery, subTypeDocInfo);

    		if(!CollectionUtils.isEmpty(filesToBeExpurgated)){
    			for(NodeRef file : filesToBeExpurgated) {
    				if(CaibConstraintsUtilsInterface.TIPO_DICTAMEN_EP.equals(subTypeDocInfo.getDictumType())){
    		    		newArchivedStatus = CaibConstraintsUtilsInterface.ESTADO_ARCHIVO_PENDIENTE_ELIMINACION_PARCIAL;

    		    	} else if(CaibConstraintsUtilsInterface.TIPO_DICTAMEN_ET.equals(subTypeDocInfo.getDictumType())){
    		    		newArchivedStatus = CaibConstraintsUtilsInterface.ESTADO_ARCHIVO_PENDIENTE_ELIMINACION_TOTAL;
    		    	} else {
    		    		throw new GdibException("El tipo de dictamen de la serie documental " + subTypeDocInfo.getDocumentarySeries() + ", al cual pertenece el expediente " +
    		    				file.getId() + ", no permite el expurgo del mismo (" + subTypeDocInfo.getDictumType() + ").");
    		    	}

    		    	LOGGER.info("Se procede a modificar el estado de archivo del expediente " + file.getId() + ". Nuevo estado: " + newArchivedStatus + ".");
    		    	LOGGER.info("Se procede a modificar el estado de archivo del contenido del expediente ...");

    		    	setFileArchivedStatus(file,nodeService.getChildAssocs(file),subTypeDocInfo,newArchivedStatus);
    		    	nodeService.setProperty(file, ConstantUtils.PROP_FECHA_MARCA_EXPURGO_QNAME, ISO8601DateFormat.format(jobRunDate));
    		    	LOGGER.info("Estado de archivo del expediente " + file.getId() + " modificado.");
    			}
    		}
    	}
    }

    /**
     * Método que efectúa una búsqueda Lucene sobre los documentos en fase de archivo, para obtener aquellos que deben ser expurgados.
     * @param queryTemplate plantilla de la consulta Lucene que se ejecutará sobre el SGD.
     * @param subTypeDocInfo Información de clasificación y valoración documental.
     * @return lista de nodos que serán expurgados (marcados con estado de archivo de eliminación parcial o total).
     */
    @SuppressWarnings("resource")
	protected List<NodeRef> searchNodesToBeExpurgated(String queryTemplate, SubTypeDocInfo subTypeDocInfo){
    	Date maxExpurgateDate;
    	int queryResultLength = 0;
    	List<NodeRef> res = new ArrayList<NodeRef>();

    	final SearchParameters params = new SearchParameters();
        params.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        params.setLanguage(SearchService.LANGUAGE_LUCENE);

        // Se calcula la fecha límite superior del periodo de expurgo que ira incluido en la consulta Lucene
        maxExpurgateDate = getMaxExpurgateDate(subTypeDocInfo);

		final StringBuilder query = new StringBuilder(400);
		Formatter formatter = new Formatter(query);
		formatter.format(
				queryTemplate,
				subTypeDocInfo.getDocumentarySeries(),
				ISO8601DateFormat.format(maxExpurgateDate)).toString();

		//PATH:"/app:company_home/st:sites/cm:rm//*" AND TYPE:"eni:expediente" AND @eni\\:cod_clasificacion:"%s" AND
		//(@eni\\:estado_archivo:"Ingresado" OR @eni\\:estado_archivo:"Trasnferido") AND @eni\\:fecha_fin_exp:[MIN TO %s]
        query.trimToSize();
        LOGGER.info("Consulta Lucene ejecutada: " + query);

        params.setQuery(query.toString());

        ResultSet resultSet = null;
        try {
            resultSet = searchService.query(params);
            if (resultSet != null && resultSet.length() != 0) {
            	queryResultLength = resultSet.length();
            	res = resultSet.getNodeRefs();
            }
            LOGGER.info("Número de documentos obtenidos al ejecutar la consulta Lucene de expurgo documental: " + queryResultLength + ".");
        }finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }

    	return res;
    }

    /**
     * Establecer el nuevo estado de archivo de los expedientes a expurgar, incluyendo su contenido.
     * @param file Nodo del SGD
     * @param subTypeDocInfo Información de clasificación y valoración documental.
     * @throws GdibException
     */
    private void setFileArchivedStatus(NodeRef nodeRef, List<ChildAssociationRef> childNodes, SubTypeDocInfo subTypeDocInfo, String newFileArchivedStatus) throws GdibException {

    	try{
			if(!CollectionUtils.isEmpty(childNodes)){
				for(Iterator<ChildAssociationRef> it = childNodes.iterator();it.hasNext();){
					ChildAssociationRef childAssociationRef = it.next();
					NodeRef node = childAssociationRef.getChildRef();
					LOGGER.debug("Se procede a modificar el estado de archivo del nodo " + node.getId() + ".");

					if(dictionaryService.isSubClass(nodeService.getType(node), ConstantUtils.TYPE_DOCUMENTO_QNAME)){
						if(!subTypeDocInfo.getVitalDocument()){
							nodeService.setProperty(node, EniModelUtilsInterface.PROP_ESTADO_ARCHIVO_QNAME,
									newFileArchivedStatus);
							LOGGER.info("Nuevo estado de archivo del nodo " + nodeRef.getId() + ": "+
									newFileArchivedStatus +".");
						}
					}
				}
			}

			// modifico el estado del archivo nodo raiz, que es el expedientes, pero en RM es de tipo "rma:recordFolder"
			nodeService.setProperty(nodeRef, EniModelUtilsInterface.PROP_ESTADO_ARCHIVO_QNAME, newFileArchivedStatus);
			LOGGER.info("Nuevo estado de archivo del nodo " + nodeRef.getId() + ": "+ newFileArchivedStatus +".");
    	} catch(Exception e){
    		throw new GdibException("Expurgo - Se produjo un error al modificar el estado de archivo del nodo " +
    				nodeRef.getId() + ": " + e.getMessage(),e);
    	}
    }


    /**
     * Obtiene la fecha límite superior en la que los expedientes y documentos de una determinada serie documental
     * deben ser expurgados a partir de la fecha de ejecución del proceso.
     * @param subTypeDocInfo Información de clasificación y valoración documental
     * @return la fecha límite superior en la que los expedientes y documentos de una determinada serie documental
     * deben ser expurgados a partir de la fecha de ejecución del proceso.
     */
    private Date getMaxExpurgateDate(SubTypeDocInfo subTypeDocInfo){
    	Date res = null;
    	Integer expurgateTimeLimit;
    	String expurgateTimeLimitString;

        Calendar expurgateDateCal = Calendar.getInstance();
        expurgateDateCal.setTime(jobRunDate);
        expurgateTimeLimitString = subTypeDocInfo.getTermDictatedAction();

        try{
        	expurgateTimeLimit = Integer.parseInt(expurgateTimeLimitString);
        	expurgateDateCal.add(Calendar.DAY_OF_YEAR, -expurgateTimeLimit);
        } catch(Exception e){
        	LOGGER.warn("No fue posible obtener el periodo máximo de conservación de la serie documental " +
        			subTypeDocInfo.getDocumentarySeries() + ": " + e.getMessage());
        }

        res = expurgateDateCal.getTime();

        return res;
    }


    public void setActive(boolean active) {
		this.active = active;
	}

    public void setExpurgateDocumentsPropertiesFilter(FilterPlaceholderProperties expurgateDocumentsPropertiesFilter) {
		this.expurgateDocumentsPropertiesFilter = expurgateDocumentsPropertiesFilter;
	}

    public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

    /**
	 * @param dictionaryService the dictionaryService to set
	 */
	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

    public void setSubTypeDocUtil(SubTypeDocUtil subTypeDocUtil) {
		this.subTypeDocUtil = subTypeDocUtil;
	}

}