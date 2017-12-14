package es.caib.gdib.ws.common.types.header;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gdibSecurity", propOrder = {
	"user","password"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class GdibSecurity {
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private String user;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private String password;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}
