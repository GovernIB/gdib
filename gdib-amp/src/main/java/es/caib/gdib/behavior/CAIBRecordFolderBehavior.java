package es.caib.gdib.behavior;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.model.ApplicationModel;
import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;


import es.caib.gdib.utils.ConstantUtils;

public class CAIBRecordFolderBehavior {

	private final static Logger LOGGER = LoggerFactory.getLogger(CAIBRecordFolderBehavior.class);

	private transient PolicyComponent eventManager;
	private transient NodeService nodeService;
	private transient ActionService actionService;
	private transient AuthorityService authorityService;
	private transient PersonService personService;
	private transient BehaviourFilter policyBehaviourFilter;

	private String groupEmailTo;
	private Boolean emailNotifications;

	private static final String PREFIX_GROUP = "GROUP_";

	public void registerEventHandlers() {
		
		eventManager.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				RecordsManagementModel.TYPE_RECORD_FOLDER,
				new JavaBehaviour(this, "onUpdateDocumentProperties", NotificationFrequency.TRANSACTION_COMMIT));

	}

	public void onUpdateDocumentProperties(NodeRef docRef, Map<QName, Serializable> before,
			Map<QName, Serializable> after)
	{
		LOGGER.debug("onUpdateDocumentProperties :: start");

		String estadoArchivoBefore = null;
		String estadoArchivoAfter = null;
		if(before.get(ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME) != null)
			estadoArchivoBefore = (String)before.get(ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME);
		if(after.get(ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME) != null)
			estadoArchivoAfter = (String)after.get(ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME);

		LOGGER.debug("onUpdateDocumentProperties : estadoArchivoBefore ({}) estadoArchivoAfter({})", estadoArchivoBefore, estadoArchivoAfter);
		// si los dos son null no se hace nada
		if(!StringUtils.isEmpty(estadoArchivoBefore) || !StringUtils.isEmpty(estadoArchivoAfter))
		{
			// Si cambio el estado
			if( (StringUtils.isEmpty(estadoArchivoBefore) && !StringUtils.isEmpty(estadoArchivoAfter))
					|| (!StringUtils.isEmpty(estadoArchivoBefore) && StringUtils.isEmpty(estadoArchivoAfter))
					|| (!estadoArchivoAfter.equals(estadoArchivoBefore)))
			{
				// cambiar el estado a todos los hijos
				for (ChildAssociationRef childAssoc : nodeService.getChildAssocs(docRef))
				{
					NodeRef child = childAssoc.getChildRef();
					if(ConstantUtils.TYPE_DOCUMENTO_QNAME.equals(nodeService.getType(child)))
					{
						LOGGER.debug("onUpdateDocumentProperties : change child nodes ({}) state", child.getId());
						// desactivo y activo el behavior para que no envie dos emails.
						policyBehaviourFilter.disableBehaviour(child);
						nodeService.setProperty(child, ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME, estadoArchivoAfter);
						policyBehaviourFilter.enableBehaviour(child);
					}
				}

				// enviar un email informando del cambio
				LOGGER.debug("onUpdateDocumentProperties : send email");
				if(emailNotifications)
					sendEmail(docRef, estadoArchivoBefore, estadoArchivoAfter);
			}
		}

		LOGGER.debug("onUpdateDocumentProperties :: end");
	}

	private void sendEmail(NodeRef exp, String estadoArchivoBefore, String estadoArchivoAfter)
	{
		LOGGER.debug("onUpdateDocumentProperties :: sendEmail : start");
		String to = "";
		Set<String> usersGroup = null;

		if(!StringUtils.isEmpty(this.groupEmailTo))
			usersGroup = authorityService.getContainedAuthorities(AuthorityType.USER, PREFIX_GROUP+this.groupEmailTo, false);

		if(!CollectionUtils.isEmpty(usersGroup))
		{
			for (String userName : usersGroup) {
				NodeRef user = personService.getPerson(userName);
				to += "," + (String)nodeService.getProperty(user, ContentModel.PROP_EMAIL);
			}
			to = to.substring(1);
//			to = "alfrescoto@yopmail.com";
		}
		LOGGER.debug("onUpdateDocumentProperties :: sendEmail : email to ({})", to);

		if(!StringUtils.isEmpty(to))
		{
			String subject = I18NUtil.getMessage("gdib.rm.expedient.estado_archivo.change.email.subject");
			String name = (String)nodeService.getProperty(exp, ContentModel.PROP_NAME);
			String body = I18NUtil.getMessage("gdib.rm.expedient.estado_archivo.change.email.body", new Object[]{name, exp.getId(), estadoArchivoBefore, estadoArchivoAfter} );

	        Action mailAction = actionService.createAction(MailActionExecuter.NAME);
	        mailAction.setParameterValue(MailActionExecuter.PARAM_SUBJECT, subject);
	        mailAction.setParameterValue(MailActionExecuter.PARAM_TO, to);
	        mailAction.setParameterValue(MailActionExecuter.PARAM_TEXT, body);
	        actionService.executeAction(mailAction, null);
		}
		LOGGER.debug("onUpdateDocumentProperties :: sendEmail : end");
	}

	public void setEventManager(PolicyComponent eventManager) {
		this.eventManager = eventManager;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setActionService(ActionService actionService) {
		this.actionService = actionService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public void setGroupEmailTo(String groupEmailTo) {
		this.groupEmailTo = groupEmailTo;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setPolicyBehaviourFilter(BehaviourFilter policyBehaviourFilter) {
		this.policyBehaviourFilter = policyBehaviourFilter;
	}

	public void setEmailNotifications(Boolean emailNotifications) {
		this.emailNotifications = emailNotifications;
	}
}
