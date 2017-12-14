package es.caib.gdib.ws.common.types;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datanodetransform", propOrder = {
	"migrationId",
	"metadata"
},namespace = "http://www.caib.es/gdib/migration/ws")
public class DataNodeTransform {
	@XmlElement(namespace = "http://www.caib.es/gdib/migration/ws")
	MigrationID migrationId;
	@XmlElement(namespace = "http://www.caib.es/gdib/migration/ws")
	List<Property> metadata;
	
	public MigrationID getMigrationId() {
		return migrationId;
	}
	public void setMigrationId(MigrationID migrationId) {
		this.migrationId = migrationId;
	}
	public List<Property> getMetadata() {
		return metadata;
	}
	public void setMetadata(List<Property> metadata) {
		this.metadata = metadata;
	}
	
}
