package es.caib.arxiudigital.apirest.JerseyClient;

public class ResultadoJersey {
	private String contenido;
	private int estadoRespuestaHttp;
	
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public int getEstadoRespuestaHttp() {
		return estadoRespuestaHttp;
	}
	public void setEstadoRespuestaHttp(int estadoRespuestaHttp) {
		this.estadoRespuestaHttp = estadoRespuestaHttp;
	}
	
	public String toString(){
		String rtdo="ResultadoJersey:\n";
		rtdo+=" - contenido:"+contenido+"\n";
		rtdo+=" - estadoRespuestaHttp:"+estadoRespuestaHttp+"\n";

		return rtdo;
	}
	
	
}
