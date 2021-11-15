package es.caib.gdib.ws.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.webscript.cuadroclasif.CuadroClasificacionSerieDocumental;
import es.caib.gdib.ws.exception.GdibException;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "property", propOrder = {
	"qname",
	"value"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class Property {
	private static final Logger LOGGER = Logger.getLogger(Property.class);
	
	@XmlElement(namespace="http://www.caib.es/gdib/repository/ws")
	String qname;
	@XmlElement(namespace="http://www.caib.es/gdib/repository/ws")
	String value;
	public String getQname() {
		return qname;
	}
	public void setQname(String qname) {
		this.qname = qname;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public Property(String qname, String value){
		this.qname = qname;
		this.value = value;
	}

	public Property(String qname){
		this.qname = qname;
	}

	public Property(QName qname, String value){
		this.qname = qname.toString();
		this.value = value;
	}

	public Property(QName qname){
		this.qname = qname.toString();
	}

	public Property(){
		this.qname = null;
		this.value = null;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Property){
			Property prop = (Property)obj;
			try {
				QName qname = GdibUtils.createQName(this.getQname());
				QName qnameComprate = GdibUtils.createQName(prop.getQname());
				return qname.equals(qnameComprate);
			} catch (GdibException e) {
				LOGGER.error(e);
			}
		}
		return false;
	}


}
