package es.caib.arxiudigital.apirest.CSGD.peticiones;

import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class ExportFile {
	private Request<ParamNodeId> exportFileRequest;

	public Request<ParamNodeId> getExportFileRequest() {
		return exportFileRequest;
	}

	public void setExportFileRequest(Request<ParamNodeId> exportFileRequest) {
		this.exportFileRequest = exportFileRequest;
	}

}
