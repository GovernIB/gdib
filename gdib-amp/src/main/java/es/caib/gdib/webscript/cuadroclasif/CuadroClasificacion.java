package es.caib.gdib.webscript.cuadroclasif;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
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

public class CuadroClasificacion extends DeclarativeWebScript {
	private static final Logger LOGGER = Logger.getLogger(CuadroClasificacion.class);
    private SubTypeDocUtil cuadroClasif;

    public void setCuadroClasif(SubTypeDocUtil cuadroClasif) {
        this.cuadroClasif = cuadroClasif;
    }

    protected Map<String, Object> executeImpl(WebScriptRequest req,
            Status status, Cache cache) {

        String method = WebScriptServletRuntime.getHttpServletRequest(req).getMethod();
        switch (method) {
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

            List<SubTypeDocInfo> allInfo = cuadroClasif.getAllInfo();
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

            HashMap<String, Serializable> clasificationTableRow = new HashMap<String, Serializable>();

            // obtengo al informacion del cuerpo de la request
            clasificationTableRow.put("code_clasificacion", getJsonObjectValue(jsonObject, "code_clasificacion"));
            clasificationTableRow.put("code_subtype", getJsonObjectValue(jsonObject, "code_subtype"));
            clasificationTableRow.put("lopd", getJsonObjectValue(jsonObject, "lopd"));
            clasificationTableRow.put("confidentiality", getJsonObjectValue(jsonObject, "confidentiality"));
            clasificationTableRow.put("accesstype", getJsonObjectValue(jsonObject, "accesstype"));
            clasificationTableRow.put("causelimitationcode", getJsonObjectValue(jsonObject, "causelimitationcode"));
            clasificationTableRow.put("normative", getJsonObjectValue(jsonObject, "normative"));
            clasificationTableRow.put("reutilizationcond", getJsonObjectValue(jsonObject, "reutilizationcond"));
            clasificationTableRow.put("valuetype", getJsonObjectValue(jsonObject, "valuetype"));
            clasificationTableRow.put("term", getJsonObjectValue(jsonObject, "term"));
            clasificationTableRow.put("secundaryvalue", getJsonObjectValue(jsonObject, "secundaryvalue"));
            clasificationTableRow.put("dictumtype", getJsonObjectValue(jsonObject, "dictumtype"));
            clasificationTableRow.put("dictatedaction", getJsonObjectValue(jsonObject, "dictatedaction"));
            clasificationTableRow.put("termdictatedaction", getJsonObjectValue(jsonObject, "termdictatedaction"));
            clasificationTableRow.put("designationclass", getJsonObjectValue(jsonObject, "designationclass"));
            clasificationTableRow.put("classificationtype", getJsonObjectValue(jsonObject, "classificationtype"));
            clasificationTableRow.put("code_subtype", ConstantUtils.DEFAULT_SUBTYPE_DOC_VALUE);

			validateClasificationTableInfo(clasificationTableRow);

			SubTypeDocInfo info = cuadroClasif.getSubTypeDocInfo(
					(String) clasificationTableRow.get("code_clasificacion"),
					(String) clasificationTableRow.get("code_subtype"));
			if (info != null)
				cuadroClasif.updateClassificationTableRow(clasificationTableRow);
			else {
				String code_clasificacion = (String) clasificationTableRow.get("code_clasificacion");
				if (StringUtils.isEmpty(code_clasificacion))
					throw new GdibException(I18NUtil.getMessage("cuadro.clasificacion.param.null.empty.error",
							new Object[] { "code_clasificacion" }));
				cuadroClasif.insertClassificationTableRow(clasificationTableRow);
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

    private Serializable getJsonObjectValue(JSONObject json, String metadata) throws JSONException{
        if(json.has(metadata))
        {
            return json.getString(metadata);
        }
        return null;
    }

    private void validateClasificationTableInfo(HashMap<String, Serializable> clasificationTableRow) throws GdibException {

    	SubTypeDocInfo subtypeDoc = cuadroClasif.getSubTypeDoc((String)clasificationTableRow.get("code_clasificacion"));

        // si "valor_secundario" tiene el valor "Si". Solo se puede poner el valor CP en "tipo_dictamen"
        String secundaryvalue = (String)clasificationTableRow.get("secundaryvalue");
        String dictumtype = (String)clasificationTableRow.get("dictumtype");
        if(ConstantUtils.VALOR_SECUNDARIO_SI.equals(secundaryvalue) && !ConstantUtils.TIPO_DICTAMEN_CP.equals(dictumtype)){
            throw new GdibException(I18NUtil.getMessage("cuadro.clasificacion.dependency.param.error",
                    new Object[]{"tipo_dictamen", dictumtype}));
        }

        // Si "accesstype" == Libre. reutilizationcond OBLIGATORIO
        String accessType = (String)clasificationTableRow.get("accesstype");
        String reutilizationcond = (String)clasificationTableRow.get("reutilizationcond");
        if(ConstantUtils.TIPO_ACCESO_LIBRE.equals(accessType) && StringUtils.isEmpty(reutilizationcond)){
            throw new GdibException(I18NUtil.getMessage("cuadro.clasificacion.dependency.param.null.empty.error",
                    new Object[]{"cond_reutilizacion", "tipo_acceso", ConstantUtils.TIPO_ACCESO_LIBRE}));
        }

        // Si la serie documental tiene subtipo documental no puede ser ET
        if(subtypeDoc != null && ConstantUtils.TIPO_DICTAMEN_ET.equals((String)clasificationTableRow.get("dictumtype")))
        	throw new GdibException(I18NUtil.getMessage("cuadro.clasificacion.create.documentaryseries.exist.subtypedoc.error"));

        // Si "dictumtype" == EP o ET. termdictatedaction OBLIGATORIO
        dictumtype = (String)clasificationTableRow.get("dictumtype");
        String termdictatedaction = (String)clasificationTableRow.get("termdictatedaction");
        if((ConstantUtils.TIPO_DICTAMEN_EP.equals(dictumtype) || ConstantUtils.TIPO_DICTAMEN_ET.equals(dictumtype))
                && StringUtils.isEmpty(termdictatedaction)){
            String dictumtype_value = ConstantUtils.TIPO_DICTAMEN_EP.equals(dictumtype)?ConstantUtils.TIPO_DICTAMEN_EP:ConstantUtils.TIPO_DICTAMEN_ET;
            throw new GdibException(I18NUtil.getMessage("cuadro.clasificacion.dependency.param.null.empty.error",
                    new Object[]{"plazo_accion_dictaminada", "accion_dictaminada", dictumtype_value}));
        }

        // LUIS - Esto sobraria al no haber columna de documento vital
//        String vitalDocument = (String)clasificationTableRow.get("vital_document");
//        // Si la serie documental no tiene subtipo documental puede ser cualquier cosa, boolean documento vital false.
//        if(subtypeDoc == null && !Boolean.valueOf(vitalDocument)
//        		&& ConstantUtils.TIPO_DICTAMEN_CP.equals(dictumtype)){
//        	throw new GdibException(I18NUtil.getMessage("cuadro.clasificacion.dictumType.no.cp.no.subtype.documentvital.false.error"));
//        }
//        // Si la serie documental no tiene subtipo documental puede ser CP, boolean documento vital true.
//        if(subtypeDoc == null && Boolean.valueOf(vitalDocument)
//        		&& !ConstantUtils.TIPO_DICTAMEN_CP.equals(dictumtype)){
//        	throw new GdibException(I18NUtil.getMessage("cuadro.clasificacion.dictumType.cp.no.subtype.documentvital.true.error"));
//        }
    }

    private Map<String, Object> executeDELETEMethod(WebScriptRequest req)
    {
//		/gdib/expedient/cuadro/{code_clasificacion}/{code_subtype}
        try {
            final String code_clasificacion = req.getExtensionPath().split("/")[0];
            final String code_subtype = req.getExtensionPath().split("/")[1];

            cuadroClasif.deleteClassificationTableRow(code_clasificacion, ConstantUtils.DEFAULT_SUBTYPE_DOC_VALUE);

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
