package es.caib.gdib.ws.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "qname")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qnameType", propOrder = { "url","name"})
public class QName {
	
	@XmlElement (required=true)
	private String url;
	
	@XmlElement(required=true)
	private String name;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

}
