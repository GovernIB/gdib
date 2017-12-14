package es.caib.gdib.ws.common.types;

import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "node", propOrder = {
	"id",
	"type",
	"name",
    "aspects",
    "properties",
    "content",
    "sign",
    "childs"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class Node {

    @XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
    protected List<String> aspects;
    @XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
    protected Content content;
    @XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
    protected DataHandler sign;
    @XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
    protected String id;
    @XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
    protected String name;
    @XmlElement(nillable = true, namespace = "http://www.caib.es/gdib/repository/ws")
    protected List<Property> properties;
    @XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
    protected String type;
    @XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
    protected List<NodeChild> childs;

    public List<String> getAspects() {
        if (aspects == null) {
            aspects = new ArrayList<String>();
        }
        return this.aspects;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content value) {
        this.content = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public void setAspects(List<String> value) {
        this.aspects = null;
        List<String> draftl = this.getAspects();
        draftl.addAll(value);
    }

	public DataHandler getSign() {
		return sign;
	}

	public void setSign(DataHandler sign) {
		this.sign = sign;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public void addProperties(List<Property> properties) {
		if(!CollectionUtils.isEmpty(properties)){
			this.properties.addAll(properties);
		}
	}

	public List<NodeChild> getChilds() {
		return childs;
	}

	public void setChilds(List<NodeChild> childs) {
		this.childs = childs;
	}
}
