package es.caib.gdib.schedulejobs;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ResealDocumentsJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobData = context.getJobDetail().getJobDataMap();

		// Extraer el job para ejecutar
        Object executerObj = jobData.get("jobExecuter");
        if (executerObj == null || !(executerObj instanceof ResealDocuments)) {
            throw new AlfrescoRuntimeException(
                    "ScheduledJob data must contain valid 'Executer' reference");
        }

        final ResealDocuments jobExecuter = (ResealDocuments) executerObj;

        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
            public Object doWork() throws Exception {
                jobExecuter.execute();
                return null;
            }
		}, AuthenticationUtil.getAdminUserName());
	}

}
