package es.caib.gdib.ws.common.types;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "certsearchresults", propOrder = {
	"resultados"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class CertSearchResults {

		@XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
		private List<CertObject> listObjects;

		public List<CertObject> getListObjects() {
			return listObjects;
		}

		public void setListObjects(List<CertObject> listObjects) {
			this.listObjects = listObjects;
		}
		
		
		
}
