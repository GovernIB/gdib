package es.caib.gdib.ws.common.types.header;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Applicant", propOrder = {
	"document",
	"name"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class Applicant {
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private String document;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private String name;
	
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
}
