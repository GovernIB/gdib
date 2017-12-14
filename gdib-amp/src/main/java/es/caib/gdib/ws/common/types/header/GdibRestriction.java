package es.caib.gdib.ws.common.types.header;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gdibRestriction", propOrder = {
	"types"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class GdibRestriction {
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private List<String> types;

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}
	

}
