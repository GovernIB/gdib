package es.caib.gdib.ws.common.types;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "content", propOrder = {
    "mimetype",
    "data",
    "encoding",
    "byteSize"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class Content {

	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
    protected String mimetype;	
    @XmlMimeType("application/octet-stream")
    @XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
    protected DataHandler data;
    @XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
    protected String encoding;
    @XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
    protected Long byteSize;

    public Long getByteSize() {
        return byteSize;
    }

    public void setByteSize(Long byteSize) {
        this.byteSize = byteSize;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String value) {
        this.mimetype = value;
    }

    public DataHandler getData() {
        return data;
    }

    public void setData(DataHandler value) {
        this.data = value;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String value) {
        this.encoding = value;
    }

}
