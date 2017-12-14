package es.caib.gdib.ws.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import es.caib.gdib.ws.common.types.header.GdibAudit;
import es.caib.gdib.ws.common.types.header.GdibRestriction;
import es.caib.gdib.ws.common.types.header.GdibSecurity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gdibHeader", propOrder = {
	"gdibAudit",
	"gdibRestriction",
	"gdibSecurity"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class GdibHeader {
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private GdibAudit gdibAudit;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private GdibRestriction gdibRestriction;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private GdibSecurity gdibSecurity;
	
	public GdibAudit getGdibAudit() {
		return gdibAudit;
	}
	public void setGdibAudit(GdibAudit gdibAudit) {
		this.gdibAudit = gdibAudit;
	}
	public GdibRestriction getGdibRestriction() {
		return gdibRestriction;
	}
	public void setGdibRestriction(GdibRestriction gdibRestriction) {
		this.gdibRestriction = gdibRestriction;
	}
	public GdibSecurity getGdibSecurity() {
		return gdibSecurity;
	}
	public void setGdibSecurity(GdibSecurity gdibSecurity) {
		this.gdibSecurity = gdibSecurity;
	}	
}
