package es.caib.arxiudigital.apirest.facade.pojos;

public class CabeceraPeticion {
	private String serviceVersion;
	private String documentoSolicitante;
	private String nombreSolicitante;
	private String nombreUsuario;
	private String documentoUsuario;
	private String organizacion;

	private String idExpediente;
	private String nombreProcedimiento;
	private String codiAplicacion;
	private String usuarioSeguridad;
	private String passwordSeguridad;
	
	public String getServiceVersion() {
		return serviceVersion;
	}
	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}
	public String getDocumentoSolicitante() {
		return documentoSolicitante;
	}
	public void setDocumentoSolicitante(String documentoSolicitante) {
		this.documentoSolicitante = documentoSolicitante;
	}
	public String getNombreSolicitante() {
		return nombreSolicitante;
	}
	public void setNombreSolicitante(String nombreSolicitante) {
		this.nombreSolicitante = nombreSolicitante;
	}
	public String getIdExpediente() {
		return idExpediente;
	}
	public void setIdExpediente(String idExpediente) {
		this.idExpediente = idExpediente;
	}
	public String getNombreProcedimiento() {
		return nombreProcedimiento;
	}
	public void setNombreProcedimiento(String nombreProcedimiento) {
		this.nombreProcedimiento = nombreProcedimiento;
	}
	public String getCodiAplicacion() {
		return codiAplicacion;
	}
	public void setCodiAplicacion(String codiAplicacion) {
		this.codiAplicacion = codiAplicacion;
	}
	public String getUsuarioSeguridad() {
		return usuarioSeguridad;
	}
	public void setUsuarioSeguridad(String usuarioSeguridad) {
		this.usuarioSeguridad = usuarioSeguridad;
	}
	public String getPasswordSeguridad() {
		return passwordSeguridad;
	}
	public void setPasswordSeguridad(String passwordSeguridad) {
		this.passwordSeguridad = passwordSeguridad;
	}
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	public String getDocumentoUsuario() {
		return documentoUsuario;
	}
	public void setDocumentoUsuario(String documentoUsuario) {
		this.documentoUsuario = documentoUsuario;
	}
	public String getOrganizacion() {
		return organizacion;
	}
	public void setOrganizacion(String organizacion) {
		this.organizacion = organizacion;
	}



}
