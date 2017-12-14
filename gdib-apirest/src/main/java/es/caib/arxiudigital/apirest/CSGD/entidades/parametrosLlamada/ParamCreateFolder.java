package es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FolderNode;

public class ParamCreateFolder {
	private String parent;
	private FolderNode folder;
	private String retrieveNode;
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public FolderNode getFolder() {
		return folder;
	}
	public void setFolder(FolderNode folder) {
		this.folder = folder;
	}
	public String getRetrieveNode() {
		return retrieveNode;
	}
	public void setRetrieveNode(String retrieveNode) {
		this.retrieveNode = retrieveNode;
	}
	

	
}
