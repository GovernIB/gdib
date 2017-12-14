package es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada;

import java.util.List;

import es.caib.arxiudigital.apirest.constantes.Permisos;

public class ParamGrantPermissions {
	private List<String> nodeIds;
	private List<String> authorities;
	private Permisos permission;
	
	public List<String> getNodeIds() {
		return nodeIds;
	}
	public void setNodeIds(List<String> nodeIds) {
		this.nodeIds = nodeIds;
	}
	public List<String> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<String> authorities) {
		this.authorities = authorities;
	}
	public Permisos getPermission() {
		return permission;
	}
	public void setPermission(Permisos permission) {
		this.permission = permission;
	}
	

	
	
}
