package es.caib.arxiudigital.apirest.facade.pojos;

/**
 * 
 * @author anadal
 *
 */
public class CabeceraLogin {

  // Ciudadano
  private String nombreSolicitante;
  private String documentoSolicitante;

  // Trabajador
  private String nombreUsuario;
  private String documentoUsuario;

  /**
   * 
   */
  public CabeceraLogin() {
    super();
  }

  /**
   * @param nombreSolicitante
   * @param documentoSolicitante
   * @param nombreUsuario
   * @param documentoUsuario
   */
  public CabeceraLogin(String nombreSolicitante, String documentoSolicitante,
      String nombreUsuario, String documentoUsuario) {
    super();
    this.nombreSolicitante = nombreSolicitante;
    this.documentoSolicitante = documentoSolicitante;
    this.nombreUsuario = nombreUsuario;
    this.documentoUsuario = documentoUsuario;
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

}
