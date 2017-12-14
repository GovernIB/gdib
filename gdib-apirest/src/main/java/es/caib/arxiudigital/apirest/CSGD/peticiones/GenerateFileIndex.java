package es.caib.arxiudigital.apirest.CSGD.peticiones;


import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;

public class GenerateFileIndex {
	private Request<ParamNodeId> generateFileIndexRequest;

	public Request<ParamNodeId> getGenerateFileIndexRequest() {
		return generateFileIndexRequest;
	}

	public void setGenerateFileIndexRequest(Request<ParamNodeId> generateFileIndexRequest) {
		this.generateFileIndexRequest = generateFileIndexRequest;
	}


}
