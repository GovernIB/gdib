package es.caib.gdib.ws.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nodeVersion", propOrder = {
	"id",
	"date"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class NodeVersion {

	@XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
	private String id;
	@XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
	private String date;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
