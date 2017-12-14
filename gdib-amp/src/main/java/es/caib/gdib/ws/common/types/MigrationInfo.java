package es.caib.gdib.ws.common.types;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "migrationInfo", propOrder = {
	"valcertSign",
	"zipContent"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class MigrationInfo {
	@XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
	private DataHandler valcertSign;
	@XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
	private DataHandler zipContent;
	
	public DataHandler getValcertSign() {
		return valcertSign;
	}
	public void setValcertSign(DataHandler valcertSign) {
		this.valcertSign = valcertSign;
	}
	public DataHandler getZipContent() {
		return zipContent;
	}
	public void setZipContent(DataHandler zipContent) {
		this.zipContent = zipContent;
	}
	
}
