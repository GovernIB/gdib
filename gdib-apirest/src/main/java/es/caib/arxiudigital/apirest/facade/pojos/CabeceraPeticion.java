package es.caib.arxiudigital.apirest.facade.pojos;

import org.apache.commons.lang3.StringEscapeUtils;

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
		this.serviceVersion = StringEscapeUtils.escapeXml(serviceVersion);
	}
	public String getDocumentoSolicitante() {
		return documentoSolicitante;
	}
	public void setDocumentoSolicitante(String documentoSolicitante) {
		this.documentoSolicitante = StringEscapeUtils.escapeXml(documentoSolicitante);
	}
	public String getNombreSolicitante() {
		return nombreSolicitante;
	}
	public void setNombreSolicitante(String nombreSolicitante) {
		this.nombreSolicitante = StringEscapeUtils.escapeXml(nombreSolicitante);
	}
	public String getIdExpediente() {
		return idExpediente;
	}
	public void setIdExpediente(String idExpediente) {
		this.idExpediente = StringEscapeUtils.escapeXml(idExpediente);
	}
	public String getNombreProcedimiento() {
		return nombreProcedimiento;
	}
	public void setNombreProcedimiento(String nombreProcedimiento) {
		this.nombreProcedimiento = StringEscapeUtils.escapeXml(nombreProcedimiento);
	}
	public String getCodiAplicacion() {
		return codiAplicacion;
	}
	public void setCodiAplicacion(String codiAplicacion) {
		this.codiAplicacion = StringEscapeUtils.escapeXml(codiAplicacion);
	}
	public String getUsuarioSeguridad() {
		return usuarioSeguridad;
	}
	public void setUsuarioSeguridad(String usuarioSeguridad) {
		this.usuarioSeguridad = StringEscapeUtils.escapeXml(usuarioSeguridad);
	}
	public String getPasswordSeguridad() {
		return passwordSeguridad;
	}
	public void setPasswordSeguridad(String passwordSeguridad) {
		this.passwordSeguridad = StringEscapeUtils.escapeXml(passwordSeguridad);
	}
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = StringEscapeUtils.escapeXml(nombreUsuario);
	}
	public String getDocumentoUsuario() {
		return documentoUsuario;
	}
	public void setDocumentoUsuario(String documentoUsuario) {
		this.documentoUsuario = StringEscapeUtils.escapeXml(documentoUsuario);
	}
	public String getOrganizacion() {
		return organizacion;
	}
	public void setOrganizacion(String organizacion) {
		this.organizacion = StringEscapeUtils.escapeXml(organizacion);
	}



}
