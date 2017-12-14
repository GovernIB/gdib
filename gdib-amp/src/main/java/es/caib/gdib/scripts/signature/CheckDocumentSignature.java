package es.caib.gdib.scripts.signature;

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.log4j.Logger;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.iface.EniModelUtilsInterface;
import es.caib.gdib.ws.common.types.Content;
import es.caib.gdib.ws.common.types.EniSignatureType;
import es.caib.gdib.ws.common.types.SignatureValidationReport;
import es.caib.gdib.ws.common.types.ValidationStatus;
import es.caib.gdib.ws.exception.GdibException;

public class CheckDocumentSignature extends DeclarativeWebScript {

	private static final Logger LOGGER =  Logger.getLogger(CheckDocumentSignature.class);

	private NodeService nodeService;

	private GdibUtils utils;

	private static final String OP_MSG_KEY = "operationMessage";

	private static final String VAL_STATUS_KEY = "validationStatus";

	private static final String DET_VAL_STATUS_KEY = "detailedValidationStatus";

	private static final String VAL_MSG_KEY = "validationMessage";

	private static final String SIGN_TYPE_KEY = "signatureType";

	private static final String SIGN_FORM_KEY = "signatureForm";

	private static final String MSG_KEY = "message";

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		Boolean isMigratedDoc = Boolean.FALSE;
		DataHandler signatureDh;
		Map<String, Object> res = new HashMap<String, Object>();
		NodeRef nodeRef,parent,firmaMigracion,firmaMigracionZip;
		String nodeId,operationMessage, valStatus, valMsg, detValStatus, signType, signForm;
		SignatureValidationReport result;

		LOGGER.info("Se solicita la validación de un documento desde el share...");

		nodeId = req.getParameter("nodeRef");
		nodeRef = new NodeRef(nodeId);
		LOGGER.info("Documento a verificar: " + nodeId);

		signatureDh = null;
		operationMessage = "No fue posible validar el documento con id " + nodeId + ". Consulte a su administrador.";
		valStatus = "";
		valMsg = "";
		detValStatus = "";
		signType = "";
		signForm = "";

