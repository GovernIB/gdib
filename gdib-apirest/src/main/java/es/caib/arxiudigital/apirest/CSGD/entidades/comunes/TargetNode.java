package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;


public class TargetNode {

	private String id;
	private DocClassification docClassification;
	private String targetType;
	
	
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public DocClassification getDocClassification() {
		return docClassification;
	}
	public void setDocClassification(DocClassification docClassification) {
		this.docClassification = docClassification;
	}
	
	
	
}
