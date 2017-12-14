package es.caib.arxiudigital.apirest.facade.resultados;

public class ResultadoSimple {
	private String codigoResultado;
	private String msjResultado;
	
	public ResultadoSimple(ResultadoSimple padre){
		codigoResultado=padre.codigoResultado;
		msjResultado=padre.msjResultado;
	}

	public ResultadoSimple(){
		
	}
	
	public String getCodigoResultado() {
		return codigoResultado;
	}
	public void setCodigoResultado(String codigoResultado) {
		this.codigoResultado = codigoResultado;
	}
	public String getMsjResultado() {
		return msjResultado;
	}
	public void setMsjResultado(String msjResultado) {
		this.msjResultado = msjResultado;
	}


}
