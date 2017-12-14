package es.caib.gdib.ws.common.types;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchresults", propOrder = {
	"numResultados",
	"numPaginas",
	"resultados"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class SearchResults {

	@XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
	protected long numResultados;
	@XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
	protected int numPaginas;
	@XmlElement(nillable = true,namespace = "http://www.caib.es/gdib/repository/ws")
	protected List<Node> resultados;
	
	public long getNumResultados() {
		return numResultados;
	}
	public void setNumResultados(long numResultados) {
		this.numResultados = numResultados;
	}
	public int getNumPaginas() {
		return numPaginas;
	}
	public void setNumPaginas(int numPaginas) {
		this.numPaginas = numPaginas;
	}
	public List<Node> getResultados() {
		return resultados;
	}
	public void setResultados(List<Node> resultados) {
		this.resultados = resultados;
	}
}
