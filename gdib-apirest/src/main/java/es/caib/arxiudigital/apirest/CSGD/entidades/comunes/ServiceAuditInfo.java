package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * Tipo de datos compuesto que representa la información de auditoría incluida en la cabecera de las 
 * peticiones de los servicios de repositorio y migración de la capa CSGD.
 * 
 * @author u104848
 *
 */
public class ServiceAuditInfo {
	private PersonIdentAuditInfo applicant;
	private PublicServantAuditInfo publicServant;
	private FileAuditInfo file;
	private String application;
	
	public PersonIdentAuditInfo getApplicant() {
		return applicant;
	}
	public void setApplicant(PersonIdentAuditInfo applicant) {
		this.applicant = applicant;
	}
	public PublicServantAuditInfo getPublicServant() {
		return publicServant;
	}
	public void setPublicServant(PublicServantAuditInfo publicServant) {
		this.publicServant = publicServant;
	}
	public FileAuditInfo getFile() {
		return file;
	}
	public void setFile(FileAuditInfo file) {
		this.file = file;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	
	
}
