package es.caib.arxiudigital.apirest.facade.pojos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.caib.arxiudigital.apirest.constantes.Aspectos;
import es.caib.arxiudigital.apirest.constantes.TiposObjetoSGD;

public class Expediente extends Nodo{

	private TiposObjetoSGD type = TiposObjetoSGD.EXPEDIENTE;
	
	/**
	 * Lista de metadatos o propiedades del nodo.
	 */
	private Map<String, Object> metadataCollection = null;
	
	/**
	 * Lista de aspectos del nodo.
	 */
	private List<Aspectos> aspects = null;
	
	/**
	 * Lista de nodos hijos de primer nivel del nodo.
	 */
	private List<Nodo> childs;
	
	public Expediente() {
		super();
	}
	
	
	public Expediente(boolean expedienteParaCrear) {
		super();
		expedienteParaCrear( expedienteParaCrear);
	}
	
	public void expedienteParaCrear(boolean expedienteParaCrear){
		if(expedienteParaCrear==true){
			createAspects();
			type = TiposObjetoSGD.EXPEDIENTE;
		}else{
			aspects = null;
			type = null;
		}
	}
	
	private void createAspects(){
		if(aspects == null){
			List<Aspectos> listaAspectos = new ArrayList<Aspectos>();
			listaAspectos.add(Aspectos.INTEROPERABLE);
			listaAspectos.add(Aspectos.TRANSFERIBLE);
			aspects = listaAspectos;
		}
	}




	public Map<String, Object> getMetadataCollection() {
		return metadataCollection;
	}


	public void setMetadataCollection(Map<String, Object> metadataCollection) {
		this.metadataCollection = metadataCollection;
	}



	public TiposObjetoSGD getType() {
		return type;
	}

	public List<Aspectos> getAspects() {
		return aspects;
	}


	public List<Nodo> getChilds() {
		return childs;
	}


	public void setChilds(List<Nodo> childs) {
		this.childs = childs;
	}
	
	
}
