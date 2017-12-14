package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeID_TargetParent;

public class LinkFolder {
	private Request<ParamNodeID_TargetParent> linkFolderRequest;

	public Request<ParamNodeID_TargetParent> getLinkFolderRequest() {
		return linkFolderRequest;
	}

	public void setLinkFolderRequest(Request<ParamNodeID_TargetParent> linkFolderRequest) {
		this.linkFolderRequest = linkFolderRequest;
	}



}
