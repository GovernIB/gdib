package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

public class MensajeError {
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
	
	public String toString() { 
		return "  {\n   code:"+code+",\n   description:"+description+"\n  }";
	}
}
