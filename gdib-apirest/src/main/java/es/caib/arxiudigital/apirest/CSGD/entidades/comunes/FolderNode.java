package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

import es.caib.arxiudigital.apirest.constantes.TiposObjetoSGD;


/**
 * 
 * Tipo de datos compuesto que representa un nodo de tipo cm:folder (agrupación documental).
 * 
 * @author u104848
 *
 */
public class FolderNode {
	private String id;
	private String name;
	private TiposObjetoSGD type;
	private List<SummaryInfoNode> childObjects;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TiposObjetoSGD getType() {
		return type;
	}
	public void setType(TiposObjetoSGD type) {
		this.type = type;
	}
	public List<SummaryInfoNode> getChildObjects() {
		return childObjects;
	}
	public void setChildObjects(List<SummaryInfoNode> childObjects) {
		this.childObjects = childObjects;
	}
	
}
