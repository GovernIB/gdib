package es.caib.gdib.ws.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "migrationId", propOrder = {
	"appId",
	"externalId"
},namespace = "http://www.caib.es/gdib/migration/ws")
public class MigrationID {

	@XmlElement(required = true, nillable = false, namespace = "http://www.caib.es/gdib/migration/ws")
	private String appId;
	@XmlElement(required = true, nillable = false, namespace = "http://www.caib.es/gdib/migration/ws")
	private String externalId;

	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
}
