package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;


/**
 * Tipo de datos compuesto que representa la información de auditoría sobre los expedientes 
 * administrativos que es incluida en la cabecera de las peticiones de los 
 * servicios de repositorio y migración de la capa CSGD.
 * 
 * @author u104848
 *
 */
public class FileAuditInfo {
	private String id;
	private ProceedingsAuditInfo proceedings;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ProceedingsAuditInfo getProceedings() {
		return proceedings;
	}
	public void setProceedings(ProceedingsAuditInfo proceedings) {
		this.proceedings = proceedings;
	}		
}
