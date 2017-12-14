package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

public class RequestMigratedDocId {
	private String applicationId;
	private String externalId;
	
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
}
