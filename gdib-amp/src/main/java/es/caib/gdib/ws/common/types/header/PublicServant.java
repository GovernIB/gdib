package es.caib.gdib.ws.common.types.header;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "publicServant", propOrder = {
	"document",
	"name",
	"organization"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class PublicServant {
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private String document;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private String name;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private String organization;
	
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	
}
