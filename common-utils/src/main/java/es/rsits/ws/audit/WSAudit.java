package es.rsits.ws.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import es.rsits.ws.audit.type.AuditData;

public class WSAudit {

	private ThreadPoolTaskExecutor auditTaskExecutor;

	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	public void auditOperation(AuditData auditData) {
		// creo una entrada de auditoria de WSAuditExecuter (Runnable)
		WSAuditExecutor audit = new WSAuditExecutor(auditData);

		runTask(audit);
	}

	public void auditError(AuditData auditData) {
		// creo una entrada de auditoria de WSAuditExecuter (Runnable)
		WSAuditExecutor audit = new WSAuditExecutor(auditData);

		runTask(audit);
	}

	private void runTask(WSAuditExecutor audit){

		// le inyecto los bean que necesita para funcionar
		beanFactory.autowireBeanProperties(audit, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		// inserto el bean runable al threadpool para executarlo
		auditTaskExecutor.execute(audit);
	}

	public ThreadPoolTaskExecutor getAuditTaskExecutor() {
		return auditTaskExecutor;
	}

	public void setAuditTaskExecutor(ThreadPoolTaskExecutor auditTaskExecutor) {
		this.auditTaskExecutor = auditTaskExecutor;
	}

	public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
