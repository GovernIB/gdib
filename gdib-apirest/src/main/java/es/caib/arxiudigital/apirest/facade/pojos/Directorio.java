package es.caib.arxiudigital.apirest.facade.pojos;

import java.util.List;

import es.caib.arxiudigital.apirest.constantes.TiposObjetoSGD;

public class Directorio extends Nodo{

	private List<Nodo> childs;
	private final TiposObjetoSGD type = TiposObjetoSGD.DIRECTORIO;

	public TiposObjetoSGD getType() {
		return type;
	}

	public List<Nodo> getChilds() {
		return childs;
	}

	public void setChilds(List<Nodo> childs) {
		this.childs = childs;
	}
	
	
	
}