		try {
			Content sign = null, contentNode = null;
			String tipoFirma = null, perfilFirma = null;

			if(utils.isType(nodeService.getType(nodeRef), ConstantUtils.TYPE_DOCUMENTO_QNAME)){
				// quitado, sea un documento eni transformado o original, los datos de tipo firma y perfil se obtienen igual
//				if(nodeService.hasAspect(nodeRef, ConstantUtils.ASPECT_TRANSFORMADO_QNAME))
//				{
//					// Document ENI transformado
//				}
//				else
//				{
					//Documento ENI
					tipoFirma = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_TIPO_FIRMA_QNAME);
					perfilFirma = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_PERFIL_FIRMA_QNAME);
					LOGGER.debug("El documento a verificar es de tipo " + ConstantUtils.TYPE_DOCUMENTO_QNAME);
//				}
			} else if(utils.isType(nodeService.getType(nodeRef), ConstantUtils.TYPE_DOCUMENTO_MIGRADO_QNAME)){
				//Documento migrado
				String name = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_NAME);
				parent = nodeService.getPrimaryParent( nodeRef).getParentRef();
				firmaMigracion =  nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS ,  name + ConstantUtils.FIRMA_MIGRACION);
				firmaMigracionZip =  nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS ,  name + ConstantUtils.FIRMA_MIGRACION_ZIP);

				contentNode = utils.getContent(firmaMigracionZip, ConstantUtils.PROP_CONTENT);
				sign = utils.getContent(firmaMigracion, ConstantUtils.PROP_CONTENT);

				if(sign == null || sign.getData() == null){
					throw new GdibException("Validación NO correcta del documento " + nodeId + ". El documento NO tiene firma electrónica y no puede ser validado.");
				}
				signatureDh = sign.getData();

				tipoFirma = (String) nodeService.getProperty(firmaMigracion, ConstantUtils.PROP_TIPO_FIRMA_QNAME);
				perfilFirma = (String) nodeService.getProperty(firmaMigracion, ConstantUtils.PROP_PERFIL_FIRMA_QNAME);
				LOGGER.debug("El documento a verificar es de tipo " + ConstantUtils.TYPE_DOCUMENTO_MIGRADO_QNAME);
				isMigratedDoc = Boolean.TRUE;
			} else {
				throw new GdibException("Validación NO correcta. El objeto " + nodeId + "  no es un documento.");
			}

			LOGGER.debug("Tipo firma ("+tipoFirma+") Perfil de firma ("+perfilFirma+")");

			//Se comprueba el perfil de firma
			EniSignatureType eniSignatureType = EniSignatureType.valueOf(tipoFirma);

			if(eniSignatureType == null){
	    		throw new GdibException("Validación NO correcta del documento " + nodeId + ". La propiedad o metadato " +
	    				EniModelUtilsInterface.PROP_TIPO_FIRMA + " del documento tiene un valor no admitido: " + tipoFirma + ".");
	    	}

			LOGGER.debug("El tipo de firma del documento a verificar es " + eniSignatureType.getName());

			if(EniSignatureType.TF01.equals(eniSignatureType)){
				//Firma mediante csv
				String csv = (String) nodeService.getProperty(nodeRef, ConstantUtils.PROP_CSV_QNAME);
				if(csv != null && !csv.isEmpty()){
					operationMessage = "Validación correcta del CSV del documento.";
					valStatus = ValidationStatus.CORRECTO.getName();
					valMsg = "El documento presenta CSV.";
					LOGGER.debug("El documento a verificar presenta CSV y es correcto.");
				} else {
					operationMessage = "Validación NO correcta del CSV del documento.";
					valStatus = ValidationStatus.NO_CORRECTO.getName();
					valMsg = "El documento no presenta CSV.";
					LOGGER.debug("El documento a verificar NO presenta CSV.");
				}
			} else {
				//Firma mediante certificado digital
				//Se obtiene el contenido y firma si no es un documento migrado
				if(!isMigratedDoc){

					if(nodeService.hasAspect(nodeRef, ConstantUtils.ASPECT_TRANSFORMADO_QNAME))
					{
						// Document ENI transformado
						contentNode = utils.getContent(nodeRef, ConstantUtils.PROP_ZIPMIGRACION_QNAME);
						LOGGER.debug("Obtengo el zip de migracion");
					}
					else
					{
						// Document
						contentNode = utils.getContent(nodeRef, ConstantUtils.PROP_CONTENT);
						LOGGER.debug("Obtengo el contenido del nodo");
					}

					// Firma electrónica explicita, requiere informar doc y firma
					if(EniSignatureType.TF04.equals(eniSignatureType)){
//						if(nodeService.hasAspect(nodeRef, ConstantUtils.ASPECT_TRANSFORMADO_QNAME))
//						{
//							// Document ENI transformado
//							sign = utils.getContent(nodeRef, ConstantUtils.PROP_FIRMAVALCERT_QNAME);
//							LOGGER.debug("Obtengo la firma Valcert");
//						}else
//						{
							// Document ENI
							sign = utils.getContent(nodeRef, ConstantUtils.PROP_FIRMA_QNAME);
							LOGGER.debug("Obtengo la firma");
//						}

						if(sign == null || sign.getData() == null){
							throw new GdibException("Validación NO correcta del documento " + nodeId + ". El documento NO tiene firma electrónica y no puede ser validado.");
						}

						signatureDh = sign.getData();
					}
				}

			}

			if(contentNode == null){
				throw new GdibException("Validación NO correcta del documento " + nodeId + ". El documento NO tiene contenido y no puede ser validado.");
			}

			//Se invoca el servicio de validación de la plataforma @firma
			result = utils.checkDocumentSignature(nodeRef.getId(),contentNode,signatureDh,tipoFirma,perfilFirma);
			LOGGER.debug("Resultrado obtenido tras validar la firma del documento: " + result.getValidationStatus().getName());

			valStatus = (result.getValidationStatus() == null?ValidationStatus.NO_DETERMINADO.getName():result.getValidationStatus().getName());
			detValStatus = (result.getDetailedValidationStatus() == null?"":result.getDetailedValidationStatus());
			valMsg = (result.getValidationMessage() == null?"":result.getValidationMessage());
			signType = (result.getSignatureType() == null?"":result.getSignatureType());
			signForm = (result.getSignatureForm() == null?"":result.getSignatureForm());

			if(result.getValidationStatus() == ValidationStatus.CORRECTO){
				operationMessage = "Resultado de validación del documento " + nodeId + " correcto.";
    		} else if(result.getValidationStatus() == ValidationStatus.NO_CORRECTO){
    			operationMessage = "Resultado de validación del documento " + nodeId + " incorrecto.";
    		} else if(result.getValidationStatus() == ValidationStatus.NO_DETERMINADO){
    			operationMessage = "Resultado de validación del documento " + nodeId + " indeterminado.";
    		}

		} catch (GdibException e) {
			operationMessage = e.getMessage();
			valStatus = ValidationStatus.NO_CORRECTO.getName();
		}

		LOGGER.info("Documento " + nodeId + " verificado, resultado obtenido: " + valStatus);

		res.put(OP_MSG_KEY, operationMessage);
		res.put(VAL_STATUS_KEY, valStatus);
		res.put(DET_VAL_STATUS_KEY, detValStatus);
		res.put(VAL_MSG_KEY, valMsg);
		res.put(SIGN_TYPE_KEY, signType);
		res.put(SIGN_FORM_KEY, signForm);
		res.put(MSG_KEY,"operationMessage");

		return res;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setUtils(GdibUtils utils) {
		this.utils = utils;
	}

}
