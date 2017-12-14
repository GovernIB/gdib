package es.caib.arxiudigital.apirest.facade.resultados;

public class Resultado <T> extends ResultadoSimple {


	private T elementoDevuelto;
	

	public T getElementoDevuelto() {
		return elementoDevuelto;
	}
	public void setElementoDevuelto(T elementoDevuelto) {
		this.elementoDevuelto = elementoDevuelto;
	}
	
	public Resultado(ResultadoSimple padre) {
		super(padre);
	}
	
	public Resultado() {
	}

}
