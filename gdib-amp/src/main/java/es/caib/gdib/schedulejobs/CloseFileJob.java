 package es.caib.gdib.schedulejobs;

import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;
import es.caib.gdib.ws.impl.RepositoryServiceSoapPortImpl;
import es.caib.gdib.ws.impl.authtrans.AuthTransRepositoryServiceSoapPortImpl;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

 public class CloseFileJob implements Job{
     private static final Logger LOGGER = Logger.getLogger(CloseFileJob.class);



     @Override
     public void execute(JobExecutionContext context) throws JobExecutionException {
         LOGGER.debug("Ejecuta el closeFileJob");
         JobDataMap jobData = context.getJobDetail().getJobDataMap();

         // Extraer el job para ejecutar
         Object executerObj = jobData.get("jobExecuter");
         LOGGER.debug("executerObj obtenido: "+executerObj.getClass());
         LOGGER.debug("executerObj obtenido: "+executerObj.toString());
         if (executerObj == null || !(executerObj instanceof CloseFile)) {
             throw new AlfrescoRuntimeException(
                     "ScheduledJob data must contain valid 'Executer' reference");
         }

         final CloseFile jobExecuter = (CloseFile) executerObj;

         AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
             public Object doWork() throws Exception {
                 LOGGER.debug("Entra en el doWork del closeFile");
                 jobExecuter.execute();
                 return null;
             }
         }, AuthenticationUtil.getAdminUserName());
     }
 }
