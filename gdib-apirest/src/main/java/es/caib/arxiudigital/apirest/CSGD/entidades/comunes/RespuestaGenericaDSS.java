package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

public class RespuestaGenericaDSS <T>{
	
	private DSSResult Result;
	private T resParam;
	public int result;
	
	


	public DSSResult getResult() {
		return Result;
	}
	public void setResult(DSSResult result) {
		Result = result;
	}
	public T getResParam() {
		return resParam;
	}
	public void setResParam(T resParam) {
		this.resParam = resParam;
	}

	
}
