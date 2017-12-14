package es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.TargetNode;

public class ParamDispatchDocument {
	
	private TargetNode targetNode;
	private String sourceNodeId;
	
	
	public TargetNode getTargetNode() {
		return targetNode;
	}
	public void setTargetNode(TargetNode targetNode) {
		this.targetNode = targetNode;
	}
	public String getSourceNodeId() {
		return sourceNodeId;
	}
	public void setSourceNodeId(String sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}

	

	
	
}
