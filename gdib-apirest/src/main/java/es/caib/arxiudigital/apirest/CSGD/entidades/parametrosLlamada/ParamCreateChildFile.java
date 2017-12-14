package es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FileNode;

public class ParamCreateChildFile {
	private String parent;
	private FileNode file;
	private String retrieveNode;
	
	public FileNode getFile() {
		return file;
	}
	public void setFile(FileNode file) {
		this.file = file;
	}
	public String getRetrieveNode() {
		return retrieveNode;
	}
	public void setRetrieveNode(String retrieveNode) {
		this.retrieveNode = retrieveNode;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}

	
}
