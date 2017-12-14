package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * Tipo de datos compuesto que representa el resultado retornado al invocar los servicios de repositorio y migración de la capa CSGD.
 * 
 * @author u104848
 *
 */
public class ResultData {
	private String code;
	private String description;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString(){
		String rtdo;
		
		rtdo = "Resultado:\n"+
				"\t"+this.getCode()+"\n"+
				"\t"+this.getDescription()+"\n";
		
		return rtdo;
	}
}
