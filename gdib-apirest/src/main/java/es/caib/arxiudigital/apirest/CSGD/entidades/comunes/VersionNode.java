package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * 
 * Tipo de datos compuesto que representa una versión de un nodo almacenado
 * en el SGD, independientemente del tipo al que pertenezca.
 * 
 * @author u104848
 *
 */
public class VersionNode {
	private String id;
	private String date;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
}
