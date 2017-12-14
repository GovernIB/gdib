package es.caib.gdib.ws.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GdibExceptionInfo", propOrder = {
    "code",
    "message"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class GdibExceptionInfo {

	@XmlElement(required = true)
	private int code;

	@XmlElement(required = true)
	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
