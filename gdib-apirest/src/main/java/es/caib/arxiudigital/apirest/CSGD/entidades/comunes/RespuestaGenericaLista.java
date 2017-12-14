package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import java.util.List;

public class RespuestaGenericaLista <T>{
	
	private ResultData result;
	private List<T> resParam;
	public ResultData getResult() {
		return result;
	}
	public void setResult(ResultData result) {
		this.result = result;
	}
	public List<T> getResParam() {
		return resParam;
	}
	public void setResParam(List<T> resParam) {
		this.resParam = resParam;
	}
	
	


	
}
