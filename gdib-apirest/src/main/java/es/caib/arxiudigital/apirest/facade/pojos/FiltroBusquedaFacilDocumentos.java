package es.caib.arxiudigital.apirest.facade.pojos;

public class FiltroBusquedaFacilDocumentos {

	//TODO Eliminar
	
	private String nombreAplicacion;
	private String autor;
	private String nombreDocumento;
	private IntervaloFechas fechaCreacion;
	private IntervaloFechas fechaModificacion;
	private String eniId;
	private String contenido;
	private String mimetype;
	private String docSeries;
	
	public String getNombreAplicacion() {
		return nombreAplicacion;
	}
	public void setNombreAplicacion(String nombreAplicacion) {
		this.nombreAplicacion = nombreAplicacion;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getNombreDocumento() {
		return nombreDocumento;
	}
	public void setNombreDocumento(String nombreDocumento) {
		this.nombreDocumento = nombreDocumento;
	}
	public IntervaloFechas getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(IntervaloFechas fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public IntervaloFechas getFechaModificacion() {
		return fechaModificacion;
	}
	public void setFechaModificacion(IntervaloFechas fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}
	public String getEniId() {
		return eniId;
	}
	public void setEniId(String eniId) {
		this.eniId = eniId;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public String getDocSeries() {
		return docSeries;
	}
	public void setDocSeries(String docSeries) {
		this.docSeries = docSeries;
	}
	
	
	
}
