package es.caib.gdib.utils;

public class SubTypeDocInfo {

	private String documentarySeries;
	private String subtypeDoc;
	private String lopd;
	private String confidentiality;
	private String accessType;
	private String causeLimitationCode;
	private String normative;
	private String reutilizationCond;
	private String valueType;
	private String timeLimit;
	private String secundaryValue;
	private String dictumType;
	private String dictatedAction;
	private String termDictatedAction;
	private Boolean vitalDocument;
	private String designationClass;
	private String classificationType;
	private String resealing;
	private String description;

	public SubTypeDocInfo() {

	}

	public SubTypeDocInfo(String documentarySeries) {
		this.documentarySeries = documentarySeries;
	}

	public SubTypeDocInfo(String documentarySeries, String subtypeDoc) {
		this.documentarySeries = documentarySeries;
		this.subtypeDoc = subtypeDoc;
	}

	public String getDocumentarySeries() {
		return documentarySeries;
	}

	public void setDocumentarySeries(String documentarySeries) {
		this.documentarySeries = documentarySeries;
	}

	public String getSubtypeDoc() {
		return subtypeDoc;
	}

	public void setSubtypeDoc(String subtypeDoc) {
		this.subtypeDoc = subtypeDoc;
	}

	public String getLopd() {
		return lopd;
	}

	public void setLopd(String lopd) {
		this.lopd = lopd;
	}

	public String getConfidentiality() {
		return confidentiality;
	}

	public void setConfidentiality(String confidentiality) {
		this.confidentiality = confidentiality;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getCauseLimitationCode() {
		return causeLimitationCode;
	}

	public void setCauseLimitationCode(String causeLimitationCode) {
		this.causeLimitationCode = causeLimitationCode;
	}

	public String getNormative() {
		return normative;
	}

	public void setNormative(String normative) {
		this.normative = normative;
	}

	public String getReutilizationCond() {
		return reutilizationCond;
	}

	public void setReutilizationCond(String reutilizationCond) {
		this.reutilizationCond = reutilizationCond;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getSecundaryValue() {
		return secundaryValue;
	}

	public void setSecundaryValue(String secundaryValue) {
		this.secundaryValue = secundaryValue;
	}

	public String getDictumType() {
		return dictumType;
	}

	public void setDictumType(String dictumType) {
		this.dictumType = dictumType;
	}

	public String getDictatedAction() {
		return dictatedAction;
	}

	public void setDictatedAction(String dictatedAction) {
		this.dictatedAction = dictatedAction;
	}

	public String getTermDictatedAction() {
		return termDictatedAction;
	}

	public void setTermDictatedAction(String termDictatedAction) {
		this.termDictatedAction = termDictatedAction;
	}

	public Boolean getVitalDocument() {
		return vitalDocument;
	}

	public void setVitalDocument(Boolean vitalDocument) {
		this.vitalDocument = vitalDocument;
	}

	public String getDesignationClass() {
		return designationClass;
	}

	public void setDesignationClass(String designationClass) {
		this.designationClass = designationClass;
	}

	public String getClassificationType() {
		return classificationType;
	}

	public void setClassificationType(String classificationType) {
		this.classificationType = classificationType;
	}

	public String getResealing() {
		return resealing;
	}

	public void setResealing(String resealing) {
		this.resealing = resealing;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
