package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * 
 * Tipo de datos compuesto que representa la cabecera incluida en las peticiones de los servicios 
 * de repositorio y migración de la capa CSGD, con información de auditoria y seguridad
 * 
 * @author u104848
 *
 */
public class ServiceHeader {
	
	private String 				serviceVersion;
	private ServiceAuditInfo    auditInfo;
	private ServiceSecurityInfo securityInfo;
	
	
	public String getServiceVersion() {
		return serviceVersion;
	}
	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	
	public ServiceAuditInfo getAuditInfo() {
		return auditInfo;
	}
	public void setAuditInfo(ServiceAuditInfo auditInfo) {
		this.auditInfo = auditInfo;
	}
	public ServiceSecurityInfo getSecurityInfo() {
		return securityInfo;
	}
	public void setSecurityInfo(ServiceSecurityInfo securityInfo) {
		this.securityInfo = securityInfo;
	}

	
}
