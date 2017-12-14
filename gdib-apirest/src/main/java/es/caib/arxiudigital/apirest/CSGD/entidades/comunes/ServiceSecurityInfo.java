package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * Tipo de datos compuesto que representa la información de seguridad 
 * incluida en la cabecera de las peticiones de los servicios de repositorio 
 * y migración de la capa CSGD.
 * 
 * @author u104848
 *
 */
public class ServiceSecurityInfo {
	private String user;
	private String password;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
