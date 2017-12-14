package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * Tipo de datos compuesto que representa el resultado retornado 
 * al invocar una operación de firma electrónica mediante los servicios 
 * de repositorio y migración de la capa CSGD (formato DSS).
 * 
 * @author u104848
 *
 */
public class DSSResult {
	private String ResultMajor;
	private String ResultMinor;
	private String ResultMessage;
	
	public String getResultMajor() {
		return ResultMajor;
	}
	public void setResultMajor(String resultMajor) {
		ResultMajor = resultMajor;
	}
	public String getResultMinor() {
		return ResultMinor;
	}
	public void setResultMinor(String resultMinor) {
		ResultMinor = resultMinor;
	}
	public String getResultMessage() {
		return ResultMessage;
	}
	public void setResultMessage(String resultMessage) {
		ResultMessage = resultMessage;
	}
	

	
	
}
