package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * Tipo de datos compuesto que representa un periodo de tiempo, 
 * mediante su fecha inicial y su fecha final.
 * 
 * El formato fecha tipo String conforme a lo establecido por el estándar ISO-8601, 
 * fecha y hora completa (YYYY-MM-DDTHH:mm:ss.sssZ)
 * 
 * @author u104848
 *
 */
public class DatePeriod {
	private String initialDate;
	private String endDate;
	
	public String getInitialDate() {
		return initialDate;
	}
	public void setInitialDate(String initialDate) {
		this.initialDate = initialDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	
}
