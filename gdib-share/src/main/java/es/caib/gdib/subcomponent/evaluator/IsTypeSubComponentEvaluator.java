package es.caib.gdib.subcomponent.evaluator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.util.StringUtils;

/**
 * Evaluador para controlar si se oculta un subcomponente.
 *
 * Si el nodo no es del tipo "parametro type" el subcomponente se oculta.
 *
 * @author RICOH.
 *
 */
public class IsTypeSubComponentEvaluator extends DefaultSubComponentEvaluator {

	private static Log logger = LogFactory.getLog(IsTypeSubComponentEvaluator.class);

	public static final String TYPE_PROP = "type";
	public static final String CSV_SEPARATOR = ";";

	@Override
	public boolean evaluate(RequestContext context, Map<String, String> evaluationProperties) {
		// recupera el nodo de la uri del contexto o como parametro
		Map<String, String> uriTokens = context.getUriTokens();
		String nodeRef = uriTokens.get("nodeRef");
		if (nodeRef == null) {
			nodeRef = context.getParameter("nodeRef");
		}
		// recupero el aspecto que esta definido como parametro en la consola de despliegue de modulos de share
		String typeParam = evaluationProperties.get(TYPE_PROP).trim();

		if (!StringUtils.isEmpty(nodeRef) && !StringUtils.isEmpty(typeParam)) {
			List<String> types = Arrays.asList(typeParam.split(CSV_SEPARATOR));
			String userId = context.getUserId();
			Connector conn;
			try {
				// realizo la conexion con el repositorio de alfresco
				conn = context.getServiceRegistry().getConnectorService()
						.getConnector(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID, userId, ServletUtil.getSession());
				// llamo a un webscript de alfresco para obtener la lista de aspectos del nodo
//				http://localhost:8080/alfresco/s/slingshot/doclib/node/workspace/SpacesStore/949482cc-3da3-48dc-89b5-048cb61178be
				Response response = conn.call("/slingshot/doclib/node/" + nodeRef.replace(":/", ""));
				if (response.getStatus().getCode() == Status.STATUS_OK) {
					// Compruebo si en la lista de aspectos esta el aspecto que recibe el evaluador como parametro
					JSONObject jsonObject = new JSONObject(response);
					JSONObject jsonResponse = new JSONObject(jsonObject.get("response").toString());
					JSONObject itemResponse = (JSONObject) jsonResponse.get("item");
					String typeResponse = (String) itemResponse.get("nodeType");
					if(!types.contains(typeResponse))
						return true;
				}
			} catch (Exception e) {
				logger.error(e);
				return true;
			}
		}
		return false;
	}

}
