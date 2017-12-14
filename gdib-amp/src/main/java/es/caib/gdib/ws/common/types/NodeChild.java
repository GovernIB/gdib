package es.caib.gdib.ws.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nodeChild", propOrder = {
	"id",
	"name",
    "type"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class NodeChild {
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	String id;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	String name;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	String type;
	
	public NodeChild(String id, String name, String type){
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
