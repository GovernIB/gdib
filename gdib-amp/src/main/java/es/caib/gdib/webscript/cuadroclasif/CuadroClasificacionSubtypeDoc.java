package es.caib.gdib.webscript.cuadroclasif;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.bcel.classfile.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.SubTypeDocInfo;
import es.caib.gdib.utils.SubTypeDocUtil;
import es.caib.gdib.ws.exception.GdibException;

public class CuadroClasificacionSubtypeDoc extends DeclarativeWebScript {
	private static final Logger LOGGER = Logger.getLogger(CuadroClasificacionSubtypeDoc.class);

	private SubTypeDocUtil cuadroClasif;

	public void setCuadroClasif(SubTypeDocUtil cuadroClasif) {
		this.cuadroClasif = cuadroClasif;
	}

	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		String method = WebScriptServletRuntime.getHttpServletRequest(req).getMethod();

		switch (method)
		{
			case "GET":
				return executeGETMethod();

			case "POST":
				return executePOSTMethod(req);

			case "DELETE":
				return executeDELETEMethod(req);

		}

		return null;
	}

	private Map<String, Object> executeGETMethod(){
		Map<String, Object> model = new HashMap<String, Object>();
		try {

			List<SubTypeDocInfo> allInfo = cuadroClasif.getAllSubtypedoc();
			model.put("data",allInfo);

		} catch (GdibException e) {
			LOGGER.error(e);
		}
		return model;
	}

	private Map<String, Object> executePOSTMethod(WebScriptRequest req){
		// obtengo la operacion que se va a realizar en la base de datos
		try {
			JSONObject jsonObject = (JSONObject) req.parseContent();

			HashMap<String, Serializable> subtypeDocTableRow = new HashMap<String, Serializable>();

			// obtengo al informacion del cuerpo de la request
			subtypeDocTableRow.put("code_clasificacion", jsonObject.getString("code_clasificacion"));
			subtypeDocTableRow.put("description", jsonObject.getString("description"));

			SubTypeDocInfo info = cuadroClasif.getSubTypeDoc((String)subtypeDocTableRow.get("code_clasificacion"));
			if(info != null)
				cuadroClasif.updateSubtypeDocTableRow(subtypeDocTableRow);
			else{
				// compruebo si hay una serie documental en el cuadro de clasificacion
				info = cuadroClasif.getSubTypeDocInfo((String)subtypeDocTableRow.get("code_clasificacion"), null);
				if(info == null)
					cuadroClasif.insertSubtypeDocRow(subtypeDocTableRow);
				else if(!ConstantUtils.TIPO_DICTAMEN_ET.equals(info.getDictumType()))
					cuadroClasif.insertSubtypeDocRow(subtypeDocTableRow);
				else
					throw new GdibException(I18NUtil.getMessage("cuadro.clasificacion.create.subtypedoc.documentaryseries.et"));
			}
		} catch (JSONException e) {
			throw new AlfrescoRuntimeException(e.getMessage());
		} catch (GdibException e) {
			throw new AlfrescoRuntimeException(e.getMessage());
		} catch (SQLException e) {
			throw new AlfrescoRuntimeException(e.getMessage());
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("result", "true");
		return model;
	}

	private Map<String, Object> executeDELETEMethod(WebScriptRequest req)
	{
		try
		{
			final String code_subtype = req.getExtensionPath();

			cuadroClasif.deleteSubtypeDocRow(code_subtype);

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
