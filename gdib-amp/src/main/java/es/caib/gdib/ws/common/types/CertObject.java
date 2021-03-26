package es.caib.gdib.ws.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchresults", propOrder = {
	"idCertificado",
	"numExpedientes"
},namespace = "http://www.caib.es/gdib/repository/ws")

public class CertObject {
	@XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
	private String idCertificado;
	@XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
	private Long numExpedientes;
	public String getId() {
		return idCertificado;
	}
	public void setId(String id) {
		this.idCertificado = id;
	}
	public Long getNumExpedientes() {
		return numExpedientes;
	}
	public void setNumExpedientes(Long numExpedientes) {
		this.numExpedientes = numExpedientes;
	}
	
}
