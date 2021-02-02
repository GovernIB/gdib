package es.caib.gdib.mail;

import com.drew.lang.StringUtil;
import es.caib.gdib.ws.exception.GdibException;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MailServiceImpl {

    private final static Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    //    private JavaMailSenderImpl mailSender;
    private transient ActionService actionService;

    /**
     * envío de email con attachments
     *
     * @param destinations      correo electrónico del destinatario (De ser varios, separados por ";")
     * @param subject asunto del mensaje
     * @param text    cuerpo del mensaje
     */
    public void send(String destinations, String subject, String text) throws GdibException {
        LOGGER.info("Se accede a send del MailAction. Se procede a enviar las notificaciones con asunto ["+subject+"] a los destinatarios ["+destinations+"]");
        LOGGER.debug("Texto de la notificacion:\n"+text);
        try {
            if (StringUtils.trimToNull(destinations) != null) {

                List<String> destinatarios = destinations.contains(";") ? Arrays.asList(destinations.split(";")) : Arrays.asList(destinations);

                destinatarios.forEach(to -> {
                    Action mailAction = actionService.createAction(MailActionExecuter.NAME);
                    mailAction.setParameterValue(MailActionExecuter.PARAM_SUBJECT, subject);
                    mailAction.setParameterValue(MailActionExecuter.PARAM_TO, to.trim());
                    mailAction.setParameterValue(MailActionExecuter.PARAM_TEXT, text);
                    actionService.executeAction(mailAction, null);
                    LOGGER.debug("mailService :: " + subject + " :: sendEmail : action mail execute");
                });

            }
        }catch (Exception e){
            LOGGER.error("Se ha producido una excepcion en el envio del mail: "+e);
            throw new GdibException(e.getMessage());
        }

    }

    public ActionService getActionService() {
        return actionService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }
}
