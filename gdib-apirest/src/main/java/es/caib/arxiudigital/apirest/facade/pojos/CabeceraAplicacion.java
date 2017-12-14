package es.caib.arxiudigital.apirest.facade.pojos;

/**
 * 
 * @author anadal
 *
 */
public class CabeceraAplicacion {

  private String codiAplicacion;
  private String organizacion;
  private String usuarioSeguridad;
  private String passwordSeguridad;


  /**
   * @param codiAplicacion
   * @param usuarioSeguridad
   * @param passwordSeguridad
   * @param organizacion
   */
  public CabeceraAplicacion(String codiAplicacion, String organizacion,
      String usuarioSeguridad, String passwordSeguridad) {
    super();
    this.codiAplicacion = codiAplicacion;
    this.usuarioSeguridad = usuarioSeguridad;
    this.passwordSeguridad = passwordSeguridad;
    this.organizacion = organizacion;
  }

  /**
   * 
   */
  public CabeceraAplicacion() {
    super();
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

  public String getOrganizacion() {
    return organizacion;
  }

  public void setOrganizacion(String organizacion) {
    this.organizacion = organizacion;
  }

}
