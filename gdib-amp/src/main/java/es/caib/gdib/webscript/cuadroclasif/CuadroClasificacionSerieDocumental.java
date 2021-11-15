package es.caib.gdib.webscript.cuadroclasif;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.SubTypeDocInfo;
import es.caib.gdib.utils.SubTypeDocUtil;
import es.caib.gdib.ws.exception.GdibException;

public class CuadroClasificacionSerieDocumental extends DeclarativeWebScript {
	private static final Logger LOGGER = Logger.getLogger(CuadroClasificacionSerieDocumental.class);
	
	private final String PARAM_FUNCTION = "function";
	private final String PARAM_SERIE_DOCUMENTAL_NAME = "name";
	private final String PARAM_SERIE_DOCUMENTAL_DESCRIPTION = "description";

	private SubTypeDocUtil cuadroClasif;
	private NodeService nodeService;
	private String rootDM;
	private String rootRM;

	public NodeRef getRootRM(){
		return new NodeRef(ConstantUtils.SPACESSTORE_PREFIX + rootRM);
	}

	public void setRootRM(String rootRM) {
		this.rootRM = rootRM;
	}

	public NodeRef getRootDM(){
		return new NodeRef(ConstantUtils.SPACESSTORE_PREFIX + rootDM);
	}

	public void setRootDM(String rootDM) {
		this.rootDM = rootDM;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setCuadroClasif(SubTypeDocUtil cuadroClasif) {
		this.cuadroClasif = cuadroClasif;
	}

	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		String method = WebScriptServletRuntime.getHttpServletRequest(req).getMethod();

		switch (method)
		{
			case "GET":
				return executeGETMethod(req);

			case "DELETE":
				return executeDELETEMethod(req);
		}

		return null;
	}

	private Map<String, Object> executeGETMethod(WebScriptRequest req)
	{
		String function = req.getParameter(PARAM_FUNCTION);
		String serieDocumental = req.getParameter(PARAM_SERIE_DOCUMENTAL_NAME);
		String serieDocumentalDes = req.getParameter(PARAM_SERIE_DOCUMENTAL_DESCRIPTION);

		// si vienen informados los parametros de funcion o name o descripcion de la serie documental
		// es que se viene del formulario de creacion de la serie documental
		if(!StringUtils.isEmpty(function)
				|| !StringUtils.isEmpty(serieDocumental)
						|| !StringUtils.isEmpty(serieDocumentalDes))
		{
			return executeGETMethodForm(function, serieDocumental, serieDocumentalDes);
		}

		// Se recupera la informacino de base de datos de la tabla de series documentales
		Map<String, Object> model = new HashMap<String, Object>();
		try {

			List<SubTypeDocInfo> allInfo = cuadroClasif.getAllDocumentalSeries();
			model.put("data",allInfo);

		} catch (GdibException e) {
			LOGGER.error(e);
		}
		return model;
	}

	private Map<String, Object> executeGETMethodForm(String function, String code_clasificacion, String description)
	{
		try
		{
			// creacion en alfresco de las carpetas de la serie tanto en DM como en RM
			createSerieDocumentalInAlfresco(function, code_clasificacion, description);

			// creacion en base de datos de la entrada en la tabla de series documentales
			HashMap<String, Serializable> tableRow = new HashMap<String, Serializable>();
			tableRow.put("code_clasificacion", code_clasificacion);
			tableRow.put("description", description);
			cuadroClasif.insertDocumentalSerieRow(tableRow);
		}
		catch (GdibException e)
		{
			throw new AlfrescoRuntimeException(e.getMessage());
		}
		catch (SQLException e)
		{
			throw new AlfrescoRuntimeException(e.getMessage());
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("code_clasificacion", code_clasificacion);
		model.put("description", description);
		return model;
	}

	@SuppressWarnings("unused")
	private void createSerieDocumentalInAlfresco(final String function, final String name, final String title)
	{
		String result = AuthenticationUtil.runAs(new RunAsWork<String>() {
			// @Override
			public String doWork() throws Exception {
				NodeRef functionNodeDM = nodeService.getChildByName(getRootDM(), ContentModel.ASSOC_CONTAINS, function);
				NodeRef functionNodeRM = nodeService.getChildByName(getRootRM(), ContentModel.ASSOC_CONTAINS, function);
				Map<QName, Serializable> props = new HashMap<QName, Serializable>();
				props.put(ContentModel.PROP_NAME, name);
				props.put(ContentModel.PROP_TITLE,  title);
				ChildAssociationRef createdChildRefDM = nodeService.createNode(functionNodeDM,
			                ContentModel.ASSOC_CONTAINS,
			                QName.createQName(NamespaceService.DICTIONARY_MODEL_1_0_URI, (String)name),
			                ConstantUtils.TYPE_FOLDER,
			                props);

				ChildAssociationRef createdChildRefRM = nodeService.createNode(functionNodeRM,
			                ContentModel.ASSOC_CONTAINS,
			                QName.createQName(NamespaceService.DICTIONARY_MODEL_1_0_URI, (String)name),
			                RecordsManagementModel.TYPE_RECORD_CATEGORY,
			                props);
				return "true";
			}
    	}, ConstantUtils.USER_ADMIN);

	}

	private Map<String, Object> executeDELETEMethod(WebScriptRequest req)
	{
//		/gdib/cuadro/seriedocumental/{code_clasificacion}/
		try {
			final String code_clasificacion = req.getExtensionPath();

			cuadroClasif.deleteDocumentalSerieRow(code_clasificacion);

		} catch (GdibException e) {
			throw new AlfrescoRuntimeException(e.getMessage());
		} catch (SQLException e) {
			throw new AlfrescoRuntimeException(e.getMessage());
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("result", "true");
		return model;
	}

}
