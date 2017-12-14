package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeID_TargetParent;

public class LinkFile {
	private Request<ParamNodeID_TargetParent> linkFileRequest;

	public Request<ParamNodeID_TargetParent> getLinkFileRequest() {
		return linkFileRequest;
	}

	public void setLinkFileRequest(Request<ParamNodeID_TargetParent> linkFileRequest) {
		this.linkFileRequest = linkFileRequest;
	}


}
