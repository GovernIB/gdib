package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

public class RespuestaDocumento{
	
	private ResultData result;
	private DocumentNode resParam;
	
	
	public ResultData getResult() {
		return result;
	}
	public void setResult(ResultData result) {
		this.result = result;
	}
	public DocumentNode getResParam() {
		return resParam;
	}
	public void setResParam(DocumentNode resParam) {
		this.resParam = resParam;
	}

	
}
