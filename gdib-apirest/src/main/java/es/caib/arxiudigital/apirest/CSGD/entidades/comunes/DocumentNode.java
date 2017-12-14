package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

import es.caib.arxiudigital.apirest.constantes.Aspectos;
import es.caib.arxiudigital.apirest.constantes.TiposObjetoSGD;

public class DocumentNode {
	private String id;
	private String name;
	private TiposObjetoSGD type;
	private List<Metadata>  metadataCollection;
	private List<Aspectos> aspects;
	private List<Content> binaryContents;
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
	public List<Metadata> getMetadataCollection() {
		return metadataCollection;
	}
	public void setMetadataCollection(List<Metadata> metadataCollection) {
		this.metadataCollection = metadataCollection;
	}
	public List<Aspectos> getAspects() {
		return aspects;
	}
	public void setAspects(List<Aspectos> aspects) {
		this.aspects = aspects;
	}
	public List<Content> getBinaryContents() {
		return binaryContents;
	}
	public void setBinaryContents(List<Content> binaryContents) {
		this.binaryContents = binaryContents;
	}

	

}
