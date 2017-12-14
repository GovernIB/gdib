package es.caib.arxiudigital.apirest.facade.resultados;

import java.util.List;

public class ResultadoBusqueda <T> extends ResultadoSimple {
	int numeroTotalResultados;
	int numeroPagina;
	List <T> listaResultado;
	
	public int getNumeroTotalResultados() {
		return numeroTotalResultados;
	}
	public void setNumeroTotalResultados(int numeroTotalResultados) {
		this.numeroTotalResultados = numeroTotalResultados;
	}
	public int getNumeroPagina() {
		return numeroPagina;
	}
	public void setNumeroPagina(int numeroPagina) {
		this.numeroPagina = numeroPagina;
	}
	public List<T> getListaResultado() {
		return listaResultado;
	}
	public void setListaResultado(List<T> listaResultado) {
		this.listaResultado = listaResultado;
	}
	
	

}
