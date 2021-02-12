package es.caib.gdib.utils;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.StringUtils;

import es.caib.gdib.ws.exception.GdibException;

public class CuadroClasificacionUtils {

	private String rootRM;

	private NodeService nodeService;

    private GdibUtils utils;
    private ExUtils exUtils;

	/**
	 * Recupera todas las categorias de funcion que hay en el RM
	 *
	 * @return lista de noderef
	 * @throws GdibException
	 */
	public List<NodeRef> getAllFunctions() throws GdibException{
		if(StringUtils.isEmpty(rootRM)){
			throw exUtils.configurationRootRMException();
		}
		NodeRef RM = utils.toNodeRef(rootRM);
		List<NodeRef> functions = new ArrayList<NodeRef>();
		Set<QName> childNodeTypeQNames = new HashSet<QName>();
		childNodeTypeQNames.add(RecordsManagementModel.TYPE_RECORD_CATEGORY);
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(RM, childNodeTypeQNames );
		for (ChildAssociationRef child : childs) {
			functions.add(child.getChildRef());
		}
		return functions;
	}

	/**
	 * Recupera todas las series documentales de una funcion
	 *
	 * @return lista de noderef
	 * @throws GdibException
	 */
	public List<NodeRef> getAllDocumentarySeries(String function) throws GdibException{
		NodeRef functionNode = this.getFunction(function);
		if(functionNode != null){
			List<NodeRef> documentarySeries = new ArrayList<NodeRef>();
			Set<QName> childNodeTypeQNames = new HashSet<QName>();
			childNodeTypeQNames.add(RecordsManagementModel.TYPE_RECORD_CATEGORY);
			List<ChildAssociationRef> childs = nodeService.getChildAssocs(functionNode, childNodeTypeQNames );
			for (ChildAssociationRef child : childs) {
				documentarySeries.add(child.getChildRef());
			}
			return documentarySeries;
		}
		return null;
	}

	/**
	 * Recupera la referencia del Nodo de una funcion del RM, buscada por el
	 * nombre
	 *
	 * @param function
	 *            nombre de la funcion dentro del RM
	 * @return nodeRef de la funcion
	 * @throws GdibException
	 */
	public NodeRef getFunction(String function) throws GdibException{
		if(StringUtils.isEmpty(rootRM)){
			throw exUtils.configurationRootRMException();
		}
		NodeRef RM = utils.toNodeRef(rootRM);
		Set<QName> childNodeTypeQNames = new HashSet<QName>();
		childNodeTypeQNames.add(RecordsManagementModel.TYPE_RECORD_CATEGORY);
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(RM, childNodeTypeQNames );
		for (ChildAssociationRef child : childs) {
			NodeRef node = child.getChildRef();
			String name = (String) nodeService.getProperty(node, ContentModel.PROP_NAME);
			if(name.equals(function))
				return node;
		}
		return null;
	}

	/**
	 * Recibiendo el nombre de una serie documental, recupera la funcion a la
	 * que pertenece dentro del RM
	 *
	 * @param documentarySeries
	 *            nombre de la serie documental dentro del RM
	 * @return nodeRef de la funcion dentro del RM
	 * @throws GdibException
	 */
	public NodeRef getFunctionFromDocumentarySeries(String documentarySeries) throws GdibException{
		List<NodeRef> functions = this.getAllFunctions();
		for (NodeRef function : functions) {
			NodeRef series = this.getDocumentarySeriesFromFunction(function, documentarySeries);
			if(series != null)
				return function;
		}
		return null;
	}

	/**
	 * Recibiendo el nombre de una serie documental, busco la serie documental
	 * dentro de una funcion del cuadro de clasificacion del RM
	 *
	 * @param function
	 *            nodeRef de la funcion del RM donde buscar la serie documental
	 * @param documentarySeries
	 *            nombre de la serie documental a buscar
	 * @return noderef de la serie documental, sino la encuentra null
	 */
	public NodeRef getDocumentarySeriesFromFunction(NodeRef function, String documentarySeries){
		Set<QName> childNodeTypeQNames = new HashSet<QName>();
		childNodeTypeQNames.add(RecordsManagementModel.TYPE_RECORD_CATEGORY);
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(function, childNodeTypeQNames );
		for (ChildAssociationRef child : childs) {
			NodeRef node = child.getChildRef();
			String name = (String) nodeService.getProperty(node, ContentModel.PROP_NAME);
			if(name.equals(documentarySeries))
				return node;
		}
		return null;
	}

	/**
	 * Recibiendo el nombre de una serie documental, busco la serie documental
	 * dentro del RM y devuelvo el nodeRef
	 *
	 * @param documentarySeries
	 *            nombre de la serie documental a buscar
	 * @return nodeRef de la serie Documental
	 * @throws GdibException
	 */
	public NodeRef getDocumentarySeries(String documentarySeries) throws GdibException{
		List<NodeRef> functions = this.getAllFunctions();
		for (NodeRef function : functions) {
			NodeRef series = this.getDocumentarySeriesFromFunction(function, documentarySeries);
			if(series != null)
				return series;
		}
		/**
		 * Went out of bucle
		 * recover series from RM
		 */
		return utils.getSerieRMParentByLucene(documentarySeries,ConstantUtils.TYPE_SERIE_QNAME_RM);
		
		
		// }

	}

	/**
	 * Obtengo el periodo de validez juridica de una serie documental y subtipo
	 * de documento. Si el subTypeDoc es nullo, se coge el periodo de validez
	 * juridica de la serie documental y si esta informado este parametro se
	 * coge de el
	 *
	 * @param documentarySeries
	 *            nombre de la serie documental
	 * @param subTypeDoc
	 *            nombre del tipo de documento
	 * @return fecha del periodo de validez juridica
	 */
	public String getLegalValidityPeriod(String documentarySeries, String subTypeDoc){
		// TODO falta implementar
		return null;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRootRM(String rootRM) {
		this.rootRM = rootRM;
	}
	public String getRootRM() {
		return rootRM;
	}

	public void setExUtils(ExUtils exUtils) {
		this.exUtils = exUtils;
	}

	public void setUtils(GdibUtils utils) {
		this.utils = utils;
	}

}