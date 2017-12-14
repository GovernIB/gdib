package es.caib.gdib.ws.common.types;

public class SignatureValidationReport {

	private ValidationStatus validationStatus;
	private String detailedValidationStatus;
	private String validationMessage;
	private String signatureType;
	private String signatureForm;
	
	public SignatureValidationReport(){
		validationStatus = ValidationStatus.NO_DETERMINADO;
		this.setDetailedValidationStatus("");
		this.setValidationMessage("");
		this.setSignatureType("");
		this.setSignatureForm("");
	}
	
	
	public ValidationStatus getValidationStatus() {
		return validationStatus;
	}
	public void setValidationStatus(ValidationStatus validationStatus) {
		this.validationStatus = validationStatus;
	}
	/**
	 * @return the detailedValidationStatus
	 */
	public String getDetailedValidationStatus() {
		return detailedValidationStatus;
	}
	/**
	 * @param detailedValidationStatus the detailedValidationStatus to set
	 */
	public void setDetailedValidationStatus(String detailedValidationStatus) {
		this.detailedValidationStatus = detailedValidationStatus;
	}
	/**
	 * @return the vaidationMessage
	 */
	public String getValidationMessage() {
		return validationMessage;
	}
	/**
	 * @param vaidationMessage the vaidationMessage to set
	 */
	public void setValidationMessage(String vaidationMessage) {
		this.validationMessage = vaidationMessage;
	}


	public String getSignatureType() {
		return signatureType;
	}


	public void setSignatureType(String signatureType) {
		this.signatureType = signatureType;
	}


	public String getSignatureForm() {
		return signatureForm;
	}


	public void setSignatureForm(String signatureForm) {
		this.signatureForm = signatureForm;
	}

	
	
}
