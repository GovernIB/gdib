package es.caib.arxiudigital.apirest.facade.pojos;

import es.caib.arxiudigital.apirest.constantes.TiposObjetoSGD;

public class Nodo {
	/**
	 * Identificador, tipo UID, del nodo
	 */
	private String id;
	/**
	 * Tipo de nodo
	 */
	private TiposObjetoSGD type;
	/**
	 * Nombre del Nodo
	 */
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
