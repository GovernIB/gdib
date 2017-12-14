package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import es.caib.arxiudigital.apirest.constantes.TiposObjetoSGD;

public class SummaryInfoNode {
	private String id;
	private TiposObjetoSGD type;
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TiposObjetoSGD getType() {
		return type;
	}
	public void setType(TiposObjetoSGD type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
